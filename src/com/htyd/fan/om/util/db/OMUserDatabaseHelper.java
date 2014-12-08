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

	public OMUserDatabaseHelper(Context context) {
		super(context, Preferences.getUserId(context) + "_om.sqlite", null,
				VERSION);
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
				new String[] { SQLSentence.COLUMN_MONTH },
				SQLSentence.COLUMN_MONTH + "= ?",
				new String[] { String.valueOf(monthNum) }, null, null, null);
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
			mBean.addState = getString(getColumnIndex(SQLSentence.COLUMN_ADDSTATE));
			mBean.addSort = getString(getColumnIndex(SQLSentence.COLUMN_ADDSORT));
			mBean.userName = getString(getColumnIndex(SQLSentence.COLUMN_USERNAME));
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
			mBean.workLocation = getString(getColumnIndex(SQLSentence.COLUMN_TASK_WORK_LOCATION));
			mBean.installLocation = getString(getColumnIndex(SQLSentence.COLUMN_TASK_INSTALL_LOCATION));
			mBean.taskDescription = getString(getColumnIndex(SQLSentence.COLUMN_TASK_DESCRIPTION));
			mBean.taskContacts = getString(getColumnIndex(SQLSentence.COLUMN_TASK_CONTACTS));
			mBean.contactsPhone = getString(getColumnIndex(SQLSentence.COLUMN_TASK_CONTACT_PHONE));
			mBean.recipientsName = getString(getColumnIndex(SQLSentence.COLUMN_TASK_RECIPIENT_NAME));
			mBean.recipientPhone = getString(getColumnIndex(SQLSentence.COLUMN_TASK_RECIPIENT_PHONE));
			mBean.taskAccessory = getString(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY));
			mBean.equipment = getString(getColumnIndex(SQLSentence.COLUMN_TASK_EQUIPMENT));
			mBean.productType = getString(getColumnIndex(SQLSentence.COLUMN_TASK_PRODUCT_TYPE));
			mBean.planStartTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASK_PLAN_STARTTIME));
			mBean.planEndTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASK_PLAN_ENDTIME));
			mBean.taskState = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_STATE));
			mBean.taskType = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_TYPE));
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
			mBean.processPerson = getString(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_PROCESS_PERSON));
			mBean.personPhone = getString(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_PERSON_PHONE));
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
			mBean.fileType = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_TYPE));
			mBean.taskId = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_TASKID));
			return mBean;
		}
	}
}
