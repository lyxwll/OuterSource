package com.fourteen.outersource.provider;

import android.provider.BaseColumns;
/**
 * @author Eibit
 *
 */
public class UserColumn implements BaseColumns{

	public static final String OT_USER_TABLE = "ot_user";
	
	//BaseColumns里面有一个属性列是 _ID
	//列名
	public static final String USER_NAME = "user_name";
	public static final String USER_PASSWORD = "user_password";
		// 列 索引值,与列名对应
	public static final int _ID_COLUMN = 0;
	public static final int USER_NAME_COLUMN = 1;
	public static final int USER_PASSWORD_COLUMN = 2;
		
		
		/*
		 * 查询结果
		 */
		public static final String[] PROJECTION =
		{
				_ID,	// 0
				USER_NAME, //1
				USER_PASSWORD// 2
		};
		
		/*
		 * 查询结果
		 */
		public static final String[] PROJECTION_NO_ID =
		{
				USER_NAME,// 2
				USER_PASSWORD// 3
		};
}
