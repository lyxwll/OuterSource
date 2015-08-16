package com.fourteen.outersource.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.fourteen.outersource.R;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.UserBean;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.toast.CustomToast;

/**
 * 注册用户帐号:RegisterStepOne类
 * 
 * @author Administrator
 */
public class RegisterStepOne extends Fragment implements OnClickListener,
		OnFocusChangeListener {
	// 消息序列号
	public static final int USER_NAME_AVAILABLE = 0X333;
	public static final int USER_NAME_UNAVAILABLE = 0X334;
	public static final int USER_EMAIL_AVAILABLE = 0X4443;
	public static final int USER_EMAIL_UNAVAILABLE = 0X4444;
	public static final int RECEIVED_USER_INFO = 0X1234;

	View view;
	EditText userNameEditText;
	EditText passwordEditText;
	EditText repaeatPassEditText;
	EditText emailEditText;
	TextView checkNameTextView;
	TextView checkPasswordTextView;
	TextView checkRepeatPassword;
	TextView checkEmailTextView;
	Button registerButton;

	RegisterJumpListener jumpListener;// fragment跳转界面监听

	//创建View视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 用inflater来加载布局文件
		view = inflater.inflate(R.layout.ot_register_step_one, null);
		userNameEditText = (EditText) view
				.findViewById(R.id.register_user_name);
		passwordEditText = (EditText) view
				.findViewById(R.id.register_user_password);
		repaeatPassEditText = (EditText) view
				.findViewById(R.id.register_user_password_repeat);
		emailEditText = (EditText) view.findViewById(R.id.register_email);
		checkNameTextView = (TextView) view.findViewById(R.id.check_user_name);
		checkPasswordTextView = (TextView) view
				.findViewById(R.id.check_password);
		checkRepeatPassword = (TextView) view
				.findViewById(R.id.check_repeat_password);
		checkEmailTextView = (TextView) view.findViewById(R.id.check_email);
		registerButton = (Button) view.findViewById(R.id.register_button);

		registerButton.setOnClickListener(this);
		userNameEditText.setOnFocusChangeListener(this);
		passwordEditText.setOnFocusChangeListener(this);
		repaeatPassEditText.setOnFocusChangeListener(this);
		emailEditText.setOnFocusChangeListener(this);
		return view;
	}

	// 注册跳转的监听
	public void setRegisterJumpListener(RegisterJumpListener jumpListener) {
		this.jumpListener = jumpListener;
	}

	//控件点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_button:// 注册帐号
			registerButton.requestFocus();
			registerButton.requestFocusFromTouch();
			if (isAllChecked()) {//检查信息是否完全
				register();//注册方法
			} else {
				if(getActivity() != null) {
					// 检测未通过
					CustomToast.makeText(getActivity(),
							R.string.register_check_failure, Toast.LENGTH_SHORT)
							.show();
				}
			}
			break;
		}
	}

	// 注册方法
	public void register() {
		// 拿到控件的值
		String name = userNameEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();
		String email = emailEditText.getText().toString().trim();
		// 请求网络
		HttpParams params = new HttpParams();
		// "user_name" 接口的参数key
		params.put("user_name", name);
		params.put("user_password", password);
		params.put("user_email", email);
		// 接口上的方法
		HttpRequest.post("register_user", params,
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

	// 聚焦改变的方法
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		// 拿到控件值
		case R.id.register_user_name:
			if (hasFocus == false) {
				// 检查输入的用户名
				checkNameInput(userNameEditText);
			}
			break;
		case R.id.register_user_password:
			if (hasFocus == false) {
				// 检查输入的密码
				checkPasswordInput(passwordEditText);
			}
			break;
		case R.id.register_user_password_repeat:
			if (hasFocus == false) {
				//检查重复密码
				checkRepeatPasswordInput();
			}
			break;
		case R.id.register_email:
			if (hasFocus == false) {
				//检测邮箱
				checkEmailInput(emailEditText);
			}
			break;
		}
	}

	// 消息发送 Looper.myLooper()消息循环
	Handler handler = new Handler(Looper.myLooper()) {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 用户名可用
			case USER_NAME_AVAILABLE:
				// 输入用户名消息可用
				checkNameTextView.setText(R.string.register_name_available);
				// 用户名可用后隐藏
				checkNameTextView.setVisibility(View.VISIBLE);
				break;
			// 用户名不可用
			case USER_NAME_UNAVAILABLE:
				checkNameTextView.setText(R.string.register_name_unavailable);
				checkNameTextView.setVisibility(View.VISIBLE);
				break;
			// 邮箱可用
			case USER_EMAIL_AVAILABLE:
				checkEmailTextView.setText(R.string.register_email_available);
				checkEmailTextView.setVisibility(View.VISIBLE);
				break;
			//邮箱不可用
			case USER_EMAIL_UNAVAILABLE:
				checkEmailTextView.setText(R.string.register_email_unavailable);
				checkEmailTextView.setVisibility(View.VISIBLE);
				break;
			// 注册后的解析
			case RECEIVED_USER_INFO:
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject.optInt("result", -1) == 0) {
					JSONObject object = jsonObject.optJSONObject("user");
					if (object != null) {
						/*UserBase.user_id = object.optInt("user_id", -1);
						UserBase.user_name = object.optString("user_name", "");
						UserBase.user_password = object.optString(
								"user_password", "");
						UserBase.user_sex = object.optInt("user_sex", -1);
						UserBase.user_birthday = object.optString(
								"user_birthday", "");
						UserBase.user_address = object.optString(
								"user_address", "");
						UserBase.user_phone = object
								.optString("user_phone", "");
						UserBase.user_email = object
								.optString("user_email", "");
						UserBase.user_email_verfied = object.optInt(
								"user_email_verfied", -1);
						UserBase.user_url = object.optString("user_url", "");
						UserBase.is_developer = object
								.optInt("is_developer", 0);
						UserBase.user_question1 = object.optString(
								"user_question1", "");
						UserBase.user_question2 = object.optString(
								"user_question2", "");
						UserBase.user_question3 = object.optString(
								"user_question3", "");
						UserBase.date = object.optString("date", "");
						UserBase.account_available = object.optString(
								"account_available", "");
						UserBase.user_money = object.optDouble("use_money", 0);*/
						
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
									R.string.register_success,
									CustomToast.LENGTH_SHORT).show();
						}
						// 跳界面
						if (jumpListener != null) {
							jumpListener.jumpToNext(0);
						}
					}
				} else {
					if(getActivity() != null) {
						// 注册失败
						CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(
								getActivity());
						builder.setTitle(R.string.register_failure);
						builder.setMessage(jsonObject.optString("msg", ""));
						builder.setPositiveButton(R.string.sure, null);
						builder.create().show();
					}
				}
				break;
			}
		}
	};

	// 密码的检查
	public void checkPasswordInput(EditText editText) {
		String value = editText.getText().toString().trim();
		if (value != null && value.length() >= 6 && value.length() <= 12) {
			//隐藏提示栏
			checkPasswordTextView.setVisibility(View.INVISIBLE);
		} else {
			checkPasswordTextView.setText(R.string.register_passowrd_length);
			//显示提示栏
			checkPasswordTextView.setVisibility(View.VISIBLE);
		}
	}

	// 重复密码的检查
	public void checkRepeatPasswordInput() {
		// 拿到密码的控件的值然后转换为字符串 然后去除前后空格
		String password = passwordEditText.getText().toString().trim();
		String repeatPassword = repaeatPassEditText.getText().toString().trim();
		// 两次密码比较
		if (password.equals(repeatPassword)) {
			//隐藏提示栏
			checkRepeatPassword.setVisibility(View.INVISIBLE);
		} else {
			checkRepeatPassword.setText(R.string.register_password_notsame);
			checkRepeatPassword.setVisibility(View.VISIBLE);
		}
	}

	// 检查用户名
	public void checkNameInput(EditText editText) {
		String value = editText.getText().toString().trim();
		if (value != null && value.length() >= 6 && value.length() <= 12) {
			// 请求网络检查
			HttpParams params = new HttpParams();
			// "user_name" 接口的
			params.put("user_name", value);
			// 请求网络的方法
			HttpRequest.post("check_username_available", params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject jsonObject) {
							int result = jsonObject.optInt("result", -1);
							if (result == 0) {
								handler.sendEmptyMessage(USER_NAME_AVAILABLE);
							} else {
								handler.sendEmptyMessage(USER_NAME_UNAVAILABLE);
							}
						}
					});
		} else {
			checkNameTextView.setText(R.string.register_name_length);
			//显示提示栏
			checkNameTextView.setVisibility(View.VISIBLE);
		}
	}

	// 检查邮箱是否正确
	public void checkEmailInput(EditText editText) {
		String value = editText.getText().toString().trim();
		if (value != null && !value.equals("")) {
			// 正则表达式检查邮箱的格式
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(value);
			boolean isMatched = matcher.matches();
			if (isMatched == true) {
				HttpParams httpParams = new HttpParams();
				httpParams.put("user_email", value);
				HttpRequest.post("check_useremail_available", httpParams,
						new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(JSONObject jsonObject) {
								int result = jsonObject.optInt("result", -1);
								if (result == 0) {
									handler.sendEmptyMessage(USER_EMAIL_AVAILABLE);
								} else {
									handler.sendEmptyMessage(USER_EMAIL_UNAVAILABLE);
								}
							}
						});
			} else {
				// 这不是一个邮箱地址
				checkEmailTextView.setText(R.string.register_email_not);
				checkEmailTextView.setVisibility(View.VISIBLE);
			}
		} else {
			// 这不是一个邮箱地址
			checkEmailTextView.setText(R.string.register_email_not);
			checkEmailTextView.setVisibility(View.VISIBLE);
		}
	}

	// 检查注册的内容是否完全
	public boolean isAllChecked() {
		boolean flg = true;
		if (userNameEditText.getText().toString() == null
				|| userNameEditText.getText().toString().equals("")
				|| userNameEditText.getText().length() < 6
				|| userNameEditText.getText().length() > 16) {
			flg = false;
		}
		if (passwordEditText.getText().toString() == null
				|| passwordEditText.getText().toString().equals("")
				|| passwordEditText.getText().length() < 6
				|| passwordEditText.getText().length() > 16) {
			flg = false;
		}
		if (repaeatPassEditText.getText().toString() == null
				|| repaeatPassEditText.getText().toString().equals("")
				|| repaeatPassEditText.getText().length() < 6
				|| repaeatPassEditText.getText().length() > 16) {
			flg = false;
		}
		if (emailEditText.getText().toString() == null
				|| emailEditText.getText().toString().equals("")) {
			flg = false;
		} else {
			String email = emailEditText.getText().toString().trim();
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			boolean isMatched = matcher.matches();
			if (isMatched == false) {
				flg = false;
				checkEmailTextView.setText(R.string.register_email_not);
				checkEmailTextView.setVisibility(View.VISIBLE);
			}
		}
		return flg;
	}

}
