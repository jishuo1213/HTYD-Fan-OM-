package com.htyd.fan.om.util.db;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.model.AttendBean;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.model.TaskProcessBean;
import com.htyd.fan.om.util.base.Preferences;

public class OMUserDatabaseHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;
	private static OMUserDatabaseHelper sHelper;

	private OMUserDatabaseHelper(Context context) {
		super(context, Preferences.getUserId(context) + "_om.sqlite", null,
				VERSION);
	}
	
	public static OMUserDatabaseHelper getInstance(Context context){
		if(sHelper == null){
			sHelper = new OMUserDatabaseHelper(context);
		}
		return sHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("fanjishuo____onCreate", db.getPath());
		db.execSQL(SQLSentence.CREATE_TABLE_CHECK);
		db.execSQL(SQLSentence.CREATE_TABLE_TASK);
		db.execSQL(SQLSentence.CREATE_TABLE_TASK_PROCESS);
		db.execSQL(SQLSentence.CREATE_TABLE_TASK_ACCESSORY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public AttendCursor queryMonthAttend(int monthNum) {
		Cursor wrapper = getReadableDatabase().query(SQLSentence.TABLE_CHECK,
				null,
				SQLSentence.COLUMN_MONTH + "= ?",
				new String[] { String.valueOf(monthNum) }, null, null, null);
		Log.i("fanjishuo____queryMonthAttend", "monthNum"+monthNum+wrapper.getCount());
		return new AttendCursor(wrapper);
	}

	public TaskCursor queryTaskByState(int state) {
		if (state == -1) {
			Cursor wrapper = getReadableDatabase().query(
					SQLSentence.TABLE_TASK, null, null, null, null, null, null);
			return new TaskCursor(wrapper);
		}
		Cursor wrapper = getReadableDatabase().query(SQLSentence.TABLE_TASK,
				new String[] { SQLSentence.COLUMN_TASK_STATE },
				SQLSentence.COLUMN_TASK_STATE + "= ?",
				new String[] { String.valueOf(state) }, null, null, null);
		return new TaskCursor(wrapper);
	}

	public TaskProcessCursor queryProcessByTaskId(int taskId) {
		Cursor wrapper = getReadableDatabase().query(
				SQLSentence.TABLE_TASK_PROCESS, null,
				SQLSentence.COLUMN_TASKPROCESS_TASK_ID + "= ?",
				new String[] { String.valueOf(taskId) }, null, null, null);
		return new TaskProcessCursor(wrapper);
	}

	public TaskAccessoryCursor queryAccessoryByTaskId(int taskId) {
		Cursor wrapper = getReadableDatabase().query(
				SQLSentence.TABLE_TASK_ACCESSORY, null,
				SQLSentence.COLUMN_TASK_ACCESSORY_TASKID + "= ?",
				new String[] { String.valueOf(taskId) }, null, null, null);
		return new TaskAccessoryCursor(wrapper);
	}

	public static class AttendCursor extends CursorWrapper {

		public AttendCursor(Cursor cursor) {
			super(cursor);
		}

		public AttendBean getAttend() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			AttendBean mBean = new AttendBean();
			String address = getString(getColumnIndex(SQLSentence.COLUMN_ADDRESS));
			String[] temp = address.split("|");
			mBean.province = temp[0];
			mBean.city = temp[1];
			mBean.district = temp[2];
			mBean.street = temp[3];
			mBean.streetNum = temp[4];
			mBean.latitude = getDouble(getColumnIndex(SQLSentence.COLUMN_LATITUDE));
			mBean.longitude = getDouble(getColumnIndex(SQLSentence.COLUMN_LONGITUDE));
			mBean.time = getLong(getColumnIndex(SQLSentence.COLUMN_TIME));
			mBean.choseLocation = getString(getColumnIndex(SQLSentence.COLUMN_CHOOSE_LOCATION));
			mBean.state = getInt(getColumnIndex(SQLSentence.COLUMN_ATTEND_STATE));
			return mBean;
		}
	}

	public static class TaskCursor extends CursorWrapper {

		public TaskCursor(Cursor cursor) {
			super(cursor);
		}

		public TaskDetailBean getTask() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			TaskDetailBean mBean = new TaskDetailBean();
			mBean.taskLocalId = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ID));
			mBean.taskNetId = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_NET_ID));
			mBean.workLocation = getString(getColumnIndex(SQLSentence.COLUMN_TASK_WORK_LOCATION));
			mBean.installLocation = getString(getColumnIndex(SQLSentence.COLUMN_TASK_INSTALL_LOCATION));
			mBean.taskDescription = getString(getColumnIndex(SQLSentence.COLUMN_TASK_DESCRIPTION));
/*			mBean.taskContacts = getString(getColumnIndex(SQLSentence.COLUMN_TASK_CONTACTS));
			mBean.contactsPhone = getString(getColumnIndex(SQLSentence.COLUMN_TASK_CONTACT_PHONE));*/
			mBean.recipientsName = getString(getColumnIndex(SQLSentence.COLUMN_TASK_RECIPIENT_NAME));
			mBean.recipientPhone = getString(getColumnIndex(SQLSentence.COLUMN_TASK_RECIPIENT_PHONE));
			mBean.taskAccessory = getString(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY));
			mBean.equipment = getString(getColumnIndex(SQLSentence.COLUMN_TASK_EQUIPMENT));
			mBean.productType = getString(getColumnIndex(SQLSentence.COLUMN_TASK_PRODUCT_TYPE));
			mBean.planStartTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASK_PLAN_STARTTIME));
			mBean.planEndTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASK_PLAN_ENDTIME));
			mBean.taskState = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_STATE));
			mBean.taskType = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_TYPE));
			mBean.saveTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASK_CREATE_TIME));
			mBean.taskTitle = getString(getColumnIndex(SQLSentence.COLUMN_TASK_TITLE));
			Log.i("fanjishuo____getTask", mBean.taskType + "");
			return mBean;
		}
	}

	public static class TaskProcessCursor extends CursorWrapper {

		public TaskProcessCursor(Cursor cursor) {
			super(cursor);
		}

		public TaskProcessBean getTaskProcess() {
			TaskProcessBean mBean = new TaskProcessBean();
			mBean.taskid = getInt(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_TASK_ID));
			mBean.taskState = getInt(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_TASK_STATE));
			mBean.startTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_STARTTIME));
			mBean.endTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_ENDTIME));
			mBean.createTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_CREATE_TIME));
			mBean.processContent = getString(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_TASK_PROCESSWHAT));
			return mBean;
		}
	}

	public static class TaskAccessoryCursor extends CursorWrapper {

		public TaskAccessoryCursor(Cursor cursor) {
			super(cursor);
		}

		public AffiliatedFileBean getAccessory() {
			AffiliatedFileBean mBean = new AffiliatedFileBean();
			mBean.filePath = getString(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_PATH));
			mBean.fileState = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_STATE));
			mBean.fileSource = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_SOURCE));
			mBean.taskId = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_TASKID));
			mBean.netId = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_NET_ID));
			mBean.fileSize = getLong(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_FILE_SIZE));
			return mBean;
		}
	}
}
