package com.htyd.fan.om.taskmanage.netthread;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.htyd.fan.om.model.TaskProcessBean;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.https.Urls;

public class DeleteProcessThread extends BaseConnectNetThread<TaskProcessBean> {

	
	public interface DeleteProcessListener{
		void onDeleteSuccess(TaskProcessBean mBean);
	}
	
	private TaskProcessBean mBean;
	private DeleteProcessListener listener;
	
	public DeleteProcessThread(Handler handler, Context context,
			TaskProcessBean param) {
		super(handler, context, param);
		mBean = param;
	}

	public void setListener(DeleteProcessListener listener){
		this.listener = listener;
	}
	
	@Override
	protected JSONObject getParams(TaskProcessBean params) {
		JSONObject json = new JSONObject();
		try {
			json.put("CLID", params.processNetId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	protected String getUrl() {
		
		return Urls.TASKPROCESSURL;
	}

	@Override
	protected String getOperate() {
		
		return "Operate=deleteRwclByClid";
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
				long temp =  OMUserDatabaseManager.getInstance(context).deleteSingleTaskProcess(mBean);
				if(temp > 0)
					listener.onDeleteSuccess(mBean);
			}
		};
	}
}
