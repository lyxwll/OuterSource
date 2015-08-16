package com.fourteen.outersource.fileexplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.MyTaskBean;
import com.fourteen.outersource.bean.ProjectBean;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.fileexplorer.adapter.FileGridAdapter;
import com.fourteen.outersource.fileexplorer.adapter.FileListAdapter;
import com.fourteen.outersource.fileexplorer.bean.FileBean;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpClientUtils;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.serveraddress.ServerAddress;
import com.fourteen.outersource.toast.CustomToast;
import com.fourteen.outersource.utils.FileUtils;
import com.fourteen.outersource.utils.Logg;

public class FileExplorerMainActivity extends Activity implements
		OnItemClickListener, OnItemLongClickListener {
	private static final String rootDir = "/"; // 文件浏览器的根目录
	private String currentPath = new String(rootDir);
	// private int rootDirBackCount = 0;

	// 文件操作
	private static final int FILE_DELETE = 0;
	private static final int FILE_RENAME = 1;
	private static final int FILE_COPY = 2;
	private static final int FILE_CUT = 3;
	private static final int FILE_UPLOAD = 4;
	// 目录操作
	private static final int DIR_DELETE = 0;
	private static final int DIR_RENAME = 1;
	private static final int DIR_COPY = 2;
	private static final int DIR_CUT = 3;
	
	private static final int START_UPLOAD = 0x125;
	private static final int PUBLISH_PROGRESS = 0x126;
	private static final int COMPLETED_UPLOAD = 0x127;

	private EditText headerPath; // 头部的路径
	private TextView back; // 返回上一级的

	private LinearLayout gridPanel;
	private LinearLayout listPanel;
	private GridView gridView;
	private ListView listView;
	private int position = 0; // 0 grid. 1 listview

	private TextView operation;
	private ImageView showType;
	private ImageView newFile;
	private ImageView newDir;

	private FileGridAdapter fileGridAdapter;
	private FileListAdapter fileListAdapter;
	private ArrayList<FileBean> fileList = new ArrayList<FileBean>();

	private String sourcePath; // 操作的源文件路径
	// private String destPath; //操作目标文件路径，就是当前路径 currentPath这个目录下
	private int action = 0; // 0复制， 1为剪切
	
	private ProjectBean projectBean;
	private MyTaskBean myTaskbean;

	private final String[][] MIME_MapTable = {
			// {后缀名，MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".docx",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" }, { ".rc", "text/plain" },
			{ ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
			{ ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" }, { "", "*/*" } };

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * 
	 * @param file
	 */
	@SuppressLint("DefaultLocale")
	private String getMIMEType(File file) {
		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase(
				Locale.CHINESE);
		if (end == "") {
			return type;
		}
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) { // MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	/**
	 * 打开文件
	 * 
	 * @param file
	 */
	private void openFile(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 获取文件file的MIME类型
		String type = getMIMEType(file);
		// 设置intent的data和Type属性。
		intent.setDataAndType(Uri.fromFile(file), type);
		// 跳转
		// 检测是否有应用来处理该Intent
		if (isIntentAvailable(this, intent)) {
			startActivity(intent);
		} else {
			// 弹出提示，找不到程序打开相关应用
			CustomToast.makeText(this, R.string.cannot_open_file,
					CustomToast.LENGTH_SHORT).show();
		}
	}

	// 检测是否有应用能够处理该intent
	public boolean isIntentAvailable(Context context, final Intent intent) {
		final PackageManager packageManager = context.getPackageManager();
		// final Intent intent = new Intent(action);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfo.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//接收GoagoActivity传过来的值:projectBean
		if(getIntent() != null && getIntent().hasExtra("projectBean")) {
			projectBean = (ProjectBean) getIntent().getSerializableExtra("projectBean");
		}
		if(getIntent() != null && getIntent().hasExtra("myTaskBean")) {
			myTaskbean = (MyTaskBean) getIntent().getSerializableExtra("myTaskBean");
		}
		if(getIntent() != null && getIntent().hasExtra("currentPath")) {
			currentPath = getIntent().getStringExtra("currentPath");
		}
		setContentView(R.layout.file_activity_main);
		gridPanel = (LinearLayout) findViewById(R.id.panel_grid);
		listPanel = (LinearLayout) findViewById(R.id.panel_list);
		gridView = (GridView) findViewById(R.id.file_grid_view);
		listView = (ListView) findViewById(R.id.file_list_view);

		headerPath = (EditText) findViewById(R.id.path);
		back = (TextView) findViewById(R.id.back);
		operation = (TextView) findViewById(R.id.operation);
		showType = (ImageView) findViewById(R.id.show_type);
		newFile = (ImageView) findViewById(R.id.new_file);
		newDir = (ImageView) findViewById(R.id.new_dir);
		showType.setOnClickListener(clickListener);
		newFile.setOnClickListener(clickListener);
		newDir.setOnClickListener(clickListener);
		operation.setOnClickListener(clickListener);
		getFileList(currentPath);
		fileGridAdapter = new FileGridAdapter(this, fileList);
		fileListAdapter = new FileListAdapter(this, fileList);
		gridView.setAdapter(fileGridAdapter);
		listView.setAdapter(fileListAdapter);
		setHeader(currentPath);
		gridView.setOnItemClickListener(this);
		gridView.setOnItemLongClickListener(this);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		back.setOnClickListener(clickListener);
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				// 返回上一级
				FileExplorerMainActivity.this.onBackPressed();
				break;
			case R.id.show_type:
				if (position == 0) {
					showType.setBackgroundResource(R.drawable.file_grid_type);
					position = 1;
					gridPanel.setVisibility(View.GONE);
					listPanel.setVisibility(View.VISIBLE);
				} else {
					showType.setBackgroundResource(R.drawable.file_list_type);
					position = 0;
					gridPanel.setVisibility(View.VISIBLE);
					listPanel.setVisibility(View.GONE);
				}
				break;
			case R.id.new_file:
				createFileDialog(true);
				break;
			case R.id.new_dir:
				createFileDialog(false);
				break;
			case R.id.operation:
				// 点击了粘贴
				String fileName = sourcePath.substring(
						sourcePath.lastIndexOf(File.separator) + 1,
						sourcePath.length()); // 拿到源文件的文件名
				Logg.out("fileName=" + fileName);
				if (action == 0) {
					if (sourcePath.equals(currentPath)) {
						FileUtils.fileCopyDir(sourcePath, currentPath
								+ File.separator + fileName + ".bak");
					} else {
						FileUtils.fileCopyDir(sourcePath, currentPath
								+ File.separator + fileName);
					}
				} else {
					if (sourcePath.equals(currentPath)) {
						FileUtils.fileCopyDir(sourcePath, currentPath
								+ File.separator + fileName + ".bak");
					} else {
						FileUtils.fileCopyDir(sourcePath, currentPath
								+ File.separator + fileName);
					}
					FileUtils.deleteFile(sourcePath);
				}
				refeshDir();
				break;
			}
		}
	};

	public void createFileDialog(final boolean isFile) {
		Builder builder = new CustomAlertDialog.Builder(this);
		if (isFile) {
			builder.setTitle(R.string.new_file);
		} else {
			builder.setTitle(R.string.new_dir);
		}
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		final EditText editText = (EditText) layoutInflater.inflate(
				R.layout.file_rename_file, null);
		builder.setView(editText);
		builder.setPositiveButton(R.string.cancel, null);
		builder.setNegativeButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String fileName = editText.getText().toString();
						// 要对文件名合法性进行检测
						if (fileName != null) {
							createFile(isFile, fileName);
						}
					}
				});
		builder.create().show();
	}

	// 真正的创建文件
	public void createFile(Boolean isFile, String fileName) {
		File parentFile = new File(currentPath);
		if (parentFile.canWrite() && parentFile.canExecute()) {
			File file = new File(currentPath + File.separator + fileName);
			if (!file.exists()) {
				if (isFile) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					file.mkdir();
				}
			}
		} else {
			CustomToast.makeText(FileExplorerMainActivity.this,
					R.string.create_new_file_error, CustomToast.LENGTH_SHORT)
					.show();
		}
		refeshDir(); // 重新更新
	}

	// 设置顶部的头
	public void setHeader(String path) {
		File file = new File(path);
		if (file.exists()) {
			headerPath.setText(file.getAbsolutePath());
		}
	}

	public void getFileList(String Path) {
		ArrayList<FileBean> list = new ArrayList<FileBean>();
		File file = new File(Path);
		if (file.exists() && file.isDirectory() && file.canExecute()) {
			String[] files = file.list(); // 列出目录下的所有文件
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					// System.out.println("files =" + files[i]);
					File fileSub = new File(file.getAbsolutePath()
							+ File.separator + files[i]);
					FileBean fileBean = new FileBean();
					int index = files[i].lastIndexOf(".");
					if (index >= 0) {
						String extendName = files[i].substring(index,
								files[i].length());
						// System.out.println("extendName= " + extendName);
						fileBean.fileExtendName = extendName;
					}
					fileBean.isFile = fileSub.isFile();
					fileBean.userCanRead = fileSub.canRead();
					fileBean.userCanWrite = fileSub.canWrite();
					fileBean.userCanExecute = fileSub.canExecute();
					fileBean.lastModified = fileSub.lastModified();
					fileBean.fileName = fileSub.getName();
					fileBean.fileParentPath = fileSub.getParent();
					fileBean.fileAbsulutePath = fileSub.getAbsolutePath();
					fileBean.size = fileSub.length();
					list.add(fileBean);
				}
				fileList.clear(); // 清空一下
				fileList.addAll(list);
			}
		} else if (file.exists() && file.isFile()) {
			fileList.clear();
			FileBean fileBean = new FileBean();
			int index = file.getName().lastIndexOf(".");
			if (index >= 0) {
				String extendName = file.getName().substring(index,
						file.getName().length());
				fileBean.fileExtendName = extendName;
				// System.out.println("extendName= " + extendName);
			}
			fileBean.isFile = file.isFile();
			fileBean.userCanRead = file.canRead();
			fileBean.userCanWrite = file.canWrite();
			fileBean.userCanExecute = file.canExecute();
			fileBean.lastModified = file.lastModified();
			fileBean.fileName = file.getName();
			fileBean.fileParentPath = file.getParent();
			fileBean.fileAbsulutePath = file.getAbsolutePath();
			fileBean.size = file.length();
			fileList.add(fileBean);
		}
		Collections.sort(fileList);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FileBean fileBean = fileList.get(position);
		if (!fileBean.isFile) { // 是目录，就跟进去
			getFileList(fileBean.fileAbsulutePath);
			currentPath = fileBean.fileAbsulutePath;
			fileGridAdapter.notifyDataSetChanged(); // 通知数据改变
			fileListAdapter.notifyDataSetChanged();
			setHeader(fileBean.fileAbsulutePath); // 设置头
		} else {
			File file = new File(fileBean.fileAbsulutePath);
			openFile(file);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		FileBean bean = fileList.get(position); // 拿到这个文件的相关信息
		if (bean.isFile) { // 如果是文件
			fileOperationOptions(bean);
		} else { // 如果是目录
			dirOpertationOptions(bean);
		}
		return true;
	}

	// 文件的相关操作选择
	public void fileOperationOptions(final FileBean bean) {
		Builder builder = new CustomAlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.operation) + " " + bean.fileName);
		String[] options;
		if(projectBean != null) {
			options = getResources().getStringArray(R.array.file_option_with_upload_dir);
		} else if(myTaskbean != null) {
			options = getResources().getStringArray(R.array.file_option_with_upload_dir);
		} else {
			options = getResources().getStringArray(R.array.file_option);
		}
		builder.setItems(options, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case FILE_DELETE:
					fileDelete(bean);
					break;
				case FILE_RENAME:
					fileRenameDialog(bean);
					break;
				case FILE_COPY:
					action = 0;
					sourcePath = bean.fileAbsulutePath;
					operation.setVisibility(View.VISIBLE); // 设置操作可见
					operation.setText(R.string.paste);
					break;
				case FILE_CUT:
					action = 1;
					sourcePath = bean.fileAbsulutePath;
					operation.setVisibility(View.VISIBLE); // 设置操作可见
					operation.setText(R.string.paste);
					break;
				case FILE_UPLOAD:
					fileUpload(bean);
					showUploadProgressbar();
					break;
				}
			}
		});
		builder.create().show();
	}

	public void fileUpload(FileBean fileBean) {
		HttpParams params = new HttpParams();
		params.put(fileBean.fileName, fileBean.fileAbsulutePath);
		if(projectBean != null) {
			HttpClientUtils.getInstance().upload(
					ServerAddress.getServerAddress(),
					ServerAddress.PROJECT_FILE_UPLOAD + "?to_path=" + projectBean.pro_directory,
					params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(JSONArray jsonArray) {
						}
						
						@Override
						public void onFailure(String result, int statusCode, String errorResponse) {
							
						}
						
						@Override
						public void onStartUpload() {
							handler.sendEmptyMessage(START_UPLOAD);
						}
						
						@Override
						public void onUploadProgress(int progress) {
							Message message = new Message();
							message.what = PUBLISH_PROGRESS;
							message.arg1 = progress;
							handler.sendMessageDelayed(message, 500);
						}
						
						@Override
						public void onUploadCompleted() {
							handler.sendEmptyMessageDelayed(COMPLETED_UPLOAD, 1000);
						}
					});
		} else if(myTaskbean != null) {
			HttpClientUtils.getInstance().upload(
					ServerAddress.getServerAddress(),
					ServerAddress.PROJECT_FILE_UPLOAD + "?to_path=" + myTaskbean.pro_model_directory,
					params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(JSONArray jsonArray) {
						}
						
						@Override
						public void onFailure(String result, int statusCode, String errorResponse) {
							
						}
						
						@Override
						public void onStartUpload() {
							handler.sendEmptyMessage(START_UPLOAD);
						}
						
						@Override
						public void onUploadProgress(int progress) {
							Message message = new Message();
							message.what = PUBLISH_PROGRESS;
							message.arg1 = progress;
							handler.sendMessageDelayed(message, 500);
						}
						
						@Override
						public void onUploadCompleted() {
							handler.sendEmptyMessageDelayed(COMPLETED_UPLOAD, 1000);
						}
					});
		}
		
	}
	
	private AlertDialog uploadProgressDialog;
	ProgressBar progressBar;
	TextView progressPercent;
	TextView progressNumber;

	public void showUploadProgressbar(){
		if(uploadProgressDialog == null) {
			CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(this);
			builder.setTitle(R.string.file_upload_progressdialog);
			View view = LayoutInflater.from(this).inflate(R.layout.ot_upload_file_progress, null);
			progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
			progressPercent = (TextView) view.findViewById(R.id.progress_percent);
			progressNumber = (TextView) view.findViewById(R.id.progress_number);
			builder.setView(view);
			uploadProgressDialog = builder.create();
		} else {
			
		}
		uploadProgressDialog.show();
	}

	Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case START_UPLOAD:
				if(uploadProgressDialog != null) {
				}
				break;
			case PUBLISH_PROGRESS:
				int progress = msg.arg1;
				if(uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
					progressBar.setProgress(progress);
					progressPercent.setText(progress + "");
					progressNumber.setText(progress + "");
				}
				break;
			case COMPLETED_UPLOAD:
				if(uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
					progressBar.setProgress(0);
					uploadProgressDialog.dismiss();
				}
				break;
			}
			return true;
		}
	});

	public void refeshDir() {
		getFileList(currentPath); // 获取获取数据
		fileGridAdapter.notifyDataSetChanged(); // 通知数据改变
		fileListAdapter.notifyDataSetChanged();
	}

	// 文件重命名
	public void fileRenameDialog(final FileBean bean) {
		Builder builder = new CustomAlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.option_rename) + " "
				+ bean.fileName);
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		final EditText editText = (EditText) layoutInflater.inflate(
				R.layout.file_rename_file, null);
		editText.setText(bean.fileName);
		editText.setSelection(bean.fileName.length());
		builder.setView(editText);
		builder.setPositiveButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 取消，什么都不做
					}
				});
		builder.setNegativeButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String fileNewName = editText.getText().toString();
						if (fileNewName != null && !fileNewName.equals("")
								&& !fileNewName.equals(bean.fileName)) {
							renameFile(bean, fileNewName);
						} else if (fileNewName != null
								&& !fileNewName.equals("")
								&& fileNewName.equals(bean.fileName)) {
							// 与原文件重名
							CustomToast.makeText(FileExplorerMainActivity.this,
									R.string.same_file_name,
									CustomToast.LENGTH_SHORT).show();
						} else {
							// 请输入正确的文件名
							CustomToast.makeText(FileExplorerMainActivity.this,
									R.string.please_input_correct_filename,
									CustomToast.LENGTH_SHORT).show();
						}
					}
				});
		builder.create().show();
	}

	// 重命名文件
	public void renameFile(FileBean bean, String fileNewName) {
		File file = new File(bean.fileAbsulutePath);
		if (file.exists() && file.canWrite()) {
			File newFile = new File(file.getParent() + File.separator
					+ fileNewName);
			Boolean success = file.renameTo(newFile);
			if (success) {
				refeshDir();
				CustomToast.makeText(FileExplorerMainActivity.this,
						R.string.rename_file_success, CustomToast.LENGTH_SHORT)
						.show();
			} else {
				// refeshDir();
				CustomToast.makeText(FileExplorerMainActivity.this,
						R.string.rename_file_failure, CustomToast.LENGTH_SHORT)
						.show();
			}
		}
	}

	// 删除文件
	public void fileDelete(FileBean bean) {
		File file = new File(bean.fileAbsulutePath);
		if (file.exists()) {
			if (file.canWrite()) {
				file.delete();
				refeshDir();
			} else {
				CustomToast.makeText(this, R.string.file_delete_error,
						CustomToast.LENGTH_SHORT).show();
			}
		} else {
			CustomToast.makeText(this, R.string.file_not_exists,
					CustomToast.LENGTH_SHORT).show();
		}
	}

	// 目录的相关操作选择
	public void dirOpertationOptions(final FileBean bean) {
		Builder builder = new CustomAlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.operation) + " "
				+ bean.fileName);
		String[] options = getResources().getStringArray(R.array.dir_option);
		builder.setItems(options, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DIR_DELETE:
					deleteDir(bean);
					break;
				case DIR_COPY:
					// 设置源
					action = 0;
					sourcePath = bean.fileAbsulutePath;
					operation.setVisibility(View.VISIBLE); // 设置操作可见
					operation.setText(R.string.paste);
					break;
				case DIR_RENAME:
					fileRenameDialog(bean);
					break;
				case DIR_CUT:
					action = 1;
					sourcePath = bean.fileAbsulutePath;
					operation.setVisibility(View.VISIBLE); // 设置操作可见
					operation.setText(R.string.paste);
					break;
				}
			}
		});
		builder.create().show();
	}

	public void deleteDir(FileBean bean) {
		File file = new File(bean.fileAbsulutePath);
		if (file.exists()) {
			if (file.canWrite()) {
				Boolean success = FileUtils.deleteFile(bean.fileAbsulutePath);
				if (success) {
					CustomToast.makeText(this, R.string.delete_dir_success,
							CustomToast.LENGTH_SHORT).show();
					refeshDir();
				} else {
					CustomToast.makeText(this, R.string.delete_dir_failure,
							CustomToast.LENGTH_SHORT).show();
				}
			} else {
				CustomToast.makeText(this, R.string.dir_delete_error,
						CustomToast.LENGTH_SHORT).show();
			}
		} else {
			CustomToast.makeText(this, R.string.dir_not_exists,
					CustomToast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		// 返回上一级
		File file = new File(currentPath);
		if (file != null && file.getParentFile() != null) { // 根目录获取父目录则为空
			getFileList(file.getParentFile().getAbsolutePath()); // 拿到父路径
			currentPath = file.getParentFile().getAbsolutePath();
			fileGridAdapter.notifyDataSetChanged(); // 通知数据改变
			fileListAdapter.notifyDataSetChanged();
			setHeader(file.getParentFile().getAbsolutePath()); // 设置头
		} else {
			FileExplorerMainActivity.this.finish();
		}
	}

}
