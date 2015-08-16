package com.fourteen.outersource.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.fragment.RegisterJumpListener;
import com.fourteen.outersource.fragment.RegisterStepFour;
import com.fourteen.outersource.fragment.RegisterStepOne;
import com.fourteen.outersource.fragment.RegisterStepThree;
import com.fourteen.outersource.fragment.RegisterStepTwo;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;
/**
 * 注册界面:RegisterActivity类
 */
public class RegisterActivity extends BaseActivity implements HeaderClickListener, RegisterJumpListener{
	
	HeaderView headerView;
	
	ArrayList<Fragment> list = new ArrayList<Fragment>();
	FrameLayout frameLayout;
	
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_register_layout);
		initView();
	}
	
	//实例化控件
	public void initView(){
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setHeaderClickListener(this);
		frameLayout = (FrameLayout) findViewById(R.id.register_content);
		headerView.setTitle(R.string.register_title);
		RegisterStepOne registerStepOne = new RegisterStepOne();
		//设置fragment的跳转监听事件
		registerStepOne.setRegisterJumpListener(this);
		//开始并提交事务
		getSupportFragmentManager().beginTransaction().add(R.id.register_content, registerStepOne).commit();
	}
	
	//网络状态变化
	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	//标题栏点击事件
	@Override
	public void onHeaderClick(View v, int chich) {
		switch(chich){
		case HeaderView.BACK_POSITION://返回
			this.finish();
			break;
		case HeaderView.OPT_POSITION://操作位置
			break;
		case HeaderView.TITLE_POSITION://标题位置
			break;
		}
	}

	//页面跳转
	@Override
	public void jumpToNext(int currentItem) {
		switch(currentItem) {
		case 0://个人信息界面
			RegisterStepTwo registerStepTwo = new RegisterStepTwo();
			registerStepTwo.setRegisterJumpListener(this);
			getSupportFragmentManager().beginTransaction().replace(R.id.register_content, registerStepTwo).commit();
			break;
		case 1://密保问题界面
			RegisterStepThree registerStepThree = new RegisterStepThree();
			registerStepThree.setRegisterJumpListener(this);
			getSupportFragmentManager().beginTransaction().replace(R.id.register_content, registerStepThree).commit();
			break;
		case 2://图片上传界面
			RegisterStepFour registerStepFour = new RegisterStepFour();
			registerStepFour.setRegisterJumpListener(this);
			getSupportFragmentManager().beginTransaction().replace(R.id.register_content, registerStepFour).commit();
			break;
		case 3:
			//跳转到主界面,结束当前界面
			Intent intent = new Intent();
			intent.setClass(this, HomeActivity.class);
			startActivity(intent);
			this.finish();
			break;
		}
	}

}
