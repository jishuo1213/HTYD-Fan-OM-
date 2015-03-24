package com.htyd.fan.om.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DateBean implements Parcelable {

	public int day;
	public int state;// 是否为当前月的天 0:不是 1:是
	public int attendState;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(day);
		dest.writeInt(state);
		dest.writeInt(attendState);
	}

	public static Parcelable.Creator<DateBean> CREATOR = new Creator<DateBean>() {

		@Override
		public DateBean[] newArray(int size) {
			return new DateBean[size];
		}

		@Override
		public DateBean createFromParcel(Parcel source) {
			DateBean mBean = new DateBean();
			mBean.day = source.readInt();
			mBean.state = source.readInt();
			mBean.attendState = source.readInt();
			return mBean;
		}
	};

}
