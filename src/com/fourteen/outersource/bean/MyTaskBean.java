package com.fourteen.outersource.bean;

import java.io.Serializable;

public class MyTaskBean implements Serializable {
	private static final long serialVersionUID = 1L;
	public int user_id;
	public String user_name;
	public String user_phone;
	public String user_email;
	public int pro_model_id;
	public int pro_id;
	public String pro_model_name;
	public double pro_model_fee;
	public String pro_model_start;
	public String pro_model_end;
	public String pro_model_desc;
	public String pro_model_directory;
	public int is_completed;
	public int apply_user_id;
	public int progress;
	public int model_weight;
}