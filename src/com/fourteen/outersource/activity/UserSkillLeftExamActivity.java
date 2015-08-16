package com.fourteen.outersource.activity;
/**
 * 用户技能剩余考试:UserSkillLeftExamActivity类
 */
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.adapter.UserLeftExamListAdapter;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.ExamBean;
import com.fourteen.outersource.bean.UserSkillBean;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class UserSkillLeftExamActivity extends BaseActivity implements HeaderClickListener, SwipeRefreshLayout.OnRefreshListener, OnItemClickListener{
	HeaderView headerView;
	SwipeRefreshLayout refreshLayout;
	UserSkillBean userSkillBean;
	TextView title;
	ListView listView;
	TextView examLeftNotify;
	
	ArrayList<ExamBean> mList = new ArrayList<ExamBean>();
	UserLeftExamListAdapter adapter;
	
	public static final int REFRESH_MSG = 0X1111;
	public static final int RECEIVED_EXAM_LIST = 0X2345;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if(getIntent() != null && getIntent().hasExtra("userskillbean")) {
			userSkillBean = (UserSkillBean) getIntent().getSerializableExtra("userskillbean");
		}
		setContentView(R.layout.ot_user_skill_left_exam);
		initView();
	}
	
	public void initView(){
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setHeaderClickListener(this);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		refreshLayout.setColorScheme(R.color.common_blue, R.color.warning_background, R.color.common_border, R.color.common_grey);
		refreshLayout.setOnRefreshListener(this);
		title = (TextView) findViewById(R.id.left_exam_title);
		examLeftNotify = (TextView) findViewById(R.id.exam_left_notify);
		listView = (ListView) findViewById(R.id.left_exam);
		adapter = new UserLeftExamListAdapter(this, mList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(userSkillBean != null) {
			headerView.setTitle(userSkillBean.category_name + getResources().getString(R.string.skill_testing));
			title.setText(Html.fromHtml(String.format(getString(R.string.left_exam_title), userSkillBean.category_name)));
			getExamList();
		}
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch(which) {
		case HeaderView.BACK_POSITION:
			this.finish();
			break;
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case REFRESH_MSG:
				refreshLayout.setRefreshing(false);
				break;
			case RECEIVED_EXAM_LIST:
				JSONObject jsonObject = (JSONObject) msg.obj;
				if(jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					JSONArray jsonArray = jsonObject.optJSONArray("list");
					parseExamList(jsonArray);
				}
				refreshLayout.setRefreshing(false);
				break;
			}
		}
	};
	
	/**
	 * 解析考试列表
	 * @param jsonArray
	 */
	public void parseExamList(JSONArray jsonArray) {
		ArrayList<ExamBean> list = new ArrayList<ExamBean>();
		if(jsonArray != null && jsonArray.length() >0) {
			for(int i=0; i< jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.optJSONObject(i);
				ExamBean bean = new ExamBean();
				bean.id = jsonObject.optInt("id", 0);
				bean.category_id = jsonObject.optInt("category_id", 0);
				bean.addtime = jsonObject.optString("addtime", "");
				bean.num_choice = jsonObject.optInt("num_choice", 0);
				bean.score_choice = jsonObject.optInt("score_choice", 0);
				bean.num_judge = jsonObject.optInt("num_judge", 0);
				bean.score_judge = jsonObject.optInt("score_judge", 0);
				bean.num_blank = jsonObject.optInt("num_blank", 0);
				bean.score_blank = jsonObject.optInt("score_blank", 0);
				bean.num_program_result = jsonObject.optInt("num_program_result", 0);
				bean.score_program_result = jsonObject.optInt("score_program_result", 0);
				bean.num_program = jsonObject.optInt("num_program", 0);
				bean.score_program = jsonObject.optInt("score_program", 0);
				bean.sum_score = jsonObject.optInt("sum_score", 0);
				bean.exam_name = jsonObject.optString("exam_name", "");
				bean.exam_time  = jsonObject.optInt("exam_time", 0);
				list.add(bean);
			}
			examLeftNotify.setVisibility(View.GONE);
		} else {
			examLeftNotify.setVisibility(View.VISIBLE);
		}
		mList.clear();
		mList.addAll(list);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onRefresh() {
		if(userSkillBean != null) {
			getExamList();
		}
		//handler.sendEmptyMessageAtTime(REFRESH_MSG, 2000);
	}

	/**
	 * 获得考试列表
	 */
	public void getExamList(){
		HttpParams params = new HttpParams();
		params.put("user_id", UserBase.getUserId(this));
		params.put("user_password", UserBase.getUserPassword(this));
		params.put("category_id", userSkillBean.category_id);
		HttpRequest.post("get_exam_by_category", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.what = RECEIVED_EXAM_LIST;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
		});
	}

	/**
	 * item点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Object object = parent.getAdapter().getItem(position);
		if(object instanceof ExamBean) {
			ExamBean bean = (ExamBean) object;
			//跳转到考试界面
			Intent intent = new Intent();
			intent.setClass(this, UserExamingActivity.class);
			Bundle bundle = new Bundle();
			//传值到UserExamingActivity界面
			bundle.putSerializable("examBean", bean);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
}
