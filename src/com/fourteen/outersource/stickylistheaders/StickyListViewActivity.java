package com.fourteen.outersource.stickylistheaders;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fourteen.outersource.R;
import com.fourteen.outersource.stickylistheaders.QuickLocationRightTool.OnTouchingLetterChangedListener;
import com.fourteen.outersource.utils.Logg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 功能描述： 快速定位 sticky list view
 */
public class StickyListViewActivity extends Activity implements OnScrollListener, OnItemClickListener, OnClickListener {

	private Context mContext;
	private StickyListHeadersListView mStickyList;
	private QuickLocationRightTool mLetterListView; // 右边的字母导航栏
	private Handler mHandler;
	private TextView mTxtOverlay;
	private WindowManager mWindowManager;
	private int mScrollState;
	private DisapearThread mDisapearThread;
	private ListView mSearchContentListView;

	private ArrayList<LetterListBean> mList = new ArrayList<LetterListBean>(); // 数据列表
	private ArrayList<String> mRightCharacterList = new ArrayList<String>(); // 右侧字母表

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getBaseContext();
		setContentView(R.layout.sticky_listview_layout);
		loadData();
		initData();
		initView();

	}

	public void loadData() {
		String[] country = mContext.getResources().getStringArray(
				R.array.stringlist);
		String[] pinying = mContext.getResources().getStringArray(
				R.array.pinyinglist);
		for (int i = 0; i < country.length && i < pinying.length; i++) {
			LetterListBean bean = new LetterListBean();
			bean.title = country[i];
			bean.pinying = pinying[i];
			mList.add(bean);
		}
	}

	public void initView() {
		//findViewById(R.id.title_back).setOnClickListener(this);
		mLetterListView = (QuickLocationRightTool) findViewById(R.id.rightCharacterListView);
		mStickyList = (StickyListHeadersListView) findViewById(R.id.list);

		mLetterListView.setB(mRightCharacterList);
		mLetterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());

		textOverlayout();
		// 初始化ListAdapter
		mStickyList = (StickyListHeadersListView) findViewById(R.id.list);
		StickyListViewAdapter adapter = new StickyListViewAdapter(mContext,
				mList);
		mStickyList.setOnItemClickListener(this);
		mStickyList.setOnScrollListener(this);
		mStickyList.setAdapter(adapter);
		mDisapearThread = new DisapearThread();

		mSearchContentListView = (ListView) findViewById(R.id.search_content_list_sll);
		mSearchContentListView.setAdapter(new SearchContentListAdapter(
				StickyListViewActivity.this));
	}

	public void initData() {
		Collections.sort(mList, new ChineseCharComp());
		mRightCharacterList.add("#");
		for (int i = 0; i < mList.size(); i++) {
			if (!mRightCharacterList.contains(mList.get(i).pinying.substring(0,1).toUpperCase())) {
				mRightCharacterList.add(mList.get(i).pinying.substring(0, 1).toUpperCase());
			}
		}
		mRightCharacterList.add("*");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mTxtOverlay.setVisibility(View.INVISIBLE);
		mStickyList.setVisibility(View.INVISIBLE);
		mWindowManager.removeView(mTxtOverlay);
	}

//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.title_back:
//			this.finish();
//		}
//	}

	/**
	 * 滚到悬浮字母
	 */
	public void textOverlayout() {
		mHandler = new Handler();
		// 初始化首字母悬浮提示框
		mTxtOverlay = (TextView) LayoutInflater.from(this).inflate(
				R.layout.sticky_listview_pop_char, null);
		mTxtOverlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(mTxtOverlay, lp);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mTxtOverlay.setText(String.valueOf(mList.get(firstVisibleItem).pinying
				.charAt(0)));// 泡泡文字以第一个可见列表为准
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.mScrollState = scrollState;
		if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
			mHandler.removeCallbacks(mDisapearThread);
			// 提示延迟1.0s再消失
			mHandler.postDelayed(mDisapearThread, 1000);
		} else {
			mTxtOverlay.setVisibility(View.VISIBLE);
		}
	}

	private class DisapearThread implements Runnable {
		@Override
		public void run() {
			// 避免在1.5s内，用户再次拖动时提示框又执行隐藏命令。
			if (mScrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
				mTxtOverlay.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 右侧导航条点击列表滚动指定位置
	 */
	public class LetterListViewListener implements OnTouchingLetterChangedListener {
		@Override
		@SuppressLint("DefaultLocale")
		public void onTouchingLetterChanged(final String s) {
			mTxtOverlay.setText(s);// 泡泡文字以第一个可见列表为准
			mTxtOverlay.setVisibility(View.VISIBLE);
			mHandler.removeCallbacks(mDisapearThread);
			mHandler.postDelayed(mDisapearThread, 1000);

			int num = 0;
			for (int i = 0; i < mList.size(); i++) {
				if ("A".equals(s) || "a".equals(s) || "#".equals(s)) {
					// 顶部
					num = 0;
				} else if ("*".equals(s)) {
					// 底部
					num = mList.size();
				} else if (isWord(mList.get(i).pinying.substring(0, 1))
						&& (character2ASCII(mList.get(i).pinying
								.substring(0, 1).toLowerCase()) < (character2ASCII(s) + 32))) {
					num += 1; // 首先判断是字母，字母的 ASCII
								// 值小于s是，滚动位置+1；如果有10个数据小于s，就滚到10处
				}
			}
			Logg.out("num=" + num);
			mStickyList.setSelectionFromTop(num, 0);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// String personalName = map.get(mPingyingList.get(position));
		String title = mList.get(position).title;
		Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 排序
	 * 
	 * @author hongjun.yan
	 * 
	 */
	public class ChineseCharComp implements Comparator<LetterListBean> {

		@Override
		public int compare(LetterListBean o1, LetterListBean o2) {
			// String s1 = converterToPinYin(o1).substring(0, 1);
			// String s2 = converterToPinYin(o2).substring(0, 1);
			Collator myCollator = Collator
					.getInstance(java.util.Locale.ENGLISH);
			if (myCollator.compare(o1.pinying, o2.pinying) < 0) {
				return -1;
			} else if (myCollator.compare(o1.pinying, o2.pinying) > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * 判断数字
	 * 
	 * @param str
	 * @return
	 */
	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("^[0-9]*$");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判读字母
	 * 
	 * @param str
	 * @return
	 */
	public boolean isWord(String str) {
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断字母数字混合
	 * 
	 * @param str
	 * @return
	 */
	public boolean isAllWord(String str) {
		Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 把单个英文字母或者字符串转换成数字ASCII码
	 * 
	 * @param input
	 * @return
	 */
	public static int character2ASCII(String input) {
		char[] temp = input.toCharArray();
		StringBuilder builder = new StringBuilder();
		for (char each : temp) {
			builder.append((int) each);
		}
		String result = builder.toString();
		return Integer.parseInt(result);
	}

	@Override
	public void onClick(View v) {
		
	}

}
