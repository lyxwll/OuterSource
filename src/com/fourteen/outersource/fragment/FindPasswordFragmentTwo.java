package com.fourteen.outersource.fragment;
/**
 * 修改密保问题:FindPasswordFragmentTwo类
 */
import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fourteen.outersource.R;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.SecretBean;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.toast.CustomToast;

public class FindPasswordFragmentTwo extends Fragment implements OnClickListener{

	View view;

	public TextView question1TextView;
	public TextView question2TextView;
	public TextView question3TextView;
	
	EditText answer1EditText;
	EditText answer2EditText;
	EditText answer3EditText;
	Button buttonNext;
	TextView userNameTextView;
	
	FindPasswordNextListener nextListener;
	
	String userName = "";
	
	ArrayList<SecretBean> list = new ArrayList<SecretBean>();
	
	public static final int RECEIVED_USER_INFO = 0X1234;
	public static final int RECEIVED_VERIFY_MSG = 0X23456;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ot_ps_find_password_two, null);
		question1TextView = (TextView) view.findViewById(R.id.register_question1);
		question2TextView=(TextView) view.findViewById(R.id.register_question2);
		question3TextView=(TextView) view.findViewById(R.id.register_question3);
		//显示密保问题列表
		if(UserBase.getUserQuestion1(getActivity()) !=null && 
				!UserBase.getUserQuestion1(getActivity()).equals("") && 
				!UserBase.getUserQuestion1(getActivity()).equals("null")) {
			question1TextView.setText(UserBase.getUserQuestion1(getActivity()));
		}
		if(UserBase.getUserQuestion2(getActivity()) != null && 
				!UserBase.getUserQuestion2(getActivity()).equals("") && 
				!UserBase.getUserQuestion2(getActivity()).equals("null")) {
			question2TextView.setText(UserBase.getUserQuestion2(getActivity()));
		}
		if(UserBase.getUserQuestion3(getActivity()) != null && 
				!UserBase.getUserQuestion3(getActivity()).equals("") && 
				!UserBase.getUserQuestion3(getActivity()).equals("null")) {
			question3TextView.setText(UserBase.getUserQuestion3(getActivity()));
		}
		  
		answer1EditText=(EditText) view.findViewById(R.id.register_answer1);
		answer2EditText=(EditText) view.findViewById(R.id.register_answer2);
		answer3EditText=(EditText) view.findViewById(R.id.register_answer3);
		
		buttonNext = (Button) view.findViewById(R.id.register_secret_done);
		question1TextView.setOnClickListener(this);
		question2TextView.setOnClickListener(this);
		question3TextView.setOnClickListener(this);
		buttonNext.setOnClickListener(this);
		userName = getArguments().getString("userName");
		userNameTextView = (TextView) view.findViewById(R.id.user_name);
		if(userName != null && !userName.equals("")) {
			userNameTextView.setText(userName);
			getSecurityquestion();
		}
		return view;
	}
	
	public void setNextListener(FindPasswordNextListener nextListener){
		this.nextListener = nextListener;
	}
	
	Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
			case RECEIVED_USER_INFO:
				JSONObject jsonObject1 = (JSONObject) msg.obj;
				if (jsonObject1.optInt("result", -1) == 0) {
					JSONObject object = jsonObject1.optJSONObject("user");
					if (object != null) {
						question1TextView.setText(object.optString("user_question1", ""));
						question2TextView.setText(object.optString("user_question2", ""));
						question3TextView.setText(object.optString("user_question3", ""));
					}
				} else {
					if(getActivity() != null) {
						// 注册失败
						CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(getActivity());
						builder.setTitle(R.string.user_not_exists);
						builder.setMessage(jsonObject1.optString("msg", ""));
						builder.setPositiveButton(R.string.sure, null);
						builder.create().show();
					}
				}
				break;
			case RECEIVED_VERIFY_MSG://接收验证消息
				JSONObject jsonObject= (JSONObject) msg.obj;
				if(jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					String[] args = new String[]{answer1EditText.getText().toString().trim(),
					answer2EditText.getText().toString().trim(),
					answer3EditText.getText().toString().trim()};
					nextListener.onClickNextButton(1, args);
					if(getActivity() != null) {
						CustomToast.makeText(getActivity(), R.string.verify_secret_success, CustomToast.LENGTH_SHORT).show();
					}
				} else {
					if(getActivity() != null) {
						CustomToast.makeText(getActivity(), R.string.verify_secret_failure, CustomToast.LENGTH_SHORT).show();
					}
				}
				break;
			}
			return true;
		}
	});
	
	/**
	 * 获取密保问题
	 */
	public void getSecurityquestion() {
		// 请求网络检查
		HttpParams params = new HttpParams();
		// "user_name" 接口的
		params.put("user_name", userName);
		HttpRequest.post("get_user_info_by_name", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						Message message = new Message();
						message.what = RECEIVED_USER_INFO;
						message.obj = jsonObject;
						handler.sendMessage(message);
					}
				});
	}
	
	//检查所有内容
	public boolean isAllChecked(){
		if(answer1EditText.getText().toString() ==null || 
				answer1EditText.getText().toString().equals("") ||
				answer1EditText.getText().length() < 6 ||
				answer2EditText.getText().length() > 12) {
			if(getActivity() != null) {
				CustomToast.makeText(getActivity(), R.string.register_answer1_notify, CustomToast.LENGTH_SHORT).show();
			}
			return false;
		} else if(answer2EditText.getText().toString() == null ||
				answer2EditText.getText().toString().equals("") ||
				answer2EditText.getText().length() < 6 ||
				answer2EditText.getText().length() > 12) {
			if(getActivity() != null) {
				CustomToast.makeText(getActivity(), R.string.register_answer2_notify, CustomToast.LENGTH_SHORT).show();
			}
			return false;
		} else if(answer3EditText.getText().toString() == null ||
				answer3EditText.getText().toString().equals("") ||
				answer3EditText.getText().length() <6 ||
				answer3EditText.getText().length() >12) {
			if(getActivity() != null) {
				CustomToast.makeText(getActivity(), R.string.register_answer3_notify, CustomToast.LENGTH_SHORT).show();
			}
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.register_secret_done://下一步按钮点击事件
			if(isAllChecked() && this.nextListener != null) {
				verifySecret();
			} else {
			}
			break;
		}
	}
	
	//验证密保答案
	public void verifySecret(){
		HttpParams params = new HttpParams();
		params.put("user_name", userName);
		params.put("answer1", answer1EditText.getText().toString().trim());
		params.put("answer2", answer2EditText.getText().toString().trim());
		params.put("answer3", answer3EditText.getText().toString().trim());
		HttpRequest.post("user_verify_secret", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.what = RECEIVED_VERIFY_MSG;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
		});
	}
}
