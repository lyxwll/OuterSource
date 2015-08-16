package com.fourteen.outersource.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fourteen.outersource.R;
import com.fourteen.outersource.adapter.RegisterQuestionAdapter;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.SecretBean;
import com.fourteen.outersource.bean.UserBean;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.toast.CustomToast;
/*
 * 密保问题:RegisterStepThree类
 */
public class RegisterStepThree extends Fragment  implements OnClickListener{
	RegisterJumpListener jumpListener;

	View view;

	public TextView question1TextView;//问题1
	public TextView question2TextView;//问题2
	public TextView question3TextView;//问题3
	
	EditText answer1EditText;//答案1
	EditText answer2EditText;//答案2 
	EditText answer3EditText;//答案3
	Button buttonDone;//完成按钮
	
	
	ArrayList<SecretBean> list = new ArrayList<SecretBean>();
	
	public static final int RECEIVED_SECRET_LIST = 0X21333;
	public static final int RECEIVED_USER_INFO = 0X1234;
	
	//创建View视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ot_register_step_three, null);
		question1TextView = (TextView) view.findViewById(R.id.register_question1);
		question2TextView=(TextView) view.findViewById(R.id.register_question2);
		question3TextView=(TextView) view.findViewById(R.id.register_question3);
		//获取密保问题
		if(getActivity() != null) {
			String question1 = UserBase.getUserQuestion1(getActivity());
			String question2 = UserBase.getUserQuestion2(getActivity());
			String question3 = UserBase.getUserQuestion3(getActivity());
			
			if(question1 !=null && 
					!question1.equals("") && 
					!question1.equals("null")) {
				question1TextView.setText(question1);
			}
			if(question2 != null && 
					!question2.equals("") && 
					!question2.equals("null")) {
				question2TextView.setText(question2);
			}
			if(question3 != null && 
					!question3.equals("") && 
					!question3.equals("null")) {
				question3TextView.setText(question3);
			}
		}
		answer1EditText=(EditText) view.findViewById(R.id.register_answer1);
		answer2EditText=(EditText) view.findViewById(R.id.register_answer2);
		answer3EditText=(EditText) view.findViewById(R.id.register_answer3);
		
