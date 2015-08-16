package com.fourteen.outersource.activity;
/**
 * 我的技能:UserSkillHomeActivity类
 */
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.adapter.SkillPopupAdapter;
import com.fourteen.outersource.adapter.UserSkillListAdapter;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.CategoryBean;
import com.fourteen.outersource.bean.UserSkillBean;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.toast.CustomToast;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class UserSkillHomeActivity extends BaseActivity implements HeaderClickListener, SwipeRefreshLayout.OnRefreshListener, OnClickListener{
	HeaderView headerView;
	SwipeRefreshLayout refreshLayout;
	Button addSkillButton;//添加试题按钮
	ListView skillListView;//试题列表
	TextView skillNotify;//信息提示
	
	private boolean isMeasuredSkillButton = false;
	private int addSkillButtomWidth = 0;
	private ArrayList<CategoryBean> categoryList = new ArrayList<CategoryBean>();
	private ArrayList<UserSkillBean> skillList = new ArrayList<UserSkillBean>();
	UserSkillListAdapter adapter;
	
	public static final int REFRESH_MSG = 0X1111;
	public static final int RECEIVED_CATEGORIES = 0X1234;
	public static final int RECEIVED_USER_CATEGORIES = 0X1235;
	
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_user_skill_home_layout);
		initView();
	}

	public void initView(){
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.user_skill_title);
		headerView.setHeaderClickListener(this);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		refreshLayout.setColorScheme(R.color.common_blue, R.color.warning_background, R.color.common_border, R.color.common_grey);
		refreshLayout.setOnRefreshListener(this);
		addSkillButton = (Button) findViewById(R.id.add_skill);
		addSkillButton.setOnClickListener(this);
		skillNotify = (TextView) findViewById(R.id.skill_notify);
		ViewTreeObserver observer = addSkillButton.getViewTreeObserver();
		observer.addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				if(!isMeasuredSkillButton) {
					addSkillButtomWidth = addSkillButton.getMeasuredWidth();
					isMeasuredSkillButton = true;
				}
				return true;
			}
		});
		skillListView = (ListView) findViewById(R.id.user_skills);
		adapter = new UserSkillListAdapter(this, skillList);
		skillListView.setAdapter(adapter);
		skillListView.setOnItemClickListener(new MyItemCliclListener());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getUserCategories();
	}
	
	/**
	 * item点击事件
	 *MyItemCliclListener
	 */
	class MyItemCliclListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Object object = parent.getAdapter().getItem(position);
			if(object instanceof UserSkillBean) {
				UserSkillBean bean = (UserSkillBean) object;
				//进行分数判断
				if(bean.exam_score < 60) { //还可以进行考试
					Intent intent = new Intent();
					//跳转到UserSkillLeftExamActivity界面
					intent.setClass(UserSkillHomeActivity.this, UserSkillLeftExamActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("userskillbean", bean);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
				    CustomToast.makeText(UserSkillHomeActivity.this, R.string.user_pass_the_skill, CustomToast.LENGTH_SHORT).show();
				}
			}
		}
		
	}
	
	/**
	 * 获取项目类别
	 */
	public void getCategories(){
		HttpParams params = new HttpParams();
		params.put("user_id", UserBase.getUserId(this));
		HttpRequest.post("get_project_cateogry", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.what = RECEIVED_CATEGORIES;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
		});
	}
	
	/**
	 * 解析项目类别
	 * @param jsonArray
	 */
	public void parseCateogries(JSONArray jsonArray){
		categoryList.clear();
		if(jsonArray != null && jsonArray.length()>0) {
			for(int i=0; i< jsonArray.length(); i++) {
				JSONObject object = jsonArray.optJSONObject(i);
				CategoryBean bean = new CategoryBean();
				bean.category_id = object.optInt("category_id", -1);
				bean.category_name = object.optString("category_name", "");
				categoryList.add(bean);
			}
		}
		showCategories(addSkillButton);
	}
	
	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch(which){
		case HeaderView.BACK_POSITION:
			this.finish();
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.add_skill:
			getCategories();
			break;
		}
	}

	@Override
	public void onRefresh() {
		getUserCategories();
		//handler.sendEmptyMessageDelayed(REFRESH_MSG, 2000);
	}

	Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
			case REFRESH_MSG:
				refreshLayout.setRefreshing(false);
				break;
			case RECEIVED_CATEGORIES://获取项目类别
				JSONObject jsonObject = (JSONObject) msg.obj;
				if(jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					JSONArray jsonArray = jsonObject.optJSONArray("list");
					parseCateogries(jsonArray);
				}
				break;
			case RECEIVED_USER_CATEGORIES: //获取到用户已选择的项目列表
				JSONObject jsonObject1 = (JSONObject) msg.obj;
				if(jsonObject1 != null && jsonObject1.optInt("result", -1) == 0) {
					JSONArray jsonArray1= jsonObject1.optJSONArray("list");
					parseUserCategories(jsonArray1);
				}
				refreshLayout.setRefreshing(false);
				break;
			}
			return true;
		}
	});
	
	/**
	 * 解析用户已选择的类别
	 */
	public void parseUserCategories(JSONArray jsonArray) {
		ArrayList<UserSkillBean> list = new ArrayList<UserSkillBean>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0; i< jsonArray.length();i++) {
				JSONObject jsonObject = jsonArray.optJSONObject(i);
				UserSkillBean bean = new UserSkillBean();
				bean.user_id = jsonObject.optInt("user_id", -1);
				bean.user_name = jsonObject.optString("user_name", "");
				bean.category_id = jsonObject.optInt("category_id", -1);
				bean.category_name = jsonObject.optString("category_name", "");
				bean.addtime = jsonObject.optString("addtime", "");
				bean.exam_time = jsonObject.optString("exam_time", "");
				bean.exam_score = jsonObject.optInt("exam_score", 0);
				list.add(bean);
			}
			skillNotify.setVisibility(View.GONE);
		} else {
			skillNotify.setVisibility(View.VISIBLE);
		}
		skillList.clear();
		skillList.addAll(list);
		adapter.notifyDataSetChanged();
	}
	
	
	/**
	 * 显示类别弹窗
	 * @param view
	 */
	public void showCategories(View view) {
		if(!this.isFinishing()) {
			final PopupWindow popupWindow = new PopupWindow(this);
			popupWindow.setWidth(240);
			popupWindow.setHeight(200);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			LayoutInflater inflater = LayoutInflater.from(this);
			View viewContent = inflater.inflate(R.layout.ot_user_skill_popup_layout, null);
			ListView listView = (ListView) viewContent.findViewById(R.id.skill_popup);
			SkillPopupAdapter adapter = new SkillPopupAdapter(this, categoryList);
			listView.setAdapter(adapter);
			popupWindow.setContentView(viewContent);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setFocusable(true);
			popupWindow.showAsDropDown(view, -(250 - addSkillButtomWidth), 5);

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView, View item, int position, long id) {
					Object object = adapterView.getAdapter().getItem(position);
					if (object instanceof CategoryBean) {
						CategoryBean bean = (CategoryBean) object;
						popupWindow.dismiss();
						submitCategory(bean);
					}
				}
			});
		}
	}
	
	/**
	 * 获取用户已选择的类别
	 */
	public void getUserCategories(){
		HttpParams params = new HttpParams();
		params.put("user_id", UserBase.getUserId(this));
		HttpRequest.post("get_user_categories", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.what = RECEIVED_USER_CATEGORIES;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
		});
	}

	/**
	 * 提交选择的类别
	 */
	public void submitCategory(CategoryBean bean){
		HttpParams params = new HttpParams();
		params.put("user_id", UserBase.getUserId(this));
		params.put("category_id", bean.category_id);
		HttpRequest.post("user_choice_category", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				Message message = new Message();
				message.what = RECEIVED_USER_CATEGORIES;
				message.obj = jsonObject;
				handler.sendMessage(message);
			}
		});
	}
}
