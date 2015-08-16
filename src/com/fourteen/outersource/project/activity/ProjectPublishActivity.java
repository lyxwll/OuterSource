package com.fourteen.outersource.project.activity;
/**
 * 项目发布:ProjectPublishActivity类
 */
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.adapter.SkillPopupAdapter;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.CategoryBean;
import com.fourteen.outersource.bean.ProjectBean;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.dialog.CustomAlertDialog.Builder;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class ProjectPublishActivity extends BaseActivity implements
		HeaderClickListener, OnClickListener {

	private ArrayList<CategoryBean> categoryList = new ArrayList<CategoryBean>();
	public static final int RECEIVED_CATEGORY_LIST = 0x123;
	public static final int PUBLISH_SUCCESS = 0x12345;

	HeaderView headerView;
	ListView skillListView;
	TextView categoryTextView;//项目类别
	EditText proNameEditText;//项目名称
	EditText proPriceEditText;//项目价格
	EditText proDescEditText;//项目描述
	TextView startTextView;//项目开始时间
	TextView endTextView;//项目结束时间
	Button publishButton;//项目发布按钮

	Calendar calendar;
	

	private ProjectBean projectBean;
	private CategoryBean bean;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_pro_publish_project);
		initView();
		initData();
	}

	public void initView() {
		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(UserBase.getUserName(this));
		headerView.setHeaderClickListener(this);

		categoryTextView = (TextView) findViewById(R.id.category_id);
		proNameEditText = (EditText) findViewById(R.id.pro_name);
		proPriceEditText = (EditText) findViewById(R.id.pro_price);
		proDescEditText = (EditText) findViewById(R.id.pro_desc);
		startTextView = (TextView) findViewById(R.id.start_time);
		endTextView = (TextView) findViewById(R.id.end_time);
		publishButton = (Button) findViewById(R.id.publish_button);

		categoryTextView.setOnClickListener(this);
		startTextView.setOnClickListener(this);
		endTextView.setOnClickListener(this);
		publishButton.setOnClickListener(this);
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.category_id:// 项目类别选择
			showCategories(categoryTextView);
			break;
		case R.id.start_time://开始时间
			startTimeChoice();
			break;
		case R.id.end_time://结束时间
			endTimeChoice();
			break;
		case R.id.publish_button:// 项目发布
			publishProject();
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RECEIVED_CATEGORY_LIST://获取项目类别列表
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					JSONArray jsonArray = jsonObject.optJSONArray("list");
					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.optJSONObject(i);
							CategoryBean bean = new CategoryBean();
							bean.category_id = object.optInt("category_id", -1);
							bean.category_name = object.optString("category_name", "");
							categoryList.add(bean);
						}
					}
				}
				break;
			case PUBLISH_SUCCESS://项目发布成功
				JSONObject jsonObject2 = (JSONObject) msg.obj;
				if (jsonObject2 != null && jsonObject2.optInt("result", -1) == 0) {
					JSONObject object = jsonObject2.optJSONObject("project");
					//
					projectBean = parseProject(object);
					// 跳转至上传项目附件的界面
					Intent intent = new Intent();
					intent.setClass(ProjectPublishActivity.this, GoagoActivity.class);
					Bundle bundle = new Bundle();
					//Activity之间的传值   
					bundle.putSerializable("projectBean", projectBean);
					intent.putExtras(bundle);
					startActivity(intent);
					ProjectPublishActivity.this.finish();
				} else {
					CustomAlertDialog.Builder builder = new Builder(
							ProjectPublishActivity.this);
					builder.setTitle(R.string.publish_failure);
					builder.setMessage(jsonObject2.optString("msg", ""));
					builder.setPositiveButton(R.string.sure, null);
					builder.create().show();
				}
				break;
			}
		}
	};
	
	/**
	 * 解析project
	 * @param jsonObject
	 * @return
	 */
	public ProjectBean parseProject(JSONObject jsonObject) {
		ProjectBean bean = null;
		if (jsonObject != null) {
			bean = new ProjectBean();
			bean.user_id = jsonObject.optInt("user_id", -1);
			bean.pro_category_id = jsonObject.optInt("pro_category_id", -1);
			bean.pro_name = jsonObject.optString("pro_name", "");
			bean.pro_price = jsonObject.optDouble("pro_price", 0);
			bean.pro_desc = jsonObject.optString("pro_desc", "");
			bean.pro_start_time = jsonObject.optString("pro_start_time", "");
			bean.pro_end_time = jsonObject.optString("pro_end_time", "");
			bean.pro_fee = jsonObject.optDouble("pro_fee", 0);
			bean.pro_directory = jsonObject.optString("pro_directory", "");
			bean.pro_date = jsonObject.optString("pro_date", "");
		}
		return bean;
	}
	

	/**
	 * 选择项目类别列表
	 */
	public void initData() {
		HttpParams params = new HttpParams();
		params.put("user_id", UserBase.getUserId(this));
		HttpRequest.post("get_project_categories", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						Message message = new Message();
						message.what = RECEIVED_CATEGORY_LIST;
						message.obj = jsonObject;
						handler.sendMessage(message);
					}
				});
	}

	/**
	 * 发布项目
	 */
	public void publishProject() {
		int categoryId = -1;
		if(bean != null) {
			categoryId = bean.category_id;
		}
		String proName = proNameEditText.getText().toString().trim();
		String proPrice = proPriceEditText.getText().toString().trim();
		String proDesc = proDescEditText.getText().toString().trim();
		String startTime = startTextView.getText().toString().trim();
		String endTime = endTextView.getText().toString().trim();
		HttpParams params = new HttpParams();
		params.put("user_id", UserBase.getUserId(this));
		if (categoryId!=-1) {
			params.put("category_id", categoryId);
		} 
		params.put("pro_name", proName);
		params.put("pro_price", proPrice);
		params.put("pro_desc", proDesc);
		params.put("pro_start_time", startTime);
		params.put("pro_end_time", endTime);
		HttpRequest.post("user_publish_requirement", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						Message message = new Message();
						message.obj = jsonObject;
						message.what = PUBLISH_SUCCESS;
						handler.sendMessage(message);
					}
				});
	}

	/**
	 * 显示项目类别弹窗
	 */
	public void showCategories(final TextView view) {
		final PopupWindow popupWindow = new PopupWindow(this);
		popupWindow.setWidth(300);
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
		popupWindow.showAsDropDown(view);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View item,
					int position, long id) {
				Object object = adapterView.getAdapter().getItem(position);
				if (object instanceof CategoryBean) {
					bean = (CategoryBean) object;
					view.setText(bean.category_name);
					popupWindow.dismiss();
				}
			}
		});
	}

	/**
	 * 项目开始时间选择对话框
	 */
	public void startTimeChoice() {
		calendar = Calendar.getInstance();
		CustomAlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.pro_start_time);
		View view = View.inflate(this, R.layout.register_steptwo_datepicker,null);
		final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
		builder.setView(view);
		
		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				new OnDateChangedListener() {
					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						calendar.set(year, monthOfYear, dayOfMonth);
					}
				});
		builder.setNegativeButton(R.string.back,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						StringBuffer buffer = new StringBuffer();
						buffer.append(String.format("%d-%02d-%02d",
								datePicker.getYear(),
								datePicker.getMonth() + 1,
								datePicker.getDayOfMonth()));
						/*
						 * buffer.append(datePicker.getYear()).append("-")
						 * .append(datePicker.getMonth() + 1).append("-")
						 * .append(datePicker.getDayOfMonth());
						 */
						startTextView.setText(buffer);
					}
				});
		builder.create().show();
	}

	/**
	 * 项目结束时间选择对话框
	 */
	public void endTimeChoice() {
		calendar = Calendar.getInstance();
		CustomAlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.pro_end_time);
		View view = View.inflate(this, R.layout.register_steptwo_datepicker,
				null);
		final DatePicker datePicker = (DatePicker) view
				.findViewById(R.id.date_picker);
		builder.setView(view);

		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				new OnDateChangedListener() {
					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						calendar.set(year, monthOfYear, dayOfMonth);
					}
				});
		builder.setNegativeButton(R.string.back,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append(String.format("%d-%02d-%02d",
								datePicker.getYear(),
								datePicker.getMonth() + 1,
								datePicker.getDayOfMonth()));
						/*
						 * stringBuffer.append(datePicker.getYear()).append("-")
						 * .append(datePicker.getMonth() + 1).append("-")
						 * .append(datePicker.getDayOfMonth());
						 */
						endTextView.setText(stringBuffer);
					}
				});
		builder.create().show();
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch (which) {
		case HeaderView.BACK_POSITION:
			this.finish();
			break;
		}
	}
	
}
