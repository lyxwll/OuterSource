package com.fourteen.outersource.adapter;

/**
 * 主界面内容:MainAdapter类
 */
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.CategoryBean;
import com.fourteen.outersource.bean.ProjectBean;
import com.fourteen.outersource.network.bitmap.BitmapUtil;

public class MainAdapter extends BaseAdapter {
	private ArrayList<ProjectBean> projectList;
	private LayoutInflater inflater;
	private CategoryBean bean;
	private int DOWN_TAG = R.id.bmd__image_downloader;

	public MainAdapter(Context context, CategoryBean bean,
			ArrayList<ProjectBean> projectList) {
		this.projectList = projectList;
		this.bean = bean;
		inflater = LayoutInflater.from(context);
	}

	public void setList(ArrayList<ProjectBean> projectList) {
		this.projectList = projectList;
	}

	@Override
	public int getCount() {
		if (projectList != null) {
			return projectList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (projectList != null && position < projectList.size()) {
			return projectList.get(position);
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
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.ot_mian_adapter, null);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.title_icon);
			holder.pro_name = (TextView) convertView
					.findViewById(R.id.ot_main_proiect_name);
			holder.pro_date = (TextView) convertView
					.findViewById(R.id.ot_proiect_publish_time);
			holder.pro_price = (TextView) convertView
					.findViewById(R.id.ot_proiect_money);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.imageView.setTag(DOWN_TAG, null);
		if (position < projectList.size()) {
			ProjectBean bean = projectList.get(position);
			holder.pro_name.setText(bean.pro_name);
			String date = bean.pro_date;
			date = date.substring(0, 10);
			holder.pro_date.setText(date);
			holder.pro_price.setText(bean.pro_price + "");
			if (this.bean.category_url != null && !this.bean.equals("")
					&& !this.bean.category_url.equals("null")) {
				BitmapUtil.getInstance().download(this.bean.category_url,
						holder.imageView);
			} else {
				holder.imageView.setImageResource(R.drawable.image_default);
			}
		}
		return convertView;
	}

	class Holder {
		ImageView imageView;
		TextView pro_name;
		TextView pro_date;
		TextView pro_price;
	}

}
