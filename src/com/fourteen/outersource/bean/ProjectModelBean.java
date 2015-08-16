package com.fourteen.outersource.bean;

import java.io.Serializable;

public class ProjectModelBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int pro_model_id;
	public int pro_id;
	public String pro_model_name;
	public double pro_model_fee;
	public String pro_model_start;
	public String pro_model_end;
	public String pro_model_desc;
	public String pro_model_directory;
	public int is_completed;  //0 未结束  1 结束
	public int apply_user_id;
	public int progress;  //模块完成进度
	public int model_weight;

}
