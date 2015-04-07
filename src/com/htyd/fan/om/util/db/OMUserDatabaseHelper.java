package com.htyd.fan.om.util.db;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.model.AttendBean;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.model.TaskListBean;
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
		db.execSQL(SQLSentence.CREATE_TABLE_CHECK);
		db.execSQL(SQLSentence.CREATE_TABLE_TASK);
		db.execSQL(SQLSentence.CREATE_TABLE_TASK_PROCESS);
		db.execSQL(SQLSentence.CREATE_TABLE_TASK_ACCESSORY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public AttendCursor queryMonthAttend(int monthNum ,int year) {
		Cursor wrapper = getReadableDatabase().query(SQLSentence.TABLE_CHECK,
				null,
				SQLSentence.COLUMN_MONTH + "=? and " + SQLSentence.COLUMN_ATTEND_YEAR + "=? ",
				new String[] { String.valueOf(monthNum),String.valueOf(year) }, null, null, null);
		return new AttendCursor(wrapper);
	}

	public TaskCursor queryUserTask() {
			Cursor wrapper = getReadableDatabase().query(
					SQLSentence.TABLE_TASK, null, null, null, null, null, SQLSentence.COLUMN_TASK_CREATE_TIME+" DESC");
			return new TaskCursor(wrapper);
	}
	
	public TaskCursor queryUserSingleTask(int taskLocalId) {
		Cursor wrapper = getReadableDatabase().query(SQLSentence.TABLE_TASK,
				null, SQLSentence.COLUMN_TASK_ID + "= ?",
				new String[] { String.valueOf(taskLocalId) }, null, null, null);
		return new TaskCursor(wrapper);
}

	public TaskProcessCursor queryProcessByTaskNetId(int taskId) {
		Cursor wrapper = getReadableDatabase().query(
				SQLSentence.TABLE_TASK_PROCESS, null,
				SQLSentence.COLUMN_TASKPROCESS_TASK_ID + "= ?",
				new String[] { String.valueOf(taskId) }, null, null, null);
		return new TaskProcessCursor(wrapper);
	}
	
	public TaskProcessCursor queryProcessByTaskLocalId(int taskId) {
		Cursor wrapper = getReadableDatabase().query(
				SQLSentence.TABLE_TASK_PROCESS, null,
				SQLSentence.COLUMN_TASKPROCESS_TASK_LOCAL_ID + "= ?",
				new String[] { String.valueOf(taskId) }, null, null, null);
		return new TaskProcessCursor(wrapper);
	}

	public TaskAccessoryCursor queryAccessoryByTaskNetId(int taskNetId) {
		Cursor wrapper = getReadableDatabase().query(
				SQLSentence.TABLE_TASK_ACCESSORY, null,
				SQLSentence.COLUMN_TASK_ACCESSORY_TASK_NET_ID + "= ?",
				new String[] { String.valueOf(taskNetId) }, null, null, null);
		return new TaskAccessoryCursor(wrapper);
	}
	
	public TaskAccessoryCursor queryAccessoryByTaskLocalId(int taskLocalId) {
		Cursor wrapper = getReadableDatabase().query(
				SQLSentence.TABLE_TASK_ACCESSORY, null,
				SQLSentence.COLUMN_TASK_ACCESSORY_TASK_LOCAL_ID + "= ?",
				new String[] { String.valueOf(taskLocalId) }, null, null, null);
		return new TaskAccessoryCursor(wrapper);
	}

	public TaskAccessoryCursor queryUnLoadFile(){
		Cursor wrapper = getReadableDatabase().query(
				SQLSentence.TABLE_TASK_ACCESSORY, null,
				SQLSentence.COLUMN_TASK_ACCESSORY_SOURCE+ "= ?"
				+" and "+SQLSentence.COLUMN_TASK_ACCESSORY_STATE + "=?",
				new String[] { String.valueOf(0),String.valueOf(0) }, null, null, null);
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
			mBean.address = getString(getColumnIndex(SQLSentence.COLUMN_ADDRESS));
			mBean.latitude = getDouble(getColumnIndex(SQLSentence.COLUMN_LATITUDE));
			mBean.longitude = getDouble(getColumnIndex(SQLSentence.COLUMN_LONGITUDE));
			mBean.time = getLong(getColumnIndex(SQLSentence.COLUMN_TIME));
			mBean.choseLocation = getString(getColumnIndex(SQLSentence.COLUMN_CHOOSE_LOCATION));
			mBean.state = getInt(getColumnIndex(SQLSentence.COLUMN_ATTEND_STATE));
			mBean.attendId = getInt(getColumnIndex(SQLSentence.COLUMN_CHECK_ID));
			mBean.attendRemark = getString(getColumnIndex(SQLSentence.COLUMN_ATTEND_REMARK));
			mBean.year = getInt(getColumnIndex(SQLSentence.COLUMN_ATTEND_YEAR));
			return mBean;
		}
	}

	public static class TaskCursor extends CursorWrapper {

		public TaskCursor(Cursor cursor) {
			super(cursor);
		}

		public TaskDetailBean getTask() {
			moveToFirst();
			if (getCount() == 0){
				return null;
			}
			TaskDetailBean mBean = new TaskDetailBean();
			mBean.taskLocalId = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ID));
			mBean.taskNetId = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_NET_ID));
			mBean.workLocation = getString(getColumnIndex(SQLSentence.COLUMN_TASK_WORK_LOCATION));
			mBean.installLocation = getString(getColumnIndex(SQLSentence.COLUMN_TASK_INSTALL_LOCATION));
			mBean.taskDescription = getString(getColumnIndex(SQLSentence.COLUMN_TASK_DESCRIPTION));
			mBean.recipientsName = getString(getColumnIndex(SQLSentence.COLUMN_TASK_RECIPIENT_NAME));
			mBean.recipientPhone = getString(getColumnIndex(SQLSentence.COLUMN_TASK_RECIPIENT_PHONE));
			mBean.equipmentNumber = getString(getColumnIndex(SQLSentence.COLUMN_TASK_EQUIPMENT_NUMBER));
			mBean.equipmentType = getString(getColumnIndex(SQLSentence.COLUMN_TASK_EQUIPMENT_TYPE));
			mBean.planStartTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASK_PLAN_STARTTIME));
			mBean.planEndTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASK_PLAN_ENDTIME));
			mBean.taskState = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_STATE));
			mBean.taskType = getString(getColumnIndex(SQLSentence.COLUMN_TASK_TYPE));
			mBean.saveTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASK_CREATE_TIME));
			mBean.taskTitle = getString(getColumnIndex(SQLSentence.COLUMN_TASK_TITLE));
			mBean.isSyncToServer = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ASYNC_STATE));
			
			mBean.taskContact = getString(getColumnIndex(SQLSentence.COLUMN_TASK_NEW_CONTACT));
			mBean.contactPhone = getString(getColumnIndex(SQLSentence.COLUMN_TASK_NEW_CONTACT_PHONE));
			mBean.assetNumber = getString(getColumnIndex(SQLSentence.COLUMN_TASK_NEW_ASSET_NUMBER));
			mBean.customerUnit = getString(getColumnIndex(SQLSentence.COLUMN_TASK_NEW_CUSTOMER_UNIT));
			mBean.equipmentFactory = getString(getColumnIndex(SQLSentence.COLUMN_TASK_NEW_EQUIPMENT_FACTORY));
			mBean.equipmentRemark = getString(getColumnIndex(SQLSentence.COLUMN_TASK_NEW_EQUIPMENT_REMARK));
			mBean.logicalAddress = getString(getColumnIndex(SQLSentence.COLUMN_TASK_NEW_LOGICAL_ADDRESS));
			mBean.taskRemark = getString(getColumnIndex(SQLSentence.COLUMN_TASK_NEW_REMARK));
			mBean.taskInstallInfo = getString(getColumnIndex(SQLSentence.COLUMN_TASK_INSTALL_INFO));
			return mBean;
		}
		
		public TaskListBean getTaskListBean(){
			if (isBeforeFirst() || isAfterLast())
				return null;
			TaskListBean mBean = new TaskListBean();
			mBean.taskNetId = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_NET_ID));
			mBean.createTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASK_CREATE_TIME));
			mBean.taskTitle = getString(getColumnIndex(SQLSentence.COLUMN_TASK_TITLE));
			mBean.taskState = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_STATE));
			mBean.isSyncToServer = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ASYNC_STATE));
			mBean.taskLocalId = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ID));
			return mBean;
		}
	}

	public static class TaskProcessCursor extends CursorWrapper {

		public TaskProcessCursor(Cursor cursor) {
			super(cursor);
		}

		public TaskProcessBean getTaskProcess() {
			TaskProcessBean mBean = new TaskProcessBean();
			mBean.taskNetid = getInt(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_TASK_ID));
//			mBean.taskState = getInt(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_TASK_STATE));
			mBean.startTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_STARTTIME));
			mBean.endTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_ENDTIME));
			mBean.createTime = getLong(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_CREATE_TIME));
			mBean.processContent = getString(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_TASK_PROCESSWHAT));
			mBean.taskLocalId = getInt(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_TASK_LOCAL_ID));
			mBean.isSyncToServer = getInt(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_ASYNC_STATE));
			mBean.processNetId = getInt(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_NET_ID));
			mBean.processLocalId = getInt(getColumnIndex(SQLSentence.COLUMN_TASKPROCESS_ID));
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
			mBean.taskId = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_TASK_NET_ID));
			mBean.netId = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_NET_ID));
			mBean.fileSize = getLong(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_FILE_SIZE));
			mBean.fileDescription = getString(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_FILE_DESCRIPTION));
			mBean.taskLocalId = getInt(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_TASK_LOCAL_ID));
			mBean.longitude = getDouble(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_LONGITUDE));
			mBean.latitude = getDouble(getColumnIndex(SQLSentence.COLUMN_TASK_ACCESSORY_LATITUDE));
			return mBean;
		}
	}
	
	public void logout(){
		sHelper.close();
		sHelper = null;
	}
}
