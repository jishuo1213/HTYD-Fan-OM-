package com.htyd.fan.om.model;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public class TaskDetailBean implements Parcelable {

	public String workProvince;// 任务所在省份
	public String workCity;// 任务所在市
	public String workDistrict;// 任务所在县区
	public String installLocation;// 安装地点
	public String taskTitle;//任务标题
	public String taskDescription;// 任务描述
	public String taskContacts;// 任务联系人
	public String contactsPhone;// 联系人手机
	public String recipientsName;// 任务领取人
	public String recipientPhone;// 领取人电话
	public String taskAccessory;// 任务附件
	public String equipment;// 设备
	public String productType;// 产品类别
	public long planStartTime;// 计划开始时间
	public long planEndTime;// 计划结束时间
	public int taskState;// 任务状态//0:在处理任务 1:待领取任务 2:已完成任务
	public int taskType;// 任务类别

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(workProvince);
		dest.writeString(workCity);
		dest.writeString(workDistrict);
		dest.writeString(installLocation);
		dest.writeString(taskTitle);
		dest.writeString(taskDescription);
		dest.writeString(taskContacts);
		dest.writeString(contactsPhone);
		dest.writeString(recipientsName);
		dest.writeString(recipientPhone);
		dest.writeString(taskAccessory);
		dest.writeString(equipment);
		dest.writeString(productType);
		dest.writeLong(planStartTime);
		dest.writeLong(planEndTime);
		dest.writeInt(taskState);
		dest.writeInt(taskType);
	}

	public static Parcelable.Creator<TaskDetailBean> CREATOR = new Creator<TaskDetailBean>() {

		@Override
		public TaskDetailBean[] newArray(int size) {

			return new TaskDetailBean[size];
		}

		@Override
		public TaskDetailBean createFromParcel(Parcel source) {
			TaskDetailBean mBean = new TaskDetailBean();
			mBean.workProvince = source.readString();
			mBean.workCity = source.readString();
			mBean.workDistrict = source.readString();
			mBean.installLocation = source.readString();
			mBean.taskTitle = source.readString();
			mBean.taskDescription = source.readString();
			mBean.taskContacts = source.readString();
			mBean.contactsPhone = source.readString();
			mBean.recipientsName = source.readString();
			mBean.recipientPhone = source.readString();
			mBean.taskAccessory = source.readString();
			mBean.equipment = source.readString();
			mBean.productType = source.readString();
			mBean.planStartTime = source.readLong();
			mBean.planEndTime = source.readLong();
			mBean.taskState = source.readInt();
			mBean.taskType = source.readInt();
			return mBean;
		}
	};

	public String getDetailAddress() {
		StringBuilder sb = new StringBuilder();
		return sb.append(workProvince).append(workCity).append(workDistrict)
				.append(installLocation).toString();
	}

	@SuppressLint("SimpleDateFormat")
	public String getTime() {
		return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
				.format(this.planStartTime);
	}
}
