package com.fourteen.outersource.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.ExamQuestionBean;
import com.fourteen.outersource.network.bitmap.BitmapUtil;
/**
 * 填空题的碎片
 * @author Administrator
 *
 */
public class BlankFragment extends QuestionFragment{
	View view;
	TextView q_id;
	TextView question;
	ImageView pic;
	EditText yourAnswer;
	
	ExamQuestionBean bean;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ot_user_blank_fragment, null);
		q_id = (TextView) view.findViewById(R.id.q_id);
		question = (TextView) view.findViewById(R.id.question);
		pic = (ImageView) view.findViewById(R.id.pic);
		yourAnswer = (EditText) view.findViewById(R.id.your_answer);
		bean = (ExamQuestionBean) getArguments().getSerializable("bean");
		if(bean != null) {
			q_id.setText(bean.q_id + ".");
			question.setText(bean.question);
			if(bean.pic != null && !bean.pic.equals("") && !bean.pic.equals("null")){
				pic.setVisibility(View.VISIBLE);
				BitmapUtil.getInstance(R.drawable.image_default, R.drawable.image_default).download(bean.pic, pic);
			}
			if(bean.your_answer != null && !bean.your_answer.equals("")) {
				yourAnswer.setText(bean.your_answer);
			}
		}
		return view;
	}
	
	@Override
	public String  getAnswer(){
		String answer = yourAnswer.getText().toString().trim();
		return answer;
	}
}
