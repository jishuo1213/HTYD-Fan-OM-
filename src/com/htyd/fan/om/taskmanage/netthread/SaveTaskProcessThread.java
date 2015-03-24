package com.htyd.fan.om.taskmanage.netthread;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.htyd.fan.om.model.TaskProcessBean;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.ui.UItoolKit;

public class SaveTaskProcessThread extends BaseConnectNetThread<TaskProcessBean> {

	private Context context;
	private OMUserDatabaseManager manager;
	private TaskProcessBean mBean;
	private SyncTaskProceeSuccess listener;
	
	
	public interface SyncTaskProceeSuccess{
		public void onSyncSuccess();
	}
	
	public SaveTaskProcessThread(Handler handler, Context context,
			TaskProcessBean param) {
		super(handler, context, param);
		this.context = context;
		manager = OMUserDatabaseManager.getInstance(context);
		mBean = param;
	}

	public void setListener(SyncTaskProceeSuccess listener){
		this.listener = listener;
	}
	
	@Override
	protected JSONObject getParams(TaskProcessBean params) {
/*		JSONArray array = new JSONArray();
		Iterator<TaskProcessBean> it = processList.iterator();
		while(it.hasNext()){
			TaskProcessBean temp = it.next();
			if (temp.isSyncToServer == 0) {
				try {
					array.put(it.next().toJson());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		JSONObject param = new JSONObject();
		try {
			param.put("RWID", taskNetId);
			param.put("Rows", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return param;*/
		JSONObject param = null;
		try {
			param = params.toJson();
			param.put("CLR", Preferences.getUserinfo(context, "YHMC"));
			param.put("CLRDH", Preferences.getUserinfo(context, "SHOUJ"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return param;
	}

	@Override
	protected String getUrl() {
		
		return Urls.TASKPROCESSURL;
	}

	@Override
	protected String getOperate() {
		
		return "Operate=saveRwClxx";
	}

	@Override
	protected boolean praseResult(String result) {
		try {
			JSONObject json = new JSONObject(result);
			if (json.has("MAXCLID")) {
				mBean.processNetId = json.getInt("MAXCLID");
			}
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
				mBean.isSyncToServer = 1;
				long temp = manager.updateSingleProcess(mBean);
				if(temp > 0){
					UItoolKit.showToastShort(context, "同步至网络成功");
					if(listener != null){
						listener.onSyncSuccess();
					}
				}
			}
		};
	}
}
