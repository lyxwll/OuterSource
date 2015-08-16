package com.fourteen.outersource.activity;
/**
 * 解锁帐号:UserLoginUnlockActivity类
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
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.toast.CustomToast;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class UserLoginUnlockActivity extends BaseActivity implements
		HeaderClickListener, OnClickListener {

	public static final int UNLOCK_SUCCESS = 0x1234;

	HeaderView headerView;
	EditText userNameEditText;//用户名
	EditText passwordEditText;//密码
	Button unLockButton;//解锁按钮

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_user_login_unlock);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.unlock_account);
		headerView.setHeaderClickListener(this);

		userNameEditText = (EditText) findViewById(R.id.name_unlock);
		passwordEditText = (EditText) findViewById(R.id.password_unlock);

		unLockButton = (Button) findViewById(R.id.unlock_confirm);
		unLockButton.setOnClickListener(this);
	}

	Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case UNLOCK_SUCCESS://解锁成功
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					CustomToast.makeText(UserLoginUnlockActivity.this,
							R.string.unlock_account_success,
							CustomToast.LENGTH_SHORT).show();
					UserLoginUnlockActivity.this.finish();
				} 
				break;
			}
			return true;
		}
	});

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.unlock_confirm://确认解锁
			unlockAccount();
			break;
		}
	}

	/**
	 * 解锁帐号
	 */
	public void unlockAccount() {
		String userName = userNameEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();
		HttpParams params = new HttpParams();
		params.put("user_name", userName);
		params.put("user_password", password);
		params.put("is_deactivation", 1);
		HttpRequest.post("user_deactivation", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						Message message = new Message();
						message.obj = jsonObject;
						message.what = UNLOCK_SUCCESS;
						handler.sendMessage(message);
					}
				});
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
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
