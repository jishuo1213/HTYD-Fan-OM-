package com.htyd.fan.om.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.htyd.fan.om.util.base.Utils;

public class TaskDetailBean implements Parcelable {

	public int taskNetId;// 任务服务器Id
	public int taskLocalId;//任务本地Id
	public String workLocation;//工作地点
	public String installLocation;// 安装地点
	public String taskTitle;// 任务标题
	public String taskDescription;// 任务描述
	public String recipientsName;// 任务领取人
	public String recipientPhone;// 领取人电话
	public String equipment;// 设备
	public String productType;// 产品类别
	public long planStartTime;// 计划开始时间
	public long planEndTime;// 计划结束时间
	public long saveTime;// 填写保存时间
	public int taskState;// 任务状态//0:在处理任务  2:已完成任务
	public int taskType;// 任务类别
	

	public TaskDetailBean() {
	}

	public void setFromJson(JSONObject json) throws JSONException {
		taskNetId = json.getInt("RWID");
		workLocation = json.getString("GZDD");
		installLocation = json.getString("AZDD");
		taskTitle = json.getString("RWBT");
		taskDescription = json.getString("RWMS");
		recipientsName = json.getString("TXR");
		recipientPhone = json.getString("TXRDH");
		planStartTime = Utils.parseDate(json.getString("JHKSSJ"));
		planEndTime = Utils.parseDate(json.getString("YJJSSJ"));
		saveTime = Utils.parseDate(json.getString("TXSJ"));
		switch (Integer.parseInt(json.getString("RWZT"))) {
		case 0:
			taskState = 0;// 在处理
			break;
		case 1:
			taskState = 0;
			break;
		default:
			taskState = 2;// 已完成
			break;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(taskNetId);
		dest.writeInt(taskLocalId);
		dest.writeString(workLocation);
		dest.writeString(installLocation);
		dest.writeString(taskTitle);
		dest.writeString(taskDescription);
		dest.writeString(recipientsName);
		dest.writeString(recipientPhone);
		dest.writeString(equipment);
		dest.writeString(productType);
		dest.writeLong(planStartTime);
		dest.writeLong(planEndTime);
		dest.writeLong(saveTime);
		dest.writeInt(taskState);
		dest.writeInt(taskType);
	}

	public static Parcelable.Creator<TaskDetailBean> CREATOR = new Creator<TaskDetailBean>() {

		@Override
		public TaskDetailBean[] newArray(int size) {

			return new TaskDetailBean[size];
		}

		@Override
		public TaskDetailBean createFromParcel(Parcel source) {
			TaskDetailBean mBean = new TaskDetailBean();
			mBean.taskNetId = source.readInt();
			mBean.taskLocalId = source.readInt();
			mBean.workLocation = source.readString();
			mBean.installLocation = source.readString();
			mBean.taskTitle = source.readString();
			mBean.taskDescription = source.readString();
			mBean.recipientsName = source.readString();
			mBean.recipientPhone = source.readString();
			mBean.equipment = source.readString();
			mBean.productType = source.readString();
			mBean.planStartTime = source.readLong();
			mBean.planEndTime = source.readLong();
			mBean.saveTime = source.readLong();
			mBean.taskState = source.readInt();
			mBean.taskType = source.readInt();
			return mBean;
		}
	};

	public String getDetailAddress() {
		StringBuilder sb = new StringBuilder();
		return sb.append(workLocation)
				.append(installLocation).toString();
	}

	@Override
	public boolean equals(Object o) {
		TaskDetailBean mBean = (TaskDetailBean) o;
		return taskNetId == mBean.taskNetId
				&& taskLocalId == mBean.taskLocalId
				&& getDetailAddress().equals(mBean.getDetailAddress())
				&& taskTitle.equals(mBean.taskTitle)
				&& taskDescription.equals(mBean.taskDescription)
				&& equipment.equals(mBean.equipment)
				&& productType.equals(mBean.productType)
				&& planStartTime == mBean.planStartTime
				&& planEndTime == mBean.planEndTime
				&& saveTime == mBean.saveTime && taskState == mBean.taskState
				&& taskType == mBean.taskType;
	}
	
	public JSONObject toJson() throws JSONException{
		JSONObject json = new JSONObject();
		json.put("RWID", "");
		json.put("GZDD", workLocation);
		json.put("AZDD", installLocation);
		json.put("XXDZ", getDetailAddress());
		json.put("RWBT", taskTitle);
		json.put("RWMS", taskDescription);
		json.put("LXR", "");
		json.put("LXRDH", "");
		json.put("JHKSSJ", Utils.formatTime(planStartTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("YJJSSJ", Utils.formatTime(planEndTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("TXR", recipientsName);
		json.put("TXRDH", recipientPhone);
		json.put("TXSJ", "");
		json.put("TXFS", "shouji");
		json.put("RWZT", "");
		json.put("SFSC", "");
		json.put("SBCJ", "");
		json.put("CPLX", "");
		json.put("LJDZ", "");
		json.put("CCBH", "");
		json.put("ZCBH", "");
		json.put("RWGL", "");
		json.put("TXID", "1");
		json.put("RZ_MKID", 11);
		json.put("LJID", "");
		json.put("CLLJ", "");
		json.put("JLID", "");
		return json;
	}
	
	public JSONObject toEditJson() throws JSONException{
		JSONObject json = new JSONObject();
		json.put("RWID", taskNetId);
		json.put("GZDD", workLocation);
		json.put("AZDD", installLocation);
		json.put("XXDZ", getDetailAddress());
		json.put("RWBT", taskTitle);
		json.put("RWMS", taskDescription);
		json.put("LXR", "");
		json.put("LXRDH", "");
		json.put("JHKSSJ", Utils.formatTime(planStartTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("YJJSSJ", Utils.formatTime(planEndTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("TXR", recipientsName);
		json.put("TXRDH", recipientPhone);
		json.put("TXSJ", Utils.formatTime(saveTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("TXFS", "shouji");
		json.put("SBCJ", "");
		json.put("CPLX", "");
		json.put("LJDZ", "");
		json.put("CCBH", "");
		json.put("ZCBH", "");
		json.put("RWGL", "");
		json.put("ZRR", "");
		json.put("ZRRDH", "");
		json.put("RZ_MKID", 11);
		return json;
	}
}
