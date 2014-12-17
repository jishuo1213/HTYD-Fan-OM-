package com.htyd.fan.om.taskmanage;

import android.content.Context;

import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.loaders.DataLoader;

public class SingleTaskLoader extends DataLoader<TaskDetailBean> {

	private int taskNetId;
	
	public SingleTaskLoader(Context context,int taskNetId) {
		super(context);
		this.taskNetId = taskNetId;
	}

	@Override
	public TaskDetailBean loadInBackground() {
		return OMUserDatabaseManager.getInstance(getContext()).getSingleTask(taskNetId);
	}

}
