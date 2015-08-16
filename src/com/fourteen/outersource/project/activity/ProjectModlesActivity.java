package com.fourteen.outersource.project.activity;
/**
 * 项目模块类
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.activity.MyTaskActivity;
import com.fourteen.outersource.adapter.ModelsAdapter;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.ProjectBean;
import com.fourteen.outersource.bean.ProjectModelBean;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.dialog.CustomAlertDialog.Builder;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;
import com.fourteen.outersource.view.XListView;
import com.fourteen.outersource.view.XListView.IXListViewListener;

public class ProjectModlesActivity extends BaseActivity implements HeaderClickListener,IXListViewListener,OnItemClickListener{

	private HeaderView headerView;
	private XListView xListView;
	ModelsAdapter adapter;
	ProjectBean projectBean;
	ProjectModelBean projectModelBean;
	ImageView imageView;
	
	int page = 0;//页
	int pageCount = 0;//页数
	//项目模块列表
	private ArrayList<ProjectModelBean> proModelList = new ArrayList<ProjectModelBean>();
	
	private static final int GET_MODLES_LIST = 0x1234;
	private static final int APPLY_MODEL = 0x1235;
	
	private String refreshTag = "projectModelRefreshTag";
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if(getIntent() != null && getIntent().hasExtra("projectBean")){
			projectBean = (ProjectBean) getIntent().getSerializableExtra("projectBean");
		}
		setContentView(R.layout.ot_pro_modles_list_layout);
		initView();
		getProjectModels();
	}
	
	public void initView(){
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.project_modles);
		headerView.setHeaderClickListener(this);
		xListView = (XListView) findViewById(R.id.project_list);
		xListView.setRefreshTag(refreshTag);
		xListView.setPullLoadEnable(true);
		xListView.setPullRefreshEnable(true);
		adapter = new ModelsAdapter(this,proModelList);
		xListView.setAdapter(adapter);
		xListView.setXListViewListener(this);
		xListView.setOnItemClickListener(this);
		imageView = (ImageView) findViewById(R.id.ot_pro_apply);
	}
	
	/**
	 * 获取项目模块  方法
	 */
	public void getProjectModels(){
		HttpParams params = new HttpParams();
		params.put("pro_id", projectBean.pro_id);
		HttpRequest.post("get_project_models", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				if(jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					pageCount = jsonObject.optInt("pageCount", 0);
					JSONArray jsonArray =  jsonObject.optJSONArray("list");
					ArrayList<ProjectModelBean> list = new ArrayList<ProjectModelBean>();
					if(jsonArray != null && jsonArray.length() > 0) {
						for(int i=0;i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.optJSONObject(i);
							ProjectModelBean bean = new ProjectModelBean();
							bean.pro_model_id = object.optInt("pro_model_id",-1);
							bean.pro_id = object.optInt("pro_id",-1);
							bean.pro_model_name = object.optString("pro_model_name","");
							bean.pro_model_fee = object.optDouble("pro_model_fee",0);
							bean.pro_model_start = object.optString("pro_model_start","");
							bean.pro_model_end = object.optString("pro_model_end","");
							bean.pro_model_desc = object.optString("pro_model_desc","");
							bean.pro_model_directory = object.optString("pro_model_directory","");
							bean.is_completed = object.optInt("is_completed",-1);
							bean.apply_user_id = object.optInt("apply_user_id",0);
							bean.progress = object.optInt("progress",0);
							bean.model_weight = object.optInt("model_weight",0);
							list.add(bean);
						}
					}
					Message message = new Message();
					message.obj = list;
					message.what = GET_MODLES_LIST;
					handler.sendMessage(message);
				}
			}
		});
	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_MODLES_LIST://获取项目模块列表
				xListView.stopRefresh();
				xListView.stopLoadMore();
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault());//可以方便地修改日期格式
				String time = dateFormat.format(calendar.getTime());
				xListView.setRefreshTime(time);
				@SuppressWarnings("unchecked")
				ArrayList<ProjectModelBean> list = (ArrayList<ProjectModelBean>) msg.obj;
				if(page == 0) {
					proModelList.clear();
				}
				proModelList.addAll(list);
				adapter.notifyDataSetChanged();
				break;
			case APPLY_MODEL://承接项目模块
				JSONObject jsonObject = (JSONObject) msg.obj;
				if(jsonObject != null && jsonObject.optInt("result",-1) == 0){
					projectModelBean = parseBean(jsonObject);
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("projectModelBean", projectModelBean);
					intent.putExtras(bundle);
					intent.setClass(ProjectModlesActivity.this, MyTaskActivity.class);
					startActivity(intent);
				}else{
					CustomAlertDialog.Builder builder = new Builder(ProjectModlesActivity.this);
					builder.setTitle(R.string.user_applay_model_failure);
					builder.setMessage(R.string.ot_applied);
					builder.setIcon(R.drawable.ic_launcher);
					builder.setPositiveButton(R.string.sure, null);
					builder.create().show();
				}
				break;
			}
		}
	};
	
	//模块列表点击事件
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object object = parent.getAdapter().getItem(position);
		if(object instanceof ProjectModelBean){
			ProjectModelBean projectModelBean = (ProjectModelBean) object;
			applyDialog(projectModelBean);
		}
	}
	
	/**
	 * 承接模块 对话框
	 * @param projectModelBean
	 */
	public void applyDialog(final ProjectModelBean projectModelBean){
		CustomAlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.project_apply);
		builder.setMessage(R.string.apply_content);
		builder.setIcon(R.drawable.ic_launcher);
		
		builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				applyModel(projectModelBean);//承接模块
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}
	/**
	 * 承接模块  方法
	 */
	public void applyModel(ProjectModelBean projectModelBean){
		if(projectModelBean != null){
			HttpParams params = new HttpParams();
			params.put("user_id", UserBase.getUserId(this));
			params.put("pro_model_id", projectModelBean.pro_model_id);
			HttpRequest.post("apply_model", params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject jsonObject) {
							Message message = new Message();
							message.obj = jsonObject;
							message.what = APPLY_MODEL;
							handler.sendMessage(message);
						}
					});
		}
	}
	
	/**
	 * 解析ProjectModelBean
	 * @param jsonObject
	 * @return
	 */
	public ProjectModelBean parseBean(JSONObject jsonObject){
		ProjectModelBean modelBean = null;
		if(jsonObject != null){
			modelBean = new ProjectModelBean();
			modelBean.pro_model_id = jsonObject.optInt("pro_model_id", -1);
			modelBean.pro_id = jsonObject.optInt("pro_id", -1);
			modelBean.pro_model_name = jsonObject.optString("pro_model_name", "");
			modelBean.pro_model_fee = jsonObject.optDouble("pro_model_fee", 0);
			modelBean.pro_model_start = jsonObject.optString("pro_model_start", "");
			modelBean.pro_model_end = jsonObject.optString("pro_model_end", "");
			modelBean.pro_model_desc = jsonObject.optString("pro_model_desc", "");
			modelBean.pro_model_directory = jsonObject.optString("pro_model_directory", "");
			modelBean.is_completed = jsonObject.optInt("is_completed", -1);
			modelBean.apply_user_id = jsonObject.optInt("apply_user_id", 0);
			modelBean.progress = jsonObject.optInt("progress", 0);
			modelBean.model_weight = jsonObject.optInt("model_weight", 0);
		}
		return modelBean;
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
	public void onRefresh() {
		page = 0;
		getProjectModels();
	}

	@Override
	public void onLoadMore() {
		if(page < pageCount - 1){
			page++;
			getProjectModels();
		}else{
			xListView.stopLoadMore();
			xListView.setNoMoreData();
		}
	}

}
