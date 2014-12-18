package com.htyd.fan.om.taskmanage.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.map.LocationReceiver;
import com.htyd.fan.om.map.OMLocationManager;
import com.htyd.fan.om.model.OMLocationBean;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.fragment.DateTimePickerDialog;
import com.htyd.fan.om.util.fragment.SelectLocationDialogFragment;
import com.htyd.fan.om.util.fragment.SpendTimePickerDialog;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.ui.UItoolKit;
import com.htyd.fan.om.util.zxing.CaptureActivity;

public class CreateTaskFragment extends Fragment{

/*	private static final int REQUESTPHOTO = 1;// 照片
	private static final int REQUESTRECORDING = 2;// 录音
*/	private static final int REQUESTSTARTDATE = 3;// 开始时间
	private static final int REQUESTENDTIME = 4;// 结束时间
	private static final int REQUESTZXING = 5;//条码扫描

	private TaskViewPanel mPanel;
	private TaskDetailBean mBean;
	private SelectViewClickListener mListener;
	private OMUserDatabaseManager mManager;
	protected long startTime;
//	private List<AffiliatedFileBean> accessoryList;
//	protected ListViewForScrollView accessoryListView;
	protected double latitiude, longitude;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mBean = new TaskDetailBean();
		mListener = new SelectViewClickListener();
		mManager = OMUserDatabaseManager.getInstance(getActivity());
		OMLocationManager.get(getActivity()).startLocationUpdate();
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
	public void onStart() {
		super.onStart();
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
				
			} /*else if (requestCode == REQUESTPHOTO) {
				if(accessoryList == null){
					accessoryList = new ArrayList<AffiliatedFileBean>();
				}
				AffiliatedFileBean mBean = new AffiliatedFileBean();
				mBean.filePath = data.getStringExtra(CameraFragment.EXTRA_PHOTO_FILENAME);
				mBean.fileState = 0;
				accessoryList.add(mBean);
				if(accessoryListView.getAdapter() == null){
					accessoryListView.setAdapter(new TaskAccessoryAdapter(accessoryList,getActivity(),this));
				}else{
					TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter)accessoryListView.getAdapter();
					mAdapter.notifyDataSetChanged();
				}
				UItoolKit.showToastShort(getActivity(), data
						.getStringExtra(CameraFragment.EXTRA_PHOTO_FILENAME));
			} else if (requestCode == REQUESTRECORDING) {
				UItoolKit.showToastShort(getActivity(),data.getStringArrayExtra(RecodingDialogFragment.FILEPATHARRAY)[0]);
			}*/else if(requestCode == REQUESTZXING){
				UItoolKit.showToastShort(getActivity(),data.getStringExtra("result"));
				mPanel.taskEquipment.setText(data.getStringExtra("result"));
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
			startTask(mBean);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

/*	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater()
				.inflate(R.menu.add_accessory_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_take_photo:
			Intent i = new Intent(getActivity(), CameraActivity.class);
			startActivityForResult(i, REQUESTPHOTO);
			return true;
		case R.id.menu_select_file:
			return true;
		case R.id.menu_recoring:
			FragmentManager fm = getActivity().getFragmentManager();
			RecodingDialogFragment dialog = new RecodingDialogFragment();
			dialog.setTargetFragment(CreateTaskFragment.this, REQUESTRECORDING);
			dialog.show(fm, null);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}*/

	private void initView(View v) {
		mPanel = new TaskViewPanel(v);
		mPanel.setListener();
//		accessoryListView = (ListViewForScrollView) v.findViewById(R.id.list_accessory);
	}

	private class TaskViewPanel {

		private TextView taskWorkLocation, taskStartTime, taskNeedTime,
				taskEquipment, taskType;
		private EditText taskTitle, taskDescription, taskInstallLocation;
				/*taskContact, taskContactPhone, addAccessory*/

		public TaskViewPanel(View v) {
//			addAccessory = (TextView) v.findViewById(R.id.tv_add_accessory);
			taskTitle = (EditText) v.findViewById(R.id.edit_task_title);
			taskDescription = (EditText) v
					.findViewById(R.id.edit_task_description);
			taskWorkLocation = (TextView) v.findViewById(R.id.tv_work_location);
			taskInstallLocation = (EditText) v
					.findViewById(R.id.edit_install_location);
			taskStartTime = (TextView) v.findViewById(R.id.tv_start_time);
			taskNeedTime = (TextView) v.findViewById(R.id.tv_work_need_time);
/*			taskContact = (EditText) v.findViewById(R.id.edit_contacts);
			taskContactPhone = (EditText) v
					.findViewById(R.id.edit_contacts_phone);*/
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
/*			mBean.taskContacts = taskContact.getText().toString();
			mBean.contactsPhone = taskContactPhone.getText().toString();*/
			mBean.equipment = taskEquipment.getText().toString();
//			mBean.taskContacts = taskDescription.getText().toString();
			mBean.planStartTime = startTime;
			mBean.planEndTime = Utils.parseDate(taskNeedTime.getText().toString(), "yyyy年MM月dd日 HH:mm:ss");
			mBean.recipientsName = Preferences.getUserinfo(getActivity(), "YHMC");
			mBean.recipientPhone = Preferences.getUserinfo(getActivity(), "SHOUJ");
			mBean.taskState = 0;
			mBean.taskType = 1;
		}

		public void setListener() {
			taskWorkLocation.setOnClickListener(mListener);
			taskStartTime.setOnClickListener(mListener);
			taskNeedTime.setOnClickListener(mListener);
			taskEquipment.setOnClickListener(mListener);
			taskType.setOnClickListener(mListener);
//			addAccessory.setOnClickListener(mListener);
//			registerForContextMenu(addAccessory);
		}
	}

	private class SelectViewClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			FragmentManager fm = getActivity().getFragmentManager();
			switch (v.getId()) {
			case R.id.tv_work_location:
				SelectLocationDialogFragment locationDialog = new SelectLocationDialogFragment();
				locationDialog.setTargetFragment(CreateTaskFragment.this, 0);
				locationDialog.show(fm, null);
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
				Intent i = new Intent(getActivity(),CaptureActivity.class);
				startActivityForResult(i, REQUESTZXING);
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
		protected void onGPSLocationReceived(Context context, OMLocationBean loc) {
		}
		
	};
/*	@Override
	public void onUpLoadClick(AffiliatedFileBean mBean,int position) {
		HttpMultipartPost post = new HttpMultipartPost(getActivity(), mBean.filePath,this);
		post.execute();
	}

	@Override
	public void onDeleteClick(AffiliatedFileBean mBean, int position) {
		accessoryList.remove(position);
//		TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter)accessoryListView.getAdapter();
//		mAdapter.notifyDataSetChanged();
	}*/
	
/*	@Override
	public void onPreviewImg(String path) {
	}*/
	
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
				Log.i("fanjishuo___doInBackground", result+"result");
				if(json.has("RWID"))
					mBean.taskNetId = Integer.parseInt(json.getString("RWID"));
				return json.getBoolean("RESULT");
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				mBean.saveTime = System.currentTimeMillis();
				mManager.openDb(1);
				mManager.insertTaskBean(mBean);
				UItoolKit.showToastShort(getActivity(), "保存成功!");
				getActivity().finish();
			}else{
				UItoolKit.showToastShort(getActivity(), "保存至网络失败");
			}
		}
	}
	
	protected void startTask(TaskDetailBean mBean){
		new SaveTask().execute(mBean);
	}
	
	protected void stopTask(SaveTask task){
		task.cancel(false);
	}

/*	@Override
	public void onUpLoadFinish(AffiliatedFileBean mBean,int position) {
	}*/

}
