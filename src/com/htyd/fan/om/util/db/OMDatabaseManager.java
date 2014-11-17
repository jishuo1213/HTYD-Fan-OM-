package com.htyd.fan.om.util.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.htyd.fan.om.model.CityBean;
import com.htyd.fan.om.model.DistrictBean;
import com.htyd.fan.om.model.ProvinceBean;
import com.htyd.fan.om.util.db.OMDatabaseHelper.CityCursor;
import com.htyd.fan.om.util.db.OMDatabaseHelper.DistrictCursor;
import com.htyd.fan.om.util.db.OMDatabaseHelper.ProvinceCursor;

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
			return new OMDatabaseManager(context);
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
		return db.insert(SQLSentence.TABLE_PROVINCE, null, cv);
	}

	public long insertDistrict(DistrictBean mBean) {
		ContentValues cv = new ContentValues();
		cv.put(SQLSentence.COLUMN_DISTRICT_CITY_ID, mBean.cityID);
		cv.put(SQLSentence.COLUMN_DISTRICT_CODE, mBean.districtCode);
		cv.put(SQLSentence.COLUMN_DISTRICT_NAME, mBean.districtName);
		return db.insert(SQLSentence.TABLE_PROVINCE, null, cv);
	}

	public List<ProvinceBean> queryProvinceList() {
		List<ProvinceBean> listProvince = new ArrayList<ProvinceBean>();
		ProvinceCursor mCursor = mHelper.queryProvince();
		if (mCursor != null && mCursor.moveToFirst()) {
			while (mCursor.moveToNext()) {
				listProvince.add(mCursor.getProvince());
			}
			mCursor.close();
			return listProvince;
		}
		return null;
	}

	public List<CityBean> queryCityList(int provinceId) {
		List<CityBean> listProvince = new ArrayList<CityBean>();
		CityCursor mCursor = mHelper.queryCity(provinceId);
		if (mCursor != null && mCursor.moveToFirst()) {
			while (mCursor.moveToNext()) {
				listProvince.add(mCursor.getCity());
			}
			mCursor.close();
			return listProvince;
		}
		return null;
	}

	public List<DistrictBean> queryDistrictList(int cityId) {
		List<DistrictBean> listProvince = new ArrayList<DistrictBean>();
		DistrictCursor mCursor = mHelper.queryDistrict(cityId);
		if (mCursor != null && mCursor.moveToFirst()) {
			while (mCursor.moveToNext()) {
				listProvince.add(mCursor.getDistrict());
			}
			mCursor.close();
			return listProvince;
		}
		return null;
	}

	public void closeDb() {
		db.close();
	}

	public void openDb() {
		db = mHelper.getWritableDatabase();
	}
}
