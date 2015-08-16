package com.fourteen.outersource.project.activity;

/**
 * 项目模块详情:ProjectModuleDetailsActivity类
 */
import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.activity.MyTaskActivity;
import com.fourteen.outersource.adapter.UploadProjectFileAdapter;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.MyTaskBean;
import com.fourteen.outersource.bean.ProjectBean;
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

public class ProjectModuleDetailsActivity extends BaseActivity implements
		HeaderClickListener, OnClickListener,
		SwipeRefreshLayout.OnRefreshListener, OnItemClickListener {

	HeaderView headerView;
	Button ot_upload_the_projectButton;// 上传文档按钮
	MyTaskBean bean;
	ProgressBar ot_pro_gress2;
	ProjectBean projectBean;

	TextView pro_model_name;// 项目模块名
	TextView pro_model_start2;// 模块开始时间
	TextView pro_model_end2;// 模块结束时间
	TextView pro_model_fee2;// 模块费用
	TextView progress2;// 进度条
	ListView listView;// 模块列表
	SwipeRefreshLayout refreshLayout;

	UploadProjectFileAdapter adapter; // 上传项目文件Adapter

	public int max = 100;
	public int progress3 = 0;

	private String currentPath;
	// 上传文件列表
	private ArrayList<UploadFileBean> arrayList = new ArrayList<UploadFileBean>();
	// 消息序列号
	public static final int PROJECT_MY_TASK = 456;
	public static final int RECEVIED_PRO_MODEL_INFO = 0X123;
	public static final int RECEIVED_UPLOAD_FILE_LIST = 0X124;
	public static final int RECEIVED_MYTASK = 0X125;
	public static final int START_DOWNLOAD = 0X126;
	public static final int SUCCESS_DOWNLOAD = 0X127;
	public static final int DOWNLOADING = 0X128;

	// 通知管理器
	NotificationManager notificationManager;
	Notification notification;
	// 远程视图
	RemoteViews remoteViews;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if (getIntent() != null && getIntent().hasExtra("bean")) {
			bean = (MyTaskBean) getIntent().getSerializableExtra("bean");
			getUploadFiles(bean.pro_model_directory);
		}
		setContentView(R.layout.ot_my_task_particulars_layout);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.ot_my_task_related);
		headerView.setHeaderClickListener(this);
		ot_upload_the_projectButton = (Button) findViewById(R.id.ot_upload_the_project2);
		ot_upload_the_projectButton.setOnClickListener(this);
		ot_pro_gress2 = (ProgressBar) findViewById(R.id.ot_pro_gress2);
		ot_pro_gress2.setOnClickListener(this);
		pro_model_name = (TextView) findViewById(R.id.pro_model_name);
		pro_model_start2 = (TextView) findViewById(R.id.pro_model_start2);
		pro_model_end2 = (TextView) findViewById(R.id.pro_model_end2);
		pro_model_fee2 = (TextView) findViewById(R.id.pro_model_fee2);
		progress2 = (TextView) findViewById(R.id.progress2);
		listView = (ListView) findViewById(R.id.ot_project_details_list);
		listView.setOnItemClickListener(this);
		adapter = new UploadProjectFileAdapter(this, arrayList);
		listView.setAdapter(adapter);
		setData();

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		refreshLayout.setColorScheme(R.color.common_blue,
				R.color.warning_background, R.color.common_border,
				R.color.common_grey);
		refreshLayout.setOnRefreshListener(this);
	}

	public void setData() {
		pro_model_name.setText(bean.pro_model_name);
		pro_model_start2.setText(bean.pro_model_start);
		pro_model_end2.setText(bean.pro_model_end);
		pro_model_fee2.setText(bean.pro_model_fee + "");
		progress2.setText(bean.progress + "");
		ot_pro_gress2.setProgress(bean.progress);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 刷新界面
		if (bean != null) {
			getUploadFiles(bean.pro_model_directory);
		}
	}

	/**
	 * 获得上传文件列表
	 * 
	 * @param path
	 */
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
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch (which) {
		case HeaderView.BACK_POSITION:
			this.onBackPressed();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ot_upload_the_project2:// 上传文档
			Intent intent = new Intent(this, FileExplorerMainActivity.class);
			Bundle bundle = new Bundle();
			// 传值到跳转页面
			bundle.putSerializable("myTaskBean", bean);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case R.id.ot_pro_gress2:// 点击进度条更新进度
			progressUpdateDialog(bean);
			break;
		}
	}

	/**
	 * 更新进度条进度对话框
	 * 
	 * @param bean
	 */
	public void progressUpdateDialog(final MyTaskBean bean) {
		CustomAlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.ot_my_task_title);
		builder.setMessage(R.string.ot_my_task_message);
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		final EditText editText = (EditText) layoutInflater.inflate(
				R.layout.ot_edit_text_the_progress_of_value, null);
		builder.setView(editText);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton(R.string.ot_download,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String progress1 = editText.getText().toString();
						bean.progress = Integer.parseInt(progress1);
						if (bean.progress >= 0 && bean.progress <= 100) {
							System.out.println("bean.progress="+bean.progress);
							progressUpdate(bean);
						} else {
							System.out.println("进度值不在0到100之间");
						}
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
	 * 进度更新方法
	 * 
	 * @param bean
	 */
	public void progressUpdate(final MyTaskBean bean) {
		HttpParams params = new HttpParams();
		params.put("user_id", UserBase.getUserId(this));
		params.put("pro_model_id", bean.pro_model_id);
		params.put("progress", bean.progress);
		HttpRequest.post("user_update_model_progress", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						Message message = new Message();
						message.what = PROJECT_MY_TASK;
						message.obj = jsonObject;
						handler.sendMessage(message);
					}
				});
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PROJECT_MY_TASK:
				setData();
				//getModelInfo();
				break;
			case RECEVIED_PRO_MODEL_INFO:// 接收项目模块信息
				//Intent intent2 = new Intent(ProjectModuleDetailsActivity.this,MyTaskActivity.class);
				//startActivity(intent2);
				break;
			case RECEIVED_UPLOAD_FILE_LIST:// 接收上传文件列表
				ArrayList<UploadFileBean> list = new ArrayList<UploadFileBean>();
				JSONObject jsonObject1 = (JSONObject) msg.obj;
				if (jsonObject1 != null
						&& jsonObject1.optInt("result", -1) == 0) {
					JSONArray jsonArray = jsonObject1.optJSONArray("list");
					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.optJSONObject(i);
							UploadFileBean bean = new UploadFileBean();
							bean.file_name = object.optString("file_name", "");
							bean.file_parent = object.optString("file_parent","");
							bean.file_type = object.optString("file_type", "");
							bean.file_size = object.optInt("file_size", 0);
							bean.file_is_dir = object.optBoolean("file_is_dir",false);
							list.add(bean);
						}
					}
				}
				arrayList.clear();
				arrayList.addAll(list);
				adapter.notifyDataSetChanged();
				setListViewHeightBaseOnChild(listView);
				refreshLayout.setRefreshing(false);
				break;
			case RECEIVED_MYTASK:
				adapter.notifyDataSetChanged();
				break;
			case START_DOWNLOAD:// 开始下载,显示通知栏
				// UploadFileBean bean = (UploadFileBean) msg.obj;
				// downLoadNotification(bean);
				break;
			case DOWNLOADING:// 下载中
				int progress = msg.arg1;
				int notificationId = msg.arg2;
				if (progress < 100) {
					// 这将代表本通知中的扩展状态栏视图。
					remoteViews = notification.contentView;
					remoteViews.setTextViewText(R.id.notify_title, progress
							+ "%");
					remoteViews.setProgressBar(R.id.notify_progress, 100,
							progress, false);
					notificationManager.notify(notificationId, notification);
				}
				break;
			case SUCCESS_DOWNLOAD:// 下载完成后,取消通知栏
				int notificationId1 = msg.arg2;
				CustomToast.makeText(getApplication(),
						R.string.download_success, CustomToast.LENGTH_SHORT)
						.show();
				remoteViews = notification.contentView;
				remoteViews.setTextViewText(R.id.notify_title, 100 + "%");
				remoteViews.setProgressBar(R.id.notify_progress, 100, 100,
						false);
				Intent intent = new Intent(ProjectModuleDetailsActivity.this,
						FileExplorerMainActivity.class);
				intent.putExtra("currentPath", SystemBase.DOWN_LOAD_PATH);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						ProjectModuleDetailsActivity.this, 0, intent, 0);
				notification.contentIntent = pendingIntent;
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(notificationId1, notification);
				SavedNotificationList.removeNotification(notificationId1);
				break;
			}
			;
		};
	};

	// 显示下载通知栏
	public int downLoadNotification(UploadFileBean bean) {
		progress3 = 1;
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

		// 显示的通知栏的布局
		remoteViews = new RemoteViews(
				ProjectModuleDetailsActivity.this.getPackageName(),
				R.layout.ot_pro_notification_layout1);
		remoteViews.setImageViewResource(R.id.notify_icon,
				R.drawable.ic_launcher);
		remoteViews.setTextViewText(R.id.notify_title,
				getString(R.string.start_download));
		remoteViews.setTextViewText(R.id.download_file_name, bean.file_name);
		// 设置下载进度条
		remoteViews.setProgressBar(R.id.notify_progress, max, progress3, false);
		notification.contentView = remoteViews;
		Intent intent = new Intent(this, FileExplorerMainActivity.class);
		intent.putExtra("currentPath", SystemBase.DOWN_LOAD_PATH);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		notification.contentIntent = pendingIntent;
		// notificationManager.notify(NOTIFICATION_ID, notification);
		int notificationId = SavedNotificationList.NOTIFICATION_BASE
				+ SavedNotificationList.notificationIndex;
		Logg.out("notificationId=" + notificationId);
		notificationManager.notify(notificationId, notification);
		SavedNotificationList.addNotificationId();
		SavedNotificationList.pushNotification(notificationId);
		return notificationId;
	}

	/**
	 * 获得项目模块信息
	 */
	public void getModelInfo() {
		HttpParams params = new HttpParams();
		params.put("pro_model_id", bean.pro_model_id);
		HttpRequest.post("get_model_info", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						Message message = new Message();
						message.what = RECEVIED_PRO_MODEL_INFO;
						message.obj = jsonObject;
						handler.sendMessage(message);
					}
				});
	}

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

	// 刷新
	@Override
	public void onRefresh() {
		getModelInfo();
		getUploadFiles(bean.pro_model_directory);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object object = parent.getAdapter().getItem(position);
		if (object instanceof UploadFileBean) {
			UploadFileBean bean2 = (UploadFileBean) object;
			if (bean2.file_is_dir) {
				currentPath = bean2.file_parent + File.separator
						+ bean2.file_name;
				getUploadFiles(currentPath);
			} else {
				// item单击下载
				downloadDialog(bean2);
			}
		}
	}

	/**
	 * 下载对话框
	 * 
	 * @param bean
	 */
	public void downloadDialog(final UploadFileBean bean) {
		CustomAlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.download_tricker);
		builder.setMessage(R.string.yes_or_no);// 设置对话框内容
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton(R.string.download,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						downloadFile(bean);// 下载文件
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
	 * 下载文件
	 * 
	 * @param bean
	 */
	public void downloadFile(final UploadFileBean bean) {
		final int notificationId = downLoadNotification(bean);
		// String path = ServerAddress.getImageDownloadAddress()+
		// bean.file_parent + "/" + bean.file_name;
		String path = ServerAddress.getServerAddress()
				+ "outersource/down_file.php?file_path=" + bean.file_parent
				+ "/" + bean.file_name;
		String targetFile = SystemBase.DOWN_LOAD_PATH + bean.file_name;
		Logg.out(">>>>>>>>>>>>>>>url=" + path + ",targetFile =" + targetFile);
		new DownLoadManager(path, targetFile, new DownListener() {

			@Override
			public void onStartDownLoad() {// 开始下载
				Message message = new Message();
				message.what = START_DOWNLOAD;
				message.obj = bean;
				message.arg2 = notificationId;
				handler.sendMessage(message);
			}

			@Override
			public void onDownloadCompleted(String fileName) {// 下载完成
				Message message = new Message();
				message.what = SUCCESS_DOWNLOAD;
				message.arg2 = notificationId;
				handler.sendMessageDelayed(message, 1000);
			}

			@Override
			public void onCompleteRateChanged(int progress) {// 下载进度变化
				Message message = new Message();
				message.what = DOWNLOADING;
				message.arg1 = progress;
				message.arg2 = notificationId;
				handler.sendMessageDelayed(message, 500);
			}
		}).singleDownload();
	}

}
