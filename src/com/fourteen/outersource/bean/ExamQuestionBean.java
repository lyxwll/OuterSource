package com.fourteen.outersource.bean;

import java.io.Serializable;

public class ExamQuestionBean implements Serializable, Comparable<ExamQuestionBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int e_id;
	public int user_id;
	public int t_id;
	public int q_id;
	public String question;
	public int category_id;
	public String pic;
	public String a;
	public String b;
	public String c;
	public String d;
	public String source_code;
	public String answer;
	public String your_answer;
	public String your_score_code;
	public int score;

	@Override
	public int compareTo(ExamQuestionBean another) {
		if(this.t_id < another.t_id) {
			return -1;
		} else if(this.t_id == another.t_id) {
			if(this.q_id < another.q_id) {
				return -1;
			} else if(this.q_id == another.q_id){
				return 0;
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}
}
