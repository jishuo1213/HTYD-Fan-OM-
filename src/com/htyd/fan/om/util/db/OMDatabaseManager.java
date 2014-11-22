package com.htyd.fan.om.util.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.htyd.fan.om.model.CityBean;
import com.htyd.fan.om.model.DistrictBean;
import com.htyd.fan.om.model.ProvinceBean;

public class OMDatabaseManager {

	private OMDatabaseHelper mHelper;
	private static OMDatabaseManager sManager;
	private Context mAppContext;
	private SQLiteDatabase db;

	private OMDatabaseManager(Context appContext) {
		mAppContext = appContext;
		mHelper = new OMDatabaseHelper(mAppContext);
		db = mHelper.getWritableDatabase();
	}

	public synchronized static OMDatabaseManager getInstance(Context context) {
		if (sManager == null) {
			return new OMDatabaseManager(context.getApplicationContext());
		}
		return sManager;
	}

	public long insertProvince(ProvinceBean mBean) {
		ContentValues cv = new ContentValues();
		cv.put(SQLSentence.COLUMN_PROVINCE_CODE, mBean.provinceCode);
		cv.put(SQLSentence.COLUMN_PROVINCE_NAME, mBean.provinceName);
		return db.insert(SQLSentence.TABLE_PROVINCE, null, cv);
	}

	public long insertCity(CityBean mBean) {
		ContentValues cv = new ContentValues();
		cv.put(SQLSentence.COLUMN_CITY_PROVINCE_ID, mBean.provinceID);
		cv.put(SQLSentence.COLUMN_CITY_CODE, mBean.cityCode);
		cv.put(SQLSentence.COLUMN_CITY_NAME, mBean.cityName);
		return db.insert(SQLSentence.TABLE_CITY, null, cv);
	}

	public long insertDistrict(DistrictBean mBean) {
		ContentValues cv = new ContentValues();
		cv.put(SQLSentence.COLUMN_DISTRICT_CITY_ID, mBean.cityID);
		cv.put(SQLSentence.COLUMN_DISTRICT_CODE, mBean.districtCode);
		cv.put(SQLSentence.COLUMN_DISTRICT_NAME, mBean.districtName);
		return db.insert(SQLSentence.TABLE_DISTRICT, null, cv);
	}

	public Cursor queryCursor(int parentId, int type) {
		switch (type) {
		case 0:
			return mHelper.queryProvince();
		case 1:
			return mHelper.queryCity(parentId);
		case 2:
			return mHelper.queryDistrict(parentId);
		}
		return null;
	}

	public void closeDb() {
		if (db.isOpen()) {
			db.close();
			Log.v("fanjishuo____closedb", "close");
		}
	}

	/**
	 * 打开数据库，0：read 1：write
	 * @param state
	 */
	public void openDb(int state) {
		if (state == 1) {
			if (db.isReadOnly() || !db.isOpen()) {
				db = mHelper.getWritableDatabase();
				Log.v("fanjishuo____opendb", "writeable");
				return;
			}
		}
		if (!db.isOpen()) {
			Log.v("fanjishuo_____opendb", "readable");
			db = mHelper.getReadableDatabase();
		}
	}
}
