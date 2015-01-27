package com.htyd.fan.om.taskmanage.fragment;

import java.lang.reflect.Field;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskProcessBean;
import com.htyd.fan.om.taskmanage.netthread.SaveTaskProcessThread;
import com.htyd.fan.om.taskmanage.netthread.SaveTaskProcessThread.SyncTaskProceeSuccess;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.base.ThreadPool;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.fragment.DateTimePickerDialog;
import com.htyd.fan.om.util.fragment.SpendTimePickerDialog;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.ui.UItoolKit;

public class CreateProcessDialog extends DialogFragment implements SyncTaskProceeSuccess{

	public static final String PROCESSBEAN = "processbean";
	private static final int REQUESTSTARTTIME = 1;// 开始时间
	private static final int REQUESTENDTIME = 2;// 结束时间
	private static final String REPROCESSBEAN = "receprocessbean";
	private static final String SHOWORNOT = "showornot";
	private static final String TASKNETID = "taskid";
	private static final String TASKLOCALID = "tasklocalid";

	private EditText processContent;
	private TextView selectStartTime, selectEndTime;
//	private RadioButton done, undone;
	protected long startTime, endTime;
	private DialogInterface dialog;
	protected Handler handler;
	protected TaskProcessBean mBean;
	
	public static DialogFragment newInstance(TaskProcessBean mBean,
			boolean showornot,int taskNetID,int taskLocalId) {
		DialogFragment fragment = new CreateProcessDialog();
		Bundle bundle = new Bundle();
		if (mBean != null)
			bundle.putParcelable(REPROCESSBEAN, mBean);
		bundle.putBoolean(SHOWORNOT, showornot);
		bundle.putInt(TASKNETID, taskNetID);
		bundle.putInt(TASKLOCALID, taskLocalId);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.add_task_process, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		if (getArguments().getBoolean(SHOWORNOT, false)) {
			builder.setTitle("查看处理内容");
			builder.setPositiveButton("确定", null);
			TaskProcessBean mBean = getArguments().getParcelable(REPROCESSBEAN);
			if(mBean.isSyncToServer == 0){
				builder.setNeutralButton("上传", dialogListener);
			}
			setViewShow((TaskProcessBean) getArguments().getParcelable(
					REPROCESSBEAN));
		} else {
			builder.setTitle("新建处理内容");
			builder.setPositiveButton("保存", dialogListener);
		}
		builder.setNegativeButton("取消", dialogListener);
		return builder.create();
	}

