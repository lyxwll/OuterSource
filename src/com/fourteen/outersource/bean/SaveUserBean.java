package com.fourteen.outersource.bean;

import java.io.Serializable;

public class SaveUserBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String user_name;
	public String user_password;

	@Override
	public String toString() {
		return user_name;
	}
}
