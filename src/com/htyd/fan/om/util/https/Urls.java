package com.htyd.fan.om.util.https;

public class Urls {
	
	public static  String BASEURL;
	
	public static final String LOCATIONURL = "http://www.weather.com.cn/data/list3/city";
	
	//public static final String BASEURL = "http://192.168.1.26:8080/zdyw/";
	
	public static  String SAVEATTENDURL;
	
	public static  String LOGINURL;
	
	public static  String TASKURL;
	
	public static  String TASKPROCESSURL;
	
	public static  String UPLOADFILE ;
	
	public static  String FILE;
	
	public static String COMMONDATA;
	
	public static String ACCESSORYFILEPATH;
	
	public Urls(String baseUrl) {
		BASEURL = baseUrl;
		SAVEATTENDURL = BASEURL + "kqAction?";
		LOGINURL = BASEURL + "loginAction?";
		TASKURL = BASEURL + "rwAction?";
		TASKPROCESSURL = BASEURL + "rwclAction?";
		UPLOADFILE = BASEURL + "uploadAction?Operate=upload";
		FILE = BASEURL + "wjAction?";
		COMMONDATA = BASEURL + "zdAction?";
	}
}