	private void setViewShow(TaskProcessBean mBean) {
		processContent.setText(mBean.processContent);
		selectStartTime.setText(Utils.formatTime(mBean.startTime));
		selectEndTime.setText(Utils.formatTime(mBean.endTime));
	/*	if (mBean.taskState == 0) {
			undone.setChecked(true);
		} else {
			done.setChecked(true);
		}*/
		processContent.setFocusable(false);
		selectStartTime.setEnabled(false);
		selectEndTime.setEnabled(false);
//		undone.setEnabled(false);
//		done.setEnabled(false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUESTSTARTTIME) {
				startTime = data
						.getLongExtra(DateTimePickerDialog.EXTRATIME, 0);
				UItoolKit.showToastShort(getActivity(),
						Utils.formatTime(startTime));
				selectStartTime.setText(Utils.formatTime(startTime));
			} else if (requestCode == REQUESTENDTIME) {
				endTime = data.getLongExtra(SpendTimePickerDialog.ENDTIME, 0);
				UItoolKit.showToastShort(getActivity(),
						Utils.formatTime(endTime));
				selectEndTime.setText(Utils.formatTime(endTime));
			}
		}
	}

	private void initView(View v) {
		processContent = (EditText) v.findViewById(R.id.edit_process_content);
		selectStartTime = (TextView) v.findViewById(R.id.tv_process_start_time);
		selectEndTime = (TextView) v.findViewById(R.id.tv_process_end_time);
//		done = (RadioButton) v.findViewById(R.id.radio_task_state_done);
//		undone = (RadioButton) v.findViewById(R.id.radio_task_state_undone);
		selectStartTime.setOnClickListener(selectTimeListener);
		selectEndTime.setOnClickListener(selectTimeListener);
	}

	private DialogInterface.OnClickListener dialogListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case Dialog.BUTTON_POSITIVE:
				CreateProcessDialog.this.dialog = dialog;
				isShowDialog(dialog,false);
				if (!checkCanSave()) {
					UItoolKit.showToastShort(getActivity(), "这些都不能不填");
					return;
				} 
				mBean = getProcessBean();
				if(Utils.isNetWorkEnable() && getArguments().getInt(TASKNETID) != 0){
					sendReult();
				} else {
					offLineCreateProcess();
				}
				break;
			case Dialog.BUTTON_NEGATIVE:
				isShowDialog(dialog,true);
				break;
			case Dialog.BUTTON_NEUTRAL:
				TaskProcessBean mBean = getArguments().getParcelable(REPROCESSBEAN);
				if(mBean.taskNetid == 0){
					UItoolKit.showToastShort(getActivity(), "该任务还未同步至服务器，不能先同步处理项");
					return;
				}
				if(mBean.isSyncToServer == 1){
					UItoolKit.showToastShort(getActivity(), "已经同步至服务器");
					return;
				}
				SaveTaskProcessThread syncThread = new SaveTaskProcessThread(handler, getActivity(), mBean);
				syncThread.setListener(CreateProcessDialog.this);
				ThreadPool.runMethod(syncThread);
			}
		}
	};

	private View.OnClickListener selectTimeListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentManager fm = getActivity().getFragmentManager();
			switch (v.getId()) {
			case R.id.tv_process_start_time:
				DateTimePickerDialog dateDialog = (DateTimePickerDialog) DateTimePickerDialog
						.newInstance(false);
				dateDialog.setTargetFragment(CreateProcessDialog.this,
						REQUESTSTARTTIME);
				dateDialog.show(fm, null);
				break;
			case R.id.tv_process_end_time:
				if (startTime == 0) {
					UItoolKit.showToastShort(getActivity(), "先选择开始时间");
					return;
				}
				SpendTimePickerDialog spendDialog = (SpendTimePickerDialog) SpendTimePickerDialog
						.newInstance(startTime);
				spendDialog.setTargetFragment(CreateProcessDialog.this,
						REQUESTENDTIME);
				spendDialog.show(fm, null);
				break;
			}
		}
	};

	protected boolean checkCanSave() {
		return !(TextUtils.isEmpty(processContent.getText())
				|| TextUtils.isEmpty(selectStartTime.getText())
				|| TextUtils.isEmpty(selectEndTime.getText()));
	}

	protected void offLineCreateProcess() {
		ThreadPool.runMethod(new Runnable() {
			@Override
			public void run() {
				OMUserDatabaseManager.getInstance(getActivity()).openDb(1);
				mBean.isSyncToServer = 0;
				if (OMUserDatabaseManager.getInstance(getActivity())
						.insertTaskProcessBean(mBean) > 0) {
					handler.post(saveSuccess);
				} else {
					handler.post(saveFail);
				}
			}
		});
	}

	protected Runnable saveSuccess = new Runnable() {
		@Override
		public void run() {
			UItoolKit.showToastShort(getActivity(), "保存至本地成功");
			sendResultToTarget(mBean);
		}
	};
	
	protected Runnable saveFail = new Runnable() {
		@Override
		public void run() {
			UItoolKit.showToastShort(getActivity(), "保存至本地失败，请重试");
		}
	};
	
	protected void sendReult() {
		if (getTargetFragment() == null) {
			return;
		}
		startTask(mBean);
		/*if (done.isChecked()) {
			mBean.taskState = 2;
		} else {
			mBean.taskState = 0;
		}*/
	}

	protected TaskProcessBean getProcessBean() {
		TaskProcessBean mBean = new TaskProcessBean();
		mBean.processContent = processContent.getText().toString();
		mBean.startTime = startTime;
		mBean.endTime = endTime;
		mBean.taskNetid = getArguments().getInt(TASKNETID);
		mBean.taskLocalId = getArguments().getInt(TASKLOCALID);
		Log.i("fanjishuo___getProcessBean", mBean.taskLocalId+"");
		mBean.createTime = System.currentTimeMillis();
		return mBean;
	}

	private class SaveTaskProcessTask extends
			AsyncTask<TaskProcessBean, Void, Boolean> {
		private TaskProcessBean mBean;

		@Override
		protected Boolean doInBackground(TaskProcessBean... params) {
			String result = "";
			mBean = params[0];
			try {
				JSONObject param = params[0].toJson();
				param.put("CLR", Preferences.getUserinfo(getActivity(), "YHMC"));
				param.put("CLRDH", Preferences.getUserinfo(getActivity(), "SHOUJ"));
				result = NetOperating.getResultFromNet(getActivity(),
						param, Urls.TASKPROCESSURL,
						"Operate=saveRwClxx");
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return parseResult(result);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				mBean.isSyncToServer = 1;
				ThreadPool.runMethod(new Runnable() {
					@Override
					public void run() {
						OMUserDatabaseManager.getInstance(getActivity()).openDb(1);
						OMUserDatabaseManager.getInstance(getActivity()).insertTaskProcessBean(mBean);
					}
				});
				UItoolKit.showToastShort(getActivity(), "保存至网络成功");
				sendResultToTarget(mBean);
			} else {
				UItoolKit.showToastShort(getActivity(), "保存不成功，请重试");
				offLineCreateProcess();
			}
			stopTask(this);
		}
	}

	protected void startTask(TaskProcessBean mBean) {
		new SaveTaskProcessTask().execute(mBean);
	}

	protected void stopTask(SaveTaskProcessTask task) {
		task.cancel(false);
	}

	protected boolean parseResult(String result) {
		try {
			JSONObject json = new JSONObject(result);
			return json.getBoolean("RESULT");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected void isShowDialog(DialogInterface dialog, boolean isshow) {
		try {
			Field field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, isshow);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	protected void sendResultToTarget(TaskProcessBean mBean) {
		Intent i = new Intent();
		i.putExtra(PROCESSBEAN, mBean);
		getTargetFragment().onActivityResult(getTargetRequestCode(),
				Activity.RESULT_OK, i);
		isShowDialog(dialog,true);
		dialog.cancel();
	}

	@Override
	public void onSyncSuccess() {
		sendSyncResult();
	}
	private void sendSyncResult(){
		if(getTargetFragment() == null){
			return;
		}
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
	}
}
