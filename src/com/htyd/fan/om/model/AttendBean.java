package com.htyd.fan.om.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AttendBean implements Parcelable {

	public double latitude;// 纬度
	public double longitude;// 经度
	public String address;// 签到位置
	public String time;// 签到时间
	public int state;// 签到状态，0正常 1非正常
	public byte canAdd;// true 可以补签 false 不能补签

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeString(address);
		dest.writeString(time);
		dest.writeInt(state);
		dest.writeByte(canAdd);
	}

	public static Parcelable.Creator<AttendBean> CREATOR = new Creator<AttendBean>() {

		@Override
		public AttendBean[] newArray(int size) {
			return new AttendBean[size];
		}

		@Override
		public AttendBean createFromParcel(Parcel source) {
			AttendBean mBean = new AttendBean();
			mBean.latitude = source.readDouble();
			mBean.longitude = source.readDouble();
			mBean.address = source.readString();
			mBean.time = source.readString();
			mBean.state = source.readInt();
			mBean.canAdd = source.readByte();
			return mBean;
		}
	};

}
