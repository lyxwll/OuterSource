package com.fourteen.outersource.activity;
/**
 * 个人中心:UserHomeActivity类
 */
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.network.bitmap.BitmapUtil;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class UserHomeActivity extends BaseActivity implements
		HeaderClickListener, OnClickListener,
		SwipeRefreshLayout.OnRefreshListener {

	HeaderView headerView;
	SwipeRefreshLayout refreshLayout;
	ImageView headerIcon;//头像
	TextView myorderTextView;//我的订单
	TextView myaccountTextView;//我的账户
	TextView modifyInformation;//修改个人信息
	TextView modifyPassword;//修改密码
	TextView modifyQuestion;//修改密保问题
	TextView feedbackTextView;//意见反馈
	TextView aboutTextView;//关于
	TextView recommendTextView;//推荐
	TextView exitTextView;//退出应用
	TextView emailVerifyTextView;//邮箱验证
	TextView unsubscribeTextView;//注销帐号

	public static final int REFRESH_MSG = 0X1111;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_user_home_layout);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(UserBase.getUserName(UserHomeActivity.this));
		headerView.setHeaderClickListener(this);
		headerIcon = (ImageView) findViewById(R.id.header_icon);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		refreshLayout.setColorScheme(R.color.common_blue,
				R.color.warning_background, R.color.common_border,
				R.color.common_grey);
		refreshLayout.setOnRefreshListener(this);

		headerIcon = (ImageView) findViewById(R.id.header_icon);
		headerIcon.setOnClickListener(this);

		myorderTextView = (TextView) findViewById(R.id.user_home_item1);
		myorderTextView.setOnClickListener(this);

		myaccountTextView = (TextView) findViewById(R.id.user_home_item2);
		myaccountTextView.setOnClickListener(this);

		modifyInformation = (TextView) findViewById(R.id.user_home_item3);
		modifyInformation.setOnClickListener(this);

		modifyPassword = (TextView) findViewById(R.id.user_home_item4);
		modifyPassword.setOnClickListener(this);

		modifyQuestion = (TextView) findViewById(R.id.user_home_item5);
		modifyQuestion.setOnClickListener(this);

		feedbackTextView = (TextView) findViewById(R.id.user_home_item6);
		feedbackTextView.setOnClickListener(this);

		aboutTextView = (TextView) findViewById(R.id.user_home_item7);
		aboutTextView.setOnClickListener(this);

		recommendTextView = (TextView) findViewById(R.id.user_home_item8);
		recommendTextView.setOnClickListener(this);

		exitTextView = (TextView) findViewById(R.id.user_home_item9);
		exitTextView.setOnClickListener(this);

		emailVerifyTextView = (TextView) findViewById(R.id.user_home_item10);
		//邮箱验证图片信息
		if (UserBase.getUserEmailVerified(UserHomeActivity.this) == 0) {
			emailVerifyTextView.setOnClickListener(this);
			Drawable nav_right = getResources().getDrawable(
					R.drawable.ot_email_no_verified);
			nav_right.setBounds(0, 0, nav_right.getMinimumWidth(),
					nav_right.getMinimumHeight());
			emailVerifyTextView.setCompoundDrawables(null, null, nav_right,
					null);
		} else {
			Drawable nav_right = getResources().getDrawable(
					R.drawable.ot_email_verified);
			nav_right.setBounds(0, 0, nav_right.getMinimumWidth(),
					nav_right.getMinimumHeight());
			emailVerifyTextView.setCompoundDrawables(null, null, nav_right,
					null);
		}
		unsubscribeTextView = (TextView) findViewById(R.id.user_home_item11);
		unsubscribeTextView.setOnClickListener(this);
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.header_icon:// 修改头像
			intent = new Intent();
			intent.setClass(this, ModifyHeadImageActivity.class);
			break;
		case R.id.user_home_item1:// 我的订单
			intent = new Intent();
			intent.setClass(this, MyTaskActivity.class);
			break;
		case R.id.user_home_item2:// 我的账户
			break;
		case R.id.user_home_item3: // 修改个人资料
			intent = new Intent();
			intent.setClass(this, UserHomeModifyInfoActivity.class);
			break;
		case R.id.user_home_item4:// 修改密码
			intent = new Intent();
			intent.setClass(this, ModifyPasswordActivity.class);
			break;
		case R.id.user_home_item5:// 修改密保问题
			intent = new Intent();
			intent.setClass(this, ModifyQuestionActivity.class);
			break;
		case R.id.user_home_item6:// 意见反馈
			intent = new Intent();
			intent.setClass(this, UserHomeFeedbackActivity.class);
			break;
		case R.id.user_home_item7:// 关于OTS
			intent = new Intent();
			intent.setClass(this, UserHomeAboutOtsActivity.class);
			break;
		case R.id.user_home_item8:// 推荐
			intent = new Intent();
			intent.setClass(this, UserHomeRecommendActivity.class);
			break;
		case R.id.user_home_item9:// 退出应用
			Intent intent1 = new Intent();
			intent1.setAction(BaseActivity.EXIT_ACTION);
			sendBroadcast(intent1);
			break;
		case R.id.user_home_item10: // 邮箱验证
			intent = new Intent(this, UserHomeVerifyEmailActivity.class);
			break;
		case R.id.user_home_item11:// 账户注销
			intent = new Intent();
			intent.setClass(this, UserHomeUnsubscribeActivity.class);
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (UserBase.getUserEmailVerified(UserHomeActivity.this) == 0) {
			// emailVerifyTextView.setOnClickListener(this);
			Drawable nav_right = getResources().getDrawable(
					R.drawable.ot_email_no_verified);
			nav_right.setBounds(0, 0, nav_right.getMinimumWidth(),
					nav_right.getMinimumHeight());
			emailVerifyTextView.setCompoundDrawables(null, null, nav_right,
					null);
		} else {
			emailVerifyTextView.setClickable(false);
			Drawable nav_right = getResources().getDrawable(
					R.drawable.ot_email_verified);
			nav_right.setBounds(0, 0, nav_right.getMinimumWidth(),
					nav_right.getMinimumHeight());
			emailVerifyTextView.setCompoundDrawables(null, null, nav_right,
					null);
		}
		if (UserBase.getUserUrl(UserHomeActivity.this) != null
				&& !UserBase.getUserUrl(UserHomeActivity.this).equals("")
				&& !UserBase.getUserUrl(UserHomeActivity.this).equals("null")) {
			BitmapUtil.getInstance(R.drawable.ic_launcher,
					R.drawable.ic_launcher).download(
					UserBase.getUserUrl(UserHomeActivity.this), headerIcon);
		}
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch (which) {
		case HeaderView.BACK_POSITION:
			this.finish();
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_MSG:
				refreshLayout.setRefreshing(false);
				break;
			}
		};
	};

	//刷新
	@Override
	public void onRefresh() {
		if (UserBase.getUserUrl(UserHomeActivity.this) != null
				&& !UserBase.getUserUrl(UserHomeActivity.this).equals("")
				&& !UserBase.getUserUrl(UserHomeActivity.this).equals("null")) {
			BitmapUtil.getInstance(R.drawable.ic_launcher,
					R.drawable.ic_launcher).download(
					UserBase.getUserUrl(UserHomeActivity.this), headerIcon);
		}
		handler.sendEmptyMessageDelayed(REFRESH_MSG, 2000);
	}

}
