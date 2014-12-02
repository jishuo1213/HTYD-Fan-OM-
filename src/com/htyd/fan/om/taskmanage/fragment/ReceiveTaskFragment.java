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
import com.htyd.fan.om.util.base.Utils;

public class ReceiveTaskFragment extends Fragment {

	private static final String SELECTTASK = "selecttask";

	private TaskViewPanel mPanel;
	protected TaskDetailBean mBean;


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
		setHasOptionsMenu(true);
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

	protected static class TaskViewPanel {

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
			taskPlanStartTime.setText(Utils.formatTime(mBean.planStartTime));
			taskPlanEndTime.setText(Utils.formatTime(mBean.planEndTime));
			taskEquipment.setText(mBean.equipment);
			taskProductType.setText(mBean.productType);
			taskState.setText(mBean.taskState + "");
			taskType.setText(mBean.taskType + "");
			taskRecipient.setText(mBean.recipientsName);
			taskRecipientPhone.setText(mBean.recipientPhone);
		}

		public void setViewEnable() {
			taskInstallLocation.setFocusable(false);
			taskTitle.setFocusable(false);
			taskContacts.setFocusable(false);
			taskContactsPhone.setFocusable(false);
			taskPlanStartTime.setFocusable(false);
			taskPlanEndTime.setFocusable(false);
			taskEquipment.setFocusable(false);
			taskProductType.setFocusable(false);
			taskState.setFocusable(false);
			taskType.setFocusable(false);
			taskRecipient.setFocusable(false);
			taskRecipientPhone.setFocusable(false);
		}
	}
}
