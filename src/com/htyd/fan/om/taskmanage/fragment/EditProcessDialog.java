package com.htyd.fan.om.taskmanage.fragment;

import java.lang.ref.WeakReference;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskProcessBean;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.base.ThreadPool;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.fragment.DateTimePickerDialog;
import com.htyd.fan.om.util.fragment.SpendTimePickerDialog;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.ui.UItoolKit;

public class EditProcessDialog extends DialogFragment {

	public static final String PROCESSBEAN = "processbean";
	
	protected static final int REQUESTSTARTTIME = 0x01;
	protected static final int REQUESTENDTIME = 0x02;

	public static final int EDITSUCCESS = 0x03;

	public static final int EDITFAIL = 0x04;

	private TextView selectStartTime,selectEndTime;
	private EditText processContent;

	private TaskProcessBean mBean;
	private long startTime,endTime;
	private EditProcessHandler handler;
	private DialogInterface dialog;
	
	
	public static DialogFragment newInstance(TaskProcessBean mBean){
		DialogFragment fragment = new EditProcessDialog();
		Bundle args = new Bundle();
		args.putParcelable(PROCESSBEAN, mBean);
		fragment.setArguments(args);
		return fragment;
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBean = getArguments().getParcelable(PROCESSBEAN);
		startTime = mBean.startTime;
		endTime = mBean.endTime;
		handler = new EditProcessHandler(this);
	}
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.add_task_process, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditProcessDialog.this.dialog = dialog;
				if (checkSame()) {
					UItoolKit.showToastShort(getActivity(), "保存成功");
				} else {
					getBean();
					if(!Utils.isNetWorkEnable()){
						mBean.isSyncToServer = 0;
						ThreadPool.runMethod(new Runnable() {
							@Override
							public void run() {
								OMUserDatabaseManager.getInstance(getActivity()).updateProcess(mBean);
							}
						});
						UItoolKit.showToastShort(getActivity(), "保存至本地成功");
						sendResult();
					} else {
						isShowDialog(dialog, false);
						ThreadPool.runMethod(new EdItProcessThread(mBean, getActivity(), handler));
					}
				}
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				isShowDialog(dialog, true);
				dialog.cancel();
			}
		});
		return builder.create();
	}

	private static class EditProcessHandler extends Handler {
		private WeakReference<EditProcessDialog> mFragment;

		public EditProcessHandler(EditProcessDialog mFragment) {
			this.mFragment = new WeakReference<EditProcessDialog>(mFragment);
		}

		@Override
		public void dispatchMessage(Message msg) {
			final EditProcessDialog dialog = mFragment.get();
			switch (msg.what) {
			case EDITSUCCESS:
				dialog.sendResult();
				break;
			case EDITFAIL:
				dialog.sendResult();
				break;
			default:
				super.dispatchMessage(msg);
			}
		}
	}
	
	protected void sendResult() {
		if(getTargetFragment() == null){
			return;
		}
		Intent i = new Intent();
		i.putExtra(PROCESSBEAN, mBean);
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
		isShowDialog(dialog, true);
		UItoolKit.showToastShort(getActivity(), "修改任务处理成功");
		dialog.cancel();
	}


	protected boolean checkSame() {
		return mBean.startTime == startTime
				&& mBean.endTime == endTime
				&& mBean.processContent.equals(processContent.getText()
						.toString());
	}
	
	protected void getBean(){
		mBean.startTime = startTime;
		mBean.endTime = endTime;
		mBean.processContent = processContent.getText().toString();
	}


	private void initView(View v) {
		selectStartTime = (TextView) v.findViewById(R.id.tv_process_start_time);
		selectEndTime = (TextView) v.findViewById(R.id.tv_process_end_time);
		processContent = (EditText) v.findViewById(R.id.edit_process_content);
		
		
		processContent.setText(mBean.processContent);
		selectStartTime.setText(Utils.formatTime(mBean.startTime));
		selectEndTime.setText(Utils.formatTime(mBean.endTime));
		
		selectEndTime.setOnClickListener(selectTimeListener);
		selectStartTime.setOnClickListener(selectTimeListener);
	}

	private View.OnClickListener selectTimeListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentManager fm = getActivity().getFragmentManager();
			switch (v.getId()) {
			case R.id.tv_process_start_time:
				DateTimePickerDialog dateDialog = (DateTimePickerDialog) DateTimePickerDialog
						.newInstance(false);
				dateDialog.setTargetFragment(EditProcessDialog.this,
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
				spendDialog.setTargetFragment(EditProcessDialog.this,
						REQUESTENDTIME);
				spendDialog.show(fm, null);
				break;
			}
		}
	};
	public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUESTSTARTTIME) {
				startTime = data
						.getLongExtra(DateTimePickerDialog.EXTRATIME, 0);
				selectStartTime.setText(Utils.formatTime(startTime));
			} else if (requestCode == REQUESTENDTIME) {
				endTime = data.getLongExtra(SpendTimePickerDialog.ENDTIME, 0);
				UItoolKit.showToastShort(getActivity(),
						Utils.formatTime(endTime));
				selectEndTime.setText(Utils.formatTime(endTime));
			}
		}
	};
	
	private class EdItProcessThread implements Runnable{
		
		private TaskProcessBean mBean;
		private Context context;
		private Handler handler;
		
		public EdItProcessThread(TaskProcessBean mBean,Context context,Handler handler) {
			this.mBean = mBean;
			this.context = context;
			this.handler = handler;
		}

		@Override
		public void run() {
			try {
				String result =  NetOperating.getResultFromNet(context, getParam(), Urls.TASKPROCESSURL, "Operate=updateRwcl");
				JSONObject json = new JSONObject(result);
				if (json.getBoolean("RESULT")) {
					OMUserDatabaseManager.getInstance(context).updateProcess(mBean);
					handler.sendMessage(handler.obtainMessage(EDITSUCCESS));
				} else {
					mBean.isSyncToServer = 0;
					OMUserDatabaseManager.getInstance(context).updateProcess(mBean);
					handler.sendMessage(handler.obtainMessage(EDITFAIL));
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendMessage(handler.obtainMessage(EDITFAIL));
			}
		}

		private JSONObject getParam() {
			JSONObject json;
			try {
				json = mBean.toJson();
				json.put("CLR", Preferences.getUserinfo(context, "YHMC"));
				json.put("CLRDH", Preferences.getUserinfo(context, "SHOUJ"));
				return json;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
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
}
