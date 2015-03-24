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
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskAccessoryCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskCursor;

public class OMUserDatabaseManager {

	private OMUserDatabaseHelper mHelper;
	private static OMUserDatabaseManager sManager;
	private Context mAppContext;
	private SQLiteDatabase db;
	private ContentValues cv;

	private OMUserDatabaseManager(Context context) {
		mAppContext = context;
		mHelper = OMUserDatabaseHelper.getInstance(mAppContext);
		db = mHelper.getWritableDatabase();
		cv = new ContentValues();
	}

	public synchronized static OMUserDatabaseManager getInstance(Context context) {
		if (sManager == null) {
			sManager = new OMUserDatabaseManager(context.getApplicationContext());
		}
		return sManager;
	}
	/*-------------------------------------------数据库插入操作---------------------------------------------*/
	public long insertAttendBean(AttendBean mBean) {
		cv.clear();
/*		StringBuilder sb = new StringBuilder();
		sb.append(mBean.province).append("|").append(mBean.city).append("|")
				.append(mBean.district).append("|").append(mBean.street)
				.append("|").append(mBean.streetNum);*/
		cv.put(SQLSentence.COLUMN_ADDRESS, mBean.address);
		cv.put(SQLSentence.COLUMN_LATITUDE, mBean.latitude);
		cv.put(SQLSentence.COLUMN_LONGITUDE, mBean.longitude);
		cv.put(SQLSentence.COLUMN_MONTH, mBean.month);
		cv.put(SQLSentence.COLUMN_TIME, mBean.time);
		cv.put(SQLSentence.COLUMN_CHOOSE_LOCATION, mBean.choseLocation);
		cv.put(SQLSentence.COLUMN_ATTEND_STATE,mBean.state);
		cv.put(SQLSentence.COLUMN_ATTEND_REMARK, mBean.attendRemark);
		cv.put(SQLSentence.COLUMN_ATTEND_YEAR, mBean.year);
		Log.i("fanjishuo_____insertAttendBean", mBean.year+"");
		return db.insert(SQLSentence.TABLE_CHECK, null, cv);
	}

	public long insertTaskBean(TaskDetailBean mBean) {
		cv.clear();
		mBean.setTaskTitle();
		cv.put(SQLSentence.COLUMN_TASK_NET_ID, mBean.taskNetId);
		cv.put(SQLSentence.COLUMN_TASK_WORK_LOCATION, mBean.workLocation);
		cv.put(SQLSentence.COLUMN_TASK_INSTALL_LOCATION, mBean.installLocation);
		cv.put(SQLSentence.COLUMN_TASK_DESCRIPTION, mBean.taskDescription);
		cv.put(SQLSentence.COLUMN_TASK_TITLE, mBean.taskTitle);
		cv.put(SQLSentence.COLUMN_TASK_RECIPIENT_NAME, mBean.recipientsName);
		cv.put(SQLSentence.COLUMN_TASK_RECIPIENT_PHONE, mBean.recipientPhone);
		cv.put(SQLSentence.COLUMN_TASK_EQUIPMENT_NUMBER, mBean.equipmentNumber);
		cv.put(SQLSentence.COLUMN_TASK_EQUIPMENT_TYPE, mBean.equipmentType);
		cv.put(SQLSentence.COLUMN_TASK_PLAN_STARTTIME, mBean.planStartTime);
		cv.put(SQLSentence.COLUMN_TASK_PLAN_ENDTIME, mBean.planEndTime);
		cv.put(SQLSentence.COLUMN_TASK_CREATE_TIME, mBean.saveTime);
		cv.put(SQLSentence.COLUMN_TASK_STATE, mBean.taskState);
		cv.put(SQLSentence.COLUMN_TASK_TYPE, mBean.taskType);
		cv.put(SQLSentence.COLUMN_TASK_ASYNC_STATE, mBean.isSyncToServer);
		
		cv.put(SQLSentence.COLUMN_TASK_NEW_ASSET_NUMBER, mBean.assetNumber);
		cv.put(SQLSentence.COLUMN_TASK_NEW_CONTACT, mBean.taskContact);
		cv.put(SQLSentence.COLUMN_TASK_NEW_CONTACT_PHONE, mBean.contactPhone);
		cv.put(SQLSentence.COLUMN_TASK_NEW_CUSTOMER_UNIT, mBean.customerUnit);
		cv.put(SQLSentence.COLUMN_TASK_NEW_EQUIPMENT_FACTORY, mBean.equipmentFactory);
		cv.put(SQLSentence.COLUMN_TASK_NEW_EQUIPMENT_REMARK, mBean.equipmentRemark);
		cv.put(SQLSentence.COLUMN_TASK_NEW_LOGICAL_ADDRESS, mBean.logicalAddress);
		cv.put(SQLSentence.COLUMN_TASK_NEW_REMARK, mBean.taskRemark);
		cv.put(SQLSentence.COLUMN_TASK_INSTALL_INFO, mBean.taskInstallInfo);
		return db.insert(SQLSentence.TABLE_TASK, null, cv);
	}

