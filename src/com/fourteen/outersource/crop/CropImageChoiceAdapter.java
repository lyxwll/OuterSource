package com.fourteen.outersource.crop;

import com.fourteen.outersource.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CropImageChoiceAdapter extends BaseAdapter{
	String[] titles;
	int[] icons;
	LayoutInflater inflater;
	
	public CropImageChoiceAdapter(Context context, String[] titles, int[] icons) {
		this.titles = titles;
		this.icons = icons;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(titles != null && icons != null) {
			return titles.length < icons.length ? titles.length : icons.length;
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(titles != null && position < titles.length) {
			return titles[position];
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.crop_iamge_choice_item, null);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
		TextView textView = (TextView) convertView.findViewById(R.id.title);
		if(icons!= null && titles != null && position < icons.length && position < titles.length){
			imageView.setBackgroundResource(icons[position]);
			textView.setText(titles[position]);
		}
		return convertView;
	}
	
}
