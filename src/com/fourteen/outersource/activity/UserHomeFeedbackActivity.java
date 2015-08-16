package com.fourteen.outersource.activity;
/**
 * 意见反馈:UserHomeFeedbackActivity类
 */
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fourteen.outersource.BaseActivity;
import com.fourteen.outersource.R;
import com.fourteen.outersource.base.UserBase;
import com.fourteen.outersource.dialog.CustomAlertDialog;
import com.fourteen.outersource.dialog.CustomAlertDialog.Builder;
import com.fourteen.outersource.network.myhttp.AsyncHttpResponseHandler;
import com.fourteen.outersource.network.myhttp.HttpParams;
import com.fourteen.outersource.network.myhttp.HttpRequest;
import com.fourteen.outersource.toast.CustomToast;
import com.fourteen.outersource.view.HeaderClickListener;
import com.fourteen.outersource.view.HeaderView;

public class UserHomeFeedbackActivity extends BaseActivity implements
		HeaderClickListener, OnClickListener {

	HeaderView headerView;
	Button commitButton;//提交意见按钮
	EditText editText;//输入框
	TextView leftTextView;//剩余字数

	public static final int COMMIT_SUCCESS = 0x1234;
	public static final int MAX_COUNT = 200;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.ot_ps_feedback_suggestion);

		headerView = (HeaderView) findViewById(R.id.header_view);
		headerView.setTitle(R.string.feedback_about);
		headerView.setHeaderClickListener(this);

		commitButton = (Button) findViewById(R.id.commit_suggestion);
		commitButton.setOnClickListener(this);
		leftTextView = (TextView) findViewById(R.id.left_text);
		editText = (EditText) findViewById(R.id.suggestion_content);
		editText.addTextChangedListener(mTextWatcher);
		editText.setSelection(editText.length()); // 将光标移动最后一个字符后面
	}

	@Override
	public void networkStatusChanged(int status) {
		headerView.setNetworkStatus(status);
	}

	Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case COMMIT_SUCCESS://提交意见成功
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject != null && jsonObject.optInt("result", -1) == 0) {
					/*CustomToast.makeText(UserHomeFeedbackActivity.this,
							R.string.commit_success, CustomToast.LENGTH_SHORT)
							.show();*/
				} else {
					// 提交建议失败
					CustomAlertDialog.Builder builder = new Builder(
							UserHomeFeedbackActivity.this);
					builder.setTitle(R.string.commit_failure);
					builder.setMessage(jsonObject.optString("msg", ""));
					builder.setPositiveButton(R.string.sure, null);
					builder.create().show();
				}
				break;
			}
			return true;
		}
	});

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.commit_suggestion://提交意见
			//if (isCheck()) {
			commitSuggestion();
			//}
			editText.getText().clear();
			break;
		}
	}

	/**
	 * 提交意见的方法
	 */
	public void commitSuggestion() {
		String msg = editText.getText().toString().trim();
		if(msg != null && !msg.equals("") && msg.length() >= 10) {
			HttpParams params = new HttpParams();
			params.put("user_id", UserBase.getUserId(this));
			params.put("user_name", UserBase.getUserName(this));
			params.put("msg", msg);
			HttpRequest.post("user_propose_feedback", params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject jsonObject) {
							Message message = new Message();
							message.what = COMMIT_SUCCESS;
							message.obj = jsonObject;
							handler.sendMessage(message);
						}
					});
		} else {
			CustomToast.makeText(this, R.string.input_suggestion_less, CustomToast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 检查输入内容
	 * @return
	 */
	public boolean isCheck() {
		boolean flag = true;
		String value = editText.getText().toString().trim();
		if (value != null && !value.equals("") && calculateLength(value) > 10) {
			CustomToast.makeText(UserHomeFeedbackActivity.this,
					R.string.commit_success, CustomToast.LENGTH_SHORT).show();
		} else {
			CustomToast.makeText(UserHomeFeedbackActivity.this,
					R.string.commit_failure, CustomToast.LENGTH_SHORT).show();
		}
		return flag;
	}

	@Override
	public void onHeaderClick(View v, int which) {
		switch (which) {
		case HeaderView.BACK_POSITION:
			this.finish();
			break;
		}
	}

	/**
	 * 剩余字数观察
	 */
	private TextWatcher mTextWatcher = new TextWatcher() {
		private int editStart;
		private int editEnd;

		@Override
		public void afterTextChanged(Editable s) {
			editStart = editText.getSelectionStart();
			editEnd = editText.getSelectionEnd();
			// 先去掉监听器，否则会出现栈溢出
			editText.removeTextChangedListener(mTextWatcher);
			// 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
			// 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
			while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}
			// mEditText.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了
			editText.setSelection(editStart);
			// 恢复监听器
			editText.addTextChangedListener(mTextWatcher);
			setLeftCount();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}
	};

	/**
	 * 计算输入内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意： 
	 * 该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
	 * @param c
	 * @return
	 */
	private long calculateLength(CharSequence c) {
		double len = 0;
		for (int i = 0; i < c.length(); i++) {
			int tmp = c.charAt(i);
			if (tmp > 0 && tmp < 127) {
				len += 0.5;
			} else {
				len++;
			}
		}
		return Math.round(len);
	}

	/**
	 * 刷新剩余输入字数
	 */
	private void setLeftCount() {
		leftTextView.setText(Html.fromHtml(String.format(
				getString(R.string.proposed_feedback_notify), MAX_COUNT
						- getInputCount())));
	}

	/**
	 * 获取用户输入的分享内容字数
	 * @return
	 */
	private long getInputCount() {
		return calculateLength(editText.getText().toString());
	}

}
