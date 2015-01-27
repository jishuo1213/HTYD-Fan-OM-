package com.htyd.fan.om.taskmanage.netthread;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.htyd.fan.om.model.TaskListBean;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.ui.UItoolKit;

public class DoneTaskThread extends BaseConnectNetThread<TaskListBean> {

	public interface DoneTaskListener{
		public void onTaskDone();
	}
	
	private int taskNetId;
	private DoneTaskListener listener;
	private TaskListBean mBean;
	private OMUserDatabaseManager manager;
	
	public DoneTaskThread(Handler handler, Context context, TaskListBean param) {
		super(handler, context, param);
		this.taskNetId = param.taskNetId;
		this.mBean = param;
		manager = OMUserDatabaseManager.getInstance(context);
	}

	@Override
	protected JSONObject getParams(TaskListBean params) {
		JSONObject param = new JSONObject();
		try {
			param.put("RWID", taskNetId);
			param.put("RWZT", 2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return param;
	}

	@Override
	protected String getUrl() {
		
		return Urls.TASKURL;
	}

	@Override
	protected String getOperate() {
		
		return "Operate=rwOver";
	}

	@Override
	protected boolean praseResult(String result) {
		try {
			JSONObject json = new JSONObject(result);
			return json.getBoolean("RESULT");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Runnable getSuccessRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				mBean.taskState = 2;
				manager.doneTaskUpdateDb(taskNetId);
				listener.onTaskDone();
				UItoolKit.showToastShort(context, "完成任务成功");
			}
		};
	}

	public void setListener(DoneTaskListener listener){
		this.listener = listener;
	}
	
}
