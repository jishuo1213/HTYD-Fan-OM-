package com.htyd.fan.om.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AffiliatedFileBean implements Parcelable {

	public String filePath;//文件路径
	public int fileState;//文件状态 1：已上传 0：未上传
	public int fileType;//文件类型
	public int taskId;//任务id
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(filePath);
		dest.writeInt(fileState);
		dest.writeInt(fileType);
		dest.writeInt(taskId);
	}
	
	public static Parcelable.Creator<AffiliatedFileBean> CREATOR = new Creator<AffiliatedFileBean>() {
		
		@Override
		public AffiliatedFileBean[] newArray(int size) {
			return new AffiliatedFileBean[size];
		}
		
		@Override
		public AffiliatedFileBean createFromParcel(Parcel source) {
			AffiliatedFileBean mBean = new AffiliatedFileBean();
			mBean.filePath = source.readString();
			mBean.fileState = source.readInt();
			mBean.fileType = source.readInt();
			mBean.taskId = source.readInt();
			return mBean;
		}
	};

}
