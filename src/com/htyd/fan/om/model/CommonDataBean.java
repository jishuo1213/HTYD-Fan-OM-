package com.htyd.fan.om.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class CommonDataBean implements Parcelable {

	public String typeDescription;
	public String typeName;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(typeDescription);
		dest.writeString(typeName);
	}

	public static Parcelable.Creator<CommonDataBean> CREATOR = new Creator<CommonDataBean>() {
		
		@Override
		public CommonDataBean[] newArray(int size) {
			return new CommonDataBean[size];
		}
		
		@Override
		public CommonDataBean createFromParcel(Parcel source) {
			CommonDataBean mBean = new CommonDataBean();
			mBean.typeDescription = source.readString();
			mBean.typeName = source.readString();
			return mBean;
		}
	};
	
	public void setFromJson(JSONObject json) throws JSONException{
		typeDescription = json.getString("SJMS");
		typeName = json.getString("SJNR");
	}
}
