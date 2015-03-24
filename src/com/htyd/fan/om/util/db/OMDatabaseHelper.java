package com.htyd.fan.om.util.db;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.htyd.fan.om.model.CityBean;
import com.htyd.fan.om.model.DistrictBean;
import com.htyd.fan.om.model.ProvinceBean;
import com.htyd.fan.om.model.CommonDataBean;

public class OMDatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "om.sqlite";
	private static final int VERSION = 1;
	private static OMDatabaseHelper sHelper;

	private OMDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	public static OMDatabaseHelper getInstance(Context context){
		if(sHelper == null){
			sHelper = new OMDatabaseHelper(context);
		}
		return sHelper;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQLSentence.CREATE_TABLE_TASK_TYPE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

/*	public ProvinceCursor queryProvince() {
		Cursor wrapped = getReadableDatabase().query(
				SQLSentence.TABLE_PROVINCE, null, null, null, null, null, null);
		return new ProvinceCursor(wrapped);
	}*/
	/*	public CityCursor queryCity(int provinceId) {
		Cursor wrapped = getReadableDatabase().query(SQLSentence.TABLE_CITY,
				null, SQLSentence.COLUMN_CITY_PROVINCE_ID + "= ?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		return new CityCursor(wrapped);
	}
	 */
	
	/*	public DistrictCursor queryDistrict(int cityId) {
		Cursor wrapped = getReadableDatabase().query(
				SQLSentence.TABLE_DISTRICT, null,
				SQLSentence.COLUMN_DISTRICT_CITY_ID + "= ?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		return new DistrictCursor(wrapped);
	}*/

	public static class ProvinceCursor extends CursorWrapper {

		public ProvinceCursor(Cursor c) {
			super(c);
		}

		public ProvinceBean getProvince() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			ProvinceBean mBean = new ProvinceBean();
			mBean.id = (int) getLong(getColumnIndex(SQLSentence.COLUMN_PROVINCE_ID));
			mBean.provinceCode = getString(getColumnIndex(SQLSentence.COLUMN_PROVINCE_CODE));
//			byte[] bs = getString(getColumnIndex(SQLSentence.COLUMN_PROVINCE_NAME)).getBytes();
			byte[] bs = getBlob(getColumnIndex(SQLSentence.COLUMN_PROVINCE_NAME));
			  try {
				mBean.provinceName =  new String(bs, "gbk");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return mBean;
		}
	}

	public static class CityCursor extends CursorWrapper {

		public CityCursor(Cursor c) {
			super(c);
		}

		public CityBean getCity() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			CityBean mBean = new CityBean();
			mBean.provinceID = (int) getLong(getColumnIndex(SQLSentence.COLUMN_CITY_PROVINCE_ID));
			mBean.Id = (int) getLong(getColumnIndex(SQLSentence.COLUMN_CITY_ID));
			mBean.cityCode = getString(getColumnIndex(SQLSentence.COLUMN_CITY_CODE));
			byte[] bs = getBlob(getColumnIndex(SQLSentence.COLUMN_CITY_NAME));
			try {
				mBean.cityName =  new String(bs, "gbk");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return mBean;
		}
	}


	public static class DistrictCursor extends CursorWrapper {

		public DistrictCursor(Cursor c) {
			super(c);
		}

		public DistrictBean getDistrict() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			DistrictBean mBean = new DistrictBean();
			mBean.cityID = (int) getLong(getColumnIndex(SQLSentence.COLUMN_DISTRICT_CITY_ID));
			mBean.districtCode = getString(getColumnIndex(SQLSentence.COLUMN_DISTRICT_CODE));
			byte[] bs = getBlob(getColumnIndex(SQLSentence.COLUMN_DISTRICT_NAME));
			try {
				mBean.districtName =  new String(bs, "gbk");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return mBean;
		}
	}
	
	public CommonDataCursor queryTaskType(String type) {
		Cursor cursor = getReadableDatabase().query(
				SQLSentence.TABLE_TASK_TYPE, null,
				SQLSentence.COLUMN_TASK_TYPE_CAT + " = ?",
				new String[] { type }, null, null, null);
		return new CommonDataCursor(cursor);
	}
	
	public static class CommonDataCursor extends CursorWrapper{

		public CommonDataCursor(Cursor cursor) {
			super(cursor);
		}
		
		public CommonDataBean getTaskType(){
			if (isBeforeFirst() || isAfterLast())
				return null;
			CommonDataBean mBean = new CommonDataBean();
			mBean.typeDescription = getString(getColumnIndex(SQLSentence.COLUMN_TASK_TYPE_CAT));
			mBean.typeName = getString(getColumnIndex(SQLSentence.COLUMN_TASK_TYPE_NAME));
			return mBean;
		}
		
	}
}
