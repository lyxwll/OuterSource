package com.fourteen.outersource.utils;


import android.util.Log;

public class Logg {
	
	public static void v(String tag, String msg) {
		if(NetworkStatus.DEBUG)
		Log.v(tag, msg);
	}

	public static void d(String tag, String msg) {
		if(NetworkStatus.DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if(NetworkStatus.DEBUG) {
			Log.i(tag, msg);
		}
			
	}

	public static void w(String tag, String msg) {
		if(NetworkStatus.DEBUG) {
			Log.w(tag, msg);
		}
	}
	
	public static void w(String tag, String msg, Throwable tr){
		if(NetworkStatus.DEBUG) {
			Log.w(tag, msg, tr);
		}
	}

	public static void e(String tag, String msg) {
		if(NetworkStatus.DEBUG) {
			Log.e(tag, msg);
		}
	}

	public static void out(String msg) {
		if(NetworkStatus.DEBUG && msg!=null) {
			System.out.println(msg);
		}
	}
}
