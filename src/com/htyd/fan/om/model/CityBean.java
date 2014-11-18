package com.htyd.fan.om.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CityBean implements Parcelable {

	public int provinceID;
	public int Id;
	public String cityCode;
	public String cityName;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(provinceID);
		dest.writeInt(Id);
		dest.writeString(cityCode);
		dest.writeString(cityName);
	}

	public static final Parcelable.Creator<CityBean> CREATOR = new Creator<CityBean>() {

		@Override
		public CityBean[] newArray(int size) {
			return new CityBean[size];
		}

		@Override
		public CityBean createFromParcel(Parcel source) {
			CityBean mBean = new CityBean();
			mBean.provinceID = source.readInt();
			mBean.Id = source.readInt();
			mBean.cityCode = source.readString();
			mBean.cityName = source.readString();
			return mBean;
		}
	};
}
