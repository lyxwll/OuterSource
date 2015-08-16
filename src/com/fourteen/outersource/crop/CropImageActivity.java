package com.fourteen.outersource.crop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.fourteen.outersource.R;
import com.fourteen.outersource.utils.FileUtils;

/*
 * 功能描述： 图片裁剪界面
 */
public class CropImageActivity extends Activity {
	private int mWidth = 300;
	private int mHeight = 300;
	private Bitmap mBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

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
		cropImage();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
	}

	private void cropImage() {
		System.gc();
		setContentView(R.layout.crop_image_layout);
		final CropImageView mCropImage = (CropImageView) findViewById(R.id.cropImg);
		try {
			mBitmap = BitmapFactory.decodeFile(CropImagePath.UPLOAD_IMAGE_PATH);
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
			System.gc();
			try {
				mBitmap = BitmapFactory
						.decodeFile(CropImagePath.UPLOAD_IMAGE_PATH);
			} catch (OutOfMemoryError error2) {
				error2.printStackTrace();
			}
		}
		if (mBitmap != null) {
			Drawable drawable = new BitmapDrawable(getResources(), mBitmap);
			mCropImage.setDrawable(drawable, mWidth, mHeight);
			findViewById(R.id.save).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							Bitmap bitmap = mCropImage.getCropImage();
							FileUtils.writeImage(bitmap, Environment
									.getExternalStorageDirectory()
									.getAbsolutePath()
									+ CropImagePath.CROP_IMAGE_PATH, 100);
							bitmap.recycle();
							bitmap = null;
							System.gc();
							Intent mIntent = new Intent();
							mIntent.putExtra(CropImagePath.CROP_IMAGE_PATH_TAG,
									Environment.getExternalStorageDirectory()
											.getAbsolutePath()
											+ CropImagePath.CROP_IMAGE_PATH);
							setResult(RESULT_OK, mIntent);
							if (mBitmap != null) {
								mBitmap.recycle();
								mBitmap = null;
							}
							finish();
						}
					}).start();
				}
			});
			findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setResult(RESULT_CANCELED);
					if (mBitmap != null) {
						mBitmap.recycle();
						mBitmap = null;
					}
					finish();
				}
			});
		}
	}

}
