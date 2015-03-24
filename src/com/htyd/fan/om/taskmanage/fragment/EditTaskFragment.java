package com.htyd.fan.om.taskmanage.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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
import com.htyd.fan.om.util.ui.UItoolKit;
import com.htyd.fan.om.util.zxing.CaptureActivity;

public class EditTaskFragment extends Fragment implements ChooseAddressListener,SaveSuccessListener {

	private static final String SELECTTASK = "selecttask";
	private static final String TASKNETID = "taskid";
	private static final String TASKLOCALID = "tasklocalid";
	
	private static final int REQUESTSTARTTIME = 1;//开始时间
	private static final int REQUESTENDTIME = 2;//结束时间
	private static final int REQUESTLOCATION = 0;//工作地点
	private static final int REQUESTZXING = 0x09;//设备条码
	private static final int REQUESTTYPE = 0x10;//设备条码
	private static final int REQUESTPROTUCT = 0x11;//产品类型
	private static final int REQUESTINPUT = 0x12;//手动输入地点
	private static final int REQUESTCODETINPUT = 0x13;//手动输入条码
	protected static final int REQUESTASSETNUM = 0x14;//资产编号
	protected static final int REQUESTASSETINPUT = 0x15;//手动输入资产编号
	protected static final int REQUESTLOGICALADDRESS = 0x16;//逻辑地址
	protected static final int REQUESTLOGICALINPUT = 0x17;//手动输入逻辑地址
	protected static final int REQUESTINSTALLINPUT = 0x18;//手动输入安装地点
	private static final int REQUESTCUSTOMERUNIT = 0x19;//客户单位
	protected static final int REQUESTFACTORY = 0x20;//设备厂家

