package com.htyd.fan.om.taskmanage;

import android.content.Context;

import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.loaders.DataLoader;

public class SingleTaskLoader extends DataLoader<TaskDetailBean> {

	private int taskSingleId;
	
	public SingleTaskLoader(Context context,int taskNetId) {
		super(context);
		this.taskSingleId = taskNetId;
	}

	@Override
	public TaskDetailBean loadInBackground() {
		return OMUserDatabaseManager.getInstance(getContext()).getSingleTask(taskSingleId);
	}

}
