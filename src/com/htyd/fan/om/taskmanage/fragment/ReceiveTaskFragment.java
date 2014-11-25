package com.htyd.fan.om.taskmanage.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;

public class ReceiveTaskFragment extends Fragment {

	private static final String SELECTTASK = "selecttask";

	private TaskViewPanel mPanel;
	private TaskDetailBean mBean;


	public static Fragment newInstance(Parcelable mBean) {
		Bundle args = new Bundle();
		args.putParcelable(SELECTTASK, mBean);
		Fragment fragment = new ReceiveTaskFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBean = (TaskDetailBean) getArguments().get(SELECTTASK);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.task_detail_fragment_layout,
				container, false);
		initView(v);
		return v;
	}

	private void initView(View v) {
		mPanel = new TaskViewPanel(v);
		mPanel.setTaskShow(mBean);
		mPanel.setViewEnable();
		getActivity().getActionBar().setTitle("查看待领取任务");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.task_detail_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/**
		 * 处理领取任务的逻辑
		 */
		return super.onOptionsItemSelected(item);
	}

	private class TaskViewPanel {

		public TextView taskLocation, taskAddress/* taskAccessory */;
		public EditText taskInstallLocation, taskTitle, taskDescription,
				taskContacts, taskContactsPhone, taskPlanStartTime,
				taskPlanEndTime, taskEquipment, taskProductType, taskState,
				taskType, taskRecipient, taskRecipientPhone;

		public TaskViewPanel(View v) {
			taskLocation = (TextView) v.findViewById(R.id.tv_task_location);
			taskAddress = (TextView) v.findViewById(R.id.tv_task_address);
			// taskAccessory = (TextView)
			// v.findViewById(R.id.tv_task_accessory);
			taskInstallLocation = (EditText) v
					.findViewById(R.id.edit_task_install_location);
			taskTitle = (EditText) v.findViewById(R.id.edit_task_title);
			taskDescription = (EditText) v
					.findViewById(R.id.edit_task_description);
			taskContacts = (EditText) v.findViewById(R.id.edit_task_contacts);
			taskContactsPhone = (EditText) v
					.findViewById(R.id.edit_task_contacts_phone);
			taskPlanStartTime = (EditText) v
					.findViewById(R.id.edit_task_plan_starttime);
			taskPlanEndTime = (EditText) v
					.findViewById(R.id.edit_task_plan_endtime);
			taskEquipment = (EditText) v.findViewById(R.id.edit_task_equipment);
			taskProductType = (EditText) v
					.findViewById(R.id.edit_task_product_type);
			taskState = (EditText) v.findViewById(R.id.edit_task_state);
			taskType = (EditText) v.findViewById(R.id.edit_task_type);
			taskRecipient = (EditText) v.findViewById(R.id.edit_task_recipient);
			taskRecipientPhone = (EditText) v
					.findViewById(R.id.edit_task_recipient_phone);
		}

		public void setTaskShow(TaskDetailBean mBean) {
			taskLocation.setText(mBean.getWorkLocation());
			taskAddress.setText(mBean.getDetailAddress());
			taskInstallLocation.setText(mBean.installLocation);
			taskTitle.setText(mBean.taskTitle);
			taskDescription.setText(mBean.taskDescription);
			taskContacts.setText(mBean.taskContacts);
			taskContactsPhone.setText(mBean.contactsPhone);
			taskPlanStartTime.setText(mBean.getStartTime());
			taskPlanEndTime.setText(mBean.getEndTime());
			taskEquipment.setText(mBean.equipment);
			taskProductType.setText(mBean.productType);
			taskState.setText(mBean.taskState + "");
			taskType.setText(mBean.taskType + "");
			taskRecipient.setText(mBean.recipientsName);
			taskRecipientPhone.setText(mBean.recipientPhone);
		}

		/*
		 * public void getTaskBean(TaskDetailBean mBean) { mBean.installLocation
		 * = taskInstallLocation.getText().toString(); mBean.taskTitle =
		 * taskTitle.getText().toString(); mBean.taskDescription =
		 * taskDescription.getText().toString(); mBean.taskContacts =
		 * taskContacts.getText().toString(); mBean.contactsPhone =
		 * taskContactsPhone.getText().toString(); mBean.equipment =
		 * taskEquipment.getText().toString(); mBean.productType =
		 * taskProductType.getText().toString(); mBean.recipientsName =
		 * taskRecipient.getText().toString(); mBean.recipientPhone =
		 * taskRecipientPhone.getText().toString(); }
		 */

		public void setViewEnable() {
			taskInstallLocation.setEnabled(false);
			taskTitle.setEnabled(false);
			taskContacts.setEnabled(false);
			taskContactsPhone.setEnabled(false);
			taskPlanStartTime.setEnabled(false);
			taskPlanEndTime.setEnabled(false);
			taskEquipment.setEnabled(false);
			taskProductType.setEnabled(false);
			taskState.setEnabled(false);
			taskType.setEnabled(false);
			taskRecipient.setEnabled(false);
			taskRecipientPhone.setEnabled(false);
		}
	}
}
