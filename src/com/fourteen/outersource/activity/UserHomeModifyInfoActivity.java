package com.fourteen.outersource.activity;
/**
 * 修改个人信息
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.fragment.RegisterStepTwo;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class UserHomeModifyInfoActivity extends BaseActivity implements HeaderClickListener{
	HeaderView headerView;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_ps_modify_question);
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.modify_user_info_title);
		headerView.setHeaderClickListener(this);
		Fragment fragment = new RegisterStepTwo();
		getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).commit();
	}
	
	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch(which) {
		case HeaderView.BACK_POSITION:
			this.finish();
			break;
		}
	}

}
