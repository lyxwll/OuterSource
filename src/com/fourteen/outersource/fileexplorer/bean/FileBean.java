package com.fourteen.outersource.fileexplorer.bean;

import java.io.Serializable;

/**
 * 文件的详细属性
 * @author Administrator
 *
 */
public class FileBean implements Serializable, Comparable<FileBean>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean isFile; //是否是文件
	public boolean userCanRead; //用户是否可读
	public boolean userCanWrite; //用户是否可写
	public boolean userCanExecute; //用户是否可执行
	
	public boolean groupCanRead; //用户是否可读
	public boolean groupCanWrite; //用户是否可写
	public boolean groupCanExecute; //用户是否可执行
	
	public boolean otherCanRead; //用户是否可读
	public boolean otherCanWrite; //用户是否可写
	public boolean otherCanExecute; //用户是否可执行
	
	public String owner; //文件的拥有者
	public String group; //文件的拥有组
	
	public long lastModified; //最后修改时间
	public long size; //文件大小
	
	public String fileName; //文件名
	
	public String fileParentPath; //文件的父路径
	public String fileAbsulutePath; //文件的绝对路径
	
	public String fileExtendName; //文件的扩展名

	@Override
	public int compareTo(FileBean another) {
		if(this.isFile && another.isFile || !this.isFile && !another.isFile) {
			return this.fileName.compareTo(another.fileName);
		} else if(this.isFile && !another.isFile) {
			return 1;
		} else if(!this.isFile && another.isFile) {
			return -1;
		}
		return this.fileName.compareTo(another.fileName);
	}

}
