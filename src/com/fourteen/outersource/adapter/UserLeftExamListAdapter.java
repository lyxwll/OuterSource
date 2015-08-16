package com.fourteen.outersource.adapter;
/**
 * 用户剩余考试列表实现:UserLeftExamListAdapter类
 */
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.ExamBean;

public class UserLeftExamListAdapter extends BaseAdapter{
	ArrayList<ExamBean> list;
	LayoutInflater inflater;
	Context mContext;
	
	public UserLeftExamListAdapter(Context context, ArrayList<ExamBean> list) {
		this.list = list;
		mContext = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(list != null) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(list != null && position < list.size()) {
			return list.get(position);
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
			convertView = inflater.inflate(R.layout.ot_user_left_exam_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.left_item_name);
			holder.score = (TextView) convertView.findViewById(R.id.left_item_score);
			holder.time = (TextView) convertView.findViewById(R.id.left_item_time);
			convertView.setTag(holder);
		} else {
			holder  = (Holder) convertView.getTag();
		}
		if(position < list.size()) {
			holder.name.setText(list.get(position).exam_name);
			holder.score.setText(list.get(position).sum_score + "");
			holder.time.setText(list.get(position).exam_time + mContext.getResources().getString(R.string.exam_time_unit));
		}
		return convertView;
	}
	
	class Holder{
		TextView name;
		TextView score;
		TextView time;
	}

}
