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
 * 判断题的碎片
 */
public class JudgeFragment extends QuestionFragment{
	View view;
	TextView q_id;
	TextView question;
	ImageView pic;
	RadioGroup choices;
	RadioButton trueChoice;
	RadioButton falseChoice;
	
	ExamQuestionBean bean;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ot_user_judge_fragment, null);
		q_id = (TextView) view.findViewById(R.id.q_id);
		question = (TextView) view.findViewById(R.id.question);
		pic = (ImageView) view.findViewById(R.id.pic);
		choices = (RadioGroup) view.findViewById(R.id.choices);
		trueChoice = (RadioButton) view.findViewById(R.id.judge_true);
		falseChoice = (RadioButton) view.findViewById(R.id.judge_false);
		bean = (ExamQuestionBean) getArguments().getSerializable("bean");
		if(bean != null) {
			q_id.setText(bean.q_id + ".");
			question.setText(bean.question);
			if(bean.pic != null && !bean.pic.equals("") && !bean.pic.equals("null")){
				pic.setVisibility(View.VISIBLE);
				BitmapUtil.getInstance(R.drawable.image_default, R.drawable.image_default).download(bean.pic, pic);
			}
			if(bean.your_answer != null && !bean.your_answer.equals("")) {
				if(bean.your_answer.equals("0")) {
					choices.check(R.id.judge_false);
				} else if(bean.your_answer.equals("1")) {
					choices.check(R.id.judge_true);
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
		case R.id.judge_true:
			answer = "1";
			break;
		case R.id.judge_false:
			answer = "0";
			break;
		}
		return answer;
	}
}
