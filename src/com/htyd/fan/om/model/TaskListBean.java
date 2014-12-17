package com.htyd.fan.om.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskListBean implements Parcelable {

	public int taskNetId;
	public int taskState;
	public long createTime;
	public String taskTitle;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(taskNetId);
		dest.writeInt(taskState);
		dest.writeLong(createTime);
		dest.writeString(taskTitle);
	}
	
	public Parcelable.Creator<TaskListBean> CREATOR = new Creator<TaskListBean>() {
		
		@Override
		public TaskListBean[] newArray(int size) {
			
			return new TaskListBean [size];
		}
		
		@Override
		public TaskListBean createFromParcel(Parcel source) {
			TaskListBean mBean = new TaskListBean();
			mBean.taskNetId = source.readInt();
			mBean.taskState = source.readInt();
			mBean.createTime = source.readLong();
			mBean.taskTitle = source.readString();
			return mBean;
		}
	};
}
