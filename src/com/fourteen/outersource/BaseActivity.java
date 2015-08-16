package com.fourteen.outersource;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import com.fourteen.outersource.utils.Logg;
import com.fourteen.outersource.utils.NetworkStatus;

/**
 * 所有activity的基类，在这个类里面实现了对网络状态的监听
 * @author Administrator
 *
 */
public abstract class BaseActivity extends FragmentActivity implements OnNetworkStatusChanged{
	
	// networkStatus, 1 available, 0 unavailable
	private int networkStatus;
	private NetworkStatusReceiver networkStatusReceiver;
	private ExitReceiver exitReceiver;
	
	public static final String EXIT_ACTION = "com.fourteen.outersource.ACTION_EXIT";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		networkStatusReceiver = new NetworkStatusReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(NetworkStatus.CONNECTIVITY_CHANGE_ACTION);
		registerReceiver(networkStatusReceiver, filter);
		Logg.out("registerReceiver(networkStatusReceiver, filter)");
		exitReceiver = new ExitReceiver();
		IntentFilter intentFilter = new IntentFilter(EXIT_ACTION);
		registerReceiver(exitReceiver, intentFilter);
	}
	
	/**
	 * 获取网络是否可用
	 * @return
	 */
	public boolean isNetworkAvailable(){
		boolean flg = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			flg = true;
		} else {
			flg = false;
		}
		return flg;
	}
	
	/**
	 * 获取到网络的类型
	 * @param context
	 * @return
	 */
	public int getNetworkType() {
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return -1;
		}
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return info.getType();
		} else {
			return -1;
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler networkHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NetworkStatus.NETWORK_AVAILABLE:
				// network reconnected, refresh data
				networkStatusChanged(networkStatus);
				break;
			case NetworkStatus.NETWORK_UNAVAILABLE:
				networkStatusChanged(networkStatus);
				break;
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (networkStatusReceiver != null) {
			Logg.out("unregisterReceiver(networkStatusReceiver)");
			unregisterReceiver(networkStatusReceiver);
		}
		if(exitReceiver != null) {
			Logg.out("unregisterReceiver(exitReceiver)");
			unregisterReceiver(exitReceiver);
		}
	}
	
	/**
	 * 网络状态接收
	 */
	class NetworkStatusReceiver extends BroadcastReceiver {

		public NetworkStatusReceiver() {
			
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action != null && action.equals(NetworkStatus.CONNECTIVITY_CHANGE_ACTION)) {
				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					//System.out.println("NETWORK_AVAILABLE");
					networkStatus = NetworkStatus.NETWORK_AVAILABLE;
					networkHandler.sendEmptyMessage(NetworkStatus.NETWORK_AVAILABLE);
				} else {
					//System.out.println("NETWORK_UNAVAILABLE");
					networkStatus = NetworkStatus.NETWORK_UNAVAILABLE;
					networkHandler.sendEmptyMessage(NetworkStatus.NETWORK_UNAVAILABLE);
				}
			}
		}

	}
	
	/**
	 * 退出接收器
	 */
	class ExitReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(EXIT_ACTION)) {
				Logg.out("receve exit action");
				BaseActivity.this.finish();
			}
		}
		
	}


}
