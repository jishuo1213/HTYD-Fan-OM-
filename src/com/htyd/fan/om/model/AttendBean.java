package com.htyd.fan.om.model;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.htyd.fan.om.util.base.Utils;

public class AttendBean implements Parcelable {


	public String province;
	public String city;
	public String district;
	public String street;
	public String streetNum;
	public long time;
	public double latitude;// 纬度
	public double longitude;// 经度
	public int state;// 签到状态，0未签到 1 正常签到 2 补签
	public String choseLocation;
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
		dest.writeInt(month);
		dest.writeString(choseLocation);
		dest.writeInt(state);
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
			mBean.month = source.readInt();
			mBean.choseLocation = source.readString();
			mBean.state = source.readInt();
			return mBean;
		}
	};

	public void SetValueBean(OMLocationBean component) {
		this.province = component.province;
		this.city = component.city;
		this.district = component.district;
		this.street = component.street;
		this.streetNum = component.streetNum;
		this.latitude = component.latitude;
		this.longitude = component.longitude;
		this.time = component.time;
	}

	public String getAddress() {
		StringBuilder sb = new StringBuilder();
		return sb.append(province).append(city).append(district).append(street)
				.append(streetNum).toString();
	}
	
	public JSONObject toJson() throws JSONException{
		JSONObject json = new JSONObject();
		json.put("QDRQ", Utils.formatTime(time,"yyyy-MM-dd"));
		json.put("TXWZ", choseLocation);
		json.put("QDWZ", getAddress());
		json.put("QDJD", longitude);
		json.put("QDWD", latitude);
		json.put("RZ_MKID",12);
		return json;
	}
	
	public void setFromJson(JSONObject json) throws JSONException{
		choseLocation = json.getString("TXWZ");
		time = Utils.parseDate(json.getString("QDRQ"),"yyyy-MM-dd");
		if(Integer.parseInt(json.getString("SFBQ")) == 0){
			state = 1;
		}else{
			state = 2;
		}
		month = Utils.getCalendarField(time, Calendar.MONTH);
	}
}
