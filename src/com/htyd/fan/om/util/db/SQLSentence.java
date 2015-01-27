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
	public static final String COLUMN_ADDRESS = "location";
	public static final String COLUMN_MONTH = "month";
	public static final String COLUMN_ATTEND_STATE = "attend_state";
	public static final String COLUMN_CHOOSE_LOCATION = "choose_location";
	
	public static final String CREATE_TABLE_CHECK = "create table attend_check ("
			+ "_id integer primary key autoincrement, time integer ,latitude real ,attend_state integer,"
			+ " longitude real  , location text ,month integer,choose_location text)";

	public static final String TABLE_TASK = "task";
	public static final String COLUMN_TASK_ID = "_id";
	public static final String COLUMN_TASK_NET_ID = "task_id";
	public static final String COLUMN_TASK_WORK_LOCATION = "worklocation";
	public static final String COLUMN_TASK_INSTALL_LOCATION = "installlocation";
	public static final String COLUMN_TASK_DESCRIPTION = "description";
	public static final String COLUMN_TASK_TITLE = "task_title";
	public static final String COLUMN_TASK_RECIPIENT_NAME = "recipientname";
	public static final String COLUMN_TASK_RECIPIENT_PHONE = "recipientphone";
	public static final String COLUMN_TASK_EQUIPMENT = "taskequipment";
	public static final String COLUMN_TASK_PRODUCT_TYPE = "producttype";
	public static final String COLUMN_TASK_PLAN_STARTTIME = "planstarttime";
	public static final String COLUMN_TASK_PLAN_ENDTIME = "planendtime";
	public static final String COLUMN_TASK_CREATE_TIME = "createtime";
	public static final String COLUMN_TASK_STATE = "taskstate";
	public static final String COLUMN_TASK_TYPE = "tasktype";
	public static final String COLUMN_TASK_ASYNC_STATE = "task_async_state";

	public static final String CREATE_TABLE_TASK = "create table task ("
			+ "_id integer primary key autoincrement, worklocation text ,installlocation text ,"
			+ "description text  ,recipientname text ,task_title text,"
			+ "recipientphone text ,taskequipment text ,producttype text ,"
			+ "planstarttime integer ,planendtime integer ,taskstate integer ,tasktype text,"
			+ "task_id integer,createtime integer,task_async_state integer)";

	public static final String TABLE_TASK_PROCESS = "taskprocess";
	public static final String COLUMN_TASKPROCESS_ID = "_id";
	public static final String COLUMN_TASKPROCESS_TASK_ID = "task_id";
	public static final String COLUMN_TASKPROCESS_TASK_LOCAL_ID = "task_local_id";
	public static final String COLUMN_TASKPROCESS_STARTTIME = "task_start_time";
	public static final String COLUMN_TASKPROCESS_ENDTIME = "task_end_time";
	public static final String COLUMN_TASKPROCESS_TASK_PROCESSWHAT = "task_process_what";
//	public static final String COLUMN_TASKPROCESS_TASK_STATE = "task_state";
	public static final String COLUMN_TASKPROCESS_CREATE_TIME = "create_time";
	public static final String COLUMN_TASKPROCESS_ASYNC_STATE = "process_async_state";

	public static final String CREATE_TABLE_TASK_PROCESS = "create table taskprocess ("
			+ "_id integer primary key autoincrement, task_id integer references task(task_id),"
			+" task_start_time integer, task_local_id integer ,task_end_time  integer,process_async_state integer, "
			+ " task_process_what text , create_time integer)";

	public static final String TABLE_TASK_ACCESSORY = "taskaccessory";
	public static final String COLUMN_TASK_ACCESSORY_ID = "_id";
	public static final String COLUMN_TASK_ACCESSORY_TASK_NET_ID = "task_id";
	public static final String COLUMN_TASK_ACCESSORY_TASK_LOCAL_ID = "task_local_id";
	public static final String COLUMN_TASK_ACCESSORY_PATH = "accessory_path";
	public static final String COLUMN_TASK_ACCESSORY_STATE = "accessory_state";
	public static final String COLUMN_TASK_ACCESSORY_SOURCE = "accessory_type";
	public static final String COLUMN_TASK_ACCESSORY_NET_ID = "accessory_net_id";
	public static final String COLUMN_TASK_ACCESSORY_FILE_SIZE = "file_size";
	public static final String COLUMN_TASK_ACCESSORY_FILE_DESCRIPTION = "file_description";

	public static final String CREATE_TABLE_TASK_ACCESSORY = "create table taskaccessory ("
			+ "_id integer primary key autoincrement, task_id integer references task(task_id), accessory_path text"
			+ ", accessory_state integer ,accessory_type integer,accessory_net_id integer,file_size integer," 
			+"file_description text,task_local_id integer)";
	
	public static final String TABLE_TASK_TYPE = "tasktype";
	public static final String COLUMN_TASK_TYPE_ID = "_id";
	public static final String COLUMN_TASK_TYPE_NAME = "type_name";
	public static final String COLUMN_TASK_TYPE_CAT = "type_cat";
	
	public static final String CREATE_TABLE_TASK_TYPE = "create table tasktype ("
	+"_id integer primary key autoincrement, type_name text,type_cat text)";
}
