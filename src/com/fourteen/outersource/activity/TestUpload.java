package com.fourteen.outersource.activity;

import org.json.JSONArray;

import com.fourteen.outersource.network.bitmap.BitmapUtil;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpClientUtils;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.serveraddress.ServerAddress;
import com.fourteen.outersource.utils.Logg;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * 测试上传项目文件的类
 * @author Administrator
 *
 */
public class TestUpload extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HttpParams params = new HttpParams();
		params.put("13.jpg", "/storage/sdcard/DCIM/13.jpg");
		params.put("file1.png", "/storage/sdcard/file1.png");
		HttpClientUtils.getInstance().upload(
				ServerAddress.getServerAddress(),
				ServerAddress.PROJECT_FILE_UPLOAD
						+ "?to_path=upload_files/projects/14197540999526",
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray jsonArray) {
						Logg.out(jsonArray.toString());
					}

					@Override
					public void onFailure(String result, int statusCode,
							String errorResponse) {

					}
				});
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		ImageView imageView = new ImageView(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		imageView.setLayoutParams(layoutParams);
		imageView.setScaleType(ScaleType.FIT_XY);
		linearLayout.addView(imageView);
		setContentView(linearLayout);

		BitmapUtil.getInstance().download("upload_files/projects/14197540999526/13.jpg", imageView);
	}
}
