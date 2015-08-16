package com.fourteen.outersource.bean;

import java.io.Serializable;

public class ExamBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public int category_id;
	public String addtime;
	public int num_choice;
	public int score_choice;
	public int num_judge;
	public int score_judge;
	public int num_blank;
	public int score_blank;
	public int num_program_result;
	public int score_program_result;
	public int num_program;
	public int score_program;
	public int sum_score;
	public String exam_name;
	public int exam_time;
	

}
