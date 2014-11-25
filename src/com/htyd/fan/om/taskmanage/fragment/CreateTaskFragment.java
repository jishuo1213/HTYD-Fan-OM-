package com.htyd.fan.om.taskmanage.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.ui.SelectLocationDialogFragment;
import com.htyd.fan.om.util.ui.UItoolKit;

public class CreateTaskFragment extends Fragment {

	private TaskViewPanel mPanel;
	private TaskDetailBean mBean;
	private SelectViewClickListener mListener;
	private OMUserDatabaseManager mManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mBean = new TaskDetailBean();
		mListener = new SelectViewClickListener();
		mManager = OMUserDatabaseManager.getInstance(getActivity());
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == 0){
				String [] str = data.getStringExtra(SelectLocationDialogFragment.LOCATION).split("|");
				mBean.workProvince = str[0];
				mBean.workCity = str[1];
				mBean.workDistrict = str[2];
				mPanel.taskWorkLocation.setText(mBean.getWorkLocation());
			}
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.create_task_menu,menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.menu_create_task:
		if(!mPanel.canSave()){
			UItoolKit.showToastShort(getActivity(), "标题、工作地点、安装地点、开始时间不能为空");
			return true;
		}
		mPanel.getTaskDetailBean(mBean);
		mManager.openDb(1);
		mManager.insertTaskBean(mBean);
		/**
		 * 上传到服务器
		 */
		return true;
		default: 
			return super.onOptionsItemSelected(item);
		}
	}
	private void initView(View v) {
		mPanel = new TaskViewPanel(v);
		mPanel.setListener();
	}

	private class TaskViewPanel {

		private TextView addAccessory;
		private EditText taskTitle, taskDescription, taskWorkLocation,
				taskInstallLocation, taskStartTime, taskNeedTime, taskContact,
				taskContactPhone, taskEquipment, taskType;

		public TaskViewPanel(View v) {
			addAccessory = (TextView) v.findViewById(R.id.tv_add_accessory);
			taskTitle = (EditText) v.findViewById(R.id.edit_task_title);
			taskDescription = (EditText) v
					.findViewById(R.id.edit_task_description);
			taskWorkLocation = (EditText) v
					.findViewById(R.id.edit_work_location);
			taskInstallLocation = (EditText) v
					.findViewById(R.id.edit_install_location);
			taskStartTime = (EditText) v.findViewById(R.id.edit_start_time);
			taskNeedTime = (EditText) v.findViewById(R.id.edit_work_need_time);
			taskContact = (EditText) v.findViewById(R.id.edit_contacts);
			taskContactPhone = (EditText) v
					.findViewById(R.id.edit_contacts_phone);
			taskEquipment = (EditText) v.findViewById(R.id.edit_task_equipment);
			taskType = (EditText) v.findViewById(R.id.edit_task_type);
		}

		public boolean canSave() {
			return !TextUtils.isEmpty(taskTitle.getText())
					&& !TextUtils.isEmpty(taskWorkLocation.getText())
					&& !TextUtils.isEmpty(taskInstallLocation.getText())
					&& !TextUtils.isEmpty(taskStartTime.getText());
		}
		
		public void getTaskDetailBean(TaskDetailBean mBean){
			mBean.taskDescription = taskDescription.getText().toString();
			mBean.installLocation = taskInstallLocation.getText().toString();
			mBean.taskTitle = taskTitle.getText().toString();
			mBean.taskContacts = taskContact.getText().toString();
			mBean.contactsPhone = taskContactPhone.getText().toString();
			mBean.equipment = taskEquipment.getText().toString();
			mBean.taskContacts = taskDescription.getText().toString();
			mBean.taskType = 1;
		}
		
		public void setListener(){
			taskWorkLocation.setOnClickListener(mListener);
			taskStartTime.setOnClickListener(mListener);
			taskNeedTime.setOnClickListener(mListener);
			taskEquipment.setOnClickListener(mListener);
			taskType.setOnClickListener(mListener);
			addAccessory.setOnClickListener(mListener);
		}
	}
	
	private class SelectViewClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.edit_work_location:
				FragmentManager fm = getActivity().getFragmentManager();
				SelectLocationDialogFragment dialog = new SelectLocationDialogFragment();
				dialog.setTargetFragment(CreateTaskFragment.this, 0);
				dialog.show(fm, null);
				break;
			}
		}
	}
}
