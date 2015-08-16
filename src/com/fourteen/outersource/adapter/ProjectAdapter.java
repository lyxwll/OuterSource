package com.fourteen.outersource.adapter;
/**
 * 项目实现:ProjectAdapter类
 */
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.ProjectBean;

public class ProjectAdapter extends BaseAdapter{
	private ArrayList<ProjectBean> projectList;
	private LayoutInflater inflater;
	
	public ProjectAdapter(Context context, ArrayList<ProjectBean> projectList) {
		this.projectList = projectList;
		inflater = LayoutInflater.from(context);
	}
	
	public void setList(ArrayList<ProjectBean> projectList){
		this.projectList = projectList;
	}

	@Override
	public int getCount() {
		if(projectList != null) {
			return projectList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(projectList != null && position < projectList.size()) {
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
		if(convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.ot_project_item, null);
			holder.pro_name = (TextView) convertView.findViewById(R.id.pro_name);
			holder.pro_start_time = (TextView) convertView.findViewById(R.id.pro_start_time);
			holder.pro_end_time = (TextView) convertView.findViewById(R.id.pro_end_time);
			holder.pro_price = (TextView) convertView.findViewById(R.id.pro_price);
			holder.pro_gress_number = (TextView) convertView.findViewById(R.id.pro_gress_number);
			holder.pro_gress = (ProgressBar) convertView.findViewById(R.id.pro_gress);
			holder.pro_desc = (TextView) convertView.findViewById(R.id.pro_desc);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		if(position < projectList.size()) {
			ProjectBean bean = projectList.get(position);
			holder.pro_name.setText(bean.pro_name);
			holder.pro_start_time.setText(bean.pro_start_time);
			holder.pro_end_time.setText(bean.pro_end_time);
			holder.pro_price.setText(bean.pro_price + "");
			holder.pro_gress_number.setText(bean.pro_gress + "");
			holder.pro_gress.setProgress(bean.pro_gress);
			holder.pro_desc.setText(bean.pro_desc);
		}
		return convertView;
	}

	class Holder{
		TextView pro_name;
		TextView pro_start_time;
		TextView pro_end_time;
		TextView pro_price;
		TextView pro_gress_number;
		ProgressBar pro_gress;
		TextView pro_desc;
	}
}
