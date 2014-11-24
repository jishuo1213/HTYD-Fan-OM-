package com.htyd.fan.om.taskmanage.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;

public class CreateTaskFragment extends Fragment {

	private TaskViewPanel mPanel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.create_task_fragment_layout,
				container, false);
		initView(v);
		return v;
	}

	private void initView(View v) {
		mPanel = new TaskViewPanel(v);
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
			return TextUtils.isEmpty(taskTitle.getText())
					&& TextUtils.isEmpty(taskWorkLocation.getText())
					&& TextUtils.isEmpty(taskInstallLocation.getText())
					&& TextUtils.isEmpty(taskStartTime.getText());
		}
		
		public void getTaskDetailBean(TaskDetailBean mBean){
			mBean.taskDescription = taskDescription.getText().toString();
			mBean.installLocation = taskInstallLocation.getText().toString();
			mBean.taskTitle = taskTitle.getText().toString();
			mBean.taskContacts = taskContact.getText().toString();
			mBean.contactsPhone = taskContactPhone.getText().toString();
			mBean.equipment = taskEquipment.getText().toString();
			mBean.taskContacts = taskDescription.getText().toString();
		}
	}
}
