package com.htyd.fan.om.taskmanage.fragment;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.map.LocationReceiver;
import com.htyd.fan.om.map.OMLocationManager;
import com.htyd.fan.om.model.CommonDataBean;
import com.htyd.fan.om.model.OMLocationBean;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.util.base.Preferences;
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
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.ui.UItoolKit;
import com.htyd.fan.om.util.zxing.CaptureActivity;

public class CreateTaskFragment extends Fragment implements ChooseAddressListener{

/*	private static final int REQUESTPHOTO = 1;// 照片
	private static final int REQUESTRECORDING = 2;// 录音
*/	private static final int REQUESTSTARTDATE = 3;// 开始时间
	private static final int REQUESTENDTIME = 4;// 结束时间
	private static final int REQUESTZXING = 5;//条码扫描
	private static final int REQUESTTYPE = 0x06;//任务类型
	private static final int REQUESTLOCATIONINPUT = 0x07;//工作地点
	private static final int REQUESTCODEINPUT = 0x08;//条码输入
	
	private TaskViewPanel mPanel;
	private TaskDetailBean mBean;
	private SelectViewClickListener mListener;
	private OMUserDatabaseManager mManager;
	protected long startTime;
	protected double latitiude, longitude;
	protected PopupMenu locationPopupMenu,barcodePopMenu;
	protected SaveTaskListener saveListener;
	protected Handler handler;
	
	public interface SaveTaskListener {
		public void onSaveSuccess(TaskDetailBean mBean,boolean isLocal);
	}
	
	public static Fragment newInstance(SaveTaskListener saveListener){
		CreateTaskFragment fragment = new CreateTaskFragment();
		fragment.setSaveListener(saveListener);
		return fragment;
	}

	public void setSaveListener(SaveTaskListener saveListener) {
		this.saveListener = saveListener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mBean = new TaskDetailBean();
		mListener = new SelectViewClickListener();
		mManager = OMUserDatabaseManager.getInstance(getActivity());
		handler = new Handler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.create_task_fragment_layout,
				container, false);
		initView(v);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (Utils.isNetWorkEnable()) {
			Log.i("fanjishuo___onActivityCreated", "onActivityCreated");
			OMLocationManager.get(getActivity()).setLocCilentOption(null);
			OMLocationManager.get(getActivity()).startLocationUpdate();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.i("fanjishuo___onStart", "onStarts");
		getActivity().registerReceiver(mLocationReceiver, new IntentFilter(OMLocationManager.ACTION_LOCATION));
	}
	
	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mLocationReceiver);
		super.onStop();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 0) {
				mPanel.taskWorkLocation.setText(data
						.getStringExtra(SelectLocationDialogFragment.LOCATION));
			} else if (requestCode == REQUESTSTARTDATE) {
				startTime = data
						.getLongExtra(DateTimePickerDialog.EXTRATIME, 0);
				mPanel.taskStartTime.setText(Utils.formatTime(startTime));
				UItoolKit.showToastShort(getActivity(), Utils.formatTime(data
						.getLongExtra(DateTimePickerDialog.EXTRATIME, 0)));
			} else if (requestCode == REQUESTENDTIME) {
				UItoolKit.showToastShort(getActivity(), Utils.formatTime(data
						.getLongExtra(SpendTimePickerDialog.ENDTIME, 0)));
				mPanel.taskNeedTime.setText( Utils.formatTime(data
						.getLongExtra(SpendTimePickerDialog.ENDTIME, 0)));
				
			}else if(requestCode == REQUESTZXING){
				UItoolKit.showToastShort(getActivity(),data.getStringExtra("result"));
				mPanel.taskEquipment.setText(data.getStringExtra("result"));
			}else if(requestCode == REQUESTTYPE){
				CommonDataBean mBean = data.getParcelableExtra(TaskTypeDialogFragment.TYPENAME);
				mPanel.taskType.setText(mBean.typeName);
			} else if (requestCode == REQUESTLOCATIONINPUT) {
				mPanel.taskWorkLocation.setText(data
						.getStringExtra(InputDialogFragment.INPUTTEXT));
			} else if (requestCode == REQUESTCODEINPUT) {
				mPanel.taskEquipment.setText(data
						.getStringExtra(InputDialogFragment.INPUTTEXT));
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.create_task_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_create_task:
			if (!mPanel.canSave()) {
				UItoolKit
						.showToastShort(getActivity(), "标题、工作地点、安装地点、开始时间不能为空");
				return true;
			}
			mPanel.getTaskDetailBean(mBean);
			if(!Utils.isNetWorkEnable()){
				offLineCreateTask();
				return true;
			}
			startTask(mBean);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void offLineCreateTask() {
		mBean.isSyncToServer = 0;
		ThreadPool.runMethod(new Runnable() {
			@Override
			public void run() {
				OMUserDatabaseManager.getInstance(getActivity()).openDb(1);
				mBean.saveTime = System.currentTimeMillis();
				long temp = OMUserDatabaseManager.getInstance(getActivity()).insertTaskBean(mBean);
				mBean.taskLocalId = (int) temp;
				if (temp > 0) {
					handler.post(saveSuccess);
				} else {
					handler.post(saveFail);
				}
			}
		});
	}

	private void initView(View v) {
		mPanel = new TaskViewPanel(v);
		mPanel.setListener();
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
							fragment.setTargetFragment(CreateTaskFragment.this, REQUESTCODEINPUT);
							fragment.show(getFragmentManager(), null);
							return true;
						default:
							return true;
						}
					}
				});
		locationPopupMenu = new PopupMenu(getActivity(), mPanel.taskWorkLocation);
		locationPopupMenu.inflate(R.menu.select_address_menu);
		locationPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menu_select_common:
					AddressListDialog dialogList = new AddressListDialog(CreateTaskFragment.this);
					dialogList.show(getActivity().getFragmentManager(), null);
					return true;
				case R.id.menu_select_all:
					FragmentManager fm = getActivity().getFragmentManager();
					SelectLocationDialogFragment locationDialog = new SelectLocationDialogFragment();
					locationDialog.setTargetFragment(CreateTaskFragment.this, 0);
					locationDialog.show(fm, null);
					return true;
				case R.id.menu_input:
					DialogFragment inputdialog = InputDialogFragment.newInstance("工作地点", "请输入工作地点");
					inputdialog.setTargetFragment(CreateTaskFragment.this, REQUESTLOCATIONINPUT);
					inputdialog.show(getFragmentManager(), null);
					return true;
				default:
					return true;
				}
			}
		});
