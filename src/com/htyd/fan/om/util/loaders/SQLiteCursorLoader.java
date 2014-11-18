package com.htyd.fan.om.util.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {

	private Cursor mCursor;

	public SQLiteCursorLoader(Context context) {
		super(context);
	}

	protected abstract Cursor loadCursor();
	protected abstract Cursor loadFromNet();

	@Override
	public Cursor loadInBackground() {
		Cursor cursor = loadCursor();
		if (cursor != null && cursor.getCount() > 0) {
			Log.i("fanjishuo____loadInBackground", "cursor != null");
			cursor.getCount();
		}else{
			Log.i("fanjishuo____loadInBackground", "cursor = null loadFromNet");
			cursor = loadFromNet();
			if(cursor != null)
				cursor.getCount();
		}
		return cursor;
	}

	@Override
	public void deliverResult(Cursor data) {
		Cursor oldCursor = mCursor;
		mCursor = data;

		if (isStarted()) {
			super.deliverResult(data);
		}

		if (oldCursor != null && oldCursor != data && !oldCursor.isClosed()) {
			oldCursor.close();
		}
	}

	@Override
	protected void onStartLoading() {
		if (mCursor != null) {
			deliverResult(mCursor);
		}
		if (takeContentChanged() || mCursor == null) {
			forceLoad();
		}
	}

	@Override
	public void onCanceled(Cursor data) {
		if (data != null && !data.isClosed()) {
			data.close();
		}
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		super.onReset();

		onStopLoading();

		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		mCursor = null;
	}
}
