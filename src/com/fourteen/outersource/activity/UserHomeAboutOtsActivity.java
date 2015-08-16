package com.fourteen.outersource.activity;
/**
 * 关于OTS:UserHomeAboutOtsActivity类
 */
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.FunctionBean;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.dialog.CustomAlertDialog.Builder;
import com.fourteen.outersource.fileexplorer.FileExplorerMainActivity;
import com.fourteen.outersource.network.bitmap.SystemBase;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.DownListener;
import com.fourteen.outersource.network.myhttp.DownLoadManager;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.network.serveraddress.ServerAddress;
import com.fourteen.outersource.toast.CustomToast;
import com.fourteen.outersource.utils.Logg;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class UserHomeAboutOtsActivity extends BaseActivity implements
		HeaderClickListener, OnClickListener, OnRefreshListener {

	HeaderView headerView;
	TextView versionTextView;//版本
	TextView functionTextView;//功能
	TextView WebsiteTextView;//网站
	TextView dailTextView;//电话
	SwipeRefreshLayout refreshLayout;
	EditText editText;
	Button button;
	private FunctionBean functionBean = new FunctionBean();

	public static final int REFRESH_MSG = 0X1111;
	public static final int FUNCTION = 0X1121;
	public static final int VERSION = 0X1122;
	
	private final int SUCCESS_DOWNLOAD = 0x112;
	private final int START_DOWNLOAD = 0x111;
	private final int DOWNLOADING = 0x113;
	
	// 通知管理器
	NotificationManager notificationManager;
	Notification notification;
	// 远程视图
	RemoteViews remoteViews;
	private static final int NOTIFICATION_ID = 999;
	public int max = 100;
	public int progress2 = 0;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_ps_about_ots);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.about);
		headerView.setHeaderClickListener(this);

		versionTextView = (TextView) findViewById(R.id.user_home_version);
		versionTextView.setOnClickListener(this);

		functionTextView = (TextView) findViewById(R.id.user_home_function);
		functionTextView.setOnClickListener(this);
		
		dailTextView=(TextView) findViewById(R.id.user_home_item3);
		dailTextView.setOnClickListener(this);

		WebsiteTextView = (TextView) findViewById(R.id.user_home_item4);
		WebsiteTextView.setOnClickListener(this);

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		refreshLayout.setColorScheme(R.color.common_blue,
				R.color.warning_background, R.color.common_border,
				R.color.common_grey);
		refreshLayout.setOnRefreshListener(this);
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_home_version://版本
			versionTextView.setClickable(false);
			getNewViersion();
			break;
		case R.id.user_home_function://功能
			functionTextView.setClickable(false);
			functionIntroduction();
			break;
		case R.id.user_home_item3://电话
			Intent intent=new Intent();
			intent.setAction("android.intent.action.CALL");
			intent.setData(Uri.parse("tel:5698745230"));
			startActivity(intent);
			break;
		case R.id.user_home_item4://网址
			Intent it = new Intent( Intent.ACTION_VIEW );
			it.setData( Uri.parse( "http://www.baidu.com/") ); 
			it = Intent.createChooser( it, null );
			startActivity( it );
			break;
		}
	}
	
	
	// 获取当前的版本号
	public int getVersion() {
		int versionCode = 0;
		PackageManager packageManager = getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	// 获取最新版本信息
	public void getNewViersion() {
		HttpParams params = new HttpParams();
		HttpRequest.post("get_latest_version", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						if (jsonObject.optInt("error", -1) == 0) {
							JSONObject object = jsonObject
									.optJSONObject("version_info");
							Message message = new Message();
							message.what = VERSION;
							message.obj = object;
							handler.sendMessage(message);
						}
					}
				});
	}

	// 弹出功能介绍窗口
	public void functionDialog() {
		CustomAlertDialog.Builder builder = new Builder(this);
		builder.setTitle(getResources().getString(R.string.funtion_introduce));
		builder.setMessage(functionBean.version_desc);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	// 获取指定的版本号
	public void functionIntroduction() {
		HttpParams Params = new HttpParams();
		Params.put("version_code", getVersion());
		HttpRequest.post("get_specified_version", Params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						if (jsonObject.optInt("error", -1) == 0) {
							JSONObject object = jsonObject
									.optJSONObject("version_info");
							Message message = new Message();
							message.what = FUNCTION;
							message.obj = object;
							handler.sendMessage(message);
						}

					}
				});
	}

	/*
	 * 解析jsonobject
	 */
	public FunctionBean resolveFunction(JSONObject jsonObject) {
		FunctionBean bean = null;
		if (jsonObject != null) {
			bean = new FunctionBean();
			bean.version_code = jsonObject.optInt("version_code", -1);
			bean.version_name = jsonObject.optString("version_name", "");
			bean.version_desc = jsonObject.optString("version_desc", "");
			bean.date = jsonObject.optString("date", "");
			bean.apk_path = jsonObject.optString("apk_path", "");
			Logg.out("bean=" + bean);
		}
		return bean;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_MSG:
				refreshLayout.setRefreshing(false);
				break;
			case FUNCTION:
				functionTextView.setClickable(true);
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject != null) {
					functionBean = resolveFunction(jsonObject);
				}
				functionDialog();
				break;
			case VERSION:
				versionTextView.setClickable(true);
				JSONObject jsonObject2 = (JSONObject) msg.obj;
				FunctionBean bean = resolveFunction(jsonObject2);
				if (getVersion() != 0 && getVersion() < bean.version_code) {
					showNewVersionDialog(bean);
				} else {
					CustomToast.makeText(UserHomeAboutOtsActivity.this,
							R.string.new_version, CustomToast.LENGTH_SHORT)
							.show();
				}
				break;
			case START_DOWNLOAD:// 开始下载,显示通知栏
				FunctionBean bean1 = (FunctionBean) msg.obj;
				downLoadNotification(bean1);
				break;
			case DOWNLOADING:// 下载中
				int progress = msg.arg1;
				Logg.out("downloading progress=" + progress);
				if (progress < 100) {
					// 这将代表本通知中的扩展状态栏视图。
					remoteViews = notification.contentView;
					remoteViews.setTextViewText(R.id.notify_title, progress+ "%");
					remoteViews.setProgressBar(R.id.notify_progress, 100,progress, false);
					notificationManager.notify(NOTIFICATION_ID, notification);
				}
				break;
			case SUCCESS_DOWNLOAD:// 下载完成后,取消通知栏
				CustomToast.makeText(getApplication(),R.string.download_success, CustomToast.LENGTH_SHORT).show();
				remoteViews = notification.contentView;
				remoteViews.setTextViewText(R.id.notify_title, 100 + "%");
				remoteViews.setProgressBar(R.id.notify_progress, 100, 100, false);
				Intent intent = new Intent(UserHomeAboutOtsActivity.this,FileExplorerMainActivity.class);
				intent.putExtra("currentPath", SystemBase.DOWN_LOAD_PATH);
				PendingIntent pendingIntent = PendingIntent.getActivity(UserHomeAboutOtsActivity.this, 0, intent, 0);
				notification.contentIntent = pendingIntent;
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(NOTIFICATION_ID, notification);
				break;
			}
		};
	};
	
	/**
	 * 显示新版本的对话框
	 * @param bean
	 */
	public void showNewVersionDialog(final FunctionBean bean){
		CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(this);
		builder.setTitle(R.string.new_version_found);
		builder.setMessage(bean.version_name + "\n" + bean.version_desc);
		builder.setNegativeButton(R.string.download, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadFile(bean);
			}
		});
		builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
	}
	
	
	/**
	 * 文件下载
	 * 
	 * @param bean
	 */
	public void downloadFile(final FunctionBean bean) {
		//String path = ServerAddress.getImageDownloadAddress() + "/" + bean.apk_path;
		String path = ServerAddress.getServerAddress()+ "outersource/down_file.php?file_path=" + bean.apk_path;
		String fileName = bean.apk_path.substring(bean.apk_path.lastIndexOf('/') +1, bean.apk_path.length());
		String targetFile = SystemBase.DOWN_LOAD_PATH + fileName;
		new DownLoadManager(path, targetFile, new DownListener() {

			@Override
			public void onStartDownLoad() {
				Message message = new Message();
				message.what = START_DOWNLOAD;
				message.obj = bean;
				handler.sendMessage(message);
			}

			@Override
			public void onDownloadCompleted(String fileName) {
				handler.sendEmptyMessageDelayed(SUCCESS_DOWNLOAD, 1000);
			}

			@Override
			public void onCompleteRateChanged(int progress) {
				Message message = new Message();
				message.what = DOWNLOADING;
				message.arg1 = progress;
				handler.sendMessageDelayed(message, 500);
			}
		}).singleDownload();
	}

	// 显示下载通知栏
	public void downLoadNotification(FunctionBean bean) {
		progress2 = 0;
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification();
		notification.icon = R.drawable.ic_launcher;// 通知栏图标
		// 通知栏上显示的滚动文字
		notification.tickerText = getResources().getString(R.string.download_new_version);
		notification.when = System.currentTimeMillis();
		notification.vibrate = null;
        notification.sound = null;
		notification.flags = Notification.FLAG_NO_CLEAR;

		// 显示的通知栏的布局
		remoteViews = new RemoteViews(UserHomeAboutOtsActivity.this.getPackageName(),
				R.layout.ot_pro_notification_layout1);
		remoteViews.setImageViewResource(R.id.notify_icon,
				R.drawable.ic_launcher);
		remoteViews.setTextViewText(R.id.notify_title,
				getString(R.string.start_download));
		remoteViews.setTextViewText(R.id.download_file_name, bean.apk_path.substring(bean.apk_path.lastIndexOf('/') + 1, bean.apk_path.length()));
		// 设置下载进度条
		remoteViews.setProgressBar(R.id.notify_progress, max, progress2, false);
		notification.contentView = remoteViews;
		Intent intent = new Intent(this, FileExplorerMainActivity.class);
		intent.putExtra("currentPath", SystemBase.DOWN_LOAD_PATH);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		notification.contentIntent = pendingIntent;
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	@Override
	public void onRefresh() {
		handler.sendEmptyMessageDelayed(REFRESH_MSG, 2000);
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
