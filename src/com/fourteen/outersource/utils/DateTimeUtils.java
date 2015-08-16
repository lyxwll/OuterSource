package com.fourteen.outersource.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateTimeUtils {

	public static String formatToDate(String datetime) {
		String str = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		try {
			str = format.format(format.parse(datetime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}
}
