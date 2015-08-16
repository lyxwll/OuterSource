package com.fourteen.outersource.view;

import com.fourteen.outersource.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class BottomView extends LinearLayout implements OnClickListener{
	View view;
	TextView textView0;
	TextView textView1;
	TextView textView2;
	TextView textView3;
	
	TextView[] textViews = new TextView[4];
	
	BottomClickListener bottomClickListener;
	
	public static final int BOTTOM_POSITION_0 = 0;
	public static final int BOTTOM_POSITION_1 = 1;
	public static final int BOTTOM_POSITION_2 = 2;
	public static final int BOTTOM_POSITION_3 = 3;
	

	public BottomView(Context context) {
		this(context, null);
	}

	public BottomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		view  = LayoutInflater.from(context).inflate(R.layout.ot_bottom_view, this);
		textView0 = (TextView) view.findViewById(R.id.ot_bottom_0);
		textView1 = (TextView) view.findViewById(R.id.ot_bottom_1);
		textView2 = (TextView) view.findViewById(R.id.ot_bottom_2);
		textView3 = (TextView) view.findViewById(R.id.ot_bottom_3);
		textViews[0] = textView0;
		textViews[1] = textView1;
		textViews[2] = textView2;
		textViews[3] = textView3;
		textView0.setOnClickListener(this);
		textView1.setOnClickListener(this);
		textView2.setOnClickListener(this);
		textView3.setOnClickListener(this);
	}
	
	public void setOnBottomCliclListener(BottomClickListener bottomClickListener){
		this.bottomClickListener = bottomClickListener;
	}

	@Override
	public void onClick(View v) {
		View view = null;
		int which = 0;
		switch(v.getId()) {
		case R.id.ot_bottom_0:
			view = textView0;
			which = BOTTOM_POSITION_0;
			//textView0.setSelected(true);
			//textView1.setSelected(false);
			//textView2.setSelected(false);
			//textView3.setSelected(false);
			break;
		case R.id.ot_bottom_1:
			view = textView1;
			which = BOTTOM_POSITION_1;
			//textView0.setSelected(false);
			//textView1.setSelected(true);
			//textView2.setSelected(false);
			//textView3.setSelected(false);
			break;
		case R.id.ot_bottom_2:
			view = textView2;
			which = BOTTOM_POSITION_2;
			//textView0.setSelected(false);
			//textView1.setSelected(false);
			//textView2.setSelected(true);
			//textView3.setSelected(false);
			break;
		case R.id.ot_bottom_3:
			view = textView3;
			which = BOTTOM_POSITION_3;
			//textView0.setSelected(false);
			//textView1.setSelected(false);
			//textView2.setSelected(false);
			//textView3.setSelected(true);
			break;
		}
		if(bottomClickListener != null) {
			bottomClickListener.onBottomClick(view, which);
		}
	}

	public TextView getTextView0() {
		return textView0;
	}

	public TextView getTextView1() {
		return textView1;
	}

	public TextView getTextView2() {
		return textView2;
	}

	public TextView getTextView3() {
		return textView3;
	}

	public void setText0(String text){
		textView0.setText(text);
	}
	
	public void setText0(int res){
		textView0.setText(getResources().getString(res));
	}
	
	public void setText1(String text){
		textView1.setText(text);
	}
	
	public void setText1(int res) {
		textView1.setText(getResources().getString(res));
	}
	
	public void setText2(String text){
		textView2.setText(text);
	}
	
	public void setText2(int res) {
		textView2.setText(getResources().getString(res));
	}
	
	public void setText3(String text){
		textView1.setText(text);
	}
	
	public void setText3(int res) {
		textView1.setText(getResources().getString(res));
	}
	
	public void setSelection(int position){
		if(position >= BOTTOM_POSITION_0 && position <= BOTTOM_POSITION_3) {
			for(int i=0; i < position;i++) {
				//textViews[i].setSelected(false);
			}
			textViews[position].setSelected(true);
			for(int i= position + 1; i < textViews.length; i++) {
				//textViews[i].setSelected(false);
			}
		}
	}
}
