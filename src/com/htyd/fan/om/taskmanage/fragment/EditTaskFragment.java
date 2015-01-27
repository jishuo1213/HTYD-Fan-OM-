package com.htyd.fan.om.taskmanage.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.htyd.fan.om.R;
import com.htyd.fan.om.map.LocationReceiver;
import com.htyd.fan.om.map.OMLocationManager;
import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.model.CommonDataBean;
import com.htyd.fan.om.model.OMLocationBean;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.taskmanage.TaskViewPanel;
import com.htyd.fan.om.taskmanage.fragment.CreateTaskFragment.SaveTaskListener;
import com.htyd.fan.om.taskmanage.netthread.CreateTaskThread;
import com.htyd.fan.om.taskmanage.netthread.CreateTaskThread.SaveSuccessListener;
import com.htyd.fan.om.util.base.ThreadPool;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskAccessoryCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.fragment.AddressListDialog;
import com.htyd.fan.om.util.fragment.AddressListDialog.ChooseAddressListener;
import com.htyd.fan.om.util.fragment.DateTimePickerDialog;
import com.htyd.fan.om.util.fragment.InputDialogFragment;
import com.htyd.fan.om.util.fragment.SelectLocationDialogFragment;
import com.htyd.fan.om.util.fragment.SpendTimePickerDialog;
import com.htyd.fan.om.util.fragment.TaskTypeDialogFragment;
import com.htyd.fan.om.util.fragment.UploadFileDialog;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.https.Utility;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;
import com.htyd.fan.om.util.ui.UItoolKit;
import com.htyd.fan.om.util.zxing.CaptureActivity;

public class EditTaskFragment extends Fragment implements ChooseAddressListener,SaveSuccessListener {

	private static final String SELECTTASK = "selecttask";
	private static final String TASKNETID = "taskid";
	private static final String TASKLOCALID = "tasklocalid";
	
	private static final int LOADERID = 0x15;
	
	private static final int REQUESTSTARTTIME = 1;//开始时间
	private static final int REQUESTENDTIME = 2;//结束时间
	private static final int REQUESTLOCATION = 0;//工作地点
	private static final int REQUESTZXING = 0x09;//设备条码
	private static final int REQUESTTYPE = 0x10;//设备条码
	private static final int REQUESTPROTUCT = 0x11;//产品类型
	private static final int REQUESTINPUT = 0x12;//手动输入地点
	private static final int REQUESTCODETINPUT = 0x13;//手动输入条码

	protected TaskViewPanel mPanel;
	protected TaskDetailBean mBean;
	private long startTime;
	protected ArrayList<AffiliatedFileBean> accessoryList;
	private LoaderManager mLoaderManager;
	private AccessoryLoaderCallback mCallback;
	protected PopupMenu popupMenu,barcodePopMenu;
	protected Handler handler;
	private SaveTaskListener listener;
	