	public long insertTaskProcessBean(TaskProcessBean mBean) {
		cv.clear();
		cv.put(SQLSentence.COLUMN_TASKPROCESS_TASK_ID, mBean.taskNetid);
//		cv.put(SQLSentence.COLUMN_TASKPROCESS_TASK_STATE, mBean.taskState);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_STARTTIME, mBean.startTime);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_ENDTIME, mBean.endTime);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_CREATE_TIME, mBean.createTime);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_TASK_PROCESSWHAT,mBean.processContent);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_TASK_LOCAL_ID, mBean.taskLocalId);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_ASYNC_STATE, mBean.isSyncToServer);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_NET_ID, mBean.processNetId);
		return db.insert(SQLSentence.TABLE_TASK_PROCESS, null, cv);
	}

	public long insertTaskAccessoryBean(AffiliatedFileBean mBean) {
		cv.clear();
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_PATH, mBean.filePath);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_STATE, mBean.fileState);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_SOURCE, mBean.fileSource);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_TASK_NET_ID, mBean.taskId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_NET_ID, mBean.netId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_FILE_SIZE, mBean.fileSize);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_FILE_DESCRIPTION, mBean.fileDescription);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_TASK_LOCAL_ID, mBean.taskLocalId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_LONGITUDE, mBean.longitude);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_LATITUDE, mBean.latitude);
		Log.i("fanjishuo____insertTaskAccessoryBean", "mBean.taskLocalId"+mBean.taskLocalId);
		return db.insert(SQLSentence.TABLE_TASK_ACCESSORY, null, cv);
	}
	/*-------------------------------------------数据库修改操作---------------------------------------------*/
	
	public long updateAttend(AttendBean mBean){
		cv.clear();
		StringBuilder sb = new StringBuilder();
		sb.append(mBean.address);
		cv.put(SQLSentence.COLUMN_ADDRESS, sb.toString());
		cv.put(SQLSentence.COLUMN_LATITUDE, mBean.latitude);
		cv.put(SQLSentence.COLUMN_LONGITUDE, mBean.longitude);
		cv.put(SQLSentence.COLUMN_MONTH, mBean.month);
		cv.put(SQLSentence.COLUMN_TIME, mBean.time);
		cv.put(SQLSentence.COLUMN_CHOOSE_LOCATION, mBean.choseLocation);
		cv.put(SQLSentence.COLUMN_ATTEND_STATE,mBean.state);
		cv.put(SQLSentence.COLUMN_ATTEND_YEAR, mBean.year);
		openDb(1);
		return db.update(SQLSentence.TABLE_CHECK, cv,
				SQLSentence.COLUMN_CHECK_ID + "=?", new String[] { String.valueOf(mBean.attendId) });
	}
	
	public long updateSyncTask(TaskDetailBean mBean){
		cv.clear();
		cv.put(SQLSentence.COLUMN_TASK_WORK_LOCATION, mBean.workLocation);
		cv.put(SQLSentence.COLUMN_TASK_INSTALL_LOCATION, mBean.installLocation);
		cv.put(SQLSentence.COLUMN_TASK_DESCRIPTION, mBean.taskDescription);
		cv.put(SQLSentence.COLUMN_TASK_RECIPIENT_NAME, mBean.recipientsName);
		cv.put(SQLSentence.COLUMN_TASK_RECIPIENT_PHONE, mBean.recipientPhone);
		cv.put(SQLSentence.COLUMN_TASK_EQUIPMENT_NUMBER, mBean.equipmentNumber);
		cv.put(SQLSentence.COLUMN_TASK_TITLE, mBean.taskTitle);
		cv.put(SQLSentence.COLUMN_TASK_EQUIPMENT_TYPE, mBean.equipmentType);
		cv.put(SQLSentence.COLUMN_TASK_PLAN_STARTTIME, mBean.planStartTime);
		cv.put(SQLSentence.COLUMN_TASK_PLAN_ENDTIME, mBean.planEndTime);
		cv.put(SQLSentence.COLUMN_TASK_STATE, mBean.taskState);
		cv.put(SQLSentence.COLUMN_TASK_TYPE, mBean.taskType);
		cv.put(SQLSentence.COLUMN_TASK_ASYNC_STATE, mBean.isSyncToServer);
		
		cv.put(SQLSentence.COLUMN_TASK_NEW_ASSET_NUMBER, mBean.assetNumber);
		cv.put(SQLSentence.COLUMN_TASK_NEW_CONTACT, mBean.taskContact);
		cv.put(SQLSentence.COLUMN_TASK_NEW_CONTACT_PHONE, mBean.contactPhone);
		cv.put(SQLSentence.COLUMN_TASK_NEW_CUSTOMER_UNIT, mBean.customerUnit);
		cv.put(SQLSentence.COLUMN_TASK_NEW_EQUIPMENT_FACTORY, mBean.equipmentFactory);
		cv.put(SQLSentence.COLUMN_TASK_NEW_EQUIPMENT_REMARK, mBean.equipmentRemark);
		cv.put(SQLSentence.COLUMN_TASK_NEW_LOGICAL_ADDRESS, mBean.logicalAddress);
		cv.put(SQLSentence.COLUMN_TASK_NEW_REMARK, mBean.taskRemark);
		cv.put(SQLSentence.COLUMN_TASK_INSTALL_INFO, mBean.taskInstallInfo);
		openDb(1);
		return db.update(SQLSentence.TABLE_TASK, cv,
				SQLSentence.COLUMN_TASK_NET_ID + "= ?",
				new String[] { String.valueOf(mBean.taskNetId) });
	}
	
	public long doneTaskUpdateDb(int taskNetId){
		cv.clear();
		cv.put(SQLSentence.COLUMN_TASK_STATE, 2);
		openDb(1);
		return db.update(SQLSentence.TABLE_TASK, cv,
				SQLSentence.COLUMN_TASK_NET_ID + "= ?",
				new String[] { String.valueOf(taskNetId) });
	}
	
	public long updateUnSyncTask(TaskDetailBean mBean){
		cv.clear();
		cv.put(SQLSentence.COLUMN_TASK_NET_ID, mBean.taskNetId);
		cv.put(SQLSentence.COLUMN_TASK_WORK_LOCATION, mBean.workLocation);
		cv.put(SQLSentence.COLUMN_TASK_INSTALL_LOCATION, mBean.installLocation);
		cv.put(SQLSentence.COLUMN_TASK_DESCRIPTION, mBean.taskDescription);
		cv.put(SQLSentence.COLUMN_TASK_RECIPIENT_NAME, mBean.recipientsName);
		cv.put(SQLSentence.COLUMN_TASK_RECIPIENT_PHONE, mBean.recipientPhone);
		cv.put(SQLSentence.COLUMN_TASK_EQUIPMENT_NUMBER, mBean.equipmentNumber);
		cv.put(SQLSentence.COLUMN_TASK_TITLE, mBean.taskTitle);
		cv.put(SQLSentence.COLUMN_TASK_EQUIPMENT_TYPE, mBean.equipmentType);
		cv.put(SQLSentence.COLUMN_TASK_PLAN_STARTTIME, mBean.planStartTime);
		cv.put(SQLSentence.COLUMN_TASK_PLAN_ENDTIME, mBean.planEndTime);
		cv.put(SQLSentence.COLUMN_TASK_STATE, mBean.taskState);
		cv.put(SQLSentence.COLUMN_TASK_TYPE, mBean.taskType);
		cv.put(SQLSentence.COLUMN_TASK_ASYNC_STATE, mBean.isSyncToServer);
		
		cv.put(SQLSentence.COLUMN_TASK_NEW_ASSET_NUMBER, mBean.assetNumber);
		cv.put(SQLSentence.COLUMN_TASK_NEW_CONTACT, mBean.taskContact);
		cv.put(SQLSentence.COLUMN_TASK_NEW_CONTACT_PHONE, mBean.contactPhone);
		cv.put(SQLSentence.COLUMN_TASK_NEW_CUSTOMER_UNIT, mBean.customerUnit);
		cv.put(SQLSentence.COLUMN_TASK_NEW_EQUIPMENT_FACTORY, mBean.equipmentFactory);
		cv.put(SQLSentence.COLUMN_TASK_NEW_EQUIPMENT_REMARK, mBean.equipmentRemark);
		cv.put(SQLSentence.COLUMN_TASK_NEW_LOGICAL_ADDRESS, mBean.logicalAddress);
		cv.put(SQLSentence.COLUMN_TASK_NEW_REMARK, mBean.taskRemark);
		cv.put(SQLSentence.COLUMN_TASK_INSTALL_INFO, mBean.taskInstallInfo);
		openDb(1);
		return db.update(SQLSentence.TABLE_TASK, cv,
				SQLSentence.COLUMN_TASK_ID + "= ?",
				new String[] { String.valueOf(mBean.taskLocalId) });
	}
	
	public long updateTaskNetId(TaskDetailBean mBean){
		cv.clear();
		cv.put(SQLSentence.COLUMN_TASK_NET_ID, mBean.taskNetId);
		openDb(1);
		return db.update(SQLSentence.TABLE_TASK, cv,
				SQLSentence.COLUMN_TASK_ID + "= ?",
				new String[] { String.valueOf(mBean.taskLocalId) });
	}
	
	public long updateDownloadAccessoryBean(AffiliatedFileBean mBean) {
		cv.clear();
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_PATH, mBean.filePath);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_STATE, mBean.fileState);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_SOURCE, mBean.fileSource);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_TASK_NET_ID, mBean.taskId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_NET_ID, mBean.netId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_FILE_SIZE, mBean.fileSize);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_FILE_DESCRIPTION, mBean.fileDescription);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_TASK_LOCAL_ID, mBean.taskLocalId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_LONGITUDE, mBean.longitude);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_LATITUDE, mBean.latitude);
		openDb(1);
		return db.update(SQLSentence.TABLE_TASK_ACCESSORY, cv, SQLSentence.COLUMN_TASK_ACCESSORY_NET_ID+" = ?",
				new String[] { String.valueOf(mBean.netId) });
	}
	
	public long updateUploadAccessoryBean(AffiliatedFileBean mBean) {
		cv.clear();
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_PATH, mBean.filePath);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_STATE, mBean.fileState);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_SOURCE, mBean.fileSource);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_TASK_NET_ID, mBean.taskId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_NET_ID, mBean.netId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_FILE_SIZE, mBean.fileSize);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_FILE_DESCRIPTION, mBean.fileDescription);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_TASK_LOCAL_ID, mBean.taskLocalId);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_LONGITUDE, mBean.longitude);
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_LATITUDE, mBean.latitude);
		openDb(1);
		return db.update(SQLSentence.TABLE_TASK_ACCESSORY, cv, SQLSentence.COLUMN_TASK_ACCESSORY_PATH+" = ?",
				new String[] {mBean.filePath});
	}
	
	public long updateTaskProcess(int taskLocalId,int taskNetId){
		cv.clear();
		cv.put(SQLSentence.COLUMN_TASKPROCESS_TASK_ID, taskNetId);
		openDb(1);
		return db.update(SQLSentence.TABLE_TASK_PROCESS, cv, SQLSentence.COLUMN_TASKPROCESS_TASK_LOCAL_ID+" = ?",
				new String[] {taskLocalId+""});
	}
	
	public long updateTaskAccessory(int taskLocalId,int taskNetId){
		cv.clear();
		cv.put(SQLSentence.COLUMN_TASK_ACCESSORY_TASK_NET_ID, taskNetId);
		openDb(1);
		return db.update(SQLSentence.TABLE_TASK_ACCESSORY, cv, SQLSentence.COLUMN_TASK_ACCESSORY_TASK_LOCAL_ID+" = ?",
				new String[] {taskLocalId+""});
	}
	
	
	public long updateSingleProcess(TaskProcessBean mBean){
		cv.clear();
		cv.put(SQLSentence.COLUMN_TASKPROCESS_TASK_ID, mBean.taskNetid);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_TASK_LOCAL_ID, mBean.taskLocalId);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_ASYNC_STATE, mBean.isSyncToServer);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_NET_ID, mBean.processNetId);
		return db.update(SQLSentence.TABLE_TASK_PROCESS, cv, SQLSentence.COLUMN_TASKPROCESS_ID+" = ?",
				new String[] {mBean.processLocalId+""});
	}
	
	public long updateProcess(TaskProcessBean mBean){
		cv.clear();
		cv.put(SQLSentence.COLUMN_TASKPROCESS_STARTTIME, mBean.startTime);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_ENDTIME, mBean.endTime);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_TASK_PROCESSWHAT,mBean.processContent);
		cv.put(SQLSentence.COLUMN_TASKPROCESS_ASYNC_STATE, mBean.isSyncToServer);
		return db.update(SQLSentence.TABLE_TASK_PROCESS, cv, SQLSentence.COLUMN_TASKPROCESS_ID+" = ?",
				new String[] {mBean.processLocalId+""});
	}
	
	/*-------------------------------------------数据库删除操作---------------------------------------------*/
	
	public long deleteTask(int taskNetId){
		openDb(1);
		return db.delete(SQLSentence.TABLE_TASK, SQLSentence.COLUMN_TASK_NET_ID
				+ " = ?", new String[] { String.valueOf(taskNetId)});
	}
	
	public long deleteTaskProcess(int taskNetId){
		openDb(1);
		return db.delete(SQLSentence.TABLE_TASK_PROCESS, SQLSentence.COLUMN_TASKPROCESS_TASK_ID
				+ " = ?", new String[] { String.valueOf(taskNetId)});
	}
	
	public long deleteSingleTaskProcess(TaskProcessBean mBean) {
		openDb(1);
		return db.delete(SQLSentence.TABLE_TASK_PROCESS,SQLSentence.COLUMN_TASKPROCESS_CREATE_TIME + 
				" = ?",new String[] { String.valueOf(mBean.createTime) });
	}
	
	public long deleteTaskAccessory(int taskNetId){
		openDb(1);
		TaskAccessoryCursor cursor =  (TaskAccessoryCursor) queryAccessoryByTaskNetId(taskNetId);
		if(cursor.getCount() > 0 && cursor.moveToFirst()){
			do{
				Utils.deleteFile(cursor.getAccessory().filePath);
			}while(cursor.moveToNext());
		}
		return db.delete(SQLSentence.TABLE_TASK_ACCESSORY, SQLSentence.COLUMN_TASK_ACCESSORY_NET_ID
				+ " = ?", new String[] { String.valueOf(taskNetId)});
	}
	
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
	
	public long detelteTaskAccessory(int  netId) {
		openDb(1);
		return db.delete(SQLSentence.TABLE_TASK_ACCESSORY,
				SQLSentence.COLUMN_TASK_ACCESSORY_NET_ID+ "= ?",
				new String[] { String.valueOf(netId) });
	}

	public void refreshTask() {
		openDb(1);
		 db.delete(SQLSentence.TABLE_TASK,
				SQLSentence.COLUMN_TASK_ASYNC_STATE+" = ?",
				new String[] { String.valueOf(1) });
		 revertSeq(SQLSentence.TABLE_TASK);
	}
	
	
	public void refreshTaskProcess(int taskLocalId){
		openDb(1);
		int num =db.delete(SQLSentence.TABLE_TASK_PROCESS,
				SQLSentence.COLUMN_TASKPROCESS_TASK_LOCAL_ID+" = ?",
				new String[] { String.valueOf(taskLocalId) });
		Log.i("fanjishuo____refreshTaskProcess", "num"+num+"taskLocalId"+taskLocalId);
	}
	/*-------------------------------------------数据库查询操作---------------------------------------------*/
	public Cursor queryAttendCursor(int monthNum,int year) {
		return mHelper.queryMonthAttend(monthNum,year);
	}

	public Cursor queryUserTask() {
		return mHelper.queryUserTask();
	}

	public Cursor queryProcessByTaskNetId(int taskId) {
		return mHelper.queryProcessByTaskNetId(taskId);
	}
	
	public Cursor queryProcessByTaskLocalId(int taskId) {
		return mHelper.queryProcessByTaskLocalId(taskId);
	}

	public Cursor queryAccessoryByTaskNetId(int taskNetId) {
		return mHelper.queryAccessoryByTaskNetId(taskNetId);
	}
	
	public Cursor queryAccessoryByTaskLocalId(int taskLocalId) {
		return mHelper.queryAccessoryByTaskLocalId(taskLocalId);
	}
	
	public Cursor queryUnLoadAccessory(){
		return mHelper.queryUnLoadFile();
	}
	
	public TaskDetailBean getSingleTask(int taskLocald){
		openDb(0);
		Log.i("fanjishuo____getSingleTask", "taskLocald"+taskLocald);
		TaskCursor cursor = mHelper.queryUserSingleTask(taskLocald);
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
