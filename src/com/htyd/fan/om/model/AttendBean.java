package com.htyd.fan.om.model;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.htyd.fan.om.util.base.Utils;

public class AttendBean implements Parcelable {

	public String address;
	public long time;
	public double latitude;// 纬度
	public double longitude;// 经度
	public int state;// 签到状态，0未签到 1 正常签到 2 补签
	public String choseLocation;
	public int month;                                              
	public int attendId;
	public String attendRemark;//考勤备注
	public int year;
	
	public AttendBean() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(address);
		dest.writeLong(time);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeInt(month);
		dest.writeString(choseLocation);
		dest.writeInt(state);
		dest.writeInt(attendId);
		dest.writeString(attendRemark);
		dest.writeInt(year);
	}

	public static Parcelable.Creator<AttendBean> CREATOR = new Creator<AttendBean>() {

		@Override
		public AttendBean[] newArray(int size) {
			return new AttendBean[size];
		}

		@Override
		public AttendBean createFromParcel(Parcel source) {
			AttendBean mBean = new AttendBean();
			mBean.address = source.readString();
			mBean.time = source.readLong();
			mBean.latitude = source.readDouble();
			mBean.longitude = source.readDouble();
			mBean.month = source.readInt();
			mBean.choseLocation = source.readString();
			mBean.state = source.readInt();
			mBean.attendId = source.readInt();
			mBean.attendRemark = source.readString();
			mBean.year = source.readInt();
			return mBean;
		}
	};

	public void SetValueBean(OMLocationBean component) {
		this.latitude = component.latitude;
		this.longitude = component.longitude;
		this.time = component.time;
		this.address = component.address;
	}

	public String getAddress() {
		return address;
	}
	
	public JSONObject toJson() throws JSONException{
		JSONObject json = new JSONObject();
		json.put("QDRQ", Utils.formatTime(time,"yyyy-MM-dd"));
		json.put("TXWZ", choseLocation);
		json.put("QDWZ", getAddress());
		json.put("QDJD", longitude);
		json.put("QDWD", latitude);
		json.put("RZ_MKID",Utils.ATTENDMODULE);
		json.put("KQBZ", attendRemark);
		return json;
	}
	
	public void setFromJson(JSONObject json) throws JSONException{
		choseLocation = json.getString("TXWZ");
		time = Utils.parseDate(json.getString("QDRQ"),"yyyy-MM-dd");
		if (Integer.parseInt(json.getString("SFBQ")) == 0) {
			state = 1;
		} else {
			state = 2;
		}
		month = Utils.getCalendarField(time, Calendar.MONTH);
		year = Utils.getCalendarField(time, Calendar.YEAR);
		Log.i("fanjishuo_____setFromJson", "year"+year+"month"+month);
		attendRemark = json.getString("KQBZ");
	}
}