	public static Fragment newInstance(Parcelable mBean) {
		Bundle args = new Bundle();
		args.putParcelable(SELECTTASK, mBean);
		Fragment fragment = new EditTaskFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (SaveTaskListener) activity;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		handler = new Handler();
		mLoaderManager = getActivity().getLoaderManager();
		mBean = (TaskDetailBean) getArguments().get(SELECTTASK);
		Bundle args = new Bundle();
		args.putInt(TASKNETID, mBean.taskNetId);
		args.putInt(TASKLOCALID, mBean.taskLocalId);
		mCallback = new AccessoryLoaderCallback();
		mLoaderManager.initLoader(LOADERID, args, mCallback);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.task_detail_fragment_layout,
				container, false);
		initView(v);
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(mLocationReceiver, new IntentFilter(OMLocationManager.ACTION_LOCATION));
	}
	
	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mLocationReceiver);
		OMLocationManager.get(getActivity()).stopLocationUpdate();
		super.onPause();
	}

	private void initView(View v) {
		mPanel = new TaskViewPanel(v);
		mPanel.setTaskShow(mBean);
		// mPanel.setViewEnable();
		mPanel.taskLocation.setOnClickListener(dialogClickListener);
		mPanel.taskPlanStartTime.setOnClickListener(dialogClickListener);
		mPanel.taskPlanEndTime.setOnClickListener(dialogClickListener);
		mPanel.taskAccessory.setOnClickListener(dialogClickListener);
		mPanel.taskEquipment.setOnClickListener(dialogClickListener);
		mPanel.taskType.setOnClickListener(dialogClickListener);
		mPanel.taskProductType.setOnClickListener(dialogClickListener);
		mPanel.taskInstallLocation.addTextChangedListener(installLocationWatcher);
		
		barcodePopMenu = new PopupMenu(getActivity(), mPanel.taskEquipment);
		barcodePopMenu.inflate(R.menu.qrcode_menu);
		barcodePopMenu
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
						case R.id.menu_scan:
							Intent i = new Intent(getActivity(),CaptureActivity.class);
							startActivityForResult(i, REQUESTZXING);
							return true;
						case R.id.menu_input_qrcode:
							DialogFragment fragment = InputDialogFragment.newInstance("设备条码", "输入设备条码");
							fragment.setTargetFragment(EditTaskFragment.this, REQUESTCODETINPUT);
							fragment.show(getFragmentManager(), null);
							return true;
						default:
							return true;
						}
					}
				});
		popupMenu = new PopupMenu(getActivity(), mPanel.taskLocation);
		popupMenu.inflate(R.menu.select_address_menu);
		popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menu_select_common:
					AddressListDialog dialogList = new AddressListDialog(EditTaskFragment.this);
					dialogList.show(getActivity().getFragmentManager(), null);
					return true;
				case R.id.menu_select_all:
					FragmentManager fm = getActivity().getFragmentManager();
					SelectLocationDialogFragment locationDialog = new SelectLocationDialogFragment();
					locationDialog.setTargetFragment(EditTaskFragment.this,
							REQUESTLOCATION);
					locationDialog.show(fm, null);
					return true;
				case R.id.menu_input:
					DialogFragment inputdialog = InputDialogFragment.newInstance("工作地点", "请输入工作地点");
					inputdialog.setTargetFragment(EditTaskFragment.this, REQUESTINPUT);
					inputdialog.show(getFragmentManager(), null);
					return true;
				default:
					return true;
				}
			}
		});
		getActivity().getActionBar().setTitle("编辑任务");
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.task_edit_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_or_receive:
			final TaskDetailBean tempBean;
			tempBean = mPanel.getTaskBean();
			if (tempBean.equals(mBean)) {
				UItoolKit.showToastShort(getActivity(), "保存成功");
				return true;
			}
			if (Utils.isNetWorkEnable()) {
				if (tempBean.taskNetId != 0) {
					updateNoramlTask(tempBean);
				} else {
					offLineEditTask(tempBean);
				}
			} else {
				offLineEditTask(tempBean);
			}
			return true;
		case R.id.menu_sync_task:
			if(mBean.isSyncToServer != 0){
				UItoolKit.showToastShort(getActivity(), "该任务已经同步至服务器");
				return true;
			}
			OMLocationManager.get(getActivity()).setLocCilentOption(null);
			OMLocationManager.get(getActivity()).startLocationUpdate();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {

		@Override
		protected void onNetWorkLocationReceived(Context context,
				OMLocationBean loc) {
			Log.i("fanjishuo____onNetWorkLocationReceived", "1111");
			OMLocationManager.get(getActivity()).stopLocationUpdate();
			TaskDetailBean mBean = mPanel.getTaskBean();
			ThreadPool.runMethod(new CreateTaskThread(handler, context, mBean, loc.longitude, loc.latitude,EditTaskFragment.this));
		}
		
		@Override
		protected void onNetDisableReceived(Context context) {
			UItoolKit.showToastShort(getActivity(), "网络连接失败");
			OMLocationManager.get(getActivity()).stopLocationUpdate();
		}
	};

	private void updateNoramlTask(final TaskDetailBean tempBean) {
		ThreadPool.runMethod(new Runnable() {
			@Override
			public void run() {
				OMUserDatabaseManager.getInstance(getActivity())
						.updateSyncTask(tempBean);
			}
		});
		new UpdateTask().execute(tempBean);
	}

	private void offLineEditTask(final TaskDetailBean tempBean) {
		tempBean.isSyncToServer = 0;
		ThreadPool.runMethod(new Runnable() {
			@Override
			public void run() {
				if (OMUserDatabaseManager.getInstance(getActivity()).updateSyncTask(tempBean) > 0){
					handler.post(editSuccess);
				}
			}
		});
	}
	
	protected Runnable editSuccess =  new Runnable() {
		@Override
		public void run() {
			UItoolKit.showToastShort(getActivity(), "保存至本地成功");
			TaskDetailBean mBean = mPanel.getTaskBean();
			mBean.isSyncToServer = 0;
			listener.onSaveSuccess(mBean, true);
			back();
		}
	};
	
	protected Runnable editFail =  new Runnable() {
		@Override
		public void run() {
			UItoolKit.showToastShort(getActivity(), "保存至本地失败，请重试");
		}
	};


	private TextWatcher installLocationWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			mPanel.taskAddress.setText(mPanel.taskLocation.getText().toString()+s.toString());
		}
	};
	
	private OnClickListener dialogClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			FragmentManager fm = getActivity().getFragmentManager();
			switch (v.getId()) {
			case R.id.tv_task_location:
				popupMenu.show();
				break;
			case R.id.edit_task_plan_starttime:
				DateTimePickerDialog dateDialog = (DateTimePickerDialog) DateTimePickerDialog
						.newInstance(true);
				dateDialog.setTargetFragment(EditTaskFragment.this,
						REQUESTSTARTTIME);
				dateDialog.show(fm, null);
				break;
			case R.id.edit_task_plan_endtime:
				if (startTime == 0) {
					UItoolKit.showToastShort(getActivity(), "请先选择开始时间");
					return;
				}
				SpendTimePickerDialog spendDialog = (SpendTimePickerDialog) SpendTimePickerDialog
						.newInstance(startTime);
				spendDialog.setTargetFragment(EditTaskFragment.this,
						REQUESTENDTIME);
				spendDialog.show(fm, null);
				break;
			case R.id.tv_task_accessory:
				UploadFileDialog uploadDialog = (UploadFileDialog) UploadFileDialog
						.newInstance(accessoryList, mBean.taskNetId, mBean.taskTitle,false,mBean.taskLocalId);
				uploadDialog.show(fm, null);
				break;
			case R.id.edit_task_equipment:
				/*Intent i = new Intent(getActivity(),CaptureActivity.class);
				startActivityForResult(i, REQUESTZXING);*/
				barcodePopMenu.show();
				break;
			case R.id.edit_task_type:
				DialogFragment taskTypeDialog = TaskTypeDialogFragment.newInstance("任务类别");
				taskTypeDialog.setTargetFragment(EditTaskFragment.this, REQUESTTYPE);
				taskTypeDialog.show(getFragmentManager(), null);
				break;
			case R.id.edit_task_product_type:
				DialogFragment dialog = TaskTypeDialogFragment.newInstance("产品类型");
				dialog.setTargetFragment(EditTaskFragment.this, REQUESTPROTUCT);
				dialog.show(getFragmentManager(), null);
				break;
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUESTLOCATION) {
				mPanel.taskLocation.setText(data
						.getStringExtra(SelectLocationDialogFragment.LOCATION));
				mPanel.taskAddress.setText(data
						.getStringExtra(SelectLocationDialogFragment.LOCATION)
						+ mPanel.taskInstallLocation.getText().toString());
			} else if (requestCode == REQUESTSTARTTIME) {
				startTime = data
						.getLongExtra(DateTimePickerDialog.EXTRATIME, 0);
				mPanel.taskPlanStartTime.setText(Utils.formatTime(startTime));
			} else if (requestCode == REQUESTENDTIME) {
				long endTime = data.getLongExtra(SpendTimePickerDialog.ENDTIME,
						0);
				mPanel.taskPlanEndTime.setText(Utils.formatTime(endTime));
			} else if (requestCode == REQUESTZXING) {
				mPanel.taskEquipment.setText(data.getStringExtra("result"));
			} else if (requestCode == REQUESTTYPE) {
				mPanel.taskType
						.setText(((CommonDataBean) data
								.getParcelableExtra(TaskTypeDialogFragment.TYPENAME)).typeName);
			} else if (requestCode == REQUESTPROTUCT) {
				mPanel.taskProductType
						.setText(((CommonDataBean) data
								.getParcelableExtra(TaskTypeDialogFragment.TYPENAME)).typeName);
			}else if(requestCode == REQUESTINPUT){
				mPanel.taskLocation.setText(data.getStringExtra(InputDialogFragment.INPUTTEXT));
			}else if(requestCode == REQUESTCODETINPUT){
				mPanel.taskEquipment.setText(data.getStringExtra(InputDialogFragment.INPUTTEXT));
			}
		}
	};

	

	private static class AccessoryLoader extends SQLiteCursorLoader {

		private OMUserDatabaseManager mManager;
		private int taskNetId;
		private int taskLocalId;

		public AccessoryLoader(Context context, int taskNetId,int taskLocalId) {
			super(context);
			mManager = OMUserDatabaseManager.getInstance(context);
			this.taskNetId = taskNetId;
			this.taskLocalId = taskLocalId;
		}

		@Override
		protected Cursor loadCursor() {
			mManager.openDb(0);
	//		if (taskNetId > 0) {
//				return mManager.queryAccessoryByTaskNetId(taskNetId);
//			} else {
				return mManager.queryAccessoryByTaskLocalId(taskLocalId);
//			}
		}

		@Override
		protected Cursor loadFromNet() {
			JSONObject param = new JSONObject();
			String result = "";
			try {
				param.put("JLID", taskNetId);
				result = NetOperating.getResultFromNet(getContext(), param,
						Urls.FILE, "Operate=getWjdz");
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			try {
				Utility.handleAccessory(mManager, result,taskNetId);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return loadCursor();
		}
	}
	
	private class AccessoryLoaderCallback implements LoaderCallbacks<Cursor>{

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new AccessoryLoader(getActivity(),args.getInt(TASKNETID),args.getInt(TASKLOCALID));
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			TaskAccessoryCursor cursor = (TaskAccessoryCursor) data;
			if(cursor != null && cursor.moveToFirst()){
				if(accessoryList == null){
					accessoryList = new ArrayList<AffiliatedFileBean>();
				}
				do{
					accessoryList.add(cursor.getAccessory());
				}while(cursor.moveToNext());
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	}
	
	private class UpdateTask extends AsyncTask<TaskDetailBean, Void, Boolean>{

		private TaskDetailBean mBean;
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				UItoolKit.showToastShort(getActivity(), "保存成功");
				listener.onSaveSuccess(mBean, false);
				back();
			}else{
				UItoolKit.showToastShort(getActivity(), "保存至网络失败");
				offLineEditTask(mBean);
			}
		}

		
		@Override
		protected Boolean doInBackground(TaskDetailBean... params) {
			JSONObject param = new JSONObject();
			mBean = params[0];
			String result = "";
			try {
				param = mBean.toEditJson();
				result = NetOperating.getResultFromNet(getActivity(), param, Urls.TASKURL, "Operate=updateRwbj");
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			try {
				return new JSONObject(result).getBoolean("RESULT");
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	@Override
	public void onAddressChoose(String address) {
		mPanel.taskLocation.setText(address);
		mPanel.taskAddress.setText(address + mPanel.taskInstallLocation.getText().toString());
	}

	protected void back() {
		ThreadPool.runMethod(new Runnable() {
			@Override
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
					getFragmentManager().beginTransaction().remove(EditTaskFragment.this).commit();
				} catch (Exception e) {
					Log.e("Exception when onBack", e.toString());
				}
			}
		});
	}

	@Override
	public void onSaveSuccess(TaskDetailBean mBean,boolean isLocal) {
		listener.onSaveSuccess(mBean,isLocal);
	}
	
	/*private class SyncSuccessRunnable implements Runnable{
		
		private TaskDetailBean mBean;
		private OMUserDatabaseManager mManager;
		
		public SyncSuccessRunnable(TaskDetailBean mBean) {
			super();
			this.mBean = mBean;
		}

		@Override
		public void run() {
			mManager.updateUnSyncTask(mBean);
		}
	}*/
}
