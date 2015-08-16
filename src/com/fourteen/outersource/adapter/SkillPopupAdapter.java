package com.fourteen.outersource.adapter;
/**
 * 技能悬浮窗实现:SkillPopupAdapter类
 */
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.CategoryBean;

public class SkillPopupAdapter extends BaseAdapter{
	ArrayList<CategoryBean> list;
	LayoutInflater inflater;
	
	public SkillPopupAdapter(Context context, ArrayList<CategoryBean> list) {
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
			convertView = inflater.inflate(R.layout.ot_user_skill_popup_item, null);
			holder.textView = (TextView) convertView.findViewById(R.id.category_title);
			convertView.setTag(holder);
		} else {
			holder  = (Holder) convertView.getTag();
		}
		if(position < list.size()) {
			holder.textView.setText(list.get(position).category_name);
		}
		return convertView;
	}
	
	class Holder{
		TextView textView;
	}

}
