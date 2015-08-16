package com.fourteen.outersource.adapter;
/**
 * 上传项目文档实现:UploadProjectFileAdapter类
 */
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.UploadFileBean;
import com.fourteen.outersource.network.bitmap.BitmapUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UploadProjectFileAdapter extends BaseAdapter{
	private ArrayList<UploadFileBean> fileList;
	private LayoutInflater inflater;
	private Context mContext;
	public boolean isFile;
	private int DOWN_TAG = R.id.bmd__image_downloader;
	
	public UploadProjectFileAdapter(Context mContext,ArrayList<UploadFileBean> fileList) {
		this.fileList = fileList;
		inflater = LayoutInflater.from(mContext);
		this.mContext = mContext;
	}
	
	public void setFileList(ArrayList<UploadFileBean> fileList){
		this.fileList = fileList;
	}

	@Override
	public int getCount() {
		if(fileList != null){
			return fileList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(fileList != null && position < fileList.size()){
			return fileList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if(convertView == null){
			holder = new Holder();
			convertView = inflater.inflate(R.layout.ot_pro_file_list_item, null);
			holder.fileTypeIcon = (ImageView) convertView.findViewById(R.id.ot_file_type_icon);
			holder.fileName = (TextView) convertView.findViewById(R.id.ot_file_name);
			holder.fileParent = (TextView) convertView.findViewById(R.id.ot_file_parent_dir);
			holder.fileType = (TextView) convertView.findViewById(R.id.ot_file_type);
			holder.fileSize = (TextView) convertView.findViewById(R.id.ot_file_size);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		//取消ImageView上的tag
		holder.fileTypeIcon.setTag(DOWN_TAG, null);
		if(position < fileList.size()){
			holder.fileName.setText(fileList.get(position).file_name);
			//holder.fileParent.setText(fileList.get(position).file_parent);
			holder.fileType.setText(fileList.get(position).file_type);
			int index = fileList.get(position).file_name.lastIndexOf(".");
			String extendName = "";
			if (index >= 0) {
				extendName = fileList.get(position).file_name.substring(index, fileList.get(position).file_name.length());
			}
			if(!fileList.get(position).file_is_dir) { //如果是文件
				if(extendName != null) {
					if(extendName.equals(".rar") || extendName.equals(".RAR")) {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_rar));
					} else if(extendName.equals(".zip") || extendName.equals(".ZIP")) {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_zip));
					} else if(extendName.equals(".bat") || extendName.equals(".BAT")) {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_bat));
					} else if(extendName.equals(".exe") || extendName.equals(".EXE")) {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_bin));
					} else if(extendName.equals(".jpg") || extendName.equals(".JPG") || extendName.equals(".png") || extendName.equals(".PNG")
							|| extendName.equals(".gif") || extendName.equals(".GIF") || extendName.equals(".bmp") || extendName.equals(".BMP")) {
						//BitmapUtil.getInstance().loadFromSD(fileList.get(position).file_parent + fileList.get(position).file_name, holder.fileTypeIcon);
						BitmapUtil.getInstance().download(fileList.get(position).file_parent + File.separator + fileList.get(position).file_name, holder.fileTypeIcon);
					} else if (extendName.equals(".mp3") || extendName.equals(".MP3") || extendName.equals(".wav") || extendName.equals(".WAV") 
							|| extendName.equalsIgnoreCase(".wav")) {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_music));
					} else if(extendName.equalsIgnoreCase(".rmvb") || extendName.equalsIgnoreCase(".avi") || extendName.equalsIgnoreCase(".mp4")
							|| extendName.equalsIgnoreCase(".rm") || extendName.equalsIgnoreCase(".mpeg") || extendName.equalsIgnoreCase(".mkv")) {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_video));
					} else if (extendName.equalsIgnoreCase(".xml")) {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_xml));
					} else if(extendName.equalsIgnoreCase(".html") || extendName.equalsIgnoreCase(".htm")) {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_html));
					} else if(extendName.equalsIgnoreCase(".java") || extendName.equalsIgnoreCase(".jar")) {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_jar));
					} else if(extendName.equalsIgnoreCase(".apk")) {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_apk));
					}
					else {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_unknown));
					}
				} else {
					holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file));
				}
				
				//设置size
				long size = fileList.get(position).file_size;
				if(size >=0 && size <=1024) {
					holder.fileSize.setText(fileList.get(position).file_size + "B");
				}
				else if(size >=1024 && size <= 1024*1024) {
					float kb = (float) (size / 1024.0);
					DecimalFormat df2  = new DecimalFormat("###.0");//这样为保持2位
					String str = df2.format(kb);
					holder.fileSize.setText(str + "KB");
				} else if(size >=1024*1024 && size < 1024*1024*1024) {
					float mb = (float) (size / (1024*1024.0));
					DecimalFormat df2  = new DecimalFormat("###.0");//这样为保持2位
					String str = df2.format(mb);
					holder.fileSize.setText(str + "MB");
				} else {
					float gb = (float) (size / (1024*1024*1024.0));
					DecimalFormat df2  = new DecimalFormat("###.0");//这样为保持2位
					String str = df2.format(gb);
					holder.fileSize.setText(str + "GB");
				}
				holder.fileType.setText(mContext.getResources().getString(R.string.file));
			} else { //如果是目录
				holder.fileType.setText(mContext.getResources().getString(R.string.dir));
				holder.fileSize.setText("");
				holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_content_folder));
			}
		}
		return convertView;
	}
	
	class Holder{
		ImageView fileTypeIcon;
		TextView fileName;
		TextView fileParent;
		TextView fileType;
		TextView fileSize;
	}
	

}
