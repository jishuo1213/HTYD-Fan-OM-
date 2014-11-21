package com.htyd.fan.om.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskDetailBean implements Parcelable {

	public String workProvince;
	public String workCity;
	public String workDistrict;
	public String installLocation;
	public String taskDescription;
	public String taskContacts;
	public String contactsPhone;
	public String recipientsName;
	public String recipientPhone;
	public String taskAccessory;
	public String equipment;
	public String productType;
	public long planStartTime;
	public long planEndTime;
	public int taskState;
	public int taskType;

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
}
