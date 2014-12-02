package com.htyd.fan.om.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskProcessBean implements Parcelable {

	public int taskid;//任务id
	public int taskState;//本次处理后任务状态
	public long startTime;//本次处理开始时间
	public long endTime;//本次处理结束时间
	public long createTime;//本次处理创建时间
	public String processContent;//处理的内容
	public String processPerson;//处理人
	public String personPhone;//处理人电话

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(taskid);
		dest.writeInt(taskState);
		dest.writeLong(startTime);
		dest.writeLong(endTime);
		dest.writeLong(createTime);
		dest.writeString(processContent);
		dest.writeString(processPerson);
		dest.writeString(personPhone);
	}

	public static Parcelable.Creator<TaskProcessBean> CREATOR = new Creator<TaskProcessBean>() {

		@Override
		public TaskProcessBean[] newArray(int size) {
			return new TaskProcessBean[size];
		}

		@Override
		public TaskProcessBean createFromParcel(Parcel source) {
			TaskProcessBean mBean = new TaskProcessBean();
			mBean.taskid = source.readInt();
			mBean.taskState = source.readInt();
			mBean.startTime = source.readLong();
			mBean.endTime = source.readLong();
			mBean.createTime = source.readLong();
			mBean.processContent = source.readString();
			mBean.processPerson = source.readString();
			mBean.personPhone = source.readString();
			return mBean;
		}
	};

}
