package com.htyd.fan.om.util.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

public abstract class DataLoader<D> extends AsyncTaskLoader<D> {
	
	private D mData;

	
	public DataLoader(Context context) {
		super(context);
	}
	
	@Override
	public void deliverResult(D data) {
		mData = data;
		if(isStarted()){
			super.deliverResult(mData);
		}
	}

	@Override
	protected void onStartLoading() {
		
		if(mData != null){
			deliverResult(mData);
		}else{
			forceLoad();
		}
	}
}
