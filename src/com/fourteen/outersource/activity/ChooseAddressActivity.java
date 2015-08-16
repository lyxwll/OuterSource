package com.fourteen.outersource.activity;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.stickylistheaders.LetterListBean;
import com.fourteen.outersource.stickylistheaders.QuickLocationRightTool;
import com.fourteen.outersource.stickylistheaders.QuickLocationRightTool.OnTouchingLetterChangedListener;
import com.fourteen.outersource.stickylistheaders.StickyListHeadersListView;
import com.fourteen.outersource.stickylistheaders.StickyListViewAdapter;
import com.fourteen.outersource.utils.GetPinyin;
import com.fourteen.outersource.utils.Logg;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;
/*
 * 选择地址:ChooseAddressActivity类
 */
public class ChooseAddressActivity extends BaseActivity implements OnScrollListener, OnItemClickListener, HeaderClickListener {
	
	private Context mContext;
	private StickyListHeadersListView mStickyList; //左侧的列表
	private LinearLayout rightPanel; //右侧的容器
	private QuickLocationRightTool mLetterListView; // 右边的字母导航栏
	private Handler mHandler;
	private TextView mTxtOverlay;
	private WindowManager mWindowManager;
	private int mScrollState;
	private DisapearThread mDisapearThread;
	
	private HeaderView headerView;
	private StickyListViewAdapter adapter;

	private ArrayList<LetterListBean> mList = new ArrayList<LetterListBean>(); // 数据列表
	private ArrayList<String> mRightCharacterList = new ArrayList<String>(); // 右侧字母表
	
	private static final int GET_PROVINCE_DATA = 0X123;
	private static final int GET_CITY_DATA = 0X124;
	private static final int GET_COUNTY_DATA = 0X125;
	private static final int GET_TOWN_DATA = 0X126;
	
	private LetterListBean mChoicedProvinceBean;//省份
	private LetterListBean mChoicedCityeBean;//城市
	private LetterListBean mChoicedCountyBean;//城镇
	private LetterListBean mChoicedTownBean;//乡村
	
