package com.fourteen.outersource.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.fourteen.outersource.bean.UserBean;

public class UserBase{

	/*public static int user_id;
	public static String user_name;
	public static String user_password;
	public static int user_sex;
	public static String user_birthday;
	public static String user_address;
	public static String user_phone;
	public static String user_email;
	public static int user_email_verfied;
	public static String user_url;
	public static int is_developer;
	public static String user_question1;
	public static String user_question2;
	public static String user_question3;
	public static String user_answer1;
	public static String user_answer2;
	public static String user_answer3;
	public static String date;
	public static int account_available;
	public static double user_money;*/
	
	
	public static void setUserBase(Context mContext, UserBean userBean) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("user_id", userBean.user_id);
		editor.putString("user_name", userBean.user_name);
		editor.putString("user_password", userBean.user_password);
		editor.putInt("user_sex", userBean.user_sex);
		editor.putString("user_birthday", userBean.user_birthday);
		editor.putString("user_address", userBean.user_address);
		editor.putString("user_phone", userBean.user_phone);
		editor.putString("user_email", userBean.user_email);
		editor.putInt("user_email_verfied", userBean.user_email_verfied);
		editor.putString("user_url", userBean.user_url);
		editor.putInt("is_developer", userBean.is_developer);
		editor.putString("user_question1", userBean.user_question1);
		editor.putString("user_question2", userBean.user_question2);
		editor.putString("user_question3", userBean.user_question3);
		editor.putString("user_answer1", userBean.user_answer1);
		editor.putString("user_answer2", userBean.user_answer2);
		editor.putString("user_answer3", userBean.user_answer3);
		editor.putString("date", userBean.date);
		editor.putInt("account_available", userBean.account_available);
		editor.putString("user_money", Double.toString(userBean.user_money));
		editor.commit();
	}

	public static int getUserId(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getInt("user_id", -1);
	}

	public static String getUserName(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_name", "");
	}

	public static void setUserPassword(Context mContext, String password) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("user_password", password);
		editor.commit();
	}

	public static String getUserPassword(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_password", "");
	}

	public static int getUserSex(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getInt("user_sex", -1);
	}

	public static String getUserBirthday(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_birthday", "");
	}

	public static String getUserAddress(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_address", "");
	}

	public static String getUserPhone(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_phone", "");
	}

	public static String getUserEmail(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_email", "");
	}

	public static int getUserEmailVerified(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getInt("user_email_verfied", 0);
	}

	public static void setUserUrl(Context mContext, String url) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("user_url", url);
		editor.commit();
	}

	public static String getUserUrl(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_url", "");
	}

	public static int getIsDveloper(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getInt("is_developer", 0);
	}

	public static String getUserQuestion1(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_question1", "");
	}

	public static String getUserQuestion2(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_question2", "");
	}

	public static String getUserQuestion3(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_question3", "");
	}

	public static String getUserAnswer1(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_answer1", "");
	}

	public static String getUserAnswer2(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_answer2", "");
	}

	public static String getUserAnswer3(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("user_answer3", "");
	}

	public static String getDate(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getString("date", "");
	}

	public static int getAccountAvailable(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		return preferences.getInt("account_available", 0);
	}

	public static double getUserMoney(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"UserBase", Context.MODE_PRIVATE);
		String money = preferences.getString("user_money", "0");
		double d = 0;
		try {
			d = Double.parseDouble(money);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return d;
	}
}