//		accessoryListView = (ListViewForScrollView) v.findViewById(R.id.list_accessory);
		getActivity().getActionBar().setTitle("新建任务");
	}

	protected Runnable saveSuccess = new Runnable() {
		@Override
		public void run() {
			UItoolKit.showToastShort(getActivity(), "保存至本地成功");
			saveListener.onSaveSuccess(mBean,true);
			back();
		}
	};
	
	protected Runnable saveFail = new Runnable() {
		@Override
		public void run() {
			UItoolKit.showToastShort(getActivity(), "保存至本地出错,请重试");
		}
	};
	
	private class TaskViewPanel {

		private TextView taskWorkLocation, taskStartTime, taskNeedTime,
				taskEquipment, taskType;
		private EditText taskTitle, taskDescription, taskInstallLocation;

		public TaskViewPanel(View v) {
			taskTitle = (EditText) v.findViewById(R.id.edit_task_title);
			taskDescription = (EditText) v
					.findViewById(R.id.edit_task_description);
			taskWorkLocation = (TextView) v.findViewById(R.id.tv_work_location);
			taskInstallLocation = (EditText) v
					.findViewById(R.id.edit_install_location);
			taskStartTime = (TextView) v.findViewById(R.id.tv_start_time);
			taskNeedTime = (TextView) v.findViewById(R.id.tv_work_need_time);
			taskEquipment = (TextView) v.findViewById(R.id.tv_task_equipment);
			taskType = (TextView) v.findViewById(R.id.tv_task_type);
		}

		public boolean canSave() {
			return !TextUtils.isEmpty(taskTitle.getText())
					&& !TextUtils.isEmpty(taskWorkLocation.getText())
					&& !TextUtils.isEmpty(taskInstallLocation.getText())
					&& !TextUtils.isEmpty(taskStartTime.getText());
		}

		public void getTaskDetailBean(TaskDetailBean mBean) {
			mBean.taskDescription = taskDescription.getText().toString();
			mBean.installLocation = taskInstallLocation.getText().toString();
			mBean.workLocation = taskWorkLocation.getText().toString();
			mBean.taskTitle = taskTitle.getText().toString();
			mBean.equipment = taskEquipment.getText().toString();
			mBean.planStartTime = startTime;
			mBean.planEndTime = Utils.parseDate(taskNeedTime.getText().toString(), "yyyy年MM月dd日 HH:mm:ss");
			mBean.recipientsName = Preferences.getUserinfo(getActivity(), "YHMC");
			mBean.recipientPhone = Preferences.getUserinfo(getActivity(), "SHOUJ");
			mBean.taskState = 0;
			mBean.taskType = taskType.getText().toString();
		}

		public void setListener() {
			taskWorkLocation.setOnClickListener(mListener);
			taskStartTime.setOnClickListener(mListener);
			taskNeedTime.setOnClickListener(mListener);
			taskEquipment.setOnClickListener(mListener);
			taskType.setOnClickListener(mListener);
		}
	}

	private class SelectViewClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			FragmentManager fm = getActivity().getFragmentManager();
			switch (v.getId()) {
			case R.id.tv_work_location:
				locationPopupMenu.show();
				break;
			case R.id.tv_start_time:
				DateTimePickerDialog dateDialog = (DateTimePickerDialog) DateTimePickerDialog
						.newInstance(true);
				dateDialog.setTargetFragment(CreateTaskFragment.this,
						REQUESTSTARTDATE);
				dateDialog.show(fm, null);
				break;
			case R.id.tv_work_need_time:
				if (startTime == 0) {
					UItoolKit.showToastShort(getActivity(), "请先选择开始时间");
					return;
				}
				SpendTimePickerDialog spendDialog = (SpendTimePickerDialog) SpendTimePickerDialog
						.newInstance(startTime);
				spendDialog.setTargetFragment(CreateTaskFragment.this,
						REQUESTENDTIME);
				spendDialog.show(fm, null);
				break;
			case R.id.tv_task_equipment:
/*				Intent i = new Intent(getActivity(),CaptureActivity.class);
				startActivityForResult(i, REQUESTZXING);*/
				barcodePopMenu.show();
				break;
			case R.id.tv_task_type:
				DialogFragment dialog = TaskTypeDialogFragment.newInstance("任务类别");
				dialog.setTargetFragment(CreateTaskFragment.this, REQUESTTYPE);
				dialog.show(getFragmentManager(), null);
				break;
			}
		}
	}
	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {

		@Override
		protected void onNetWorkLocationReceived(Context context,
				OMLocationBean loc) {
			latitiude = loc.latitude;
			longitude = loc.longitude;
			OMLocationManager.get(getActivity()).stopLocationUpdate();
		}
		
		@Override
		protected void onNetDisableReceived(Context context) {
			UItoolKit.showToastShort(getActivity(), "网络连接失败");
			OMLocationManager.get(getActivity()).stopLocationUpdate();
		}
	};
	
	private class SaveTask extends AsyncTask<TaskDetailBean, Void, Boolean>{

		@Override
		protected Boolean doInBackground(TaskDetailBean... params) {
			String result = "";
			try {
				JSONObject param = params[0].toJson();
				param.put("LQR", Preferences.getUserinfo(getActivity(), "YHID"));
				param.put("RWJD", longitude);
				param.put("RWWD", latitiude);
				result = NetOperating.getResultFromNet(getActivity(), param, Urls.TASKURL, "Operate=saveRwxx");
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			try {
				JSONObject json = new JSONObject(result);
				if(json.has("RWID"))
					mBean.taskNetId = Integer.parseInt(json.getString("RWID"));
				if(json.has("TXSJ"))
					mBean.saveTime = Utils.parseDate(json.getString("TXSJ"));
				return json.getBoolean("RESULT");
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				mManager.openDb(1);
				mBean.isSyncToServer = 1;
				ThreadPool.runMethod(new Runnable() {
					@Override
					public void run() {
						long temp =  mManager.insertTaskBean(mBean);
						if(temp > 0){
							back();
							mBean.taskLocalId = (int) temp;
							saveListener.onSaveSuccess(mBean,false);
						}
					}
				});
				UItoolKit.showToastShort(getActivity(), "保存成功!");
			}else{
				UItoolKit.showToastShort(getActivity(), "保存至网络失败");
				offLineCreateTask();
			}
		}
	}
	
	protected void startTask(TaskDetailBean mBean){
		new SaveTask().execute(mBean);
	}
	
	protected void stopTask(SaveTask task){
		task.cancel(false);
	}

	@Override
	public void onAddressChoose(String address) {
		mPanel.taskWorkLocation.setText(address);
	}
	
	protected void back() {
		ThreadPool.runMethod(new Runnable() {
			@Override
			public void run() {
				finishActivity();
			}
		});
	}
	protected void finishActivity() {
		try {
			Instrumentation inst = new Instrumentation();
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			getFragmentManager().beginTransaction().remove(CreateTaskFragment.this).commit();
		} catch (Exception e) {
			Log.e("Exception when onBack", e.toString());
		}
	}
}
