package com.htyd.fan.om.util.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OMDatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "om.sqlite";
	private static final int VERSION = 1;
	
	public OMDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
