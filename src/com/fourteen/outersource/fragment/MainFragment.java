package com.fourteen.outersource.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.fourteen.outersource.R;
import com.fourteen.outersource.adapter.MainAdapter;
import com.fourteen.outersource.bean.CategoryBean;
import com.fourteen.outersource.bean.ProjectBean;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.project.activity.ProjectDetailsActivity;
import com.fourteen.outersource.view.LMListView;
import com.fourteen.outersource.view.LMListView.LMListViewListener;

public class MainFragment extends Fragment implements LMListViewListener, OnItemClickListener{
	View view;
	LMListView mListView;
	CategoryBean bean;
	private MainAdapter adapter;

	private static final int RECEIVED_PROJECTS = 0X234;
	private ArrayList<ProjectBean> projectList = new ArrayList<ProjectBean>();
	
	//集合中最大可包含的页码数量
	//集合中最大可包含的数据量为 MAX_CONTAINS_PAGE * pageSize
	private static final int MAX_CONTAINS_PAGE = 20;
	int page = 0;
	private int pageCount =0;
	private boolean isNew = true;
	private int containgPage = 0;
	private int action = -1;
	private int pageSize = 5;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bean = (CategoryBean) getArguments().getSerializable("categoryBean");
		getProjects();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ot_main_fragment, null);
		mListView = (LMListView) view.findViewById(R.id.project_list);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(true);
		if(getActivity() != null) {
			adapter = new MainAdapter(getActivity(),bean, projectList);
		}
		mListView.setAdapter(adapter);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);
		return view;
	}
	
	/**
	 * 获取项目信息
	 */
	public void getProjects(){
		HttpParams params = new HttpParams();
		params.put("category_id", bean.category_id);
		params.put("page", page);
		HttpRequest.post("get_projects", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				if(jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					pageCount = jsonObject.optInt("pageCount", 0);
					pageSize = jsonObject.optInt("pageSize", 10);
					JSONArray jsonArray =  jsonObject.optJSONArray("list");
					ArrayList<ProjectBean> list = new ArrayList<ProjectBean>();
					if(jsonArray != null && jsonArray.length() > 0) {
						for(int i=0;i < jsonArray.length(); i++) {
							JSONObject object1 = jsonArray.optJSONObject(i);
							ProjectBean bean1 = new ProjectBean();
							bean1.pro_id = object1.optInt("pro_id", -1);
							bean1.user_id = object1.optInt("user_id", 0);
							bean1.pro_category_id = object1.optInt("pro_category_id", -1);
							bean1.pro_name = object1.optString("pro_name", "");
							bean1.pro_price = object1.optDouble("pro_price", 0);
							bean1.pro_desc = object1.optString("pro_desc", "");
							bean1.pro_start_time = object1.optString("pro_start_time", "");
							bean1.pro_end_time = object1.optString("pro_end_time", "");
							bean1.pro_directory = object1.optString("pro_directory", "");
							bean1.pro_date = object1.optString("pro_date", "");
							bean1.pro_gress = object1.optInt("pro_gress", 0);
							bean1.pro_close = object1.optInt("pro_close", 0);
							list.add(bean1);
						}
					}
					Message message = new Message();
					message.what = RECEIVED_PROJECTS;
					message.obj = list;
					handler.sendMessage(message);
				}
			}
		});
	}
	
	Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
			case RECEIVED_PROJECTS:
				if(MainFragment.this.getActivity() != null && !MainFragment.this.getActivity().isFinishing() && mListView != null) {
					mListView.stopRefresh();
					mListView.stopLoadMore();
					/*Calendar calendar = Calendar.getInstance();
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault());//可以方便地修改日期格式
					String time = dateFormat.format(calendar.getTime());
					mListView.setRefreshTime(time);*/
					@SuppressWarnings("unchecked")
					ArrayList<ProjectBean> list = (ArrayList<ProjectBean>) msg.obj;
					if(isNew) { //重新加载
						projectList.clear();
						projectList.addAll(list);
						adapter.notifyDataSetChanged();
						containgPage = 1;
					} else {
						int count = 0;
						if(action == 0) { //加载上一页
							if(containgPage >= MAX_CONTAINS_PAGE) {
								//移走最后一页
								int size = projectList.size();
								if(projectList.size() > pageSize) {
									for(int i=0; i< pageSize; i++) {
										projectList.remove(size-1);
										size --;
										count++;
									}
								}
								//containgPage; //包含的页码不变
							} else {
								containgPage ++;
							}
							projectList.addAll(0, list); //将前一页加载到最前面
							adapter.notifyDataSetChanged();
							if(count > 0) {
								mListView.setSelectionFromTop(count, 0);
							}
						} else if(action == 1) { //加载下一页
							if(containgPage >=MAX_CONTAINS_PAGE) {
								//移走最前一页
								if(projectList.size() > pageSize) {
									for(int i=0; i< pageSize; i++) {
										projectList.remove(0);
										count ++;
									}
								}
								//containgPage; //包含的页码不变
							} else {
								containgPage++; //未超过最大页码，所包含的页数加1
							}
							projectList.addAll(list);
							adapter.notifyDataSetChanged();
							if(count > 0) {
								mListView.setSelectionFromTop(projectList.size()- list.size() - count, 0);
							}
						}
					}
				}
				break;
			}
			return true;
		}
	});
	
	//item点击事件
	@Override
	public void onItemClick(AdapterView<?> adapaterView, View view, int position, long id) {
		Object object = adapaterView.getAdapter().getItem(position);
		if(object instanceof ProjectBean && getActivity() != null) {
			ProjectBean bean = (ProjectBean) object;
			Intent intent = new Intent();
			//传值到项目详情:ProjectDetailsActivity类
			intent.setClass(getActivity(), ProjectDetailsActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("projectBean", bean);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
	
       //下拉刷新
	@Override
	public void onLoadLast() {
		action = 0;
		if(page > 0) {//加载前一页
			if(containgPage  >= MAX_CONTAINS_PAGE) {
				isNew = false;
				page --;
				getProjects();
			} else {
				mListView.setIsFirstPage();
				mListView.stopRefresh();
			}
		} else if(page == 0) {
			//isNew  = true;
			//getProjects(); //重新加载
			mListView.setIsFirstPage();
			mListView.stopRefresh();
		} 
	}
      //加载更多
	@Override
	public void onLoadMore() {
		action = 1;
		isNew = false; //并不是重新加载
		if(page  < pageCount -1) {
			page ++;
			getProjects();
		} else {
			mListView.stopLoadMore();
			mListView.setNoMoreData();
		}
	}
	
	
}
