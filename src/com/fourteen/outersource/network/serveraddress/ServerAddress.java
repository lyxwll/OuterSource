package com.fourteen.outersource.network.serveraddress;

public class ServerAddress {
	//使用内外网标志
	public static final int IN_OR_OUT = 1;
	
	public static final String IN_SERVER_ADDRESS = "http://192.168.100.252/";
	public static final String URL = "outersource/action.php";
	public static final String HEADER_UPLOAD_PATH = "outersource/upload_user_header.php";
	public static final String PROJECT_FILE_UPLOAD= "outersource/upload_file.php";
	
	public static final String OUT_SERVER_ADDRESS = "http://openapk.hk2008.8ahost.com/";
	
	public static final String IMAGE_DOWNLOA_IN = "http://192.168.100.252/outersource/";
	public static final String IMAGE_DOWNLOAD_OUT = "http://openapk.hk2008.8ahost.com/outersource/";
	
	public static String getServerAddress() {
		if(IN_OR_OUT == 0) {
			return IN_SERVER_ADDRESS;
		} else {
			return OUT_SERVER_ADDRESS;
		}
	}

	public static String getImageDownloadAddress(){
		if(IN_OR_OUT == 0) {
			return IMAGE_DOWNLOA_IN;
		} else {
			return IMAGE_DOWNLOAD_OUT;
		}
	}
	
	///for map
	/*public static final String PROTOCOL =  "http://";
	public static final String DOMAIN = "192.168.100.252";
	public static final String PORT = "";*/
	
	
	
}
