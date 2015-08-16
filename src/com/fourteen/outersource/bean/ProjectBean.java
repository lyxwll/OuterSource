package com.fourteen.outersource.bean;

import java.io.Serializable;

public class ProjectBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int pro_id;
	public int user_id;
	public int pro_category_id;
	public String pro_name;
	public double pro_price;
	public String pro_desc;
	public String pro_start_time;
	public String pro_end_time;
	public double pro_fee;
	public String pro_directory;
	public String pro_date;
	public int pro_gress; //项目完成总进度
	public int pro_close; //项目是否关闭

}
