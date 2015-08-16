package com.fourteen.outersource.fileexplorer.adapter;

import java.io.File;
import java.util.ArrayList;

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

/**
 * 文件适配器，图标形式
 * @author Administrator
 *
 */
public class FileGridAdapter extends BaseAdapter{
	
	private ArrayList<FileBean> fileList;
	private LayoutInflater layoutInflater;
	private Context mContext;
	private int DOWN_TAG = R.id.bmd__image_downloader;
	
	public FileGridAdapter(Context context, ArrayList<FileBean> fileList) {
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
		Holder holder = null;
		if(convertView == null) {
			holder = new Holder();
			convertView = layoutInflater.inflate(R.layout.file_gird_item, null);
			holder.fileTypeIcon = (ImageView) convertView.findViewById(R.id.file_type_icon);
			holder.fileName =  (TextView) convertView.findViewById(R.id.file_name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.fileTypeIcon.setTag(DOWN_TAG, null);
		if(position < fileList.size()) {
			holder.fileName.setText(fileList.get(position).fileName);
			if(fileList.get(position).isFile == true) {
				String extendName = fileList.get(position).fileExtendName;
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
					} else {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_unknown));
					}
				} else {
					holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file));
				}
			} else {
				File file = new File(fileList.get(position).fileAbsulutePath);
				if(file.exists() && file.isDirectory() && file.canExecute()) {
					String[] files = file.list();
					if(files != null && files.length > 0) { 
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_content_folder));
					} else {
						holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_folder));
					}
				} else {
					holder.fileTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_folder));
				}
			}
		}
		return convertView;
	}
	
	
	class Holder {
		ImageView fileTypeIcon;
		TextView fileName;
	}

}
