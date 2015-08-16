package com.fourteen.outersource.activity;
/**
 * 修改密保问题:ModifyQuestionActivity类
 */
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.fragment.RegisterJumpListener;
import com.fourteen.outersource.fragment.RegisterStepThree;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class ModifyQuestionActivity extends BaseActivity implements HeaderClickListener, OnClickListener, OnFocusChangeListener, RegisterJumpListener{
	HeaderView headerView;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_ps_modify_question);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(UserBase.getUserName(ModifyQuestionActivity.this));
		headerView.setHeaderClickListener(this);
		RegisterStepThree registerStepThree = new RegisterStepThree();
		registerStepThree.setRegisterJumpListener(this);
		getSupportFragmentManager().beginTransaction().add(R.id.content, registerStepThree).commit();
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

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
	public void jumpToNext(int currentItem) {
		// TODO Auto-generated method stub
		
	}

}
