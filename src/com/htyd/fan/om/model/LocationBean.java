package com.htyd.fan.om.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;

public class LocationBean implements Parcelable {

	public String province;
	public String city;
	public String district;
	public String street;
	public String streetNum;
	public String time;

	public LocationBean() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(province);
		dest.writeString(city);
		dest.writeString(district);
		dest.writeString(street);
		dest.writeString(streetNum);
		dest.writeString(time);
	}

	public static Parcelable.Creator<LocationBean> CREATOR = new Creator<LocationBean>() {

		@Override
		public LocationBean[] newArray(int size) {
			return new LocationBean[size];
		}

		@Override
		public LocationBean createFromParcel(Parcel source) {
			LocationBean mBean = new LocationBean();
			mBean.province = source.readString();
			mBean.city = source.readString();
			mBean.district = source.readString();
			mBean.street = source.readString();
			mBean.streetNum = source.readString();
			mBean.time = source.readString();
			return mBean;
		}
	};

	public void SetValueBean(AddressComponent component) {
		this.province = component.province;
		this.city = component.city;
		this.district = component.district;
		this.street = component.street;
		this.streetNum = component.streetNumber;
	}

	public String getAddress() {
		StringBuilder sb = new StringBuilder();
		return sb.append(province).append(city).append(district).append(street)
				.append(streetNum).toString();
	}
}
