package com.fourteen.outersource.activity;
/**
 * 修改密码:ModifyPasswordActivity类
 */
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.dialog.CustomAlertDialog.Builder;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.toast.CustomToast;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class ModifyPasswordActivity extends BaseActivity implements
		HeaderClickListener, OnClickListener{

	// 消息序列号
	public static final int MODIFY_SUCCESS = 0X1234;

	HeaderView headerView;
	EditText passwordEditText;//旧密码
	EditText newpwdEditText;//新密码
	EditText reapeatpwdEditText;//重复新密码
//	TextView checkPassword;
//	TextView checkNewPassword;
//	TextView checkRepeatNewPassword;
	Button completeButton;//完成修改按钮

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_ps_modify_password);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(UserBase.getUserName(this));
		headerView.setHeaderClickListener(this);

		passwordEditText = (EditText) findViewById(R.id.password_edit);
		//checkPassword = (TextView) findViewById(R.id.check_user_old_pwd);

		newpwdEditText = (EditText) findViewById(R.id.new_password_edit);
		//checkNewPassword = (TextView) findViewById(R.id.check_user_new_pwd);

		reapeatpwdEditText = (EditText) findViewById(R.id.new_password_edit2);
		//checkRepeatNewPassword = (TextView) findViewById(R.id.check_user_repeat_pwd);

		completeButton = (Button) findViewById(R.id.modify_complete);
		completeButton.setOnClickListener(this);
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MODIFY_SUCCESS://密码修改成功
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject.optInt("result", -1) == 0) {
						UserBase.setUserPassword(ModifyPasswordActivity.this, jsonObject.optString("user_password", ""));
						CustomToast.makeText(ModifyPasswordActivity.this,
								R.string.modify_password_success,
								CustomToast.LENGTH_SHORT).show();
				} else {
					// 密码修改失败
					CustomAlertDialog.Builder builder = new Builder(
							ModifyPasswordActivity.this);
					builder.setTitle(R.string.modify_password_failure);
					builder.setMessage(jsonObject.optString("msg", ""));
					builder.setPositiveButton(R.string.sure, null);
					builder.create().show();
				}
				break;
			}
			return true;
		}
	});

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.modify_complete://密码修改完成
			if (isAllChecked()) {
				completeModify();
				
			} else {// 修改不成功
				CustomToast.makeText(this, R.string.modify_password_notify,
						CustomToast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	// 修改密码 方法
	public void completeModify() {
		String password = passwordEditText.getText().toString().trim();
		String newPassword = newpwdEditText.getText().toString().trim();
		HttpParams params = new HttpParams();
		params.put("user_name", UserBase.getUserName(ModifyPasswordActivity.this));
		params.put("user_password", password);
		params.put("user_new_password", newPassword);
		HttpRequest.post("change_user_password", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						Message message = new Message();
						message.what = MODIFY_SUCCESS;
						message.obj = jsonObject;
						handler.sendMessage(message);
					}
				});
	}


	// 检查内容是否完全
	public boolean isAllChecked() {
		boolean flg = true;
		if (passwordEditText.getText().toString() == null
				|| passwordEditText.getText().toString().equals("")
				|| passwordEditText.getText().length() < 6
				|| passwordEditText.getText().length() > 12) {
			flg = false;
		}
		if (newpwdEditText.getText().toString() == null
				|| newpwdEditText.getText().toString().equals("")
				|| newpwdEditText.getText().length() < 6
				|| newpwdEditText.getText().length() > 12) {
			flg = false;
		}
		if (reapeatpwdEditText.getText().toString() == null
				|| reapeatpwdEditText.getText().toString().equals("")
				|| reapeatpwdEditText.getText().length() < 6
				|| reapeatpwdEditText.getText().length() > 12) {
			flg = false;
		}
		if(!newpwdEditText.getText().toString().equals(reapeatpwdEditText.getText().toString())) {
			flg = false;
		}
		return flg;
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch (which) {
		case HeaderView.BACK_POSITION:
			this.finish();
			break;
		}
	}

}
