package com.fourteen.outersource.stickylistheaders;

import java.util.ArrayList;

import com.fourteen.outersource.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * 功能描述： 快速定位 sticky list view adapter
*/
public class StickyListViewAdapter extends BaseAdapter implements
		StickyListHeadersAdapter {

	private ArrayList<LetterListBean> mList;
	private LayoutInflater inflater;

	public StickyListViewAdapter(Context context, ArrayList<LetterListBean> mList) {
		inflater = LayoutInflater.from(context);
		this.mList = mList;
	}
	
	public void setList(ArrayList<LetterListBean> mList){
		this.mList = mList;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.sticky_listview_item_layout, parent, false);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position < mList.size()) {
			holder.text.setText(mList.get(position).title + "");
		}
		return convertView;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = inflater.inflate(R.layout.sticky_listview_header, parent, false);
			holder.text = (TextView) convertView.findViewById(R.id.text1);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}
		// set header text as first char in name
		if (position < mList.size()) {
			String headerText = ""
					+ mList.get(position).pinying.subSequence(0, 1).charAt(0);
			holder.text.setText(headerText);
		}

		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		// return the first character of the country as ID because this is what
		// headers are based upon
		if (position < mList.size()) {
			return mList.get(position).pinying.subSequence(0, 1).charAt(0);
		} else {
			return 0;
		}
	}

	class HeaderViewHolder {
		TextView text;
	}

	class ViewHolder {
		TextView text;
	}

}
