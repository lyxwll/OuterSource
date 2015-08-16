package com.fourteen.outersource.bean;

import java.io.Serializable;



public class UploadFileBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	  public String file_name;   //文件名
	  public String file_parent; //文件的父目录
	  public String file_type;  //文件的类型
	  public int file_size;    //文件的大小
	  public boolean file_is_dir; //是否目录，true目录，false文件
}
