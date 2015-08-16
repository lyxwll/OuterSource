package com.fourteen.outersource.crop;

import java.io.File;
import java.net.URI;

import com.fourteen.outersource.R;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.utils.FileUtils;
import com.fourteen.outersource.utils.Logg;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

/**
 * 裁剪图片代码 
 * Intent intent = new Intent();
 * intent.setClass(this,CropImageMainAvtivity.class);
 * intent.putExtra(CropImagePath.CROP_IMAGE_WIDTH,250);
 * intent.putExtra(CropImagePath.CROP_IMAGE_HEIGHT, 250);
 * startActivityForResult(intent, CropImagePath.START_CROP_IMAGE);
 * 返回值(Intent)data， 需要判断resultCode 是否为 RESULT_OK 使用 data。
 * getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG); 获得裁剪后的图片路径
 * 
 */
/*
 * 功能描述： 图片裁剪主入口
 */
public class CropImageMainAvtivity extends Activity {
	private static final String LOG_TAG = "CropImageMainAvtivity";
	private int mWidth = 300;// 图片截取的宽
	private int mHeight = 300;// 图片截取的高
	private boolean mCanceled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent() != null) {
			if (getIntent().hasExtra(CropImagePath.CROP_IMAGE_WIDTH)) {
				mWidth = getIntent().getIntExtra(
						CropImagePath.CROP_IMAGE_WIDTH, 300);
			}
			if (getIntent().hasExtra(CropImagePath.CROP_IMAGE_HEIGHT)) {
				mHeight = getIntent().getIntExtra(
						CropImagePath.CROP_IMAGE_HEIGHT, 300);
			}
		}
		showCropImageDialog();// 显示图片选择对话框
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 显示图片选择对话框
	 */
	private void showCropImageDialog() {

		int[] images = { R.drawable.crop_picture_image,
				R.drawable.crop_camera_image };
		String[] titles = getResources().getStringArray(
				R.array.image_upload_method);
		final CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(
				this);
		builder.setTitle(R.string.image_upload);
		CropImageChoiceAdapter adapter = new CropImageChoiceAdapter(this,
				titles, images);
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:// 选择内存中的图片
					mCanceled = true;
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					// 通过结果码启动Activity
					startActivityForResult(intent, CropImagePath.CHOOSE_IMAGE);
					dialog.dismiss();
					dialog.cancel();
					break;
				case 1:// 使用相机照的图片
					mCanceled = true;
					Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent1.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(
									CropImagePath.UPLOAD_IMAGE_PATH)));
					startActivityForResult(intent1, CropImagePath.TAKE_PHONO);
					dialog.dismiss();
					dialog.cancel();
					break;
				}
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (mCanceled == false) {
					Logg.d(LOG_TAG, "canceled crop by user, exit");
					setFailure();
				}
			}
		});
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {

		case CropImagePath.CHOOSE_IMAGE:// 选择图片
			if (resultCode == RESULT_OK && data != null) {
				Uri imageUri = data.getData();
				String[] filePathColumn = { MediaColumns.DATA };
				Cursor cursor = getContentResolver().query(imageUri,
						filePathColumn, null, null, null);
				if (cursor != null) {
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String picturePath = cursor.getString(columnIndex);

					File file = new File(picturePath);
					if (file.exists()) {
						FileUtils.copyFiles(picturePath,
								CropImagePath.UPLOAD_IMAGE_PATH, true);
						Intent intent = new Intent();
						intent.setClass(this, CropImageActivity.class);
						intent.putExtra(CropImagePath.CROP_IMAGE_WIDTH, mWidth);
						intent.putExtra(CropImagePath.CROP_IMAGE_HEIGHT,
								mHeight);
						startActivityForResult(intent,
								CropImagePath.CROP_IMAGE_REQUEST_CODE);
					}
					cursor.close();
				} else {
					URI uri = URI.create(data.getData().toString());
					File fileP = new File(uri);
					if (fileP != null && fileP.exists()) {
						FileUtils.copyFiles(fileP.getAbsolutePath(),
								CropImagePath.UPLOAD_IMAGE_PATH, true);
						Intent intent = new Intent();
						intent.setClass(this, CropImageActivity.class);
						intent.putExtra(CropImagePath.CROP_IMAGE_WIDTH, mWidth);
						intent.putExtra(CropImagePath.CROP_IMAGE_HEIGHT,
								mHeight);
						startActivityForResult(intent,
								CropImagePath.CROP_IMAGE_REQUEST_CODE);
					}
				}
			} else {
				// 未选择图片
				setFailure();
			}
			break;
		case CropImagePath.TAKE_PHONO: // 拍照
			if (resultCode == RESULT_OK) {
				File file = new File(CropImagePath.UPLOAD_IMAGE_PATH);
				if (file.exists()) {
					Intent intent = new Intent();
					intent.setClass(this, CropImageActivity.class);
					intent.putExtra(CropImagePath.CROP_IMAGE_WIDTH, mWidth);
					intent.putExtra(CropImagePath.CROP_IMAGE_HEIGHT, mHeight);
					startActivityForResult(intent,
							CropImagePath.CROP_IMAGE_REQUEST_CODE);
				}
			} else {
				// 未拍照
				setFailure();
			}
			break;
		case CropImagePath.CROP_IMAGE_REQUEST_CODE: // 裁剪结束
			if (resultCode == RESULT_OK && data != null) {
				// 已裁剪成功
				setResult(RESULT_OK, data);
				this.finish();
			} else {
				// 未裁剪
				setFailure();
			}
			break;
		}
	}

	public void setFailure() {
		setResult(RESULT_CANCELED);
		finish();
	}

}
