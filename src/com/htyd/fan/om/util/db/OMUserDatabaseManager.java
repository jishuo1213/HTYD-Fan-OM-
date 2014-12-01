package com.htyd.fan.om.util.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.htyd.fan.om.model.AttendBean;
import com.htyd.fan.om.model.TaskDetailBean;

public class OMUserDatabaseManager {

	private OMUserDatabaseHelper mHelper;
	private static OMUserDatabaseManager sManager;
	private Context mAppContext;
	private SQLiteDatabase db;

	private OMUserDatabaseManager(Context context) {
		mAppContext = context;
		mHelper = new OMUserDatabaseHelper(mAppContext);
		db = mHelper.getWritableDatabase();
	}

	public synchronized static OMUserDatabaseManager getInstance(Context context) {
		if (sManager == null) {
			return new OMUserDatabaseManager(context.getApplicationContext());
		}
		return sManager;
	}

	public long insertAttendBean(AttendBean mBean) {
		ContentValues cv = new ContentValues();
		StringBuilder sb = new StringBuilder();
		sb.append(mBean.province).append("|").append(mBean.city).append("|")
				.append(mBean.district).append("|").append(mBean.street)
				.append("|").append(mBean.streetNum);
		cv.put(SQLSentence.COLUMN_ADDRESS, sb.toString());
		cv.put(SQLSentence.COLUMN_LATITUDE, mBean.latitude);
		cv.put(SQLSentence.COLUMN_LONGITUDE, mBean.longitude);
		cv.put(SQLSentence.COLUMN_MONTH, mBean.month);
		cv.put(SQLSentence.COLUMN_TIME, mBean.time);
		cv.put(SQLSentence.COLUMN_USERNAME, mBean.userName);
		cv.put(SQLSentence.COLUMN_ADDSTATE, mBean.addState);
		cv.put(SQLSentence.COLUMN_ADDSORT, mBean.addSort);
		return db.insert(SQLSentence.TABLE_CHECK, null, cv);
	}

	public long insertTaskBean(TaskDetailBean mBean) {
		ContentValues cv = new ContentValues();
		StringBuilder sb = new StringBuilder();
		sb.append(mBean.workProvince).append("|").append(mBean.workCity)
				.append("|").append(mBean.workDistrict);
		cv.put(SQLSentence.COLUMN_TASK_WORK_LOCATION, sb.toString());
		cv.put(SQLSentence.COLUMN_TASK_INSTALL_LOCATION, mBean.installLocation);
		cv.put(SQLSentence.COLUMN_TASK_DESCRIPTION, mBean.taskDescription);
		cv.put(SQLSentence.COLUMN_TASK_CONTACTS, mBean.taskContacts);
		cv.put(SQLSentence.COLUMN_TASK_CONTACT_PHONE, mBean.contactsPhone);
		cv.put(SQLSentence.COLUMN_TASK_RECIPIENT_NAME, mBean.recipientsName);
		cv.put(SQLSentence.COLUMN_TASK_RECIPIENT_PHONE, mBean.recipientPhone);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY, mBean.taskAccessory);
		cv.put(SQLSentence.COLUMN_TASK_EQUIPMENT, mBean.equipment);
		cv.put(SQLSentence.COLUMN_TASK_PRODUCT_TYPE, mBean.productType);
		cv.put(SQLSentence.COLUMN_TASK_PLAN_STARTTIME, mBean.planStartTime);
		cv.put(SQLSentence.COLUMN_TASK_PLAN_ENDTIME, mBean.planEndTime);
		cv.put(SQLSentence.COLUMN_TASK_STATE, mBean.taskState);
		cv.put(SQLSentence.COLUMN_TASK_TYPE, mBean.taskType);
		return db.insert(SQLSentence.TABLE_TASK, null, cv);
	}

	public Cursor queryAttendCursor(int monthNum) {
		return mHelper.queryMonthAttend(monthNum);
	}

	public Cursor queryTaskCursorByState(int state) {
		return mHelper.queryTaskByState(state);
	}

	public void closeDb() {
		if (db.isOpen()) {
			db.close();
			Log.v("fanjishuo____closedb", "close");
		}
	}

	/**
	 * 打开数据库 0：read 1：write
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
