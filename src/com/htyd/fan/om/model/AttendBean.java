package com.htyd.fan.om.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;

public class AttendBean implements Parcelable {

	public String province;
	public String city;
	public String district;
	public String street;
	public String streetNum;
	public long time;
	public double latitude;// 纬度
	public double longitude;// 经度
//	public int state;// 签到状态，0正常 1非正常
	public String addState;
	public String addSort;
	public String userName;
	public int month;

	public AttendBean() {
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
		dest.writeLong(time);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeString(addState);
		dest.writeString(addSort);
		dest.writeString(userName);
		dest.writeInt(month);
	}

	public static Parcelable.Creator<AttendBean> CREATOR = new Creator<AttendBean>() {

		@Override
		public AttendBean[] newArray(int size) {
			return new AttendBean[size];
		}

		@Override
		public AttendBean createFromParcel(Parcel source) {
			AttendBean mBean = new AttendBean();
			mBean.province = source.readString();
			mBean.city = source.readString();
			mBean.district = source.readString();
			mBean.street = source.readString();
			mBean.streetNum = source.readString();
			mBean.time = source.readLong();
			mBean.latitude = source.readDouble();
			mBean.longitude = source.readDouble();
			mBean.addState = source.readString();
			mBean.addSort = source.readString();
			mBean.userName = source.readString();
			mBean.month = source.readInt();
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
