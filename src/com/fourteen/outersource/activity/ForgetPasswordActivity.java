package com.fourteen.outersource.activity;
/**
 * 忘记密码:ForgetPasswordActivity类
 */
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.fragment.FindPasswordFragmentOne;
import com.fourteen.outersource.fragment.FindPasswordFragmentThree;
import com.fourteen.outersource.fragment.FindPasswordFragmentTwo;
import com.fourteen.outersource.fragment.FindPasswordNextListener;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class ForgetPasswordActivity extends BaseActivity implements HeaderClickListener, OnClickListener, FindPasswordNextListener{


	HeaderView headerView;
	String userName;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_ps_find_password);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.find_password_by_username);
		headerView.setHeaderClickListener(this);
		FindPasswordFragmentOne fragmentOne = new FindPasswordFragmentOne();
		fragmentOne.setNextListener(this);
		getSupportFragmentManager().beginTransaction().replace(R.id.find_content, fragmentOne).commit();
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
		case R.id.find_password_next:
			break;
		}
	}

	Handler handler = new Handler(Looper.myLooper()) {
		public void handleMessage(Message msg) {
			
		};
	};

	@Override
	public void onClickNextButton(int step, String[] args) {
		switch(step) {
		case 0://修改密保问题
			userName = args[0];
			FindPasswordFragmentTwo fragmentTwo = new FindPasswordFragmentTwo();
			Bundle bundle = new Bundle();
			bundle.putString("userName", userName);
			fragmentTwo.setArguments(bundle);
			fragmentTwo.setNextListener(this);
			getSupportFragmentManager().beginTransaction().replace(R.id.find_content, fragmentTwo).commit();
			break;
		case 1://修改密码
			FindPasswordFragmentThree fragmentThree = new FindPasswordFragmentThree();
			Bundle bundle2 = new Bundle();
			if(args != null && args.length ==3) {
				bundle2.putString("userName", userName);
				bundle2.putString("answer1", args[0]);
				bundle2.putString("answer2", args[1]);
				bundle2.putString("answer3", args[2]);
			}
			fragmentThree.setArguments(bundle2);
			fragmentThree.setNextListener(this);
			getSupportFragmentManager().beginTransaction().replace(R.id.find_content, fragmentThree).commit();
			break;
		case 2:
			this.finish();
			break;
		}
	}
}
