package com.htyd.fan.om.util.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;


public class Utils {
	
	@SuppressLint("SimpleDateFormat")
	public static long parseDate(String source) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return	sdf.parse(source).getTime();
	}
}
