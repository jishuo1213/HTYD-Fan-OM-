package com.htyd.fan.om.taskmanage;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.util.base.Utils;

public class TaskViewPanel {

	public TextView taskLocation, taskAddress, taskPlanStartTime,
			taskPlanEndTime, taskAccessory, taskState,taskEquipment, taskProductType, taskType;
	public EditText taskInstallLocation, taskTitle, taskDescription, taskRecipient,
			taskRecipientPhone;
	private int taskId;
	private long saveTime;

	public TaskViewPanel(View v) {
		taskLocation = (TextView) v.findViewById(R.id.tv_task_location);
		taskAddress = (TextView) v.findViewById(R.id.tv_task_address);
		taskAccessory = (TextView) v.findViewById(R.id.tv_task_accessory);
		taskInstallLocation = (EditText) v
				.findViewById(R.id.edit_task_install_location);
		taskTitle = (EditText) v.findViewById(R.id.edit_task_title);
		taskDescription = (EditText) v.findViewById(R.id.edit_task_description);
		taskPlanStartTime = (TextView) v
				.findViewById(R.id.edit_task_plan_starttime);
		taskPlanEndTime = (TextView) v
				.findViewById(R.id.edit_task_plan_endtime);
		taskEquipment = (TextView) v.findViewById(R.id.edit_task_equipment);
		taskProductType = (TextView) v
				.findViewById(R.id.edit_task_product_type);
		taskState = (TextView) v.findViewById(R.id.edit_task_state);
		taskType = (TextView) v.findViewById(R.id.edit_task_type);
		taskRecipient = (EditText) v.findViewById(R.id.edit_task_recipient);
		taskRecipientPhone = (EditText) v
				.findViewById(R.id.edit_task_recipient_phone);
	}

	public void setTaskShow(TaskDetailBean mBean) {
		taskLocation.setText(mBean.workLocation);
		taskAddress.setText(mBean.getDetailAddress());
		taskInstallLocation.setText(mBean.installLocation);
		taskTitle.setText(mBean.taskTitle);
		taskDescription.setText(mBean.taskDescription);
		taskPlanStartTime.setText(Utils.formatTime(mBean.planStartTime));
		taskPlanEndTime.setText(Utils.formatTime(mBean.planEndTime));
		taskEquipment.setText(mBean.equipment);
		taskProductType.setText(mBean.productType);
		taskState.setText(mBean.taskState + "");
		if (mBean.taskState == 0) {
			taskState.setText("在处理");
		} else {
			taskState.setText("已完成");
		}
		taskType.setText(mBean.taskType);
		taskRecipient.setText(mBean.recipientsName);
		taskRecipientPhone.setText(mBean.recipientPhone);
		taskId = mBean.taskNetId;
		saveTime = mBean.saveTime;
	}

	public void setViewEnable() {
		taskInstallLocation.setFocusable(false);
		taskTitle.setFocusable(false);
		taskPlanStartTime.setFocusable(false);
		taskPlanEndTime.setFocusable(false);
		taskEquipment.setFocusable(false);
		taskProductType.setFocusable(false);
		taskType.setFocusable(false);
		taskRecipient.setFocusable(false);
		taskRecipientPhone.setFocusable(false);
		taskInstallLocation.setFocusable(false);
		taskTitle.setFocusable(false);
		taskDescription.setFocusable(false);
	}

	public TaskDetailBean getTaskBean() {
		TaskDetailBean taskBean = new TaskDetailBean();
		taskBean.taskNetId = taskId;
		taskBean.workLocation = taskLocation.getText().toString();
		taskBean.installLocation = taskInstallLocation.getText().toString();
		taskBean.taskTitle = taskTitle.getText().toString();
		taskBean.taskDescription = taskDescription.getText().toString();
		taskBean.planStartTime = Utils.parseDate(taskPlanStartTime.getText()
				.toString(), "yyyy年MM月dd日 HH:mm:ss");
		taskBean.planEndTime = Utils.parseDate(taskPlanEndTime.getText()
				.toString(), "yyyy年MM月dd日 HH:mm:ss");
		taskBean.equipment = taskEquipment.getText().toString();
		taskBean.productType = taskProductType.getText().toString();
		String stateStr = taskState.getText().toString();
		if (TextUtils.equals(stateStr, "在处理")) {
			taskBean.taskState = 0;
		} else {
			taskBean.taskState = 2;
		}
		taskBean.taskType = taskType.getText().toString();
		taskBean.recipientsName = taskRecipient.getText().toString();
		taskBean.recipientPhone = taskRecipientPhone.getText().toString();
		taskBean.saveTime = this.saveTime;
		return taskBean;
	}
}
