package com.fourteen.outersource.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fourteen.outersource.R;
import com.fourteen.outersource.toast.CustomToast;

public class FindPasswordFragmentOne extends Fragment implements OnClickListener{

	View view;//定义一个控件
	EditText nameEditText;//定义输入名称的控件
	Button nextButton;//定义下一步的控件
	FindPasswordNextListener nextListener;//通过接口定义一个对象
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ot_ps_find_password_one, null);
		nameEditText = (EditText) view.findViewById(R.id.user_edit);
		nextButton= (Button) view.findViewById(R.id.find_password_next);
		nextButton.setOnClickListener(this);
		return view;
	}
	
	//构造方法
	public  void setNextListener(FindPasswordNextListener nextListener){
		this.nextListener = nextListener;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.find_password_next://下一步
			if(nameEditText.getText().toString() != null && !nameEditText.getText().toString().equals("")) {
				if(this.nextListener != null) {
					String[] args = new String[]{nameEditText.getText().toString().trim()};
					nextListener.onClickNextButton(0, args);
				}
			} else {
				if(getActivity() != null) {
					CustomToast.makeText(getActivity(), R.string.user_name_empty, CustomToast.LENGTH_SHORT).show();
				}
			}
			break;
		}
	}
}