	//当前进行到哪一步了
	private int mStep = 0;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getBaseContext();
		setContentView(R.layout.ot_choose_address_layout);
		initView(); //初始化控件
		loadingDataForProvince(); //加载全国省份
	}
	
	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.choose_address_title);
		headerView.setHeaderClickListener(this);
		
		mStickyList = (StickyListHeadersListView) findViewById(R.id.list);
		textOverlayout();
		// 初始化ListAdapter
		mStickyList = (StickyListHeadersListView) findViewById(R.id.list);
		adapter = new StickyListViewAdapter(mContext, mList);
		mStickyList.setOnItemClickListener(this);
		mStickyList.setOnScrollListener(this);
		mStickyList.setAdapter(adapter);
		mDisapearThread = new DisapearThread();
		
		rightPanel = (LinearLayout) findViewById(R.id.rightPanel);
	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case GET_PROVINCE_DATA://获取省份数据
				headerView.setTitle(R.string.choose_province);
				break;
			case GET_CITY_DATA://获取城市数据
				headerView.setTitle(R.string.choose_city);
				break;
			case GET_COUNTY_DATA:
				headerView.setTitle(R.string.choose_county);
				break;
			case GET_TOWN_DATA:
				headerView.setTitle(R.string.choose_town);
				break;
			}
			ArrayList<LetterListBean> list = new ArrayList<LetterListBean>();
			JSONObject jsonObject = (JSONObject) msg.obj;
			if(jsonObject.optInt("result", -1) == 0) {
				JSONArray jsonArray = jsonObject.optJSONArray("list");
				if(jsonArray != null) {
					for(int i=0; i< jsonArray.length(); i++) {
						JSONObject object = jsonArray.optJSONObject(i);
						LetterListBean bean = new LetterListBean();
						bean.id = object.optInt("ID", -1);
						bean.title = object.optString("Name", "");
						bean.pinying = GetPinyin.getPinYin(bean.title);
						list.add(bean);
					}
					
					/*initData();*/
					Collections.sort(list, new ChineseCharComp());
					mRightCharacterList.clear();
					mRightCharacterList.add("#");
					for (int i = 0; i < list.size(); i++) {
						if (!mRightCharacterList.contains(list.get(i).pinying.substring(0,1).toUpperCase())) {
							mRightCharacterList.add(list.get(i).pinying.substring(0, 1).toUpperCase());
						}
					}
					
					mRightCharacterList.add("*");
					mList.clear();
					mList.addAll(list);
					adapter.notifyDataSetChanged();
					mStickyList.setSelection(0);
					mLetterListView = new QuickLocationRightTool(ChooseAddressActivity.this);
					mLetterListView.setB(mRightCharacterList);
					mLetterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
					rightPanel.removeAllViews();
					rightPanel.addView(mLetterListView);
				}
			}
		}
	};
	
	/**
	 * 获取全国省份列表
	 */
	public void loadingDataForProvince(){
		mStep = 0;
		HttpParams params = new HttpParams();
		HttpRequest.post("get_province", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.what = GET_PROVINCE_DATA;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
		});
	}
	
	/**
	 * 通过provinceId获取省份的城市列表
	 * @param provinceId
	 */
	public void loadingDataForCity(int provinceId){
		mStep = 1;
		HttpParams params = new HttpParams();
		params.put("province_id", provinceId);
		HttpRequest.post("get_city", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.what = GET_CITY_DATA;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
		});
	}

	/**
	 * 通过cityId获取城市的城镇列表
	 * @param cityId
	 */
	public void loadingDataForCounty(int cityId){
		mStep = 2;
		HttpParams params = new HttpParams();
		params.put("city_id", cityId);
		HttpRequest.post("get_county", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.what = GET_COUNTY_DATA;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
		});
	}
	/**
	 * 通过countyId获取城镇的乡村列表
	 * @param countyId
	 */
	public void loadingDataForTown(int countyId){
		mStep = 3;
		HttpParams params = new HttpParams();
		params.put("county_id", countyId);
		HttpRequest.post("get_town", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.what = GET_TOWN_DATA;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
		});
	}

	//item项的点击事件
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch(mStep) {
		case 0:
			mChoicedProvinceBean = (LetterListBean) parent.getAdapter().getItem(position);
			loadingDataForCity(mChoicedProvinceBean.id);
			break;
		case 1:
			mChoicedCityeBean =  (LetterListBean) parent.getAdapter().getItem(position);
			loadingDataForCounty(mChoicedCityeBean.id);
			break;
		case 2:
			mChoicedCountyBean =  (LetterListBean) parent.getAdapter().getItem(position);
			loadingDataForTown(mChoicedCountyBean.id);
			break;
		case 3:
			mChoicedTownBean =  (LetterListBean) parent.getAdapter().getItem(position);
			//选择完成，准备返回
			String address = mChoicedProvinceBean.title + mChoicedCityeBean.title + mChoicedCountyBean.title + mChoicedTownBean.title;
			Intent intent = new Intent();
			intent.putExtra("address", address);
			setResult(RESULT_OK, intent);
			this.finish();
			break;
		}
	}

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
		if(mList != null && mList.size() > firstVisibleItem) {
			mTxtOverlay.setText(String.valueOf(mList.get(firstVisibleItem).pinying.charAt(0)));// 泡泡文字以第一个可见列表为准
		}
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

	/**
	 * 排序
	 * @author hongjun.yan
	 * 
	 */
	public class ChineseCharComp implements Comparator<LetterListBean> {

		@Override
		public int compare(LetterListBean o1, LetterListBean o2) {
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
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onHeaderClick(View v, int chich) {
		switch(chich) {
		case HeaderView.BACK_POSITION:
			this.finish();
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mTxtOverlay.setVisibility(View.INVISIBLE);
		mStickyList.setVisibility(View.INVISIBLE);
		mWindowManager.removeView(mTxtOverlay);
	}
}
