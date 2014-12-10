package com.htyd.fan.om.util.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;


public class Utils {
	
	@SuppressLint("SimpleDateFormat")
	public static long parseDate(String source){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return	sdf.parse(source).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public static long parseDate(String source,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return	sdf.parse(source).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String formatTime(long time){
		return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
		.format(time);
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String formatTime(long time,String format){
		return new SimpleDateFormat(format).format(time);
	}
	
	public static int getCalendarField(long time, int field) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		return c.get(field);
	}
}
