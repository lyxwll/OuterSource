package com.fourteen.outersource.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fourteen.outersource.R;
import com.fourteen.outersource.bean.ExamQuestionBean;
import com.fourteen.outersource.network.bitmap.BitmapUtil;
/**
 * 选择题的碎片
 */
public class ChoiceFragment extends QuestionFragment{
	View view;
	TextView q_id;
	TextView question;
	ImageView pic;
	RadioGroup choices;
	RadioButton a;
	RadioButton b;
	RadioButton c;
	RadioButton d;
	
	ExamQuestionBean bean;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ot_user_choice_fragment, null);
		q_id = (TextView) view.findViewById(R.id.q_id);
		question = (TextView) view.findViewById(R.id.question);
		pic = (ImageView) view.findViewById(R.id.pic);
		choices = (RadioGroup) view.findViewById(R.id.choices);
		a = (RadioButton) view.findViewById(R.id.a);
		b = (RadioButton) view.findViewById(R.id.b);
		c= (RadioButton) view.findViewById(R.id.c);
		d = (RadioButton) view.findViewById(R.id.d);
		bean = (ExamQuestionBean) getArguments().getSerializable("bean");
		if(bean != null) {
			q_id.setText(bean.q_id + ".");
			question.setText(bean.question);
			if(bean.pic != null && !bean.pic.equals("") && !bean.pic.equals("null")){
				pic.setVisibility(View.VISIBLE);
				BitmapUtil.getInstance(R.drawable.image_default, R.drawable.image_default).download(bean.pic, pic);
			}
			a.setText("A: " + bean.a);
			b.setText("B: " + bean.b);
			c.setText("C: " + bean.c);
			d.setText("D: " + bean.d);
			if(bean.your_answer != null && !bean.your_answer.equals("")) {
				if(bean.your_answer.equals("A")) {
					choices.check(R.id.a);
				} else if(bean.your_answer.equals("B")) {
					choices.check(R.id.b);
				} else if(bean.your_answer.equals("C")) {
					choices.check(R.id.c);
				} else if(bean.your_answer.equals("D")) {
					choices.check(R.id.d);
				} else {
					choices.check(-1);
				}
			}
		}
		return view;
	}
	
	@Override
	public String  getAnswer(){
		int id = choices.getCheckedRadioButtonId();
		String answer = "";
		switch(id){
		case R.id.a:
			answer = "A";
			break;
		case R.id.b:
			answer = "B";
			break;
		case R.id.c:
			answer = "C";
			break;
		case R.id.d:
			answer = "D";
			break;
		}
		return answer;
	}
}
