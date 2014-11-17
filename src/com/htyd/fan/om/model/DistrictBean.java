package com.htyd.fan.om.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DistrictBean implements Parcelable {

	public int cityID;
	public String districtCode;
	public String districtName;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(cityID);
		dest.writeString(districtCode);
		dest.writeString(districtName);
	}

	public static final Parcelable.Creator<DistrictBean> CREATOR = new Creator<DistrictBean>() {

		@Override
		public DistrictBean[] newArray(int size) {
			return new DistrictBean[size];
		}

		@Override
		public DistrictBean createFromParcel(Parcel source) {
			DistrictBean mBean = new DistrictBean();
			mBean.cityID = source.readInt();
			mBean.districtCode = source.readString();
			mBean.districtName = source.readString();
			return mBean;
		}
	};

}
