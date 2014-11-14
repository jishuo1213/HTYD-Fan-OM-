package com.htyd.fan.om.attendmanage.model;

import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationBean implements Parcelable {

	public String province;
	public String city;
	public String district;
	public String street;
	public String streetNum;
	public String time;

	
	
	public LocationBean() {
	}
	public LocationBean(AddressComponent component) {
		province = component.province;
		city = component.city;
		district = component.district;
		street = component.street;
		streetNum = component.streetNumber;
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
}
