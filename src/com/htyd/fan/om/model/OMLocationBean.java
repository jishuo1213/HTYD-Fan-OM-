package com.htyd.fan.om.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.location.BDLocation;
import com.htyd.fan.om.util.base.Utils;

public class OMLocationBean implements Parcelable {

	public String province;
	public String city;
	public String district;
	public String street;
	public String streetNum;
	public String address;
	public long time;
	public float direction;
	public double latitude;
	public double longitude;
	public int result;//网络还是GPS

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
		dest.writeFloat(direction);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeInt(result);
		dest.writeString(address);
	}

	public static Parcelable.Creator<OMLocationBean> CREATOR = new Creator<OMLocationBean>() {

		@Override
		public OMLocationBean[] newArray(int size) {
			return new OMLocationBean[size];
		}

		@Override
		public OMLocationBean createFromParcel(Parcel source) {
			OMLocationBean mBean = new OMLocationBean();
			mBean.province = source.readString();
			mBean.city = source.readString();
			mBean.district = source.readString();
			mBean.street = source.readString();
			mBean.streetNum = source.readString();
			mBean.time = source.readLong();
			mBean.direction = source.readFloat();
			mBean.latitude = source.readDouble();
			mBean.longitude = source.readDouble();
			mBean.result = source.readInt();
			mBean.address = source.readString();
			return mBean;
		}
	};
	
	public void setValue(BDLocation loc){
		province  = loc.getProvince();
		city = loc.getCity();
		district = loc.getDistrict();
		street = loc.getStreet();
		streetNum = loc.getStreetNumber();
		direction = loc.getDirection();
		latitude = loc.getLatitude();
		longitude = loc.getLongitude();
		result = loc.getLocType();
		time = Utils.parseDate(loc.getTime());
		address = loc.getAddrStr();
	}
}
