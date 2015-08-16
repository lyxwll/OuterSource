package com.fourteen.outersource.project.activity;
/**
 * 上传附件的项目详情:GoagoActivity类
 */
import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.adapter.UploadProjectFileAdapter;
import com.fourteen.outersource.bean.ProjectBean;
import com.fourteen.outersource.bean.ProjectModelBean;
import com.fourteen.outersource.bean.UploadFileBean;
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
import com.fourteen.outersource.notification.SavedNotificationList;
import com.fourteen.outersource.toast.CustomToast;
import com.fourteen.outersource.utils.Logg;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class GoagoActivity extends BaseActivity implements HeaderClickListener,
		OnClickListener, SwipeRefreshLayout.OnRefreshListener,
		OnItemClickListener {

	HeaderView headerView;
	SwipeRefreshLayout refreshLayout;
	// UploadProjectFileAdapter projectFileAdapter;
	TextView textView;
	ListView fileListView;
	Button ot_upload_the_projectButton;
	LinearLayout layout;
	ProjectBean projectBean;
	TextView proName;
	TextView proStartTime;
	TextView proEndTime;
	TextView proPrice;
	TextView progressNumber;//进度值
	ProgressBar progress;//进度条
	TextView proDesc;//项目描述

	ProjectModelBean modleBean;

	UploadProjectFileAdapter adapter; // 适配器
	ArrayList<UploadFileBean> uploadFileList = new ArrayList<UploadFileBean>(); // 文件列表
	private static final int RECEIVED_UPLOAD_FILE_LIST = 0X1234;

	private final int SUCCESS_DOWNLOAD = 0x112;
	private final int START_DOWNLOAD = 0x111;
	private final int DOWNLOADING = 0x113;
	// 通知管理器
	NotificationManager notificationManager;
	Notification notification;
	// 远程视图
	RemoteViews remoteViews;

	private String currentPath;//当前路径

	DownLoadManager downLoadManager;//下载管理器

	public int max = 100;//最大进度值
	public int progress2 = 0;//最小进度值

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// 接收ProjectPublishActivity传过来的值:projectBean
		if (getIntent() != null && getIntent().hasExtra("projectBean")) {
			projectBean = (ProjectBean) getIntent().getSerializableExtra(
					"projectBean");
		}
		setContentView(R.layout.ot_pro_upload_project);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.project_detail);
		headerView.setHeaderClickListener(this);
		ot_upload_the_projectButton = (Button) findViewById(R.id.ot_upload_the_project);
		textView = (TextView) findViewById(R.id.list_textview);
		fileListView = (ListView) findViewById(R.id.project_file_list);
		fileListView.setOnItemClickListener(this);
		adapter = new UploadProjectFileAdapter(this, uploadFileList);
		fileListView.setAdapter(adapter);
		ot_upload_the_projectButton.setOnClickListener(this);
		textView.setOnClickListener(this);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		refreshLayout.setColorScheme(R.color.common_blue,
				R.color.warning_background, R.color.common_border,
				R.color.common_grey);
		refreshLayout.setOnRefreshListener(this);

		proName = (TextView) findViewById(R.id.pro_name);
		proStartTime = (TextView) findViewById(R.id.pro_start_time);
		proEndTime = (TextView) findViewById(R.id.pro_end_time);
		proPrice = (TextView) findViewById(R.id.pro_price);
		progressNumber = (TextView) findViewById(R.id.pro_gress_number);
		progress = (ProgressBar) findViewById(R.id.pro_gress);
		proDesc = (TextView) findViewById(R.id.pro_desc);
		if (projectBean != null) {
			currentPath = projectBean.pro_directory;
			getUploadFiles(currentPath);
			proName.setText(projectBean.pro_name);
			proStartTime.setText(projectBean.pro_start_time);
			proEndTime.setText(projectBean.pro_end_time);
			proPrice.setText(projectBean.pro_price + "");
			progressNumber.setText(projectBean.pro_gress + "");
			progress.setProgress(projectBean.pro_gress);
			proDesc.setText(projectBean.pro_desc);
		}
		// 项目模块点击事件
		layout = (LinearLayout) findViewById(R.id.ot_content_click);
		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				//跳转到项目模块界面
				intent.setClass(getApplication(), ProjectModlesActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("projectBean", projectBean);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (projectBean != null) {
			getUploadFiles(projectBean.pro_directory);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ot_upload_the_project://上传文档
			Intent intent = new Intent(this, FileExplorerMainActivity.class);
			Bundle bundle = new Bundle();
			// 在Activity之间传值:传给FileExplorerMainActivity
			bundle.putSerializable("projectBean", projectBean);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch (which) {
		case HeaderView.BACK_POSITION:
			this.onBackPressed();
			break;
		}
	}

	//获取上传文件列表
	public void getUploadFiles(String path) {
		HttpParams params = new HttpParams();
		params.put("path", path);
		HttpRequest.post("list_project_file", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						Message message = new Message();
						message.what = RECEIVED_UPLOAD_FILE_LIST;
						message.obj = jsonObject;
						handler.sendMessage(message);
					}
				});
	}

	@Override
	public void onRefresh() {
		getUploadFiles(currentPath);
	}

	//项目文件的item点击事件
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object object = parent.getAdapter().getItem(position);
		if (object instanceof UploadFileBean) {
			UploadFileBean bean = (UploadFileBean) object;
			if (bean.file_is_dir) {
				currentPath = bean.file_parent + File.separator
						+ bean.file_name;
				getUploadFiles(currentPath);
			} else {
				// item单击下载
				downloadDialog(bean);
			}
		}
	}

	// 文件下载对话框
	public void downloadDialog(final UploadFileBean bean) {
		CustomAlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.download_tricker);
		builder.setMessage(R.string.yes_or_no);// 设置对话框内容
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton(R.string.download,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						downloadFile(bean);
					}
				});

		builder.setNegativeButton(R.string.cancle,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.create().show();
	}

	/**
	 * 文件下载
	 * @param bean
	 */
	public void downloadFile(final UploadFileBean bean) {
		final int notificationId  = downLoadNotification(bean);
		//String path = ServerAddress.getImageDownloadAddress() + bean.file_parent + "/" + bean.file_name;
		String path = ServerAddress.getServerAddress()+ "outersource/down_file.php?file_path=" + bean.file_parent + "/" + bean.file_name;
		String targetFile = SystemBase.DOWN_LOAD_PATH + bean.file_name;
		Logg.out(">>>>>>>>>>>>>>>url="+ path + ",targetFile =" +targetFile);
		new DownLoadManager(path, targetFile, new DownListener() {

			@Override
			public void onStartDownLoad() {//开始下载
				Message message = new Message();
				message.what = START_DOWNLOAD;
				message.obj = bean;
				message.arg2 = notificationId;
				handler.sendMessage(message);
			}

			@Override
			public void onDownloadCompleted(String fileName) {//下载完成
				Message message = new Message();
				message.arg2 = notificationId;
				message.what = SUCCESS_DOWNLOAD;
				handler.sendMessageDelayed(message, 1000);
			}

			@Override
			public void onCompleteRateChanged(int progress) {//进度值改变
				Message message = new Message();
				message.what = DOWNLOADING;
				message.arg1 = progress;
				message.arg2 = notificationId;
				handler.sendMessageDelayed(message, 500);
			}
		}).singleDownload();
	}

	// 显示下载通知栏
	public int downLoadNotification(UploadFileBean bean) {
		progress2 = 0;
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification();
		notification.icon = R.drawable.ic_launcher;// 通知栏图标
		// 通知栏上显示的滚动文字
		notification.tickerText = getResources().getString(
				R.string.download_tricker);
		notification.when = System.currentTimeMillis();
		notification.vibrate = null;
        notification.sound = null;
		notification.flags = Notification.FLAG_NO_CLEAR;

		// 远程视图,显示的通知栏的布局
		remoteViews = new RemoteViews(GoagoActivity.this.getPackageName(),
				R.layout.ot_pro_notification_layout1);
		remoteViews.setImageViewResource(R.id.notify_icon,
				R.drawable.ic_launcher);
		remoteViews.setTextViewText(R.id.notify_title,
				getString(R.string.start_download));
		remoteViews.setTextViewText(R.id.download_file_name, bean.file_name);
		// 设置下载进度条
		remoteViews.setProgressBar(R.id.notify_progress, max, progress2, false);
		notification.contentView = remoteViews;
		Intent intent = new Intent(this, FileExplorerMainActivity.class);
		intent.putExtra("currentPath", SystemBase.DOWN_LOAD_PATH);
		//PendingIntent:包装intent
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, 0);
		notification.contentIntent = pendingIntent;
		int notificationId = SavedNotificationList.NOTIFICATION_BASE + SavedNotificationList.notificationIndex;
		Logg.out("notificationId=" + notificationId);
		notificationManager.notify(notificationId, notification);
		SavedNotificationList.addNotificationId();
		SavedNotificationList.pushNotification(notificationId);
		return notificationId;
	}

	@Override
	public void onBackPressed() {
		if (projectBean != null) {
			int lastSeparator = currentPath.lastIndexOf("/");
			String parentPath = currentPath.substring(0, lastSeparator);
			Logg.out("parentPath=" + parentPath);
			if (parentPath.contains(projectBean.pro_directory)
					&& parentPath.length() >= projectBean.pro_directory
							.length()) {
				currentPath = parentPath;
				getUploadFiles(currentPath);
			} else {
				super.onBackPressed();
			}
		} else {
			super.onBackPressed();
		}
	}

	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case RECEIVED_UPLOAD_FILE_LIST://接收上传文件列表
				ArrayList<UploadFileBean> list = new ArrayList<UploadFileBean>();
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					JSONArray jsonArray = jsonObject.optJSONArray("list");
					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.optJSONObject(i);
							UploadFileBean bean = new UploadFileBean();
							bean.file_name = object.optString("file_name", "");
							bean.file_parent = object.optString("file_parent",
									"");
							bean.file_type = object.optString("file_type", "");
							bean.file_size = object.optInt("file_size", 0);
							bean.file_is_dir = object.optBoolean("file_is_dir",
									false);
							list.add(bean);
						}
					}
				}
				uploadFileList.clear();
				uploadFileList.addAll(list);
				adapter.notifyDataSetChanged();
				setListViewHeightBaseOnChild(fileListView);
				refreshLayout.setRefreshing(false);
				// mScrollView.requestLayout();
				// fileListView.stopRefresh();
				break;
			case START_DOWNLOAD:// 开始下载,显示通知栏
				//UploadFileBean bean = (UploadFileBean) msg.obj;
				//downLoadNotification(bean);
				break;
			case DOWNLOADING:// 下载中
				int progress = msg.arg1;
				int notificationId = msg.arg2;
				if (progress < 100) {
					// 这将代表本通知中的扩展状态栏视图。
					remoteViews = notification.contentView;
					remoteViews.setTextViewText(R.id.notify_title, progress + "%");
					remoteViews.setProgressBar(R.id.notify_progress, 100, progress, false);
					notificationManager.notify(notificationId, notification);
				}
				break;
			case SUCCESS_DOWNLOAD:// 下载完成后,取消通知栏
				int notificationId1 = msg.arg2;
				CustomToast.makeText(getApplication(),R.string.download_success, CustomToast.LENGTH_SHORT).show();
				remoteViews = notification.contentView;
				remoteViews.setTextViewText(R.id.notify_title, 100 + "%");
				remoteViews.setProgressBar(R.id.notify_progress, 100, 100, false);
				Intent intent = new Intent(GoagoActivity.this,
						FileExplorerMainActivity.class);
				intent.putExtra("currentPath", SystemBase.DOWN_LOAD_PATH);
				PendingIntent pendingIntent = PendingIntent.getActivity(GoagoActivity.this, 0, intent, 0);
				notification.contentIntent = pendingIntent;
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(notificationId1, notification);
				SavedNotificationList.removeNotification(notificationId1);
				break;
			}
			return true;
		}
	});

	public void setListViewHeightBaseOnChild(ListView listView) {
		ListAdapter mAdapter = listView.getAdapter();
		if (mAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < mAdapter.getCount(); i++) {
			View mView = mAdapter.getView(i, null, listView);
			mView.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			totalHeight += mView.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (mAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*if (notificationManager != null) {
			notificationManager.cancel(NOTIFICATION_ID);
		}*/
	}
}
