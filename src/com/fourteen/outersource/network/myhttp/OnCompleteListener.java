package com.fourteen.outersource.network.myhttp;

import com.fourteen.outersource.network.myhttp.AsyncHttpClient.HttpClientThread;

public interface OnCompleteListener {
	public void onComplete(HttpClientThread clientThread);
}
