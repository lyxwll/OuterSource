package com.fourteen.outersource.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//DbHelper 用来创建数据库
public class DBHelper extends SQLiteOpenHelper
{

	public static final String DATABASE_NAME = "ot_source.db";//数据库名
	public static final int DATABASE_VERSION = 2; //数据库版本号
	
	/*
	 * 构建创建表的SQL语句
	 */
	
	private static final String STAGE_TABLE = "CREATE TABLE " + UserColumn.OT_USER_TABLE + " (" + 
	        UserColumn._ID + " integer primary key autoincrement," + 
			UserColumn.USER_NAME + " char(255)," + 
			UserColumn.USER_PASSWORD + " varchar(255)" +
			");";
	
	
	/*
	 * 在SQLiteOpenHelper类中必须有构造函数
	 */
	public DBHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/*
	 * 创建数据库，只有在第一次才会调用这个函数
	 */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//创建期数表
		db.execSQL(STAGE_TABLE);
	}
	
	/*
	 * 重写数据库更新方法，此处只是很简单的将以前的数据库删除，然后再调用onCreate方法创建一个数据库
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		//删除旧表
		db.execSQL("DROP TABLE IF EXISTS " + UserColumn.OT_USER_TABLE);
		onCreate(db);
	}
}
