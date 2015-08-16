package com.fourteen.outersource.network.myhttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fourteen.outersource.encrypt.Security;
import com.fourteen.outersource.network.serveraddress.ServerAddress;
import com.fourteen.outersource.utils.Logg;

public class HttpRequest {

	/**
	 * 封装好加密请求方法 POST
	 * @param method 方法名
	 * @param params 参数列表
	 * @param responseHandler 结果回调
	 */
	public static void post(String method, HttpParams params, final AsyncHttpResponseHandler responseHandler) {
		try {
			JSONObject paramsObject = new JSONObject();
			for (int i = 0; i < params.getParamsCount(); i++) {
				paramsObject.put(params.get(i).key, params.get(i).vObject.toString());
			}
			JSONObject paramatersObject = new JSONObject();
			paramatersObject.put("method", method);
			paramatersObject.put("params", paramsObject);
			
			Logg.out("request URL= " + paramatersObject.toString());
			String paramaters = Security.encrypt(paramatersObject.toString(), Security.KEY);
			Logg.out("encrypt paramaters = " + paramaters );
			HttpParams params2 = new HttpParams();
			params2.put("paramaters", paramaters);
			HttpClientUtils.getInstance().post(ServerAddress.getServerAddress(), ServerAddress.URL, params2, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					/*if(responseHandler != null) {
						responseHandler.onSuccess(jsonObject);
					}*/
				}
				
				@Override
				public void onSuccess(JSONArray jsonArray) {
					/*if(responseHandler != null) {
						responseHandler.onSuccess(jsonArray);
					}*/
				}
				
				@Override
				public void onFailure(String result, int statusCode, String errorResponse) {
					/*if(responseHandler != null) {
						responseHandler.onFailure(result, statusCode, errorResponse);
					}*/
					//for testing and developer not encrypt
					if(statusCode == 200 && result != null && result != "") {
						String resultData = Security.decrypt(result, Security.KEY);
						Logg.out("decrypt result data = " + resultData);
						if(resultData != null && !resultData.equals("")) {
							try {
								JSONObject jsonObject = new JSONObject(resultData);
								responseHandler.onSuccess(jsonObject);
							} catch (JSONException e) {
								e.printStackTrace();
								try {
									JSONArray jsonArray = new JSONArray(resultData);
									responseHandler.onSuccess(jsonArray);
								} catch (JSONException e1) {
									e1.printStackTrace();
									responseHandler.onFailure(resultData, statusCode, errorResponse);
								}
							}
						} else {
							responseHandler.onFailure("get Data Failre, please check the interface", statusCode, errorResponse);
						}
					}
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 封装好加密请求方法 get
	 * @param method 方法名
	 * @param params 参数列表
	 * @param responseHandler 结果回调
	 */
	public static void get(String method, HttpParams params, final AsyncHttpResponseHandler responseHandler) {
		try {
			JSONObject paramsObject = new JSONObject();
			for (int i = 0; i < params.getParamsCount(); i++) {
				paramsObject.put(params.get(i).key, params.get(i).vObject.toString());
			}
			JSONObject paramatersObject = new JSONObject();
			paramatersObject.put("method", method);
			paramatersObject.put("params", paramsObject);
			
			Logg.out("request URL= " + paramatersObject.toString());
			String paramaters = Security.encrypt(paramatersObject.toString(), Security.KEY);
			Logg.out("encrypt paramaters = " + paramaters );
			HttpParams params2 = new HttpParams();
			params2.put("paramaters", paramaters);
			HttpClientUtils.getInstance().get(ServerAddress.getServerAddress(), ServerAddress.URL, params2, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					if(responseHandler != null) {
						responseHandler.onSuccess(jsonObject);
					}
				}
				
				@Override
				public void onSuccess(JSONArray jsonArray) {
					if(responseHandler != null) {
						responseHandler.onSuccess(jsonArray);
					}
				}
				
				@Override
				public void onFailure(String result, int statusCode, String errorResponse) {
					/*if(responseHandler != null) {
						responseHandler.onFailure(result, statusCode, errorResponse);
					}*/
					//for testing and developer not encrypt
					/*if(statusCode == 200 && result != null && result != "") {
						String resultData = Security.decrypt(result, Security.KEY);
						Logg.out("decrypt result data = " + resultData);
						try {
							JSONObject jsonObject = new JSONObject(resultData);
							responseHandler.onSuccess(jsonObject);
						} catch (JSONException e) {
							e.printStackTrace();
							try {
								JSONArray jsonArray = new JSONArray(resultData);
								responseHandler.onSuccess(jsonArray);
							} catch (JSONException e1) {
								e1.printStackTrace();
								responseHandler.onFailure(resultData, statusCode, errorResponse);
							}
						}
					}*/
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
}
