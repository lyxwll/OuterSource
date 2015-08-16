package com.fourteen.outersource.crop;

import android.os.Environment;

/*
 * 功能描述： 图片裁剪参数
*/
public class CropImagePath {
	public static final String CROP_IMAGE_WIDTH = "width";
	public static final String CROP_IMAGE_HEIGHT = "height";
	
	public static final int CHOOSE_IMAGE = 1; // 选择图片
	public static final int TAKE_PHONO = 2; // 拍照
	public static final int CROP_IMAGE_REQUEST_CODE = 3; //裁剪
	public static final int START_CROP_IMAGE = 4; //开始裁剪
	
	public static final String UPLOAD_IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fourteen/image_upload"; //选择图片地址
	
	public static final String CROP_IMAGE_PATH_TAG = "cropImagePath";  //返回图片intent key
	
	public static final String CROP_IMAGE_PATH = "/fourteen/crop_image"; //  剪切后的图片地址
}
