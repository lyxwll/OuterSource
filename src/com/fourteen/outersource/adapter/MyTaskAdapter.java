package com.fourteen.outersource.adapter;

/**
 * 我的任务实现:MyTaskAdapter类
 */
import java.util.ArrayList;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.MyTaskBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyTaskAdapter extends BaseAdapter {
	private ArrayList<MyTaskBean> mytaskList;
	private LayoutInflater inflater;

	public MyTaskAdapter(Context context, ArrayList<MyTaskBean> mytaskList) {
		this.mytaskList = mytaskList;
		inflater = LayoutInflater.from(context);
	}

	public void setList(ArrayList<MyTaskBean> mytaskList) {
		this.mytaskList = mytaskList;
	}

	@Override
	public int getCount() {
		if (mytaskList != null) {
			return mytaskList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mytaskList != null && position < mytaskList.size()) {
			return mytaskList.get(position);
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
			convertView = inflater.inflate(R.layout.ot_my_task_item, null);
			holder.pro_model_end = (TextView) convertView
					.findViewById(R.id.pro_model_end);
			holder.pro_model_name = (TextView) convertView
					.findViewById(R.id.pro_model_name);
			holder.pro_model_fee = (TextView) convertView
					.findViewById(R.id.pro_model_fee);
			holder.progress = (TextView) convertView
					.findViewById(R.id.progress);
			holder.pro_model_start = (TextView) convertView
					.findViewById(R.id.pro_model_start);
			holder.ot_pro_gress = (ProgressBar) convertView
					.findViewById(R.id.ot_pro_gress);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		if (position < mytaskList.size()) {
			MyTaskBean myTaskbean = mytaskList.get(position);
			holder.pro_model_end.setText(myTaskbean.pro_model_end + "");
			holder.pro_model_name.setText(myTaskbean.pro_model_name);
			holder.pro_model_start.setText(myTaskbean.pro_model_start + "");
			holder.progress.setText(myTaskbean.progress + "");
			holder.pro_model_fee.setText(myTaskbean.pro_model_fee + "");
			holder.ot_pro_gress.setProgress(myTaskbean.progress);
		}
		return convertView;
	}

	class Holder {
		TextView pro_model_name;
		TextView pro_model_fee;
		TextView pro_model_start;
		TextView pro_model_end;
		TextView progress;
		ProgressBar ot_pro_gress;
	}

}
