package com.htyd.fan.om.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.htyd.fan.om.util.base.Utils;

public class TaskProcessBean implements Parcelable {

	public int taskid;//任务id
	public int taskState;//本次处理后任务状态
	public long startTime;//本次处理开始时间
	public long endTime;//本次处理结束时间
	public long createTime;//本次处理创建时间
	public String processContent;//处理的内容

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(taskid);
		dest.writeInt(taskState);
		dest.writeLong(startTime);
		dest.writeLong(endTime);
		dest.writeLong(createTime);
		dest.writeString(processContent);
	}

	public static Parcelable.Creator<TaskProcessBean> CREATOR = new Creator<TaskProcessBean>() {

		@Override
		public TaskProcessBean[] newArray(int size) {
			return new TaskProcessBean[size];
		}

		@Override
		public TaskProcessBean createFromParcel(Parcel source) {
			TaskProcessBean mBean = new TaskProcessBean();
			mBean.taskid = source.readInt();
			mBean.taskState = source.readInt();
			mBean.startTime = source.readLong();
			mBean.endTime = source.readLong();
			mBean.createTime = source.readLong();
			mBean.processContent = source.readString();
			return mBean;
		}
	};

	public void setFromJson(JSONObject json) throws NumberFormatException, JSONException{
		taskid = Integer.parseInt(json.getString("RWID"));
		startTime = Utils.parseDate(json.getString("KSSJ"));
		endTime = Utils.parseDate(json.getString("JSSJ"));
		createTime = Utils.parseDate(json.getString("TXSJ"));
		processContent = json.getString("CLNR");
		if((int) Integer.parseInt(json.getString("WCBZ")) == 1){
			taskState = 0;
			return;
		}
		taskState = Integer.parseInt(json.getString("WCBZ"));
	}
	
	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("RWID", taskid + "");
		json.put("KSSJ", Utils.formatTime(startTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("JSSJ", Utils.formatTime(endTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("CLNR", processContent);
		json.put("WCBZ", taskState==0?1:2);
		json.put("TXSJ", Utils.formatTime(createTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("TXR", "");
		json.put("TXRDH", "");
		json.put("RZ_MKID", Utils.TASKMODULE);
		return json;
	}
}