	protected TaskViewPanel mPanel;
	protected TaskDetailBean mBean;
	private long startTime;
	protected ArrayList<AffiliatedFileBean> accessoryList;
	protected PopupMenu popupMenu,barcodePopMenu,assetNumPopMenu,logicalAddressPopMenu,equipmentInstallLocation;
	protected Handler handler;
	private SaveTaskListener listener;
	protected double longitude,latitude;
	
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
		mBean = (TaskDetailBean) getArguments().get(SELECTTASK);
		Bundle args = new Bundle();
		args.putInt(TASKNETID, mBean.taskNetId);
		args.putInt(TASKLOCALID, mBean.taskLocalId);
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
		try {
			getActivity().unregisterReceiver(mLocationReceiver);
		} catch (Exception e) {
		}
		OMLocationManager.get(getActivity()).stopLocationUpdate();
		super.onPause();
	}

	private void initView(View v) {
		mPanel = new TaskViewPanel(v);
		mPanel.setTaskShow(mBean);
		mPanel.taskLocation.setOnClickListener(editTaskClickListener);
		mPanel.taskPlanStartTime.setOnClickListener(editTaskClickListener);
		mPanel.taskPlanEndTime.setOnClickListener(editTaskClickListener);
		mPanel.taskAccessory.setOnClickListener(editTaskClickListener);
		mPanel.taskEquipment.setOnClickListener(editTaskClickListener);
		mPanel.taskType.setOnClickListener(editTaskClickListener);
		
		mPanel.taskEquipmentType.setOnClickListener(editTaskClickListener);
		mPanel.customerUnit.setOnClickListener(editTaskClickListener);
		mPanel.taskInstallLocation.setOnClickListener(editTaskClickListener);
		mPanel.assetNumber.setOnClickListener(editTaskClickListener);
		mPanel.logicalAddress.setOnClickListener(editTaskClickListener);
		mPanel.equipmentFactory.setOnClickListener(editTaskClickListener);
		
		initPopMenu();
		getActivity().getActionBar().setTitle("编辑任务");
	}

	protected void initPopMenu() {
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
		assetNumPopMenu = new PopupMenu(getActivity(), mPanel.assetNumber);
		assetNumPopMenu.inflate(R.menu.qrcode_menu);
		assetNumPopMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menu_scan:
					Intent i = new Intent(getActivity(),
							CaptureActivity.class);
					startActivityForResult(i, REQUESTASSETNUM);
					return true;
				case R.id.menu_input_qrcode:
					InputDialogFragment inputFragment = (InputDialogFragment) InputDialogFragment.newInstance("资产编号", "输入资产编号");
					inputFragment.setTargetFragment(EditTaskFragment.this,
							REQUESTASSETINPUT);
					inputFragment.show(getFragmentManager(), null);
					return true;
				default:
					return true;
				}
			}
		});
		logicalAddressPopMenu = new PopupMenu(getActivity(), mPanel.logicalAddress);
		logicalAddressPopMenu.inflate(R.menu.qrcode_menu);
		logicalAddressPopMenu
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
						case R.id.menu_scan:
							Intent i = new Intent(getActivity(),
									CaptureActivity.class);
							startActivityForResult(i, REQUESTLOGICALADDRESS);
							return true;
						case R.id.menu_input_qrcode:
							InputDialogFragment inputFragment = (InputDialogFragment) InputDialogFragment.newInstance("逻辑地址", "输入逻辑地址");
							inputFragment.setTargetFragment(
									EditTaskFragment.this, REQUESTLOGICALINPUT);
							inputFragment.show(getFragmentManager(), null);
							return true;
						default:
							return true;
						}
					}
				});
		equipmentInstallLocation = new PopupMenu(getActivity(), mPanel.taskInstallLocation);
		equipmentInstallLocation.inflate(R.menu.equipment_install_address);
		equipmentInstallLocation.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menu_autoget:
							if (Utils.isNetWorkEnable()) {
								OMLocationManager.get(getActivity()).setLocCilentOption(null);
								OMLocationManager.get(getActivity()).startLocationUpdate();
							} else {
								UItoolKit.showToastShort(getActivity(),"网络连接不可用，不能自动获取");
							}
					return true;
				case R.id.menu_input_install:
					InputDialogFragment inputFragment = (InputDialogFragment) InputDialogFragment.newInstance("安装地点", "输入安装地点");
					inputFragment.setTargetFragment(
							EditTaskFragment.this, REQUESTINSTALLINPUT);
					inputFragment.show(getFragmentManager(), null);
					return true;
				default:
					return true;
				}
			}
		});
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
			if (longitude != 0 || latitude != 0) {
				tempBean.taskInstallInfo = longitude + "|" + latitude;
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
			TaskDetailBean mBean = mPanel.getTaskBean();
			if (longitude != 0 || latitude != 0) {
				mBean.taskInstallInfo = longitude + "|" + latitude;
			}
			ThreadPool.runMethod(new CreateTaskThread(handler, getActivity(), mBean,EditTaskFragment.this));
/*			OMLocationManager.get(getActivity()).setLocCilentOption(null);
			OMLocationManager.get(getActivity()).startLocationUpdate();*/
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {

		@Override
		protected void onNetWorkLocationReceived(Context context,
				OMLocationBean loc) {
			longitude = loc.longitude;
			latitude = loc.latitude;
			mPanel.taskLocation.setText(loc.province+loc.city+loc.district);
			mPanel.taskInstallLocation.setText(loc.address);
			OMLocationManager.get(getActivity()).stopLocationUpdate();
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

	private OnClickListener editTaskClickListener = new OnClickListener() {
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
				try {
					getActivity().unregisterReceiver(mLocationReceiver);
				} catch (Exception e) {
				}
				OMLocationManager.get(getActivity()).stopLocationUpdate();
				UploadFileDialog uploadDialog = (UploadFileDialog) UploadFileDialog
						.newInstance(mBean.taskNetId, mBean.taskTitle,false,mBean.taskLocalId);
				uploadDialog.show(fm, null);
				break;
			case R.id.edit_task_equipment:
				barcodePopMenu.show();
				break;
			case R.id.edit_task_type:
				DialogFragment taskTypeDialog = TaskTypeDialogFragment.newInstance("任务类别");
				taskTypeDialog.setTargetFragment(EditTaskFragment.this, REQUESTTYPE);
				taskTypeDialog.show(getFragmentManager(), null);
				break;
			case R.id.edit_task_equipment_type:
				DialogFragment dialog = TaskTypeDialogFragment.newInstance("设备类型");
				dialog.setTargetFragment(EditTaskFragment.this, REQUESTPROTUCT);
				dialog.show(getFragmentManager(), null);
				break;
			case R.id.tv_customer_unit:
				InputDialogFragment inputCustomer = (InputDialogFragment) InputDialogFragment.newInstance("客户单位", "输入客户单位");
				inputCustomer.setTargetFragment(EditTaskFragment.this, REQUESTCUSTOMERUNIT);
				inputCustomer.show(getFragmentManager(), null);
				break;
			case R.id.edit_task_install_location:
				equipmentInstallLocation.show();
				break;
			case R.id.tv_asset_number:
				assetNumPopMenu.show();
				break;
			case R.id.tv_task_logical_address:
				logicalAddressPopMenu.show();
				break;
			case R.id.tv_task_equipment_factory:
				DialogFragment factoryEquipment = new EquipmentFactoryDialog();
				factoryEquipment.setTargetFragment(EditTaskFragment.this, REQUESTFACTORY);
				factoryEquipment.show(getFragmentManager(), null);
				break;
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUESTLOCATION) {
				mPanel.taskLocation.setText(data
						.getStringExtra(SelectLocationDialogFragment.LOCATION));
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
				mPanel.taskEquipmentType
						.setText(((CommonDataBean) data
								.getParcelableExtra(TaskTypeDialogFragment.TYPENAME)).typeName);
			}else if(requestCode == REQUESTINPUT){
				mPanel.taskLocation.setText(data.getStringExtra(InputDialogFragment.INPUTTEXT));
			}else if(requestCode == REQUESTCODETINPUT){
				mPanel.taskEquipment.setText(data.getStringExtra(InputDialogFragment.INPUTTEXT));
			} else if (requestCode == REQUESTCUSTOMERUNIT) {
				mPanel.customerUnit.setText(data.getStringExtra(InputDialogFragment.INPUTTEXT));
			} else if (requestCode == REQUESTASSETNUM){
				mPanel.assetNumber.setText(data.getStringExtra("result"));
			} else if (requestCode == REQUESTASSETINPUT){
				mPanel.assetNumber.setText(data.getStringExtra(InputDialogFragment.INPUTTEXT));
			} else if (requestCode == REQUESTLOGICALADDRESS){
				mPanel.logicalAddress.setText(data.getStringExtra("result"));
			} else if (requestCode == REQUESTLOGICALINPUT){
				mPanel.logicalAddress.setText(data.getStringExtra(InputDialogFragment.INPUTTEXT));
			} else if (requestCode == REQUESTINSTALLINPUT){
				mPanel.taskInstallLocation.setText(data.getStringExtra(InputDialogFragment.INPUTTEXT));
			} else if(requestCode == REQUESTFACTORY){
				mPanel.equipmentFactory.setText(data.getStringExtra(EquipmentFactoryDialog.FACTORYNAME));
			}
		}
	};
	
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
}
