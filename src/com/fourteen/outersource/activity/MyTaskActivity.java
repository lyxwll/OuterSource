package com.fourteen.outersource.activity;
/**
 * 我的任务:MyTaskActivity类
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.adapter.MyTaskAdapter;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.MyTaskBean;
import com.fourteen.outersource.bean.ProjectModelBean;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.project.activity.ProjectModuleDetailsActivity;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;
import com.fourteen.outersource.view.XListView;
import com.fourteen.outersource.view.XListView.IXListViewListener;

public class MyTaskActivity extends BaseActivity implements HeaderClickListener, IXListViewListener, OnItemClickListener {
	HeaderView headerView;
	XListView XlistView;
	MyTaskBean bean;
	ProjectModelBean projectModelBean;
	int page = 0;
	private int pageCount = 0;
	private MyTaskAdapter adapter;
	//我的任务列表
	private ArrayList<MyTaskBean> mytaskList = new ArrayList<MyTaskBean>();

	public static final int RECEIVED_MYTASK = 123;
	private String refreshTag = "myTaskRefreshTag";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		//接收Activity传过来的值
		if(getIntent() != null && getIntent().hasExtra("projectModelBean")){
			projectModelBean = (ProjectModelBean) getIntent().getSerializableExtra("projectModelBean");
		}
		setContentView(R.layout.ot_my_task_layout);
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.my_task);
		headerView.setHeaderClickListener(this);
		XlistView = (XListView) findViewById(R.id.ot_my_task_Xlistview);
		XlistView.setRefreshTag(refreshTag);
		XlistView.setPullLoadEnable(true);
		XlistView.setPullRefreshEnable(true);
		adapter = new MyTaskAdapter(getApplication(), mytaskList);
		XlistView.setAdapter(adapter);
		XlistView.setXListViewListener(this);
		XlistView.setOnItemClickListener(this);
		getProjects();
	}
	

	/**
	 * 获取用户承接的项目模块
	 */
	public void getProjects() {
		HttpParams params = new HttpParams();
		params.put("user_id", UserBase.getUserId(this));
		params.put("page", page);
		HttpRequest.post("get_user_model", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						System.out.println("返回:" + jsonObject);
						if (jsonObject != null
								&& jsonObject.optInt("result", -1) == 0) {
							pageCount = jsonObject.optInt("pageCount", 0);
							JSONArray jsonArray = jsonObject
									.optJSONArray("list");
							ArrayList<MyTaskBean> list = new ArrayList<MyTaskBean>();
							if (jsonArray != null && jsonArray.length() > 0) {
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object1 = jsonArray
											.optJSONObject(i);
									MyTaskBean bean1 = new MyTaskBean();
									bean1.user_id = object1.optInt("user_id",
											-1);
									bean1.user_name = object1.optString(
											"user_name", "");
									bean1.user_phone = object1.optString(
											"user_phone", "");
									bean1.user_email = object1.optString(
											"user_email", "");
									bean1.pro_model_id = object1.optInt(
											"pro_model_id", -1);
									bean1.pro_id = object1.optInt("pro_id", -1);
									bean1.pro_model_name = object1.optString(
											"pro_model_name", "");
									bean1.pro_model_fee = object1.optDouble(
											"pro_model_fee", 0);
									bean1.pro_model_start = object1.optString(
											"pro_model_start", "");
									bean1.pro_model_end = object1.optString(
											"pro_model_end", "");
									bean1.pro_model_desc = object1.optString(
											"pro_model_desc", "");
									bean1.pro_model_directory = object1
											.optString("pro_model_directory",
													"");
									bean1.is_completed = object1.optInt(
											"is_completed", 0);
									bean1.apply_user_id = object1.optInt(
											"apply_user_id", 0);
									bean1.progress = object1.optInt("progress",
											0);
									bean1.model_weight = object1.optInt(
											"model_weight", 0);
									list.add(bean1);
								}
							}
							Message message = new Message();
							message.what = RECEIVED_MYTASK;
							message.obj = list;
							handler.sendMessage(message);
						}
					}
				});
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RECEIVED_MYTASK:
				XlistView.stopRefresh();
				XlistView.stopLoadMore();
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault());//可以方便地修改日期格式
				String time = dateFormat.format(calendar.getTime());
				XlistView.setRefreshTime(time);
				@SuppressWarnings("unchecked")
				ArrayList<MyTaskBean> list = (ArrayList<MyTaskBean>) msg.obj;
				if (page == 0) {
					mytaskList.clear();
				}
				mytaskList.addAll(list);
				adapter.notifyDataSetChanged();
				break;
			}
		};
	};

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
	public void onRefresh() {
		page = 0;
		getProjects();
	}

	@Override
	public void onLoadMore() {
		if (page < pageCount - 1) {
			page++;
			getProjects();
		} else {
			XlistView.stopLoadMore();
			XlistView.setNoMoreData();
		}
	}

	//模块列表Item项的点击事件
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		Object object = adapterView.getAdapter().getItem(position);
		if (object instanceof MyTaskBean) {
			MyTaskBean bean=(MyTaskBean) object;
			Intent intent=new Intent();
			Bundle bundle=new Bundle();
			//传值到跳转页面
			bundle.putSerializable("bean", bean);
			intent.putExtras(bundle);
			intent.setClass(getApplication(), ProjectModuleDetailsActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getProjects();
	}
	
}
