package com.fourteen.outersource.adapter;
/**
 * 项目模块实现:ModelsAdapter类
 */
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.ProjectModelBean;

public class ModelsAdapter extends BaseAdapter{
	private ArrayList<ProjectModelBean> proModleList;
	private LayoutInflater inflater;
	
	public ModelsAdapter(Context context,ArrayList<ProjectModelBean> proModleList) {
		this.proModleList = proModleList;
		inflater = LayoutInflater.from(context);
	}
	
	public void setList(ArrayList<ProjectModelBean> proModleList){
		this.proModleList = proModleList;
	}
	
	@Override
	public int getCount() {
		if(proModleList != null) {
			return proModleList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(proModleList != null && position < proModleList.size()) {
			return proModleList.get(position);
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
			convertView = inflater.inflate(R.layout.ot_pro_modles_adapter, null);
			holder.pro_model_name = (TextView) convertView.findViewById(R.id.ot_proiect_modle_name);
			holder.pro_model_start = (TextView) convertView.findViewById(R.id.ot_pro_modle_time);
			holder.pro_model_end = (TextView) convertView.findViewById(R.id.ot_pro_model_end);
			holder.pro_model_fee = (TextView) convertView.findViewById(R.id.ot_pro_modle_fee);
			holder.ot_apply_user_id = (TextView) convertView.findViewById(R.id.ot_apply_user_id);
			holder.progress = (TextView) convertView.findViewById(R.id.ot_model_progress);
			holder.pro_model_gress = (ProgressBar) convertView.findViewById(R.id.ot_pro_model_gress);
			holder.pro_ot_apply = (ImageView) convertView.findViewById(R.id.ot_pro_apply);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		if(position < proModleList.size()) {
			ProjectModelBean bean = proModleList.get(position);
			holder.pro_model_name.setText(bean.pro_model_name);
			holder.pro_model_fee.setText(bean.pro_model_fee + "");
			holder.pro_model_start.setText(bean.pro_model_start + "");
			holder.pro_model_end.setText(bean.pro_model_end + "");
			holder.ot_apply_user_id.setText(bean.apply_user_id + "");
			holder.progress.setText(bean.progress + "");
			holder.pro_model_gress.setProgress(bean.progress);
			if(proModleList.get(position).apply_user_id != -1){
				holder.pro_ot_apply.setBackgroundResource(R.drawable.ot_email_no_verified);
			}else{
				holder.pro_ot_apply.setBackgroundResource(R.drawable.ot_email_verified);
			}
		}
		return convertView;
	}

	class Holder{
		TextView pro_model_name;
		TextView pro_model_fee;
		TextView pro_model_start;
		TextView pro_model_end;
		TextView ot_apply_user_id;
		TextView progress;
		ProgressBar pro_model_gress;
		ImageView pro_ot_apply;
	}
	
}
