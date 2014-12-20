package com.htyd.fan.om.util.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.model.AttendBean;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.model.TaskProcessBean;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskCursor;

public class OMUserDatabaseManager {

	private OMUserDatabaseHelper mHelper;
	private static OMUserDatabaseManager sManager;
	private Context mAppContext;
	private SQLiteDatabase db;

	private OMUserDatabaseManager(Context context) {
		mAppContext = context;
		mHelper = OMUserDatabaseHelper.getInstance(mAppContext);
		db = mHelper.getWritableDatabase();
	}

	public synchronized static OMUserDatabaseManager getInstance(Context context) {
		if (sManager == null) {
			Log.e("fanjishuo______getInstance", "new db manager");
			sManager = new OMUserDatabaseManager(context.getApplicationContext());
		}
		return sManager;
	}
	/*-------------------------------------------数据库插入操作---------------------------------------------*/
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
		cv.put(SQLSentence.COLUMN_CHOOSE_LOCATION, mBean.choseLocation);
		cv.put(SQLSentence.COLUMN_ATTEND_STATE,mBean.state);
		return db.insert(SQLSentence.TABLE_CHECK, null, cv);
	}

	public long insertTaskBean(TaskDetailBean mBean) {
		ContentValues cv = new ContentValues();
		cv.put(SQLSentence.COLUMN_TASK_NET_ID, mBean.taskNetId);
		cv.put(SQLSentence.COLUMN_TASK_WORK_LOCATION, mBean.workLocation);
		cv.put(SQLSentence.COLUMN_TASK_INSTALL_LOCATION, mBean.installLocation);
		cv.put(SQLSentence.COLUMN_TASK_DESCRIPTION, mBean.taskDescription);
		cv.put(SQLSentence.COLUMN_TASK_TITLE, mBean.taskTitle);
		cv.put(SQLSentence.COLUMN_TASK_RECIPIENT_NAME, mBean.recipientsName);
		cv.put(SQLSentence.COLUMN_TASK_RECIPIENT_PHONE, mBean.recipientPhone);
		cv.put(SQLSentence.COLUMN_TASK_EQUIPMENT, mBean.equipment);
		cv.put(SQLSentence.COLUMN_TASK_PRODUCT_TYPE, mBean.productType);
		cv.put(SQLSentence.COLUMN_TASK_PLAN_STARTTIME, mBean.planStartTime);
		cv.put(SQLSentence.COLUMN_TASK_PLAN_ENDTIME, mBean.planEndTime);
		cv.put(SQLSentence.COLUMN_TASK_CREATE_TIME, mBean.saveTime);
		cv.put(SQLSentence.COLUMN_TASK_STATE, mBean.taskState);
		cv.put(SQLSentence.COLUMN_TASK_TYPE, mBean.taskType);
		return db.insert(SQLSentence.TABLE_TASK, null, cv);
	}

	public long insertTaskProcessBean(TaskProcessBean mBean) {
		ContentValues cv = new ContentValues();
		cv.put(SQLSentence.COLUMN_TASKPROCESS_TASK_ID, mBean.taskid);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_TASK_STATE, mBean.taskState);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_STARTTIME, mBean.startTime);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_ENDTIME, mBean.endTime);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_CREATE_TIME, mBean.createTime);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_TASK_PROCESSWHAT,
				mBean.processContent);
		return db.insert(SQLSentence.TABLE_TASK_PROCESS, null, cv);
	}

	public long insertTaskAccessoryBean(AffiliatedFileBean mBean) {
		ContentValues cv = new ContentValues();
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_PATH, mBean.filePath);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_STATE, mBean.fileState);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_SOURCE, mBean.fileSource);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_TASKID, mBean.taskId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_NET_ID, mBean.netId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_FILE_SIZE, mBean.fileSize);
		return db.insert(SQLSentence.TABLE_TASK_ACCESSORY, null, cv);
	}
	/*-------------------------------------------数据库修改操作---------------------------------------------*/
	public long updateTask(TaskDetailBean mBean){
		ContentValues cv = new ContentValues();
		cv.put(SQLSentence.COLUMN_TASK_WORK_LOCATION, mBean.workLocation);
		cv.put(SQLSentence.COLUMN_TASK_INSTALL_LOCATION, mBean.installLocation);
		cv.put(SQLSentence.COLUMN_TASK_DESCRIPTION, mBean.taskDescription);
		cv.put(SQLSentence.COLUMN_TASK_RECIPIENT_NAME, mBean.recipientsName);
		cv.put(SQLSentence.COLUMN_TASK_RECIPIENT_PHONE, mBean.recipientPhone);
		cv.put(SQLSentence.COLUMN_TASK_EQUIPMENT, mBean.equipment);
		cv.put(SQLSentence.COLUMN_TASK_PRODUCT_TYPE, mBean.productType);
		cv.put(SQLSentence.COLUMN_TASK_PLAN_STARTTIME, mBean.planStartTime);
		cv.put(SQLSentence.COLUMN_TASK_PLAN_ENDTIME, mBean.planEndTime);
		cv.put(SQLSentence.COLUMN_TASK_STATE, mBean.taskState);
		cv.put(SQLSentence.COLUMN_TASK_TYPE, mBean.taskType);
		openDb(1);
		return db.update(SQLSentence.TABLE_TASK, cv,
				SQLSentence.COLUMN_TASK_NET_ID + "= ?",
				new String[] { String.valueOf(mBean.taskNetId) });
	}
	
	public long updateTaskAccessoryBean(AffiliatedFileBean mBean) {
		ContentValues cv = new ContentValues();
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_PATH, mBean.filePath);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_STATE, mBean.fileState);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_SOURCE, mBean.fileSource);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_TASKID, mBean.taskId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_NET_ID, mBean.netId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_FILE_SIZE, mBean.fileSize);
		openDb(1);
		return db.update(SQLSentence.TABLE_TASK_ACCESSORY, cv, SQLSentence.COLUMN_TASK_ACCESSORY_NET_ID+" = ?",
				new String[] { String.valueOf(mBean.netId) });
	}
	
	public long updateAccessoryBean(AffiliatedFileBean mBean) {
		ContentValues cv = new ContentValues();
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_PATH, mBean.filePath);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_STATE, mBean.fileState);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_SOURCE, mBean.fileSource);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_TASKID, mBean.taskId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_NET_ID, mBean.netId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_FILE_SIZE, mBean.fileSize);
		openDb(1);
		return db.update(SQLSentence.TABLE_TASK_ACCESSORY, cv, SQLSentence.COLUMN_TASK_ACCESSORY_PATH+" = ?",
				new String[] {mBean.filePath});
	}
	
	/*-------------------------------------------数据库删除操作---------------------------------------------*/
	
	public long deleteTask(TaskDetailBean mBean){
		openDb(1);
		return db.delete(SQLSentence.TABLE_TASK, "_id = ?", new String [] {String.valueOf(mBean.taskLocalId)});
	}
	
	public long detelteTaskAccessory(AffiliatedFileBean mBean) {
		openDb(1);
		return db.delete(SQLSentence.TABLE_TASK_ACCESSORY,
				SQLSentence.COLUMN_TASK_ACCESSORY_PATH + "= ?",
				new String[] { String.valueOf(mBean.filePath) });
	}
	
	public long detelteTaskAccessory(String path) {
		openDb(1);
		return db.delete(SQLSentence.TABLE_TASK_ACCESSORY,
				SQLSentence.COLUMN_TASK_ACCESSORY_PATH + "= ?",
				new String[] { String.valueOf(path) });
	}

	/*-------------------------------------------数据库查询操作---------------------------------------------*/
	public Cursor queryAttendCursor(int monthNum) {
		return mHelper.queryMonthAttend(monthNum);
	}

	public Cursor queryUserTask() {
		return mHelper.queryUserTask();
	}

	public Cursor queryProcessByTaskId(int taskId) {
		return mHelper.queryProcessByTaskId(taskId);
	}

	public Cursor queryAccessoryByTaskId(int taskId) {
		return mHelper.queryAccessoryByTaskId(taskId);
	}
	
	public Cursor queryUnLoadAccessory(){
		return mHelper.queryUnLoadFile();
	}
	
	public TaskDetailBean getSingleTask(int taskNetId){
		openDb(0);
		TaskCursor cursor = mHelper.queryUserSingleTask(taskNetId);
		return cursor.getTask();
	}
	/*--------------------------------------------------------------------------------------------------------*/
	/**
	 * 删除数据库表
	 * @param tableName
	 */
	
	public void clearFeedTable(String tableName) {
		String sql = "DELETE FROM " + tableName + ";";
		openDb(1);
		db.execSQL(sql);
		revertSeq(tableName);
	}

	private void revertSeq(String tableName) {
		String sql = "update sqlite_sequence set seq=0 where name='"+ tableName + "'";
		db.execSQL(sql);
	}

	public void logoutDb(){
		mHelper.logout();
		closeDb();
		sManager = null;
	}
	/**
	 * 打开数据库 0：read 1：write
	 * 
	 * @param state
	 * @return
	 */

	public void openDb(int state) {
		if (state == 1) {
			if (db.isReadOnly() || !db.isOpen()) {
				db = mHelper.getWritableDatabase();
				Log.d("fanjishuo____openDb", "open");
			}
		}
		if (!db.isOpen()) {
			db = mHelper.getReadableDatabase();
			Log.d("fanjishuo____openDb", "open");
		}
	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDb() {
		if (db.isOpen()) {
			db.close();
			Log.d("fanjishuo____closeDb", "close");
		}
	}
}
