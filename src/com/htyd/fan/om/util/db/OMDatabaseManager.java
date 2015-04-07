package com.htyd.fan.om.util.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.CommonDataBean;
import com.htyd.fan.om.util.db.OMDatabaseHelper.CityCursor;
import com.htyd.fan.om.util.db.OMDatabaseHelper.DistrictCursor;
import com.htyd.fan.om.util.db.OMDatabaseHelper.ProvinceCursor;

public class OMDatabaseManager {

	  public static final String DB_NAME = "city_cn.s3db";
	    public static final String PACKAGE_NAME = "com.htyd.fan.om";
	    public static final String DB_PATH = "/data"
	            + Environment.getDataDirectory().getAbsolutePath() + "/"+ PACKAGE_NAME;
	
	private OMDatabaseHelper mHelper;
	private static OMDatabaseManager sManager;
	private Context mAppContext;
	private SQLiteDatabase omDb;
	private String cityDbPath = DB_PATH + "/" + DB_NAME;
	private SQLiteDatabase cityDb;

	private OMDatabaseManager(Context appContext) {
		mAppContext = appContext;
		mHelper =  OMDatabaseHelper.getInstance(mAppContext);
		omDb = mHelper.getWritableDatabase();
		openCItyDataBase();
	}

	public synchronized static OMDatabaseManager getInstance(Context context) {
		if (sManager == null) {
			sManager =  new OMDatabaseManager(context.getApplicationContext());
		}
		return sManager;
	}
	
	
	public long insertTaskType(CommonDataBean mBean) {
		ContentValues cv = new ContentValues();
		cv.put(SQLSentence.COLUMN_TASK_TYPE_CAT, mBean.typeDescription);
		cv.put(SQLSentence.COLUMN_TASK_TYPE_NAME, mBean.typeName);
		return omDb.insert(SQLSentence.TABLE_TASK_TYPE, null, cv);
	}
	
	public Cursor queryCursor(String parentId, int type) {
		if(cityDb == null){
			return null;
		}
		switch (type) {
		case 0:
			return queryProvince();
		case 1:
			return queryCity(parentId);
		case 2:
			return queryDistrict(parentId);
		}
		return null;
	}
	
	public long deleteType(String cat){
		openDb(1);
		return omDb.delete(SQLSentence.TABLE_TASK_TYPE, SQLSentence.COLUMN_TASK_TYPE_CAT
				+ " = ?", new String[] { cat});
	}
	
	public Cursor queryTaskType(String type){
		return mHelper.queryTaskType(type);
	}
	
	private void openCItyDataBase() {
		File file = new File(cityDbPath);
		try {
			if (!file.exists()) {
				InputStream is = mAppContext.getResources().openRawResource(
						R.raw.city);
				FileOutputStream fos = new FileOutputStream(cityDbPath);
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
					fos.flush();
				}
				fos.close();
				is.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		cityDb = SQLiteDatabase.openOrCreateDatabase(cityDbPath, null);
	}
	
	public void clearFeedTable(String tableName) {
		openDb(1);
		String sql = "DELETE FROM " + tableName + ";";
		omDb.execSQL(sql);
		revertSeq(tableName);
	}

	private void revertSeq(String tableName) {
		String sql = "update sqlite_sequence set seq=0 where name='"+ tableName + "'";
		omDb.execSQL(sql);
	}
	
	
	private Cursor queryProvince(){
		Cursor wrapped = cityDb.query("province", null, null, null, null, null, null);
		return new ProvinceCursor(wrapped);
	}
	
	private Cursor queryCity(String parentId){
		Cursor wrapped = cityDb.query("city", null, "pcode = ?", new String[]{parentId}, null, null, null);
		return new CityCursor(wrapped);
	}
	
	private Cursor queryDistrict(String parentId){
		Cursor wrapped = cityDb.query("district", null, "pcode = ?", new String[]{parentId}, null, null, null);
		return new DistrictCursor(wrapped);
	}

	public void closeDb() {
		if (omDb.isOpen()) {
			omDb.close();
		}
	}

	/**
	 * 打开数据库，0：read 1：write
	 * @param state
	 */
	public void openDb(int state) {
		if (state == 1) {
			if (omDb.isReadOnly() || !omDb.isOpen()) {
				omDb = mHelper.getWritableDatabase();
				return;
			}
		}
		if (!omDb.isOpen()) {
			omDb = mHelper.getReadableDatabase();
		}
	}
}
