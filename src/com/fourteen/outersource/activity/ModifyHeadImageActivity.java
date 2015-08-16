package com.fourteen.outersource.activity;

/**
 * 修改头像:ModifyHeadImageActivity类
 */
import android.os.Bundle;
import android.view.View;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.fragment.RegisterJumpListener;
import com.fourteen.outersource.fragment.RegisterStepFour;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class ModifyHeadImageActivity extends BaseActivity implements
		HeaderClickListener, RegisterJumpListener {

	HeaderView headerView;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_ps_modify_headimage);

		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.modify_headimage);
		headerView.setHeaderClickListener(this);

		RegisterStepFour registerStepFour = new RegisterStepFour();
		registerStepFour.setRegisterJumpListener(this);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.content_headimage, registerStepFour).commit();
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void jumpToNext(int currentItem) {

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
