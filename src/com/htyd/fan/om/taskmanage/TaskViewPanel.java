package com.htyd.fan.om.taskmanage;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.util.base.Utils;

public class TaskViewPanel {

	public TextView taskLocation, taskPlanStartTime, taskPlanEndTime,
			taskAccessory, taskState, taskEquipment, taskEquipmentType,
			taskType, taskInstallLocation, customerUnit, equipmentFactory,
			assetNumber, logicalAddress;
	public EditText taskDescription, taskRecipient, taskRecipientPhone,
			taskContact, contactPhone, taskRemark, equipmentRemark;
	private int taskNetId, taskLocalId;
	private long saveTime;
	private int isSyncToServer;
	private String taskInstallInfo;
	private TaskDetailBean mBean;

	public TaskViewPanel(View v) {
		taskLocation = (TextView) v.findViewById(R.id.tv_task_location);
//		taskAddress = (TextView) v.findViewById(R.id.tv_task_address);
		taskAccessory = (TextView) v.findViewById(R.id.tv_task_accessory);
		taskInstallLocation = (TextView) v
				.findViewById(R.id.edit_task_install_location);
//		taskTitle = (EditText) v.findViewById(R.id.edit_task_title);
		taskDescription = (EditText) v.findViewById(R.id.edit_task_description);
		taskPlanStartTime = (TextView) v
				.findViewById(R.id.edit_task_plan_starttime);
		taskPlanEndTime = (TextView) v
				.findViewById(R.id.edit_task_plan_endtime);
		taskEquipment = (TextView) v.findViewById(R.id.edit_task_equipment);
		taskEquipmentType = (TextView) v
				.findViewById(R.id.edit_task_equipment_type);
		taskState = (TextView) v.findViewById(R.id.edit_task_state);
		taskType = (TextView) v.findViewById(R.id.edit_task_type);
		taskRecipient = (EditText) v.findViewById(R.id.edit_task_recipient);
		taskRecipientPhone = (EditText) v
				.findViewById(R.id.edit_task_recipient_phone);
		
		taskContact = (EditText) v.findViewById(R.id.edit_task_contact);
		contactPhone = (EditText) v.findViewById(R.id.edit_task_contact_phone);
		customerUnit = (TextView) v.findViewById(R.id.tv_customer_unit);
		taskRemark = (EditText) v.findViewById(R.id.edit_task_reamrk);
		equipmentFactory = (TextView) v.findViewById(R.id.tv_task_equipment_factory);
		assetNumber = (TextView) v.findViewById(R.id.tv_asset_number);
		logicalAddress = (TextView) v.findViewById(R.id.tv_task_logical_address);
		equipmentRemark = (EditText) v.findViewById(R.id.edit_equipment_reamrk);
		mBean = new TaskDetailBean();
	}

	public void setTaskShow(TaskDetailBean mBean) {
		taskLocation.setText(mBean.workLocation);
//		taskAddress.setText(mBean.getDetailAddress());
		taskInstallLocation.setText(mBean.installLocation);
//		taskTitle.setText(mBean.taskTitle);
		taskDescription.setText(mBean.taskDescription);
		taskPlanStartTime.setText(Utils.formatTime(mBean.planStartTime));
		taskPlanEndTime.setText(Utils.formatTime(mBean.planEndTime));
		taskEquipment.setText(mBean.equipmentNumber);
		taskEquipmentType.setText(mBean.equipmentType);
		taskState.setText(mBean.taskState + "");
		if (mBean.taskState == 0) {
			taskState.setText("在处理");
		} else {
			taskState.setText("已完成");
		}
		taskType.setText(mBean.taskType);
		taskRecipient.setText(mBean.recipientsName);
		taskRecipientPhone.setText(mBean.recipientPhone);
		taskNetId = mBean.taskNetId;
		saveTime = mBean.saveTime;
		isSyncToServer = mBean.isSyncToServer;
		taskLocalId = mBean.taskLocalId;
		taskInstallInfo = mBean.taskInstallInfo;
		
		taskContact.setText(mBean.taskContact);
		contactPhone.setText(mBean.contactPhone);
		customerUnit.setText(mBean.customerUnit);
		taskRemark.setText(mBean.taskRemark);
		equipmentFactory.setText(mBean.equipmentFactory);
		assetNumber.setText(mBean.assetNumber);
		logicalAddress.setText(mBean.logicalAddress);
		equipmentRemark.setText(mBean.equipmentRemark);
	}

	public void setViewEnable() {
		taskInstallLocation.setFocusable(false);
//		taskTitle.setFocusable(false);
		taskPlanStartTime.setFocusable(false);
		taskPlanEndTime.setFocusable(false);
		taskEquipment.setFocusable(false);
		taskEquipmentType.setFocusable(false);
		taskType.setFocusable(false);
		taskRecipient.setFocusable(false);
		taskRecipientPhone.setFocusable(false);
		taskInstallLocation.setFocusable(false);
//		taskTitle.setFocusable(false);
		taskDescription.setFocusable(false);
		
		taskRemark.setFocusable(false);
		equipmentRemark.setFocusable(false);
	}

	public TaskDetailBean getTaskBean() {
		mBean.taskNetId = taskNetId;
		mBean.workLocation = taskLocation.getText().toString();
		mBean.installLocation = taskInstallLocation.getText().toString();
	//	mBean.taskTitle = taskTitle.getText().toString();
		mBean.taskDescription = taskDescription.getText().toString();
		mBean.planStartTime = Utils.parseDate(taskPlanStartTime.getText()
				.toString(), "yyyy年MM月dd日 HH:mm:ss");
		mBean.planEndTime = Utils.parseDate(taskPlanEndTime.getText()
				.toString(), "yyyy年MM月dd日 HH:mm:ss");
		mBean.equipmentNumber = taskEquipment.getText().toString();
		mBean.equipmentType = taskEquipmentType.getText().toString();
		String stateStr = taskState.getText().toString();
		if (TextUtils.equals(stateStr, "在处理")) {
			mBean.taskState = 0;
		} else {
			mBean.taskState = 2;
		}
		mBean.taskType = taskType.getText().toString();
		mBean.recipientsName = taskRecipient.getText().toString();
		mBean.recipientPhone = taskRecipientPhone.getText().toString();
		mBean.saveTime = this.saveTime;
		mBean.isSyncToServer = isSyncToServer;
		mBean.taskLocalId = taskLocalId;
		mBean.taskInstallInfo = taskInstallInfo;
		
		mBean.taskContact = taskContact.getText().toString();
		mBean.contactPhone = contactPhone.getText().toString();
		mBean.customerUnit = customerUnit.getText().toString();
		mBean.taskRemark = taskRemark.getText().toString();
		mBean.equipmentFactory = equipmentFactory.getText().toString();
		mBean.assetNumber = assetNumber.getText().toString();
		mBean.logicalAddress = logicalAddress.getText().toString();
		mBean.equipmentRemark = equipmentRemark.getText().toString();
		mBean.setTaskTitle();
		return mBean;
	}
}
