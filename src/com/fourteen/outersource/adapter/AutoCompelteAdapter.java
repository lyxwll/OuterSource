package com.fourteen.outersource.adapter;
/**
 * 自动补全:AutoCompelteAdapter类
 */
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.SaveUserBean;

public class AutoCompelteAdapter extends BaseAdapter implements Filterable {
	private LayoutInflater inflater;
	private List<SaveUserBean> list; 

	private ArrayFilter mFilter;
	private List<SaveUserBean> filterList = new ArrayList<SaveUserBean>();

	public AutoCompelteAdapter(Context context, List<SaveUserBean> mlist) {
		this.list = mlist;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(filterList != null) {
			return filterList.size();
		}
		return 0;
	}

	@Override
	public SaveUserBean getItem(int position) {
		return filterList.get(position);
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
			convertView = inflater
					.inflate(R.layout.ot_autocompeleted_item_1, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.item1);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		if (position < filterList.size()) {
			holder.title.setText(filterList.get(position).user_name);
		}
		return convertView;
	}

	class Holder {
		ImageView icon;
		TextView title;
	}

	private class ArrayFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			ArrayList<SaveUserBean> mList = new ArrayList<SaveUserBean>();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).user_name.contains(constraint)) {
					mList.add(list.get(i));
				}
			}
			results.values = mList;
			results.count = mList.size();
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			filterList = (List<SaveUserBean>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

}
