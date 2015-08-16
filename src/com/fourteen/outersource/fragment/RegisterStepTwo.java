package com.fourteen.outersource.fragment;

import java.util.Calendar;

import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fourteen.outersource.R;
import com.fourteen.outersource.activity.ChooseAddressActivity;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.bean.UserBean;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.toast.CustomToast;
/**
 * 用户信息填写:RegisterStepTwo类
 * @author Administrator
 */
public class RegisterStepTwo extends Fragment implements OnClickListener {
	RegisterJumpListener jumpListener;

	View view;
	LinearLayout manLayout;//男
	LinearLayout womanLayout;//女
	TextView birthdayTextView;//日期
	TextView addressTextView;//地址
	EditText phoneEditText;//电话
	Button nextButton;//确认按钮

	Calendar calendar;//日期类
	int position = 0; //0 男， 1女

	public static final int REQUEST_ADDRESS = 12345;//请求地址码
	public static final int RECEIVED_USER_INFO = 0X1234;//接收用户信息码
	//创建View视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ot_register_step_two, null);
		manLayout = (LinearLayout) view.findViewById(R.id.layout_man);
		womanLayout = (LinearLayout) view.findViewById(R.id.layout_woman);
		birthdayTextView = (TextView) view.findViewById(R.id.birthday);
		addressTextView = (TextView) view.findViewById(R.id.address_edit);
		phoneEditText = (EditText) view.findViewById(R.id.phone_edit);
		nextButton = (Button) view.findViewById(R.id.next_button);
		//性别选择
		if(getActivity() != null) {
			if(UserBase.getUserSex(getActivity()) == 0) {
				position = 0;
				manLayout.setBackgroundResource(R.drawable.sex_selected);
				womanLayout.setBackgroundResource(R.drawable.sex_normal);
			} else if(UserBase.getUserSex(getActivity()) == 1) {
				position = 1;
				manLayout.setBackgroundResource(R.drawable.sex_normal);
				womanLayout.setBackgroundResource(R.drawable.sex_selected);
			}
			String birthday = UserBase.getUserBirthday(getActivity());
			String address = UserBase.getUserAddress(getActivity());
			String phone = UserBase.getUserPhone(getActivity());
			if(birthday != null && !birthday.equals("") && !birthday.equals("null")) {
				birthdayTextView.setText(birthday);
			}
			if(address != null && !address.equals("") && !address.equals("null")) {
				addressTextView.setText(address);
			}
			if(phone != null && !phone.equals("") && !phone.equals("null")) {
				phoneEditText.setText(phone);
			}
		}
		manLayout.setOnClickListener(this);
		womanLayout.setOnClickListener(this);
		birthdayTextView.setOnClickListener(this);
		addressTextView.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		return view;
	}

	public void setRegisterJumpListener(RegisterJumpListener jumpListener) {
		this.jumpListener = jumpListener;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_man://性别选择:男
			position = 0;
			manLayout.setBackgroundResource(R.drawable.sex_selected);
			womanLayout.setBackgroundResource(R.drawable.sex_normal);
			break;
		case R.id.layout_woman://女
			position = 1;
			manLayout.setBackgroundResource(R.drawable.sex_normal);
			womanLayout.setBackgroundResource(R.drawable.sex_selected);
			break;
		case R.id.birthday://生日
			showCalender();//显示日期
			break;
		case R.id.address_edit://地址
			showAddress();
			break;
		case R.id.next_button://上传用户信息
			uploadUserInfo();
			break;
		}
	}

	//显示出生日期选择
	public void showCalender() {
		if(getActivity() != null) {
			calendar = Calendar.getInstance();
			//自定义dialog
			CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(getActivity());
			builder.setTitle(R.string.choice_birthday);
			View view = View.inflate(getActivity(),R.layout.register_steptwo_datepicker, null);
			
			final DatePicker datePicker = (DatePicker) view
					.findViewById(R.id.date_picker);
			builder.setView(view);
			//实例化日期
			datePicker.init(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					new OnDateChangedListener() {
						@Override
						public void onDateChanged(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							//设置日期显示格式
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
							//保存选择的日期
							StringBuffer sb = new StringBuffer();
							sb.append(String.format("%d-%02d-%02d",
									datePicker.getYear(),
									datePicker.getMonth() + 1,
									datePicker.getDayOfMonth()));
							birthdayTextView.setText(sb);
							dialog.cancel();
						}
					});
			builder.create().show();
		}
	}
	
	//显示地址选择
	public void showAddress(){
		if(getActivity() != null) {
			Intent intent = new Intent();
			//跳转到ChooseAddressActivity界面
			intent.setClass(getActivity(), ChooseAddressActivity.class);
			startActivityForResult(intent, REQUEST_ADDRESS);
		}
	}
	
	//使用结果码启动的Activity 即:startActivityForResult(intent, REQUEST_ADDRESS);
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case REQUEST_ADDRESS:
			if(resultCode == Activity.RESULT_OK && data != null && data.hasExtra("address")) {
				String address = data.getStringExtra("address");
				addressTextView.setText(address);
			}
			break;
		}
	}
	   
	
	/**
	 * 上传用户信息
	 */
	public void uploadUserInfo(){
		if(isAllChecked() && getActivity() != null) {
			HttpParams params = new HttpParams();
			params.put("user_name", UserBase.getUserName(getActivity()));
			params.put("user_password", UserBase.getUserPassword(getActivity()));
			params.put("user_sex", position);
			params.put("user_birthday", birthdayTextView.getText().toString().trim());
			params.put("user_address", addressTextView.getText().toString().trim());
			params.put("user_phone", phoneEditText.getText().toString().trim());
			HttpRequest.post("change_user_info", params, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					Message message = new Message();
					message.what = RECEIVED_USER_INFO;
					message.obj = jsonObject;
					handler.sendMessage(message);
				}
			});
		}
	}
	
	Handler handler = new Handler(Looper.myLooper()) {
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case RECEIVED_USER_INFO:
				JSONObject jsonObject1 = (JSONObject) msg.obj;
				if (jsonObject1.optInt("result", -1) == 0) {
					JSONObject object = jsonObject1.optJSONObject("user");
					if (object != null) {
						UserBean userBean = new UserBean();
						userBean.user_id = object.optInt("user_id", -1);
						userBean.user_name = object.optString("user_name", "");
						userBean.user_password = object.optString(
								"user_password", "");
						userBean.user_sex = object.optInt("user_sex", -1);
						userBean.user_birthday = object.optString(
								"user_birthday", "");
						userBean.user_address = object.optString(
								"user_address", "");
						userBean.user_phone = object
								.optString("user_phone", "");
						userBean.user_email = object
								.optString("user_email", "");
						userBean.user_email_verfied = object.optInt(
								"user_email_verfied", -1);
						userBean.user_url = object.optString("user_url", "");
						userBean.is_developer = object
								.optInt("is_developer", 0);
						userBean.user_question1 = object.optString(
								"user_question1", "");
						userBean.user_question2 = object.optString(
								"user_question2", "");
						userBean.user_question3 = object.optString(
								"user_question3", "");
						userBean.date = object.optString("date", "");
						userBean.account_available = object.optInt(
								"account_available", 0);
						userBean.user_money = object.optDouble("use_money", 0);
						if(getActivity() != null) {
							UserBase.setUserBase(getActivity(), userBean);
							CustomToast.makeText(getActivity(),
									R.string.change_user_info_success,
									CustomToast.LENGTH_SHORT).show();
						}
						// 跳界面
						if (jumpListener != null) {
							jumpListener.jumpToNext(1);
						}
					}
				} else {
					if(getActivity() != null) {
						// 注册失败
						CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(getActivity());
						builder.setTitle(R.string.change_user_info_failure);
						builder.setMessage(jsonObject1.optString("msg", ""));
						builder.setPositiveButton(R.string.sure, null);
						builder.create().show();
					}
				}
			}
		};
	};

	//检查全部信息
	public boolean isAllChecked(){
		if(birthdayTextView.getText().toString() == null || birthdayTextView.getText().toString().equals("")) {
			return false;
		} else if(addressTextView.getText().toString() == null || addressTextView.getText().toString().equals("")){
			return false;
		} else if(phoneEditText.getText().toString() == null || phoneEditText.getText().toString().equals("")) {
			return false;
		}
		return true;
	}
}
