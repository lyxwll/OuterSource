package com.fourteen.outersource.activity;
/**
 * 主界面:HomeActivity类
 */
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.CategoryBean;
import com.fourteen.outersource.bean.ProjectBean;
import com.fourteen.outersource.fragment.MainFragment;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.project.activity.GoagoActivity;
import com.fourteen.outersource.project.activity.MyProjectsActivity;
import com.fourteen.outersource.project.activity.ProjectPublishActivity;
import com.fourteen.outersource.view.BottomClickListener;
import com.fourteen.outersource.view.BottomView;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;
import com.fourteen.outersource.view.OtClassifyListenter;
import com.fourteen.outersource.view.OtClassifyView;

public class HomeActivity extends BaseActivity implements HeaderClickListener,
		BottomClickListener, OtClassifyListenter {
	HeaderView headerView;//顶部
	BottomView bottomView;//底部
	ProjectBean projectBean;
	OtClassifyView otClassifyView;//项目类别滑动视图
	ViewPager viewPager;
	private static final int RECEIVED_CATEGORIES = 0X123;
	ArrayList<Fragment> fragments = new ArrayList<Fragment>();//ArrayList是List的实现，由数组支持
	ArrayList<CategoryBean> list = new ArrayList<CategoryBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ot_home_layout);
		initView();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.app_name);
		headerView.showOpt();
		headerView.getOptButton().setBackgroundResource(
				R.drawable.ot_user_center_icon);
		headerView.setOptText("");
		headerView.setHeaderClickListener(this);
		bottomView = (BottomView) findViewById(R.id.bottom_view);
		bottomView.setOnBottomCliclListener(this);
		otClassifyView = (OtClassifyView) findViewById(R.id.ot_classify);
		otClassifyView.setItemSelectedListener(this);
		getCategories();
		viewPager = (ViewPager) findViewById(R.id.ot_vPager);
		viewPager.setOnPageChangeListener(new PageChangeListener());
	}

	/**
	 * 页面改变监听事件
	 *PageChangeListener
	 */
	class PageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			otClassifyView.setSelectedItem(position);
		}

	}

	class MyFragmentPagetAdapter extends FragmentPagerAdapter {
		ArrayList<Fragment> fragments;

		public MyFragmentPagetAdapter(FragmentManager fm) {
			super(fm);
		}

		public MyFragmentPagetAdapter(FragmentManager fm,
				ArrayList<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = fragments.get(position);
			return fragment;
		}

		@Override
		public int getCount() {
			return list.size();
		}

	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch (which) {
		case HeaderView.OPT_POSITION://个人中心
			Intent intent = new Intent();
			intent.setClass(this, UserHomeActivity.class);
			startActivity(intent);
			break;
		case HeaderView.BACK_POSITION://返回
			Intent intent2 = new Intent();
			intent2.setAction(BaseActivity.EXIT_ACTION);
			sendBroadcast(intent2);
			break;
		}
	}

	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case RECEIVED_CATEGORIES:
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					JSONArray jsonArray = jsonObject.optJSONArray("list");
					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.optJSONObject(i);
							CategoryBean bean = new CategoryBean();
							bean.category_id = object.optInt("category_id", -1);
							bean.category_name = object.optString("category_name", "");
							bean.category_url = object.optString("category_url", "");
							list.add(bean);
						}
						otClassifyView.setCategoryList(list);
					}
				}
				for (int i = 0; i < list.size(); i++) {
					Fragment fragment = new MainFragment();
					//bundle传值
					Bundle bundle = new Bundle();
					bundle.putSerializable("categoryBean", list.get(i));
					bundle.putString("refreshTag", "MainFragment"+ i);
					fragment.setArguments(bundle);
					fragments.add(fragment);
				}
				viewPager.setAdapter(new MyFragmentPagetAdapter(
						getSupportFragmentManager(), fragments));
				viewPager.setCurrentItem(0, true);
				break;
			}
			return true;
		}
	});

	/**
	 * 获取项目类别
	 */
	public void getCategories() {
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

	//底部点击事件
	@Override
	public void onBottomClick(View v, int which) {
		Intent intent = null;
		switch (which) {
		case BottomView.BOTTOM_POSITION_0://我的任务
			intent = new Intent();
			intent.setClass(this, MyTaskActivity.class);
			break;
		case BottomView.BOTTOM_POSITION_1://我的项目
			intent = new Intent(this, GoagoActivity.class);
			intent = new Intent(this, MyProjectsActivity.class);
			break;
		case BottomView.BOTTOM_POSITION_2://发布项目
			intent = new Intent();
			intent.setClass(this, ProjectPublishActivity.class);
			break;
		case BottomView.BOTTOM_POSITION_3://我的技能
			intent = new Intent();
			intent.setClass(this, UserSkillHomeActivity.class);
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}

	@Override
	public void otclassifylistener(View view, int position) {
		viewPager.setCurrentItem(position, true);
	}

}