		buttonDone=(Button) view.findViewById(R.id.register_secret_done);
		question1TextView.setOnClickListener(this);
		question2TextView.setOnClickListener(this);
		question3TextView.setOnClickListener(this);
		buttonDone.setOnClickListener(this);
		initData();
		return view;
	}
	
	Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
			case RECEIVED_SECRET_LIST://接收密码问题列表
				list.clear();
				JSONObject jsonObject = (JSONObject) msg.obj;
				if(jsonObject.optInt("result", -1) == 0) {
					JSONArray array = jsonObject.optJSONArray("list");
					for(int i=0; i< array.length(); i++) {
						JSONObject object = array.optJSONObject(i);
						SecretBean bean = new SecretBean();
						bean.pas_id = object.optInt("pas_id", -1);
						bean.pas_name = object.optString("pas_name", "");
						list.add(bean);
					}
				}
				break;
			case RECEIVED_USER_INFO://接收用户信息
				JSONObject jsonObject1 = (JSONObject) msg.obj;
				if (jsonObject1.optInt("result", -1) == 0) {
					JSONObject object = jsonObject1.optJSONObject("user");
					if (object != null) {
						UserBean userBean = new UserBean();
						userBean.user_id = object.optInt("user_id", -1);
						userBean.user_name = object.optString("user_name", "");
						userBean.user_password = object.optString(
								"user_password", "");
						userBean.user_sex = object.optInt("user_sex", -1);
						userBean.user_birthday = object.optString(
								"user_birthday", "");
						userBean.user_address = object.optString(
								"user_address", "");
						userBean.user_phone = object
								.optString("user_phone", "");
						userBean.user_email = object
								.optString("user_email", "");
						userBean.user_email_verfied = object.optInt(
								"user_email_verfied", -1);
						userBean.user_url = object.optString("user_url", "");
						userBean.is_developer = object
								.optInt("is_developer", 0);
						userBean.user_question1 = object.optString(
								"user_question1", "");
						userBean.user_question2 = object.optString(
								"user_question2", "");
						userBean.user_question3 = object.optString(
								"user_question3", "");
						userBean.date = object.optString("date", "");
						userBean.account_available = object.optInt(
								"account_available", 0);
						userBean.user_money = object.optDouble("use_money", 0);
						if(getActivity() != null) {
							UserBase.setUserBase(getActivity(), userBean);
							CustomToast.makeText(getActivity(),
									R.string.register_change_secret_success,
									CustomToast.LENGTH_SHORT).show();
						}
						// 跳界面
						if (jumpListener != null) {
							jumpListener.jumpToNext(2);
						}
					}
				} else {
					if(getActivity() != null) {
						// 注册失败
						CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(getActivity());
						builder.setTitle(R.string.register_change_secret_failure);
						builder.setMessage(jsonObject1.optString("msg", ""));
						builder.setPositiveButton(R.string.sure, null);
						builder.create().show();
					}
				}
				break;
			}
		
			return true;
		}
	});
	
	/**
	 * 获取密保问题列表
	 */
	public void initData(){
		HttpParams params = new HttpParams();
		HttpRequest.post("get_password_secret", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.obj = jsonObject;
				message.what = RECEIVED_SECRET_LIST;
				handler.sendMessage(message);
			}
		});
	}
	
	public void setRegisterJumpListener(RegisterJumpListener jumpListener) {
		this.jumpListener = jumpListener;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.register_question1:
			showQuestion(question1TextView);
			break;
		case R.id.register_question2:
			showQuestion(question2TextView);
			break;
		case R.id.register_question3:
			showQuestion(question3TextView);
			break;
		case R.id.register_secret_done:
			if(isAllChecked()) {
				doUpload();
			}
			break;
		}
	}
	
	/**
	 * 提交密保问题
	 */
	public void doUpload(){
		if(getActivity() != null) {
			String question1 = question1TextView.getText().toString().trim();
			String question2 = question2TextView.getText().toString().trim();
			String question3 = question3TextView.getText().toString().trim();
			String answer1 = answer1EditText.getText().toString().trim();
			String answer2 = answer2EditText.getText().toString().trim();
			String answer3 = answer3EditText.getText().toString().trim();
			HttpParams httpParams = new HttpParams();
			httpParams.put("user_name", UserBase.getUserName(getActivity()));
			httpParams.put("user_password", UserBase.getUserPassword(getActivity()));
			httpParams.put("question1", question1);
			httpParams.put("question2", question2);
			httpParams.put("question3", question3);
			httpParams.put("answer1", answer1);
			httpParams.put("answer2", answer2);
			httpParams.put("answer3", answer3);
			HttpRequest.post("change_user_secret", httpParams, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					Message message = new Message();
					message.what = RECEIVED_USER_INFO;
					message.obj = jsonObject;
					handler.sendMessage(message);
				}
			});
		}
	}
	
	/**
	 * 显示密保问题
	 * @param view
	 */
	public void showQuestion(final TextView view){
		if(getActivity() != null) {
			//PopupWindow:悬浮窗
			final PopupWindow popupWindow = new PopupWindow(getActivity());
			popupWindow.setWidth(400);
			popupWindow.setHeight(300);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			View viewContent = inflater.inflate(R.layout.ot_register_question_layout, null);
			ListView listView = (ListView) viewContent.findViewById(R.id.register_questions);
			RegisterQuestionAdapter adapter = new RegisterQuestionAdapter(getActivity(), list);
			listView.setAdapter(adapter);
			popupWindow.setContentView(viewContent);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setFocusable(true);
			popupWindow.showAsDropDown(view, 0, 0);
			
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView, View item, int position, long id) {
					Object object = adapterView.getAdapter().getItem(position);
					if(object instanceof SecretBean) {
						SecretBean bean = (SecretBean) object;
						view.setText(bean.pas_name);
						popupWindow.dismiss();
					}
				}
			});
		}
	}

	//检查全部信息
	public boolean isAllChecked(){
		if(question1TextView.getText().toString() == null || 
				question1TextView.getText().equals("") && getActivity() != null) {
			CustomToast.makeText(getActivity(), R.string.register_question1_notify, CustomToast.LENGTH_SHORT).show();
			return false;
		} else if(question2TextView.getText().toString() == null ||
				question2TextView.getText().toString().equals("") && getActivity() != null) {
			CustomToast.makeText(getActivity(), R.string.register_question2_notify, CustomToast.LENGTH_SHORT).show();
			return false;
		} else if(question3TextView.getText().toString() == null ||
				question3TextView.getText().toString().equals("") && getActivity() != null) {
			CustomToast.makeText(getActivity(), R.string.register_question3_notify, CustomToast.LENGTH_SHORT).show();
			return false;
		} else if(answer1EditText.getText().toString() ==null || 
				answer1EditText.getText().toString().equals("") ||
				answer1EditText.getText().length() < 6 ||
				answer2EditText.getText().length() > 12 && getActivity() != null) {
			CustomToast.makeText(getActivity(), R.string.register_answer1_notify, CustomToast.LENGTH_SHORT).show();
			return false;
		} else if(answer2EditText.getText().toString() == null ||
				answer2EditText.getText().toString().equals("") ||
				answer2EditText.getText().length() < 6 ||
				answer2EditText.getText().length() > 12 && getActivity() != null) {
			CustomToast.makeText(getActivity(), R.string.register_answer2_notify, CustomToast.LENGTH_SHORT).show();
			return false;
		} else if(answer3EditText.getText().toString() == null ||
				answer3EditText.getText().toString().equals("") ||
				answer3EditText.getText().length() <6 ||
				answer3EditText.getText().length() >12 && getActivity() != null) {
			CustomToast.makeText(getActivity(), R.string.register_answer3_notify, CustomToast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
