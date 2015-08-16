package com.fourteen.outersource.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.fragment.RegisterJumpListener;
import com.fourteen.outersource.fragment.RegisterStepTwo;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class ModifyInformationActivity extends BaseActivity implements
		HeaderClickListener, OnClickListener, RegisterJumpListener {

	HeaderView headerView;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_ps_modify_information);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.modify_Information);
		headerView.setHeaderClickListener(this);

		RegisterStepTwo registerStepTwo = new RegisterStepTwo();
		registerStepTwo.setRegisterJumpListener(this);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.content_information, registerStepTwo).commit();

	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void jumpToNext(int currentItem) {

	}

	@Override
	public void onClick(View v) {

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
