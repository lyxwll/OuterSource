package com.fourteen.outersource.activity;
/**
 * 注销帐号:UserHomeUnsubscribeActivity类
 */
import org.json.JSONObject;

import android.content.Intent;
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

public class UserHomeUnsubscribeActivity extends BaseActivity implements
		HeaderClickListener, OnClickListener {
	public static final int UNSUBSCRIBE_SUCCESS = 0X1234;
	HeaderView headerView;
	Button unsubscribeButton;//注销帐号按钮
	EditText passwordUnsubscribe;//输入密码

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_ps_unsubscribe);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(UserBase.getUserName(this));
		headerView.setHeaderClickListener(this);

		passwordUnsubscribe = (EditText) findViewById(R.id.password_unsubscribe);

		unsubscribeButton = (Button) findViewById(R.id.unsubscribe_confirm);
		unsubscribeButton.setOnClickListener(this);

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.unsubscribe_confirm://确认注销
			Unsubscribe();//注销帐号
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UNSUBSCRIBE_SUCCESS:
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					CustomToast.makeText(UserHomeUnsubscribeActivity.this,
							R.string.unsubscribe_success,
							CustomToast.LENGTH_SHORT).show();
					// 退出应用的广播
					Intent intent = new Intent();
					intent.setAction(BaseActivity.EXIT_ACTION);
					sendBroadcast(intent);
				} else {
					// 注销失败
					CustomAlertDialog.Builder builder = new Builder(
							UserHomeUnsubscribeActivity.this);
					builder.setTitle(R.string.commit_failure);
					builder.setMessage(jsonObject.optString("msg", ""));
					builder.setPositiveButton(R.string.sure, null);
					builder.create().show();
				}
				break;
			}
		};
	};

	/**
	 * 注销帐号
	 */
	public void Unsubscribe() {
		String password = passwordUnsubscribe.getText().toString().trim();
		HttpParams params = new HttpParams();
		params.put("user_name", UserBase.getUserName(this));
		params.put("user_password", password);
		params.put("is_deactivation", 0);
		HttpRequest.post("user_deactivation", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						Message message = new Message();
						message.what = UNSUBSCRIBE_SUCCESS;
						message.obj = jsonObject;
						handler.sendMessage(message);
					}
				});
	}
}
