package com.htyd.fan.om.util.db;

public class SQLSentence {

	public static final String TABLE_PROVINCE = "province";
	public static final String COLUMN_PROVINCE_ID = "_id";
	public static final String COLUMN_PROVINCE_NAME = "province_name";
	public static final String COLUMN_PROVINCE_CODE = "province_code";

	public static final String CREATE_TABLE_PROVINCE = "create table province ("
			+ "_id integer primary key autoincrement , province_name text,province_code text )";

	public static final String TABLE_CITY = "city";
	public static final String COLUMN_CITY_ID = "_id";
	public static final String COLUMN_CITY_NAME = "city_name";
	public static final String COLUMN_CITY_CODE = "city_code";
	public static final String COLUMN_CITY_PROVINCE_ID = "province_id";

	public static final String CREATE_TABLE_CITY = "create table city ("
			+ "_id integer primary key autoincrement, city_name text, city_code text,province_id integer references province(_id))";

	public static final String TABLE_DISTRICT = "district";
	public static final String COLUMN_DISTRICT_NAME = "district_name";
	public static final String COLUMN_DISTRICT_CODE = "district_code";
	public static final String COLUMN_DISTRICT_CITY_ID = "city_id";

	public static final String CREATE_TABLE_DISTRICT = "create table district ("
			+ "district_name text,district_code text,city_id integer references city(_id))";

	public static final String TABLE_CHECK = "attend_check";
	public static final String COLUMN_CHECK_ID = "_id";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_ADDSTATE = "addstate";
	public static final String COLUMN_ADDSORT = "addsort";
	public static final String COLUMN_USERNAME = "name";
	public static final String COLUMN_ADDRESS = "location";
	public static final String COLUMN_MONTH = "month";

/*	public static final String CREATE_TABLE_CHECK = "create table check ("
			+ "_id integer primary key autoincrement, time integer ,latitude real ,longitude real ,"
			+ "addstate text, addsort text, name text ,location text ,month integer)";*/
	
	public static final  String CREATE_TABLE_CHECK = "create table attend_check (" 
			+ "_id integer primary key autoincrement, time integer ,latitude real ,"
			+"longitude real ,addstate text ,addsort text ,name text, location text ,month integer)";
	
	public static final String TABLE_TASK = "task";
	public static final String COLUMN_TASK_ID = "_id";
	public static final String COLUMN_TASK_WORK_LOCATION = "worklocation";
	public static final String COLUMN_TASK_INSTALL_LOCATION = "installlocation";
	public static final String COLUMN_TASK_DESCRIPTION = "description";
	public static final String COLUMN_TASK_CONTACTS = "taskcontacts";
	public static final String COLUMN_TASK_CONTACT_PHONE = "contactphone";
	public static final String COLUMN_TASK_RECIPIENT_NAME = "recipientname";
	public static final String COLUMN_TASK_RECIPIENT_PHONE = "recipientphone";
	public static final String COLUMN_TASK_ACCESSORY = "taskaccesspry";
	public static final String COLUMN_TASK_EQUIPMENT = "taskequipment";
	public static final String COLUMN_TASK_PRODUCT_TYPE = "producttype";
	public static final String COLUMN_TASK_PLAN_STARTTIME = "planstarttime";
	public static final String COLUMN_TASK_PLAN_ENDTIME = "planendtime";
	public static final String COLUMN_TASK_STATE = "taskstate";
	public static final String COLUMN_TASK_TYPE = "tasktype";

	public static final String CREATE_TABLE_TASK = "create table task ("
			+ "_id integer primary key autoincrement, worklocation text ,installlocation text ,"
			+ "description text ,taskcontacts text , contactphone text ,recipientname text ,"
			+ "recipientphone text ,taskaccesspry text ,taskequipment text ,producttype text ,"
			+ "planstarttime integer ,planendtime integer ,taskstate integer ,tasktype integer)";

}
