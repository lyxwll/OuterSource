package com.fourteen.outersource.view;
/**
 * 项目类别滑动视图
 */
import java.util.ArrayList;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.CategoryBean;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OtClassifyView extends LinearLayout {
	private LinearLayout linearLayout;
	private View view;
	private ArrayList<View> arrayList = new ArrayList<View>();
	private ArrayList<CategoryBean> list; 
	private OtClassifyListenter otClassifyListenter;
	private ArrayList<Integer> viewWidthList = new ArrayList<Integer>();
	private int currentIndex;
	private HorizontalScrollView horizontalScrollView;//水平滑动视图
	private Context mContext;

	public OtClassifyView(Context context) {
		this(context, null);
	}

	public OtClassifyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		view = layoutInflater.inflate(R.layout.ot_classify_layout, this);
		linearLayout = (LinearLayout) view.findViewById(R.id.ot_linearlayout);
		horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.ot_thitle_classify);
	}
	
	/**
	 * 设置类别列表
	 * @param list
	 */
	public void setCategoryList(ArrayList<CategoryBean> list) {
	    this.list = list;
	    LayoutInflater inflater = LayoutInflater.from(mContext);
	    linearLayout.removeAllViews();
	    for(int i=0; i< this.list.size(); i++) {
	    	TextView textView = (TextView) inflater.inflate(R.layout.ot_classfy_item, null);
	    	textView.setText(list.get(i).category_name);
	    	linearLayout.addView(textView);
	    	arrayList.add(textView);
	    }
		for (int i = 0; i < arrayList.size(); i++) {
			arrayList.get(i).setOnClickListener(new OnItemSelectedListener(arrayList.get(i), i));
			arrayList.get(i).measure(0, 0);
			viewWidthList.add(arrayList.get(i).getMeasuredWidth() + 2);
		}
		arrayList.get(0).setSelected(true);
		currentIndex = 0;
	}

	/**
	 * Item选择监听事件
	 */
	class OnItemSelectedListener implements OnClickListener {

		private View view;
		private int position;

		public OnItemSelectedListener(View view, int position) {
			this.view = view;
			this.position = position;
		}

		public void onClick(View v) {
			if (otClassifyListenter != null) {
				otClassifyListenter.otclassifylistener(view, position);
			}
			for (int i = 0; i < arrayList.size(); i++) {
				arrayList.get(i).setSelected(false);
			}
			v.setSelected(true);
			comptuingScroll(position);
			currentIndex = position;
		}

	}

	public void setItemSelectedListener(OtClassifyListenter otClassifyListenter) {
		this.otClassifyListenter = otClassifyListenter;
	}

	/**
	 * 设置选择项
	 * @param position
	 */
	public void setSelectedItem(int position) {
		for (int i = 0; i < arrayList.size(); i++) {
			arrayList.get(i).setSelected(false);
		}
		arrayList.get(position).setSelected(true);
		comptuingScroll(position);
		currentIndex = position;
	}

	/**
	 * 设置导航栏滑动
	 * @param position
	 */
	public void comptuingScroll(int position) {
		if (position > currentIndex) {
			int delta = 0;
			for (int i = currentIndex; i < position; i++) {
				delta += viewWidthList.get(i);
			}
			horizontalScrollView.smoothScrollBy(delta, 0);
		} else if (position < currentIndex) {
			int delta = 0;
			for (int i = position; i < currentIndex; i++) {
				delta += viewWidthList.get(i);
			}
			horizontalScrollView.smoothScrollBy(-delta, 0);
		} else {

		}
	}

}
