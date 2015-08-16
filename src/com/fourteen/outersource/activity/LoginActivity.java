package com.fourteen.outersource.activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.adapter.AutoCompelteAdapter;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.SaveUserBean;
import com.fourteen.outersource.bean.UserBean;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.encrypt.Security;
import com.fourteen.outersource.network.bitmap.BitmapUtil;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.provider.OTUserProvider;
import com.fourteen.outersource.provider.UserColumn;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

/*
 * 用户登录 LoginActivity类
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
		HeaderClickListener, OnCheckedChangeListener, OnFocusChangeListener {

	HeaderView headerView;
	AutoCompleteTextView userEditText;
	EditText passEditText;
	CheckBox savePassword;
	TextView registerTextView;
	Button loginButton;
	Button exitButton;
	ImageView loginIcon;
	TextView forgetpasswprdTextView;

	AutoCompelteAdapter adapter;// 自动补全adapter
	UserBean userBean;

	ArrayList<SaveUserBean> savedUsers = new ArrayList<SaveUserBean>();

	public static final int RECEIVED_USER_INFO = 0X1234;
	public static final int RECEIVED_USER_INFO_BY_NAME = 0X1235;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_login_layout);
		initView();
	}

	// 实例化控件
	private void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.hiddenBack();
		headerView.setTitle(R.string.login_title);
		headerView.setHeaderClickListener(this);
		loginIcon = (ImageView) findViewById(R.id.login_icon);
		userEditText = (AutoCompleteTextView) findViewById(R.id.login_user_name);
		userEditText.setOnFocusChangeListener(this);
		passEditText = (EditText) findViewById(R.id.login_user_password);
		savePassword = (CheckBox) findViewById(R.id.login_save_account);
		registerTextView = (TextView) findViewById(R.id.login_register);
		loginButton = (Button) findViewById(R.id.login_button);
		exitButton = (Button) findViewById(R.id.login_exit);
		forgetpasswprdTextView = (TextView) findViewById(R.id.login_forget_password);
		forgetpasswprdTextView.setOnClickListener(this);
		savePassword.setOnCheckedChangeListener(this);
		registerTextView.setOnClickListener(this);
		loginButton.setOnClickListener(this);
		exitButton.setOnClickListener(this);
		initData();
	}

	public void initData() {
		savedUsers.clear();
		Cursor cursor = getContentResolver().query(OTUserProvider.OT_USER_URI,
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				SaveUserBean bean = new SaveUserBean();
				bean.user_name = Security.decrypt(cursor.getString(cursor
						.getColumnIndex(UserColumn.USER_NAME)), Security.KEY);
				bean.user_password = Security.decrypt(cursor.getString(cursor
						.getColumnIndex(UserColumn.USER_PASSWORD)),
						Security.KEY);
				savedUsers.add(bean);
			}
			cursor.close();
		}
		if (savedUsers.size() == 1) {
			userEditText.setText(savedUsers.get(0).user_name);
			passEditText.setText(savedUsers.get(0).user_password);
			getUserinfoByName();
		}
		adapter = new AutoCompelteAdapter(this, savedUsers);
		userEditText.setAdapter(adapter);
		userEditText.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object object = parent.getAdapter().getItem(position);
				if (object instanceof SaveUserBean) {
					getUserinfoByName();
					SaveUserBean bean = (SaveUserBean) object;
					passEditText.setText(bean.user_password);
				}
			}

		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		saveAccount(isChecked);
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	// 控件点击事件
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.login_register:// 注册用户
			// 跳转到注册用户界面：RegisterActivity
			intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.login_button:// 用户登录
			if (isFullCompleted()) { // 是否填写完成
				login();
			}
			break;
		case R.id.login_exit:// 退出登录
			this.finish();
			break;
		case R.id.login_forget_password:// 忘记密码
			// 跳转到忘记密码界面：ForgetPasswordActivity
			intent = new Intent(this, ForgetPasswordActivity.class);
			startActivity(intent);
			break;
		}
	}

	// 保存用户账户
	public void saveAccount(boolean isChecked) {
		String name = userEditText.getText().toString();
		String password = passEditText.getText().toString();
		if (name != null && !name.equals("")) {
			name = Security.encrypt(name, Security.KEY);
			password = Security.encrypt(password, Security.KEY);
			ContentValues contentValues = new ContentValues();
			contentValues.put("user_name", name);
			contentValues.put("user_password", password);
			if (isChecked) { // 保存
				Cursor cursor = getContentResolver().query(
						OTUserProvider.OT_USER_URI, null, " `user_name` = ? ",
						new String[] { name }, null);
				if (cursor != null && cursor.getCount() > 0) { // 用户账户已存在，更新
					cursor.close();
					getContentResolver().update(OTUserProvider.OT_USER_URI,
							contentValues, " `user_name` = ? ",
							new String[] { name });
				} else { // 用户账户不存在，则加入
					getContentResolver().insert(OTUserProvider.OT_USER_URI,
							contentValues);
				}
			} else { // 删除
				getContentResolver().delete(OTUserProvider.OT_USER_URI,
						" `user_name` = ? ", new String[] { name });
			}
		}
	}

	// 检查用户名和密码是否填写完全
	public boolean isFullCompleted() {
		boolean flg = false;
		String message = null;
		final EditText view;
		if (userEditText.getText().toString() == null
				|| userEditText.getText().toString().equals("")) {
			message = getString(R.string.login_input_username);
			view = userEditText;
		} else if (passEditText.getText().toString() == null
				|| passEditText.getText().toString().equals("")) {
			message = getString(R.string.login_input_userpassword);
			view = passEditText;
		} else {
			flg = true;
			view = userEditText;
		}
		if (flg == false) {
			showErrorDialog(view, message);
		}
		return flg;
	}

	// 显示错误信息的对话框
	public void showErrorDialog(final View view, String message) {
		AlertDialog.Builder builder = new CustomAlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setTitle(R.string.login_notify);
		builder.setPositiveButton(R.string.sure,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						view.setFocusable(true);
						view.setFocusableInTouchMode(true);
						view.requestFocus();
					}
				});
		builder.create().show();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RECEIVED_USER_INFO:// 接收用户信息
				JSONObject jsonObject = (JSONObject) msg.obj;
				userBean = new UserBean();
				if (jsonObject.optInt("result", -1) == 0) {
					JSONObject object = jsonObject.optJSONObject("user");
					if (object != null) {
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

						/*
						 * UserBase.user_id = object.optInt("user_id", -1);
						 * UserBase.user_name = object.optString("user_name",
						 * ""); UserBase.user_password = object.optString(
						 * "user_password", ""); UserBase.user_sex =
						 * object.optInt("user_sex", -1); UserBase.user_birthday
						 * = object.optString( "user_birthday", "");
						 * UserBase.user_address = object.optString(
						 * "user_address", ""); UserBase.user_phone = object
						 * .optString("user_phone", ""); UserBase.user_email =
						 * object .optString("user_email", "");
						 * UserBase.user_email_verfied = object.optInt(
						 * "user_email_verfied", -1); UserBase.user_url =
						 * object.optString("user_url", "");
						 * UserBase.is_developer = object
						 * .optInt("is_developer", 0); UserBase.user_question1 =
						 * object.optString( "user_question1", "");
						 * UserBase.user_question2 = object.optString(
						 * "user_question2", ""); UserBase.user_question3 =
						 * object.optString( "user_question3", "");
						 * UserBase.date = object.optString("date", "");
						 * UserBase.account_available = object.optInt(
						 * "account_available", 0); UserBase.user_money =
						 * object.optDouble("use_money", 0);
						 */
						UserBase.setUserBase(LoginActivity.this, userBean);
					}
					// 跳转到主界面:HomeActivity
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, HomeActivity.class);
					// bundle传值到HomeActivity
					Bundle bundle = new Bundle();
					bundle.putSerializable("userBean", userBean);
					intent.putExtras(bundle);
					startActivity(intent);
					LoginActivity.this.finish();
				} else {
					String errMsg = jsonObject.optString("msg", "");
					CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(
							LoginActivity.this);
					builder.setTitle(R.string.login_falure);
					builder.setMessage(errMsg);
					builder.setNegativeButton(R.string.unlock_account,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent();
									// 跳转到用户解锁界面
									intent.setClass(getApplication(),
											UserLoginUnlockActivity.class);
									startActivity(intent);
								}
							});
					builder.setPositiveButton(R.string.back,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
					builder.create().show();
				}
				break;
			case RECEIVED_USER_INFO_BY_NAME:// 通过用户名获取用户信息
				JSONObject jsonObject1 = (JSONObject) msg.obj;
				if (jsonObject1.optInt("result", -1) == 0) {
					JSONObject object = jsonObject1.optJSONObject("user");
					if (object != null) {
						String url = object.optString("user_url", "");
						if (url != null && !url.equals("")
								&& !url.equals("null")) {
							BitmapUtil.getInstance().download(url, loginIcon);
						}
					}
				}
				break;
			}
		};
	};

	/**
	 * 登录，请求网络
	 */
	public void login() {
		String name = userEditText.getText().toString().trim();
		String password = passEditText.getText().toString().trim();
		HttpParams params = new HttpParams();
		params.put("user_name", name);
		params.put("user_password", password);
		if (isNetworkAvailable()) {
			HttpRequest.post("user_login", params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject jsonObject) {
							System.out.println("登录返回:" + jsonObject);
							Message message = new Message();
							message.what = RECEIVED_USER_INFO;
							message.obj = jsonObject;
							handler.sendMessage(message);
						}

						@Override
						public void onFailure(String result, int statusCode,
								String errorResponse) {
						}
					});
		} else {
			CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(
					this);
			builder.setTitle(R.string.login_notify);
			builder.setMessage(R.string.network_unreachable);
			builder.setPositiveButton(R.string.sure, null);
			builder.create().show();
		}
	}

	@Override
	public void onHeaderClick(View v, int chich) {

	}

	// 改变聚焦
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.login_user_name:// 用户名
			if (hasFocus == false) {
				getUserinfoByName();
			}
			break;
		}
	}

	// 通过用户名获取用户信息
	private void getUserinfoByName() {
		HttpParams params = new HttpParams();
		params.put("user_name", userEditText.getText().toString().trim());
		HttpRequest.post("get_user_info_by_name", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						Message message = new Message();
						message.what = RECEIVED_USER_INFO_BY_NAME;
						message.obj = jsonObject;
						handler.sendMessage(message);
					}
				});
	}

}
