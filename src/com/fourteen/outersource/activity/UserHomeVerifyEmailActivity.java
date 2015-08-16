package com.fourteen.outersource.activity;
/**
 * 邮箱验证:UserHomeVerifyEmailActivity类
 */
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.UserBean;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.utils.Logg;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class UserHomeVerifyEmailActivity extends BaseActivity implements HeaderClickListener, OnClickListener{
	HeaderView headerView;
	TextView emailTitle;//邮箱
	Button verifyButton;//验证
	private static final int RECEIVED_VERIFY_MSG = 0X111;
	private static final int RECEIVED_VERIFY_FAIL = 0X112;
	private static final int GET_USER_INFO = 0X123;
	
	String[][] EMAIL_TYPE = {
			{"qq.com", "http://mail.qq.com"},
			{"163.com", "http://mail.163.com"},
			{"126.com", "http://mail.126.com"},
			{"gmail.com", "http://mail.google.com"},
			{"yahoo.com", "http://mail.yahoo.com"}
	};
	
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_userhome_verify_email);
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.verify_email_header);
		headerView.setHeaderClickListener(this);
		emailTitle = (TextView) findViewById(R.id.email_title);
		verifyButton = (Button) findViewById(R.id.verify_button);
		//邮箱验证图标信息
		emailTitle.setText(getResources().getString(R.string.verify_email_title)+ " : " + UserBase.getUserEmail(UserHomeVerifyEmailActivity.this));
		if(UserBase.getUserEmailVerified(UserHomeVerifyEmailActivity.this)== 0) {
			Drawable nav_right=getResources().getDrawable(R.drawable.ot_email_no_verified);
			nav_right.setBounds(0, 0, nav_right.getMinimumWidth(), nav_right.getMinimumHeight());
			emailTitle.setCompoundDrawables(null, null, nav_right, null);
		} else {
			Drawable nav_right=getResources().getDrawable(R.drawable.ot_email_verified);
			nav_right.setBounds(0, 0, nav_right.getMinimumWidth(), nav_right.getMinimumHeight());
			emailTitle.setCompoundDrawables(null, null, nav_right, null);
		}
		verifyButton.setOnClickListener(this);
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch(which){
		case HeaderView.BACK_POSITION:
			this.finish();
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		//重新获取个人资料
		getUserInfo();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.verify_button:
			verifyEmail();//验证邮箱
			break;
		}
	}
	
	Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
			case RECEIVED_VERIFY_MSG://接收验证消息
				JSONObject jsonObject = (JSONObject) msg.obj;
				if(jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					showSuccessDialog();
				}
				break;
			case RECEIVED_VERIFY_FAIL:
				showFailDialog();//显示失败对话框
				break;
			case GET_USER_INFO://获得用户信息
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
						UserBase.setUserBase(UserHomeVerifyEmailActivity.this, userBean);
					}
				}
				if(UserBase.getUserEmailVerified(UserHomeVerifyEmailActivity.this) == 1){
					Drawable nav_right=getResources().getDrawable(R.drawable.ot_email_verified);
					nav_right.setBounds(0, 0, nav_right.getMinimumWidth(), nav_right.getMinimumHeight());
					emailTitle.setCompoundDrawables(null, null, nav_right, null);
					verifyButton.setEnabled(false);
					verifyButton.setClickable(false);
				}
				break;
			}
			return true;
		}
	});
	
	/**
	 * 发送失败对话框
	 */
	public void showFailDialog(){
		CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(this);
		builder.setTitle(R.string.send_email_fail);
		builder.setMessage(R.string.send_email_fail_info);
		builder.setNegativeButton(R.string.re_send_email, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				verifyEmail();
			}
		});
		builder.setPositiveButton(R.string.check_email_cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
	}
	
	/**
	 * 验证发送成功的对话框
	 */
	public void showSuccessDialog(){
		CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(this);
		builder.setTitle(R.string.send_email_success);
		builder.setMessage(R.string.send_email_title);
		builder.setNegativeButton(R.string.check_email_now, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				openEmail();
			}
		});
		builder.setPositiveButton(R.string.check_email_cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
	}
	
	/**
	 * 验证邮箱
	 */
	public void verifyEmail(){
		HttpParams params = new HttpParams();
		params.put("user_id", UserBase.getUserId(UserHomeVerifyEmailActivity.this));
		params.put("user_email", UserBase.getUserEmail(UserHomeVerifyEmailActivity.this));
		HttpRequest.post("verify_email", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.what = RECEIVED_VERIFY_MSG;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
			
			@Override
			public void onFailure(String result, int statusCode,String errorResponse) {
				handler.sendEmptyMessage(RECEIVED_VERIFY_FAIL);
			}
		});
	}
	
	/**
	 * 打开邮件地址
	 */
	public void openEmail(){
		String email = UserBase.getUserEmail(UserHomeVerifyEmailActivity.this);
		String email_host = email.substring(email.indexOf('@') + 1, email.length());
		Logg.out("email_host=" + email_host);
		String url = "";
		for(int i=0; i< EMAIL_TYPE.length; i++) {
			if(email_host.equals(EMAIL_TYPE[i][0])){
				url = EMAIL_TYPE[i][1];
				break;
			}
		}
		if(!url.equals("")) {
			Uri uri = Uri.parse(url);  
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}
	
	/**
	 * 获得用户信息
	 */
	public void getUserInfo(){
		HttpParams params = new HttpParams();
		params.put("user_name", UserBase.getUserName(UserHomeVerifyEmailActivity.this));
		HttpRequest.post("get_user_info_by_name", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.what = GET_USER_INFO;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
		});
	}
}
