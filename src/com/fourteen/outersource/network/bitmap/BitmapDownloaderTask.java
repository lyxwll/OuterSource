package com.fourteen.outersource.network.bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;

import com.fourteen.outersource.utils.Logg;


import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.ImageView;
/**
 * 图片网络下载器
 * BitmapDownloaderTask 此类主要是从网络上下载图片
 * @author Administrator
 *
 */
public class BitmapDownloaderTask extends AsyncTask<String, Void, Boolean> {
	private static final String TAG = BitmapDownloaderTask.class.getCanonicalName();
	public String mUrl;
	private final Context mContext;
	private final BitmapDownloadListener mListener;
	private HttpGet mGetRequest;
	private String picPath = null;

	/**
	 * 下载回调接口
	 */
	public interface BitmapDownloadListener {
		public void onComplete();

		public void onError();

		public void onCancel();
	}

	public BitmapDownloaderTask(ImageView imageView, BitmapDownloadListener listener) {
		mContext = imageView.getContext().getApplicationContext();
		mListener = listener;
	}
	
	public BitmapDownloaderTask(ImageView imageView, String path, BitmapDownloadListener listener) {
		mContext = imageView.getContext().getApplicationContext();
		mListener = listener;
		picPath = path;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		mUrl = params[0];
		Boolean finished = false;
		try {
			finished = downloadBitmap();
		} catch (Exception e) {
			Logg.w(TAG, "Error downloading bitmap" + e.getMessage());
		}
		return finished;
	}

	//for 2.2 where onCancelled(Object obj) is not implemented
	@Override
	protected void onCancelled() {
		mListener.onCancel();
		//if the task is cancelled, abort the image request
		if (mGetRequest != null) {
			Logg.w(TAG, "Aborting get request for:  " + mUrl);
			mGetRequest.abort();
			mGetRequest = null;
		}
	}

	
	@Override
	// Once the image is downloaded, associates it to the imageView
	// 一旦图片下载完成，将图片跟imageview关联起来
	protected void onPostExecute(Boolean done) {
		if (isCancelled()) {
			done = false;
		}
		Logg.w(TAG, "onPostExecute:  " + done);
		if (done) {
			mListener.onComplete();
		} else {
			mListener.onError();
		}
	}

	/**
	 * 解析URL返回状态码
	 * @return 返回状态码 
	 */
	private int resolveUrl() {
		HttpHead headRequest = new HttpHead(mUrl);
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		int statusCode = HttpStatus.SC_OK;
		try {
			HttpResponse response = client.execute(headRequest);
			statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT || statusCode == HttpStatus.SC_MOVED_PERMANENTLY ||
					statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				mUrl = response.getFirstHeader("Location").getValue();
				return resolveUrl();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		return statusCode;
	}

	/**
	 * 从网络上下载图片
	 */
	private Boolean downloadBitmap() {
		if (isCancelled()) {
			return false;
		}
		//get the filename before we follow any redirects. very important
		String filename = MD5.md5(mUrl); 
		Boolean finished = true;
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");

		try {
			mGetRequest = new HttpGet(mUrl);
			HttpResponse response = client.execute(mGetRequest);
			Logg.out("aaaaaaaaaaaaaa  download image from server");
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT || statusCode == HttpStatus.SC_MOVED_PERMANENTLY ||
					statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				statusCode = resolveUrl();
				if (statusCode == HttpStatus.SC_OK) {
					mGetRequest = new HttpGet(mUrl);
					response = client.execute(mGetRequest);
					statusCode = response.getStatusLine().getStatusCode();
				}
			}

			if (isCancelled()) {
				Logg.i(TAG, "Download of " + mUrl + " was cancelled");
				finished = false;
			} else if (statusCode != HttpStatus.SC_OK) {
				Logg.w(TAG, "Error " + statusCode + " while retrieving bitmap from " + mUrl);
				finished = false;
			} else {
				if (isCancelled()) {
					return false;
				}
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inputStream = null;
					try {
						inputStream = entity.getContent();
						if (isCancelled()) {
							return false;
						}
						FileOutputStream fos = null;
						if(picPath == null) {
							fos = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
						} else {
							File file = new File(picPath, filename);
							fos = new FileOutputStream(file);
						}

						byte[] buffer = new byte[1024];
						int len = 0;
						while (!isCancelled() && (len = inputStream.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
						fos.flush();
						fos.close();
						if (isCancelled()) {
							return false;
						}
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
						entity.consumeContent();
					}
				}
			}
		} catch (IllegalArgumentException e) {
			finished = false;
			Logg.w(TAG, "Error while retrieving bitmap from " + mUrl + e.getMessage());
		} catch (FileNotFoundException e) {
			mGetRequest.abort();
			finished = false;
			Logg.w(TAG, "Error while retrieving bitmap from " + mUrl + e.getMessage());
		} catch (IOException e) {
			mGetRequest.abort();
			finished = false;
			Logg.w(TAG, "Error while retrieving bitmap from " + mUrl+ e.getMessage());
		} finally {
			mGetRequest = null;
			client.close();
		}
		return finished;
	}
}