package com.fourteen.outersource.activity;
/**
 * 推荐应用:UserHomeRecommendActivity类
 */
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class UserHomeRecommendActivity extends BaseActivity implements HeaderClickListener, OnClickListener {
	HeaderView headerView;
	TextView WebsiteTextView2;//网址
	TextView WebsiteTextView4;
	TextView WebsiteTextView6;

	@Override
	protected void onCreate(Bundle bundle) {
		setContentView(R.layout.ot_ps_recommend);
		super.onCreate(bundle);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.recommend);
		headerView.setHeaderClickListener(this);

		WebsiteTextView2 = (TextView) findViewById(R.id.user_home_item2);
		WebsiteTextView2.setOnClickListener(this);
		WebsiteTextView4 = (TextView) findViewById(R.id.user_home_item4);
		WebsiteTextView4.setOnClickListener(this);
		WebsiteTextView6 = (TextView) findViewById(R.id.user_home_item6);
		WebsiteTextView6.setOnClickListener(this);
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
		case R.id.user_home_item2:
			Intent it = new Intent( Intent.ACTION_VIEW );
			it.setData( Uri.parse( "http://www.csdn.net/") ); 
			it = Intent.createChooser( it, null );
			startActivity( it );
			break;
		case R.id.user_home_item4:
			Intent it1 = new Intent( Intent.ACTION_VIEW );
			it1.setData( Uri.parse( "http://www.baidu.com/") ); 
			it1 = Intent.createChooser( it1, null );
			startActivity( it1 );
			break;
		case R.id.user_home_item6:
			Intent it2 = new Intent( Intent.ACTION_VIEW );
			it2.setData( Uri.parse( "http://www.baidu.com/") ); 
			it2 = Intent.createChooser( it2, null );
			startActivity( it2 );
		    break;
		}
	}
}
