package com.fourteen.outersource.fragment;

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

import com.fourteen.outersource.R;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.toast.CustomToast;
import com.fourteen.outersource.utils.Logg;

public class FindPasswordFragmentThree extends Fragment implements OnClickListener{

	View view;
	EditText password;
	EditText repeatPassword;
	Button doneButton;
	FindPasswordNextListener nextListener;
	
	String userName = "";
	String answer1 = "";
	String answer2 = "";
	String answer3 = "";
	
	public static final int RECEIVED_CHANGE_PASSWORD_MSG = 0X123;
	public static final int CHANGE_PASSWORD_ERROR = 0X234;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ot_ps_find_password_three, null);
		password = (EditText) view.findViewById(R.id.user_password);
		repeatPassword = (EditText) view.findViewById(R.id.user_repeat_password);
		doneButton = (Button) view.findViewById(R.id.find_password_done);
		if(getArguments() != null) {
			userName = getArguments().getString("userName");
			answer1 = getArguments().getString("answer1");
			answer2 = getArguments().getString("answer2");
			answer3 = getArguments().getString("answer3");
		}
		doneButton.setOnClickListener(this);
		return view;
	}
	
	public  void setNextListener(FindPasswordNextListener nextListener){
		this.nextListener = nextListener;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.find_password_done:
			if(isAllChecked()) {
				changePassword();
			} 
			break;
		}
	}
	
	Handler handler = new Handler(Looper.myLooper()) {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RECEIVED_CHANGE_PASSWORD_MSG:
				JSONObject jsonObject = (JSONObject) msg.obj;
				if(jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					if(getActivity() != null) {
						CustomToast.makeText(getActivity(), R.string.password_finded, CustomToast.LENGTH_SHORT).show();
					}
					if(nextListener != null) {
						nextListener.onClickNextButton(2, null);
					}
				} else {
					if(getActivity() != null) {
						CustomToast.makeText(getActivity(), R.string.password_not_find, CustomToast.LENGTH_SHORT).show();
					}
				}
				break;
			case CHANGE_PASSWORD_ERROR:
				if(getActivity() != null) {
					CustomToast.makeText(getActivity(), R.string.password_not_find, CustomToast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};

	public void changePassword(){
		HttpParams params = new HttpParams();
		params.put("user_name", userName);
		params.put("answer1", answer1);
		params.put("answer2", answer2);
		params.put("answer3", answer3);
		params.put("user_password", password.getText().toString().trim());
		HttpRequest.post("user_find_password", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Logg.out(jsonObject.toString());
				Message message = new Message();
				message.what = RECEIVED_CHANGE_PASSWORD_MSG;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
			
			@Override
			public void onFailure(String result, int statusCode,
					String errorResponse) {
				Message message = new Message();
				message.what = CHANGE_PASSWORD_ERROR;
				message.obj = result;
				handler.sendMessage(message);
			}
		});
	}
	
	public boolean isAllChecked(){
		boolean flg = true;
		if(password.getText().toString() == null || 
				password.getText().toString().equals("") || 
				password.getText().length() <6 ||
				password.getText().length() > 16) {
			if(getActivity() != null) {
				CustomToast.makeText(getActivity(), R.string.register_passowrd_length, CustomToast.LENGTH_SHORT).show();
			}
			return false;
		} else if(repeatPassword.getText().toString() == null ||
				repeatPassword.getText().toString().equals("") ||
				repeatPassword.getText().length() <6 || 
				repeatPassword.getText().length() > 16) {
			if(getActivity() != null) {
				CustomToast.makeText(getActivity(), R.string.register_passowrd_length, CustomToast.LENGTH_SHORT).show();
			}
			return false;
		} else if(!password.getText().toString().equals(repeatPassword.getText().toString())) {
			if(getActivity() != null) {
				CustomToast.makeText(getActivity(), R.string.register_password_notsame, CustomToast.LENGTH_SHORT).show();
			}
			return false;
		}
		return flg;
	}
}
