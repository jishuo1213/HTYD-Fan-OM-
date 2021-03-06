package com.htyd.fan.om.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AffiliatedFileBean implements Parcelable {

	public String filePath;//文件路径
	public int fileState;//文件状态 1：已上传 或 已下载 0：未上传 或 未下载
	public int fileSource;//文件来源//0：本地创建的附件 1：从服务器获取的附件 
	public int taskId;//任务网络id
	public int netId;//服务器的附件ID
	public long fileSize;//文件大小
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(filePath);
		dest.writeInt(fileState);
		dest.writeInt(fileSource);
		dest.writeInt(taskId);
		dest.writeInt(netId);
		dest.writeLong(fileSize);
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
			mBean.fileSource = source.readInt();
			mBean.taskId = source.readInt();
			mBean.netId = source.readInt();
			mBean.fileSize = source.readLong();
			return mBean;
		}
	};
	
	public void setFromJson(JSONObject json,int taskId) throws JSONException{
		filePath = json.getString("WJDZ");
		fileSource = 1;
		fileState = 0;
		this.taskId = taskId;
		netId = Integer.parseInt(json.getString("WJID"));
		fileSize = Long.parseLong(json.getString("WJDX"));
		Log.i("fanjishuo____setFromJson", fileSize+"");
	}
}
