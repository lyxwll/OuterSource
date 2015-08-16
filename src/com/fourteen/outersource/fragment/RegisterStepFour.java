package com.fourteen.outersource.fragment;

/**
 * 图片上传:RegisterStepFour类
 */
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fourteen.outersource.R;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.crop.CropImageMainAvtivity;
import com.fourteen.outersource.crop.CropImagePath;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.network.bitmap.BitmapUtil;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpClientUtils;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.network.serveraddress.ServerAddress;
import com.fourteen.outersource.toast.CustomToast;
import com.fourteen.outersource.utils.Logg;

public class RegisterStepFour extends Fragment implements OnClickListener {

	View view;
	ImageView imageView;// 图片
	Button uploadButton;// 上传图片按钮
	ProgressBar progressBar;// 进度条
	String imagePath = null;// 图片路径
	String url = "";
	RegisterJumpListener jumpListener;

	// 消息码
	private static final int START_UPLOAD = 111;
	private static final int PROGRESS_UPLOAD = 112;
	private static final int FINISH_UPLOAD = 113;
	private static final int RECEIVED_URL = 114;
	private static final int UPLOAD_HEADER_SUCCESS = 115;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ot_register_step_four, null);
		uploadButton = (Button) view.findViewById(R.id.upload_image);
		imageView = (ImageView) view.findViewById(R.id.register_image);
		if (UserBase.getUserUrl(getActivity()) != null
				&& UserBase.getUserUrl(getActivity()).length() > 5) {
			BitmapUtil.getInstance(R.drawable.ic_launcher,
					R.drawable.ic_launcher).download(
					UserBase.getUserUrl(getActivity()), imageView);
		}
		progressBar = (ProgressBar) view.findViewById(R.id.progress);
		uploadButton.setOnClickListener(this);
		imageView.setOnClickListener(this);
		return view;
	}

	public void setRegisterJumpListener(RegisterJumpListener jumpListener) {
		this.jumpListener = jumpListener;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_image:// 图片选择
			if (getActivity() != null) {
				Intent intent = new Intent();
				// 跳转至CropImageMainAvtivity界面
				intent.setClass(getActivity(), CropImageMainAvtivity.class);
				intent.putExtra(CropImagePath.CROP_IMAGE_WIDTH, 160);
				intent.putExtra(CropImagePath.CROP_IMAGE_HEIGHT, 160);
				startActivityForResult(intent, CropImagePath.START_CROP_IMAGE);
			}
			break;
		case R.id.upload_image:// 上传头像图片按钮
			uploadHeader();
			break;
		default:
			break;
		}
	}

	Handler handler = new Handler(Looper.myLooper()) {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_UPLOAD:// 开始上传
				progressBar.setVisibility(View.VISIBLE);
				break;
			case PROGRESS_UPLOAD:// 上传进度
				progressBar.setProgress(msg.arg1);
				break;
			case FINISH_UPLOAD:// 完成上传
				progressBar.setVisibility(View.INVISIBLE);
				break;
			case RECEIVED_URL:// 接收图片下载地址
				JSONArray jsonArray = (JSONArray) msg.obj;
				if (jsonArray != null && jsonArray.length() > 0) {
					JSONObject jsonObject = jsonArray.optJSONObject(0);
					if (jsonObject.optInt("error", -1) == 0) {
						url = jsonObject.optString("url", "");
						BitmapUtil.getInstance(R.drawable.ic_launcher,
								R.drawable.ic_launcher)
								.download(url, imageView);
					}
				}
				break;
			case UPLOAD_HEADER_SUCCESS:// 上传头像成功
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject.optInt("result", -1) == 0) {
					if (getActivity() != null) {
						// UserBase.user_url = url;
						UserBase.setUserUrl(getActivity(), url);
						CustomToast.makeText(getActivity(),
								R.string.register_upload_header_success,
								CustomToast.LENGTH_SHORT).show();
						// 跳转界面到主界面:jumpListener.jumpToNext(3);
						if (jumpListener != null) {
							jumpListener.jumpToNext(3);
						}
					}
				} else {
					if (getActivity() != null) {
						// 上传失败
						CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(
								getActivity());
						builder.setTitle(R.string.register_upload_header_faliure);
						builder.setMessage(jsonObject.optString("msg", ""));
						builder.setPositiveButton(R.string.sure, null);
						builder.create().show();
					}
				}
				break;
			}
		}
	};

	// 通过结果码启动Activity时:
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CropImagePath.START_CROP_IMAGE:
			if (resultCode == Activity.RESULT_OK && data != null) {
				imagePath = data
						.getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG);
				Logg.out("imagePath=" + imagePath);
				HttpParams params = new HttpParams();
				params.put("header.png", imagePath);
				HttpClientUtils.getInstance().upload(
						ServerAddress.getServerAddress(),
						ServerAddress.HEADER_UPLOAD_PATH, params,
						new AsyncHttpResponseHandler() {
							@Override
							public void onStartUpload() {
								handler.sendEmptyMessage(START_UPLOAD);
							}

							@Override
							public void onUploadProgress(int progress) {
								Message message = new Message();
								message.what = PROGRESS_UPLOAD;
								message.arg1 = progress;
								handler.sendMessage(message);
							}

							@Override
							public void onUploadCompleted() {
								handler.sendEmptyMessage(FINISH_UPLOAD);
							}

							@Override
							public void onSuccess(JSONArray jsonArray) {
								Message message = new Message();
								message.what = RECEIVED_URL;
								message.obj = jsonArray;
								handler.sendMessage(message);
							}

						});
			}
			break;
		}
	}

	/**
	 * 上传头像
	 */
	public void uploadHeader() {
		if (url != null && !url.equals("") && getActivity() != null) {
			HttpParams params = new HttpParams();
			params.put("user_name", UserBase.getUserName(getActivity()));
			params.put("user_password", UserBase.getUserPassword(getActivity()));
			params.put("user_url", url);
			HttpRequest.post("upload_user_header_icon", params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject jsonObject) {
							Message message = new Message();
							message.what = UPLOAD_HEADER_SUCCESS;
							message.obj = jsonObject;
							handler.sendMessage(message);
						}
					});
		} else {
			if (getActivity() != null) {
				CustomToast.makeText(getActivity(),
						R.string.register_nochoose_icon,
						CustomToast.LENGTH_SHORT).show();
			}
		}
	}

}
