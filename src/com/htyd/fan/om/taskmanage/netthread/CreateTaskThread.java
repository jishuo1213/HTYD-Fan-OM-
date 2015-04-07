package com.htyd.fan.om.taskmanage.netthread;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.ui.UItoolKit;

public class CreateTaskThread extends BaseConnectNetThread<TaskDetailBean> {


//	private double longitude,latitude;
	private TaskDetailBean mBean;
	private OMUserDatabaseManager manager;
	private SaveSuccessListener listener;
	
	public interface SaveSuccessListener{
		public void onSaveSuccess(TaskDetailBean mBean,boolean isLocal);
	}
	
	public CreateTaskThread(Handler handler, Context context,
			TaskDetailBean param,SaveSuccessListener listener) {
		super(handler, context, param);
		mBean = param;
		manager = OMUserDatabaseManager.getInstance(context);
		this.listener = listener;
	}

	@Override
	protected JSONObject getParams(TaskDetailBean params) {
		JSONObject param = null;
		if(params.taskNetId == 0){
			try {
				param = params.toJson();
				param.put("LQR", Preferences.getUserinfo(context, "YHID"));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} else {
			try {
				param = params.toEditJson();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return param;
	}

	@Override
	protected String getUrl() {
		return Urls.TASKURL;
	}

	@Override
	protected String getOperate() {
		return (mBean.taskNetId == 0)? "Operate=saveRwxx":"Operate=updateRwbj";
	}

	@Override
	protected boolean praseResult(String result) {
		try {
			JSONObject json = new JSONObject(result);
			if(json.has("RWID"))
				mBean.taskNetId = Integer.parseInt(json.getString("RWID"));
			return json.getBoolean("RESULT");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Runnable getSuccessRunnable() {
		mBean.isSyncToServer = 1;
		return new Runnable() {
			@Override
			public void run() {
				long temp = manager.updateUnSyncTask(mBean);
				manager.updateTaskProcess(mBean.taskLocalId, mBean.taskNetId);
				manager.updateTaskAccessory(mBean.taskLocalId, mBean.taskNetId);
				if (temp > 0) {
					UItoolKit.showToastShort(context, "同步任务成功");
					listener.onSaveSuccess(mBean,false);
					Utils.back();
				}
			}
		};
	}
}
