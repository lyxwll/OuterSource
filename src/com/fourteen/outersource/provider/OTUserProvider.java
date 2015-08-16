package com.fourteen.outersource.provider;

import com.fourteen.outersource.utils.Logg;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class OTUserProvider extends ContentProvider {

	// 设置标志
	private static final String TAG = "OTUserProvider";
	// 对数据库的接口进行包装：
	private DBHelper dbHelper;
	private SQLiteDatabase otSourceDb;
	public static final String AUTHORITY = "com.fourteen.outersource.provider.OTUserProvider";
	
	//生成Uri
	public static final Uri OT_USER_URI = Uri.parse("content://" + AUTHORITY+ "/ot_user");
    
	public static final int OT_USERS = 1;
	public static final int OT_USER_ID = 2;
	
	
	//Uri匹配
	private static final UriMatcher uriMatcher;
	
	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		uriMatcher.addURI(AUTHORITY, "ot_user", OT_USERS);
		uriMatcher.addURI(AUTHORITY, "ot_user/#", OT_USER_ID);
	}

	/**
	 * 重写onCreate()方法
	 */
	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());  //getContext()
		otSourceDb = dbHelper.getWritableDatabase(); //获取一个可写的数据库
		return (otSourceDb == null) ? false : true;
	}
	
	/**
	 * 根据传入的URI，返回URI所表示数据类型
	 */
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri))
		{
		case OT_USERS:
			return "vnd.android.cursor.dir/com.fourteen.outersource.provider";
		case OT_USER_ID:
			return "vnd.android.cursor.item/com.fourteen.outersource.provider";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	/**
	 * 删除指定数据列
	 */
	@Override
	public int delete(Uri uri, String where, String[] selectionArgs) {
		int count;
		switch (uriMatcher.match(uri)) //匹配URI
		{
		case OT_USERS:  //当期学生表
			count = otSourceDb.delete(UserColumn.OT_USER_TABLE, where, selectionArgs);
			break;
		case OT_USER_ID:  //当期学生
			/*String studentId = uri.getPathSegments().get(1); //获取到要删除的记录的ID
			count = studentDb.delete(
					StudentColumns.CURRENT_STUDENT_TABLE,
					StudentColumns._ID
							+ "="
							+ studentId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ")" : ""), selectionArgs);*/
			count = otSourceDb.delete(UserColumn.OT_USER_TABLE, where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}



	/**
	 * 插入数据
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		long rowId;
		switch (uriMatcher.match(uri)) {
		case OT_USERS:  //当期学生表
			//nullColumnHack：当values参数为空或者里面没有内容的时候，
			//我们insert是会失败的（底层数据库不允许插入一个空行），
			//为了防止这种情况，我们要在这里指定一个列名，到时候如果发现将要插入的行为空行时，
			//就会将你指定的这个列名的值设为null，然后再向数据库中插入。
			rowId = otSourceDb.insert(UserColumn.OT_USER_TABLE, null, initialValues);
			if (rowId > 0)
			{
				Uri noteUri = ContentUris.withAppendedId(OT_USER_URI, rowId); //为新插入的数据加上URI的ID部分
				getContext().getContentResolver().notifyChange(noteUri, null); //通知数据的改变
				Logg.i(TAG + "insert", noteUri.toString());
				return noteUri;
			}
			else
			{
				Logg.out("Insert crrent_student error");
			}
			break;
		default:
			break;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}



	/**
	 * 查询数据
	 */
	//select uid, uname  from qq_user where uid < 100005 and uname="tianyu";
	
	//uri  
	//String[] = {"uid", "uname"} 
	//where uid=? and uname=? 
	//String[] = {"100005","tianyu"}
	//order by uid desc;
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
		Logg.e(TAG + ":query", " in Query");
		//构建一个SQL查询语句的辅助类
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch (uriMatcher.match(uri))
		{
		case OT_USERS:
			qb.setTables(UserColumn.OT_USER_TABLE);  //设置表名
			break;
		case OT_USER_ID:
			qb.setTables(UserColumn.OT_USER_TABLE);  //设置表名
			qb.appendWhere(UserColumn._ID + "="+ uri.getPathSegments().get(1));  //设置查询的where 条件子句
			break;  //uri.getPathSegments().get(1)=_id  uri.getPathSegments()返回path部分，类型为List 从0开始
		default:
			break;
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = BaseColumns._ID;  //如果orderby子句为空，则按照id进行排序
		}
		else
		{
			orderBy = sortOrder;   //否则就按照传入的参数进行排序
		}
		//第一个参数是数据库，第二个参数列名数组，第三个是where部分，第四个参数是一个字符串数组，里边的每一项依次替代在第三个参数中出现的问号（?）
		//第五个参数相当于SQL语句当中的groupby部分,第六个参数相当于SQL语句当中的having部分,第七个参数描述是怎么进行排序
		Cursor c = qb.query(otSourceDb, projection, selection, selectionArgs,null, null, orderBy);
		//登记表内容的变化
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,String[] selectionArgs) {
		int count;
		Logg.i(TAG + "update", values.toString());
		Logg.i(TAG + "update", uri.toString());
		Logg.i(TAG + "update :match", "" + uriMatcher.match(uri));
		switch (uriMatcher.match(uri))
		{
		case OT_USERS:
			Logg.out("OT_USERS");
			Logg.i(TAG + "update", OT_USERS + "");
			count = otSourceDb.update(UserColumn.OT_USER_TABLE, values, where,selectionArgs);
			break;
		case OT_USER_ID:
			Logg.out("OT_USER_ID");
			
			/*
			String id = uri.getPathSegments().get(1);
			String mywhere = StudentColumns._ID + "=" + id;
			if (where != null && !"".equals(where)) {
				where = mywhere + " and " + where;
			}
			where = mywhere + " and " + where;
			*/
			count = otSourceDb.update(UserColumn.OT_USER_TABLE, values, where, selectionArgs);
			
			/*
			String workID = uri.getPathSegments().get(1);  
			Log.i(TAG + "update", workID + "");
			count = studentDb.update(
					StudentColumns.CURRENT_STUDENT_TABLE,
					values,
					StudentColumns._ID + "=" + workID + " and " + where, selectionArgs);
					*/
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

}
