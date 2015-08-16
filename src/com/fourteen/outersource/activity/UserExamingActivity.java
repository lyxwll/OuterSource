package com.fourteen.outersource.activity;
/**
 * 用户技能测试:UserExamingActivity类
 */
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.ExamBean;
import com.fourteen.outersource.bean.ExamQuestionBean;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.fragment.BlankFragment;
import com.fourteen.outersource.fragment.ChoiceFragment;
import com.fourteen.outersource.fragment.JudgeFragment;
import com.fourteen.outersource.fragment.ProgramFragment;
import com.fourteen.outersource.fragment.ProgramResultFragment;
import com.fourteen.outersource.fragment.QuestionFragment;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.toast.CustomToast;
import com.fourteen.outersource.utils.Logg;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class UserExamingActivity extends BaseActivity implements HeaderClickListener, OnClickListener{
	HeaderView headerView;
	Button submitButton;
	TextView leftTimeTextView;
	Button lastButton;
	Button nexButton;
	LinearLayout initLayout;
	FrameLayout questionLayout;

	/**
	 * 考试出题规则
	 */
	ExamBean examBean;
	/**
	 * 剩下的考试时间，以秒为单位
	 */
	public int leftTime;
	
	/**
	 * 当前的题的下标
	 */
	public int currentIndex = -1;
	/**
	 * 当前的题的bean
	 */
	public ExamQuestionBean questionBean;
	/**
	 * 考试时间是否结束
	 */
	public boolean isTimeOver = false;
	/**
	 * 试题列表
	 */
	ArrayList<ExamQuestionBean> list = new ArrayList<ExamQuestionBean>();
	public static final int RECEVIED_EXAM_QUESTIONS = 0X12345;
	public static final int TIME_CLICK = 0X23456;
	public static final int TIME_OVER = 0X34567;
	public static final int RECEIVED_SUM_SCORE = 0X12567;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if(getIntent() != null && getIntent().hasExtra("examBean")) {
			examBean = (ExamBean) getIntent().getSerializableExtra("examBean");
			leftTime = examBean.exam_time;
		}
		setContentView(R.layout.ot_user_examimg);
		initView();
	}
	
	public void initView(){
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setHeaderClickListener(this);
		submitButton = (Button) findViewById(R.id.submit_exam);
		leftTimeTextView = (TextView) findViewById(R.id.left_time);
		initLayout = (LinearLayout) findViewById(R.id.init_panel);
		questionLayout = (FrameLayout) findViewById(R.id.question_panel);
		lastButton = (Button) findViewById(R.id.last_question);
		nexButton = (Button) findViewById(R.id.next_question);
		lastButton.setOnClickListener(this);
		nexButton.setOnClickListener(this);
		submitButton.setOnClickListener(this);
		if(examBean != null){
			headerView.setTitle(examBean.exam_name);
			getExamQuestion();
		}
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.last_question://上一题
			getLastQuestion();
			break;
		case R.id.next_question://下一题
			getNextQuestion();
			break;
		case R.id.submit_exam://提交试卷
			uploadExaming();
			break;
		}
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch(which) {
		case HeaderView.BACK_POSITION:
			this.onBackPressed();
			break;
		}
	}
	
	Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
			case RECEVIED_EXAM_QUESTIONS://接收考试问题
				initLayout.setVisibility(View.GONE);
				questionLayout.setVisibility(View.VISIBLE);
				new CounterThread().start();
				getNextQuestion();
				break;
			case TIME_CLICK://考试剩余时间
				if(leftTime > 0) {
					leftTime--;
					int hours = leftTime /3600;
					int minute = (leftTime %3600) / 60;
					int second = leftTime - (hours * 3600 + minute * 60);
					leftTimeTextView.setText(hours + getString(R.string.hours) + minute + getString(R.string.minute) + second + getString(R.string.second));
				}
				break;
			case TIME_OVER://考试结束
				isTimeOver = true;
				CustomToast.makeText(UserExamingActivity.this, R.string.time_for_upload, CustomToast.LENGTH_SHORT).show();
				uploadExaming();
				break;
			case RECEIVED_SUM_SCORE://接收总成绩
				JSONObject jsonObject = (JSONObject) msg.obj;
				if(jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					int sumScore = jsonObject.optInt("sum_score", 0);
					CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(UserExamingActivity.this);
					builder.setTitle(R.string.your_sum_score_title);
					builder.setMessage(getString(R.string.your_sum_score) + sumScore);
					builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							UserExamingActivity.this.finish();
						}
					});
					if(!UserExamingActivity.this.isFinishing()) {
						builder.create().show();
					}
				}
				break;
			}
			return true;
		}
	});
	
	/**
	 * 提交此题的答案
	 */
	public void uploadQuestionAnswer(){
		if(questionBean != null) {
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.question_panel);
			if(fragment instanceof QuestionFragment) {
				QuestionFragment questionFragment = (QuestionFragment) fragment;
				HttpParams params = new HttpParams();
				params.put("e_id", examBean.id);
				params.put("user_id", UserBase.getUserId(UserExamingActivity.this));
				params.put("t_id", questionBean.t_id);
				params.put("q_id", questionBean.q_id);
				params.put("category_id", questionBean.category_id);
				params.put("answer", questionBean.answer);
				params.put("source_code", questionBean.source_code);
				params.put("score", questionBean.score);
				if(questionBean.t_id == 5) {
					params.put("your_answer", "");
					params.put("your_source_code", questionFragment.getAnswer());
					questionBean.your_score_code = questionFragment.getAnswer();
				} else {
					params.put("your_answer",questionFragment.getAnswer());
					params.put("your_source_code", "");
					questionBean.your_answer = questionFragment.getAnswer();
				}
				HttpRequest.post("upload_examing_answer", params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						Logg.out("==============" + jsonObject.toString());
					}
				});
			}
		}
	}
	
	
	/**
	 * 获取上一题
	 */
	public void getLastQuestion(){
		uploadQuestionAnswer();
		if(currentIndex > 0) {
			currentIndex --;
			if(currentIndex >= 0 && currentIndex <list.size()) {
				questionBean = list.get(currentIndex);
				if(questionBean.t_id == 1) {//选择题
					ChoiceFragment choiceFragment = new ChoiceFragment();
					Bundle bundle = new Bundle();
					bundle.putSerializable("bean", questionBean);
					choiceFragment.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.question_panel, choiceFragment).commit();
				} else if(questionBean.t_id == 2) {//判断题
					JudgeFragment judgeFragment = new JudgeFragment();
					Bundle bundle = new Bundle();
					bundle.putSerializable("bean", questionBean);
					judgeFragment.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.question_panel, judgeFragment).commit();
				} else if(questionBean.t_id == 3) {//填空题
					BlankFragment blankFragment = new BlankFragment();
					Bundle bundle = new Bundle();
					bundle.putSerializable("bean", questionBean);
					blankFragment.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.question_panel, blankFragment).commit();
				} else if(questionBean.t_id == 4) {//程序结果题
					ProgramResultFragment programResultFragment = new ProgramResultFragment();
					Bundle bundle = new Bundle();
					bundle.putSerializable("bean", questionBean);
					programResultFragment.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.question_panel, programResultFragment).commit();
				} else if(questionBean.t_id == 5) {//程序设计题
					ProgramFragment programFragment = new ProgramFragment();
					Bundle bundle = new Bundle();
					bundle.putSerializable("bean", questionBean);
					programFragment.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.question_panel, programFragment).commit();
				}
		    }
		} else {
			CustomToast.makeText(this, R.string.it_is_first_question, CustomToast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 获取下一题
	 */
	public void getNextQuestion(){
		uploadQuestionAnswer();
		if(currentIndex < list.size()-1) {
			currentIndex++;
			questionBean = list.get(currentIndex);
			if(questionBean.t_id == 1) {
				ChoiceFragment choiceFragment = new ChoiceFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable("bean", questionBean);
				choiceFragment.setArguments(bundle);
				getSupportFragmentManager().beginTransaction().replace(R.id.question_panel, choiceFragment).commit();
			} else if(questionBean.t_id == 2) {
				JudgeFragment judgeFragment = new JudgeFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable("bean", questionBean);
				judgeFragment.setArguments(bundle);
				getSupportFragmentManager().beginTransaction().replace(R.id.question_panel, judgeFragment).commit();
			} else if(questionBean.t_id == 3) {
				BlankFragment blankFragment = new BlankFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable("bean", questionBean);
				blankFragment.setArguments(bundle);
				getSupportFragmentManager().beginTransaction().replace(R.id.question_panel, blankFragment).commit();
			} else if(questionBean.t_id == 4) {
				ProgramResultFragment programResultFragment = new ProgramResultFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable("bean", questionBean);
				programResultFragment.setArguments(bundle);
				getSupportFragmentManager().beginTransaction().replace(R.id.question_panel, programResultFragment).commit();
			} else if(questionBean.t_id == 5) {
				ProgramFragment programFragment = new ProgramFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable("bean", questionBean);
				programFragment.setArguments(bundle);
				getSupportFragmentManager().beginTransaction().replace(R.id.question_panel, programFragment).commit();
			}
		} else {
			CustomToast.makeText(this, R.string.no_more_question, CustomToast.LENGTH_SHORT).show();
		}
		
	}
	
	class CounterThread extends Thread {
		@Override
		public void run() {
			if(examBean != null) {
				for(int i=0; i < examBean.exam_time; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(TIME_CLICK);
				}
				handler.sendEmptyMessage(TIME_OVER);
			}
		}
	}
	
	/**
	 * 解析考试问题
	 * @param jsonObject
	 */
	public void parseExamQuestions(JSONObject jsonObject){
		if(jsonObject != null && jsonObject.optInt("result", -1) == 0) {
			JSONArray jsonArray = jsonObject.optJSONArray("list");
			if(jsonArray != null && jsonArray.length() > 0) {
				for(int i=0; i< jsonArray.length();i++) {
					JSONObject object = jsonArray.optJSONObject(i);
					ExamQuestionBean bean = new ExamQuestionBean();
					bean.e_id = object.optInt("e_id", 0);
					bean.user_id = object.optInt("user_id", 0);
					bean.t_id = object.optInt("t_id", 0);
					bean.q_id = object.optInt("q_id", 0);
					bean.question = object.optString("question", "");
					bean.category_id = object.optInt("category_id", 0);
					bean.pic = object.optString("pic", "");
					bean.a = object.optString("a", "");
					bean.b = object.optString("b", "");
					bean.c = object.optString("c", "");
					bean.d = object.optString("d", "");
					bean.source_code = object.optString("source_code", "");
					bean.answer = object.optString("answer", "");
					bean.score = object.optInt("score", 0);
					list.add(bean);
				}
			}
		}
		Collections.sort(list);
		for(int i=0; i< list.size();i++) {
			Logg.out("t_id=" + list.get(i).t_id + ", q_id=" + list.get(i).q_id);
		}
		Message message = new Message();
		message.what = RECEVIED_EXAM_QUESTIONS;
		message.obj = jsonObject;
		handler.sendMessage(message);
	}
	
	/**
	 * 获得考试问题
	 */
	public void getExamQuestion(){
		HttpParams params = new HttpParams();
		params.put("user_id", UserBase.getUserId(UserExamingActivity.this));
		params.put("exam_id", examBean.id);
		HttpRequest.post("get_examing", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				parseExamQuestions(jsonObject);
			}
		});
	}

	/**
	 * 返回事件
	 */
	@Override
	public void onBackPressed() {
		if(!isTimeOver) {//考试时间完毕
			CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(this);
			builder.setTitle(R.string.examing_warnning);
			builder.setMessage(R.string.no_end_notify);
			builder.setNegativeButton(R.string.submit_exam, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					uploadExaming();
				}
			});
			builder.setPositiveButton(R.string.maintain_exam, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			builder.create().show();
		} else {
			super.onBackPressed();
		}
	}
	
	/**
	 * 提交试卷
	 */
	public void uploadExaming(){
		HttpParams params = new HttpParams();
		params.put("e_id", examBean.id);
		params.put("user_id", UserBase.getUserId(UserExamingActivity.this));
		params.put("category_id", examBean.category_id);
		HttpRequest.post("upload_examing", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.what = RECEIVED_SUM_SCORE;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
		});
	}
	
}
