package com.fourteen.outersource.project.activity;

/**
 * 我的项目:MyProjectsActivity类
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.adapter.ProjectAdapter;
import com.fourteen.outersource.adapter.SkillPopupAdapter;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.CategoryBean;
import com.fourteen.outersource.bean.ProjectBean;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;
import com.fourteen.outersource.view.XListView;
import com.fourteen.outersource.view.XListView.IXListViewListener;

public class MyProjectsActivity extends BaseActivity implements
		HeaderClickListener, OnClickListener, IXListViewListener,
		OnItemClickListener {
	private HeaderView headerView;
	private static final int RECEIVED_CATEGORIES = 0X1234;
	private static final int RECEIVED_PROJECTS = 0X1235;
	private ArrayList<CategoryBean> categoryList = new ArrayList<CategoryBean>();
	private ArrayList<ProjectBean> projectList = new ArrayList<ProjectBean>();
	private CategoryBean bean; // 选中的类别bean
	private Button showCategories;
	private XListView mListView;
	private ProjectAdapter adapter;

	private int lastCategory = -1; // 上次的类别
	private int currentCategory = -1; // 这次的类别
	private int page = 0; // 页码
	private int pageCount; // 总页数
	private boolean isRefresh = false;
	private boolean isLoadMore = false;
	private String refreshTag = "myProjectRefreshTag";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getAllCategories();
		getProjects();
		setContentView(R.layout.ot_my_projects_layout);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.my_projects);
		headerView.setHeaderClickListener(this);
		showCategories = (Button) findViewById(R.id.show_categories);
		showCategories.setOnClickListener(this);
		mListView = (XListView) findViewById(R.id.project_list);
		mListView.setRefreshTag(refreshTag);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(true);
		// mListView.removeFooterView(null);
		// mListView.removeHeaderView(null);
		adapter = new ProjectAdapter(this, projectList);
		mListView.setAdapter(adapter);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch (which) {
		case HeaderView.BACK_POSITION:
			this.finish();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.show_categories:
			showCategories(showCategories);
			break;
		}
	}

	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case RECEIVED_CATEGORIES:// 接收项目类别
				categoryList.clear();
				CategoryBean bean = new CategoryBean();
				bean.category_id = -1;
				bean.category_name = "ALL";
				categoryList.add(bean);
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					JSONArray jsonArray = jsonObject.optJSONArray("list");
					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.optJSONObject(i);
							CategoryBean bean1 = new CategoryBean();
							bean1.category_id = object
									.optInt("category_id", -1);
							bean1.category_name = object.optString(
									"category_name", "");
							categoryList.add(bean1);
						}
					}
				}
				break;
			case RECEIVED_PROJECTS:// 接收项目列表
				mListView.stopRefresh();
				mListView.stopLoadMore();
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"MM-dd HH:mm:ss", Locale.getDefault());// 可以方便地修改日期格式
				String time = dateFormat.format(calendar.getTime());
				mListView.setRefreshTime(time);
				JSONObject jsonObject1 = (JSONObject) msg.obj;
				ArrayList<ProjectBean> list = new ArrayList<ProjectBean>();
				if (jsonObject1 != null
						&& jsonObject1.optInt("result", -1) == 0) {
					pageCount = jsonObject1.optInt("pageCount", 0);
					JSONArray jsonArray1 = jsonObject1.optJSONArray("list");
					if (jsonArray1 != null && jsonArray1.length() > 0) {
						for (int i = 0; i < jsonArray1.length(); i++) {
							JSONObject object1 = jsonArray1.optJSONObject(i);
							ProjectBean bean1 = new ProjectBean();
							bean1.pro_id = object1.optInt("pro_id", -1);
							bean1.user_id = object1.optInt("user_id", 0);
							bean1.pro_category_id = object1.optInt(
									"pro_category_id", -1);
							bean1.pro_name = object1.optString("pro_name", "");
							bean1.pro_price = object1.optDouble("pro_price", 0);
							bean1.pro_desc = object1.optString("pro_desc", "");
							bean1.pro_start_time = object1.optString(
									"pro_start_time", "");
							bean1.pro_end_time = object1.optString(
									"pro_end_time", "");
							bean1.pro_directory = object1.optString(
									"pro_directory", "");
							bean1.pro_date = object1.optString("pro_date", "");
							bean1.pro_gress = object1.optInt("pro_gress", 0);
							bean1.pro_close = object1.optInt("pro_close", 0);
							list.add(bean1);
						}
					}
					if (currentCategory != lastCategory) {
						projectList.clear();
						projectList.addAll(list);
						adapter.notifyDataSetChanged();
					} else {
						if (isRefresh == true && isLoadMore == false) {
							// isRefresh = false;
							projectList.clear();
							projectList.addAll(list);
							adapter.notifyDataSetChanged();
						} else if (isLoadMore == true && isRefresh == false) {
							// isLoadMore = false;
							projectList.addAll(list);
							adapter.notifyDataSetChanged();
						} else if (isRefresh == false && isLoadMore == false) {
							projectList.clear();
							projectList.addAll(list);
							adapter.notifyDataSetChanged();
						} else {

						}
					}
				}
				lastCategory = currentCategory;
				break;
			}
			return true;
		}
	});

	/**
	 * 获取所有的项目类别列表
	 */
	public void getAllCategories() {
		HttpParams params = new HttpParams();
		params.put("user_id", UserBase.getUserId(this));
		HttpRequest.post("get_project_categories", params,
				new AsyncHttpResponseHandler() {
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
	 * 获取用户发布项目需求列表
	 */
	public void getProjects() {
		HttpParams params = new HttpParams();
		params.put("user_id", UserBase.getUserId(this));
		params.put("category_id", currentCategory);
		params.put("page", page);
		HttpRequest.post("get_user_projects", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						Message message = new Message();
						message.what = RECEIVED_PROJECTS;
						message.obj = jsonObject;
						handler.sendMessage(message);
					}
				});
	}

	/**
	 * 显示项目类别弹窗
	 */
	public void showCategories(final Button view) {
		final PopupWindow popupWindow = new PopupWindow(this);
		popupWindow.setWidth(400);
		popupWindow.setHeight(300);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		LayoutInflater inflater = LayoutInflater.from(this);
		View viewContent = inflater.inflate(
				R.layout.ot_user_skill_popup_layout, null);
		ListView listView = (ListView) viewContent
				.findViewById(R.id.skill_popup);
		SkillPopupAdapter adapter = new SkillPopupAdapter(this, categoryList);
		listView.setAdapter(adapter);
		popupWindow.setContentView(viewContent);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.showAsDropDown(view, 0, 4);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View item,
					int position, long id) {
				Object object = adapterView.getAdapter().getItem(position);
				if (object instanceof CategoryBean) {
					bean = (CategoryBean) object;
					view.setText(bean.category_name);
					popupWindow.dismiss();
					lastCategory = currentCategory;
					currentCategory = bean.category_id;
					page = 0; // 页号从0开始
					isRefresh = false;
					isLoadMore = false;
					getProjects();
				}
			}
		});
	}

	/**
	 * 刷新列表
	 */
	@Override
	public void onRefresh() {
		page = 0;
		isRefresh = true;
		isLoadMore = false;
		getProjects();
	}

	/**
	 * 加载列表
	 */
	@Override
	public void onLoadMore() {
		if (page < pageCount - 1) {
			isLoadMore = true;
			isRefresh = false;
			page++;
			getProjects();
		} else {
			mListView.stopLoadMore();
			mListView.setNoMoreData();
		}
	}

	/**
	 * 项目列表点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object object = parent.getAdapter().getItem(position);
		if (object instanceof ProjectBean) {
			ProjectBean bean = (ProjectBean) object;
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			// Activity之间的传值
			bundle.putSerializable("projectBean", bean);
			intent.putExtras(bundle);
			// 界面跳转到项目详情界面
			intent.setClass(this, GoagoActivity.class);
			startActivity(intent);
		}
	}

}
