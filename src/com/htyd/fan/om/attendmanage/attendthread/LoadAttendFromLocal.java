package com.htyd.fan.om.attendmanage.attendthread;

import android.content.Context;
import android.os.Handler;

import com.htyd.fan.om.attendmanage.fragment.AttendCalendarNewFragment;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.AttendCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;

public class LoadAttendFromLocal implements Runnable {

	private Handler handler;
	private int year;
	private int month;
	private OMUserDatabaseManager mManager;
	
	
	public LoadAttendFromLocal(Handler handler, int year, int month,Context context) {
		super();
		this.handler = handler;
		this.year = year;
		this.month = month;
		mManager = OMUserDatabaseManager.getInstance(context);
	}


	@Override
	public void run() {
		AttendCursor cursor =  (AttendCursor) mManager.queryAttendCursor(month, year);
		if(cursor.getCount() > 0){
			handler.sendMessage(handler.obtainMessage(AttendCalendarNewFragment.LOCALSUCCESS, cursor));
		}else{
			handler.sendMessage( handler.obtainMessage(AttendCalendarNewFragment.LOCALFAILED));
			cursor.close();
		}
	}

}
