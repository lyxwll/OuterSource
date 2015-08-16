package com.fourteen.outersource.network.myhttp;

import java.util.ArrayList;
public class HttpParams {
	
	private ArrayList<Param> params;
	//private HashMap<String, Object> hashMap;
	
	public HttpParams() {
		params = new ArrayList<Param>();
	}
	
	public void put(String key, boolean value) {
		if(params != null && key != null) {
			Param param = new Param();
			param.key = key;
			param.vObject= value;
			params.add(param);
		}
	}
	
	public void put(String key, byte value) {
		if(params != null && key != null) {
			Param param = new Param();
			param.key = key;
			param.vObject= value;
			params.add(param);
		}
	}
	
	public void put(String key, char value) {
		if(params != null && key != null) {
			Param param = new Param();
			param.key = key;
			param.vObject= value;
			params.add(param);
		}
	}
	
	public void put(String key, int value){
		if(params != null && key != null) {
			Param param = new Param();
			param.key = key;
			param.vObject= value;
			params.add(param);
		}
	}
	
	public void put(String key, long value) {
		if(params != null && key != null) {
			Param param = new Param();
			param.key = key;
			param.vObject= value;
			params.add(param);
		}
	}
	
	public void put(String key, float value) {
		if(params != null && key != null) {
			Param param = new Param();
			param.key = key;
			param.vObject= value;
			params.add(param);
		}
	}
	
	public void put(String key, double value) {
		if(params != null && key != null) {
			Param param = new Param();
			param.key = key;
			param.vObject= value;
			params.add(param);
		}
	}
	
	public void put(String key, String value) {
		if(params != null && key != null) {
			Param param = new Param();
			param.key = key;
			param.vObject= value;
			params.add(param);
		}
	}
	
	public int getParamsCount() {
		return params.size();
	}
	
	public Param get(int index) {
		return params.get(index);
	}
}
