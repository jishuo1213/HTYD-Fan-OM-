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
}
