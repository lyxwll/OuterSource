package com.fourteen.outersource.adapter;
/**
 * 用户技能列表实现:UserSkillListAdapter
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
import com.fourteen.outersource.bean.UserSkillBean;
import com.fourteen.outersource.utils.DateTimeUtils;

public class UserSkillListAdapter extends BaseAdapter{
	ArrayList<UserSkillBean> list;
	LayoutInflater inflater;
	
	public UserSkillListAdapter(Context context, ArrayList<UserSkillBean> list) {
		this.list = list;
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
			convertView = inflater.inflate(R.layout.ot_user_skill_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.skill_item_name);
			holder.addTime = (TextView) convertView.findViewById(R.id.skill_item_addtime);
			holder.examTime = (TextView) convertView.findViewById(R.id.skill_item_exam_time);
			holder.score = (TextView) convertView.findViewById(R.id.skill_item_score);
			holder.grade = (ImageView) convertView.findViewById(R.id.skill_item_grade);
			convertView.setTag(holder);
		} else {
			holder  = (Holder) convertView.getTag();
		}
		if(position < list.size()) {
			holder.name.setText(list.get(position).category_name);
			String addtime = DateTimeUtils.formatToDate(list.get(position).addtime);
			holder.addTime.setText(addtime);
			String examTime = list.get(position).exam_time;
			if(examTime != null && !examTime.equals("") && !examTime.equals("null")) {
				examTime = DateTimeUtils.formatToDate(examTime);
				holder.examTime.setText(examTime);
			} else {
				holder.examTime.setText(R.string.user_skill_no_exam);
			}
			holder.score.setText(list.get(position).exam_score + "");
			if(list.get(position).exam_score < 60) {
				holder.grade.setBackgroundResource(R.drawable.ot_email_no_verified);
			} else {
				holder.grade.setBackgroundResource(R.drawable.ot_email_verified);
			}
		}
		return convertView;
	}
	
	class Holder{
		TextView name;
		TextView addTime;
		TextView examTime;
		TextView score;
		ImageView grade;
		
	}

}
