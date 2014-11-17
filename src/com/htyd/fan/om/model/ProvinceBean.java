package com.htyd.fan.om.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProvinceBean implements Parcelable {

	public int id;
	public String provinceCode;
	public String provinceName;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(provinceCode);
		dest.writeString(provinceName);
	}

	public static final Parcelable.Creator<ProvinceBean> CREATOR = new Creator<ProvinceBean>() {

		@Override
		public ProvinceBean[] newArray(int size) {

			return new ProvinceBean[size];
		}

		@Override
		public ProvinceBean createFromParcel(Parcel source) {
			ProvinceBean mBean = new ProvinceBean();
			mBean.id = source.readInt();
			mBean.provinceCode = source.readString();
			mBean.provinceName = source.readString();
			return mBean;
		}
	};
}
