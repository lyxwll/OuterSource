package com.fourteen.outersource.fileexplorer.adapter;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.fourteen.outersource.R;
import com.fourteen.outersource.fileexplorer.bean.FileBean;
import com.fourteen.outersource.network.bitmap.BitmapUtil;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter {
	private ArrayList<FileBean> fileList;
	private LayoutInflater layoutInflater;
	private Context mContext;
	private int DOWN_TAG = R.id.bmd__image_downloader;

	public FileListAdapter(Context context, ArrayList<FileBean> fileList) {
		this.fileList = fileList;
		layoutInflater = LayoutInflater.from(context);
		this.mContext = context;
	}
	
	public void setFileList(ArrayList<FileBean> fileList) {
		this.fileList = fileList;
	}

	@Override
	public int getCount() {
		if(fileList != null) {
			return fileList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(fileList != null && position < fileList.size()) {
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
		if(convertView == null) {
			holder = new Holder();
			convertView = layoutInflater.inflate(R.layout.file_list_item, null);
			holder.fileTypeIcon = (ImageView) convertView.findViewById(R.id.file_type_icon);
			holder.fileName =  (TextView) convertView.findViewById(R.id.file_name);
			holder.fileModifyDate = (TextView) convertView.findViewById(R.id.last_modify_time);
			holder.fileType = (TextView) convertView.findViewById(R.id.file_type);
			holder.fileSize = (TextView) convertView.findViewById(R.id.file_size);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		//取消掉imageView上的tag
		holder.fileTypeIcon.setTag(DOWN_TAG, null);
		if(position < fileList.size()) {
			holder.fileName.setText(fileList.get(position).fileName);
			if(fileList.get(position).isFile == true) {  //是文件
				String extendName = fileList.get(position).fileExtendName; //拿到扩展名,扩展名可能会为空
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
						BitmapUtil.getInstance().loadFromSD(fileList.get(position).fileAbsulutePath, holder.fileTypeIcon);
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
			} else if(fileList.get(position).isFile == false) { //是目录
				File file = new File(fileList.get(position).fileAbsulutePath);
				if(file.exists() && file.isDirectory() && file.canExecute()) {
					String[] files = file.list();
					if(files != null && files.length > 0) { //不是空目录
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_content_folder));
					} else {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_folder));
					}
				} else {
					holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_folder));
				}
			}

			long lastMOdifyTime = fileList.get(position).lastModified;
			Date date = new Date(lastMOdifyTime);
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			holder.fileModifyDate.setText(formater.format(date));
			if(fileList.get(position).isFile) {
				long size = fileList.get(position).size;
				if(size >=0 && size <=1024) {
					holder.fileSize.setText(fileList.get(position).size + "B");
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
				//holder.fileSize.setText(fileList.get(position).size + "");
			} else {
				holder.fileType.setText(mContext.getResources().getString(R.string.dir));
				holder.fileSize.setText("");
			}
		}
		return convertView;
	}

	class Holder {
		ImageView fileTypeIcon;
		TextView fileName;
		TextView fileModifyDate;
		TextView fileType;
		TextView fileSize;
	}

}
