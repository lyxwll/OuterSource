package com.fourteen.outersource.adapter;
/**
 * 注册问题实现:RegisterQuestionAdapter类
 */
import java.util.ArrayList;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.SecretBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RegisterQuestionAdapter extends BaseAdapter{
	ArrayList<SecretBean> list;
	LayoutInflater inflater;
	
	public RegisterQuestionAdapter(Context context, ArrayList<SecretBean> list) {
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
			convertView = inflater.inflate(R.layout.ot_register_question_item, null);
			holder.textView = (TextView) convertView.findViewById(R.id.question_title);
			convertView.setTag(holder);
		} else {
			holder  = (Holder) convertView.getTag();
		}
		if(position < list.size()) {
			holder.textView.setText(list.get(position).pas_name);
		}
		return convertView;
	}
	
	class Holder{
		TextView textView;
	}

}
