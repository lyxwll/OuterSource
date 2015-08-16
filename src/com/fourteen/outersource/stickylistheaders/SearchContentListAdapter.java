package com.fourteen.outersource.stickylistheaders;

import java.util.List;

import com.fourteen.outersource.R;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchContentListAdapter extends BaseAdapter {

	private Context mContext;
	private List<String> mDataList;

	public void setContentDataList(List<String> dataList){
		mDataList = dataList;
	}
	
	public SearchContentListAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return mDataList != null ? mDataList.size() : 0;
	}

	@Override
	public Object getItem(int arg0) {
		return mDataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.sticky_listview_item_layout, null);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position < mDataList.size()) {
			holder.text.setText(mDataList.get(position));
		}
		return convertView;
	}

	class ViewHolder {
		TextView text;
	}

}
