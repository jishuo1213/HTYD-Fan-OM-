package com.htyd.fan.om.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.htyd.fan.om.util.base.Utils;

public class TaskDetailBean implements Parcelable {

	public int taskNetId;// 任务服务器Id
	public int taskLocalId;//任务本地Id
	public int taskState;// 任务状态//0:在处理任务  2:已完成任务
	public int isSyncToServer;//是否同步至服务器 0:未同步 1:已同步
	
	/**--------------------------------- 任务信息--------------------------------------*/
	public String workLocation;//工作地点
	public String taskTitle;// 任务标题
	public String taskDescription;// 任务描述
	public String recipientsName;// 接受人
	public String recipientPhone;// 接受人电话
	public String taskType;// 任务类别
	public long planStartTime;// 计划开始时间
	public long planEndTime;// 计划结束时间
	public long saveTime;// 填写保存时间
	
	public String taskContact;//任务联系人
	public String contactPhone;//联系人电话
	public String customerUnit;//客户单位
	public String taskRemark;//任务备注

	/**-------------------------------- 设备信息----------------------------------------*/
	public String equipmentNumber;// 设备编号
	public String installLocation;// 安装地点
	public String equipmentType;// 设备类型
	
	public String equipmentFactory;//设备厂家
	public String assetNumber;//资产编号
	public String logicalAddress;//逻辑地址
	public String equipmentRemark;//设备备注
	
	public String taskInstallInfo;//任务安装地点信息
	
	public TaskDetailBean() {
	}

	public void setFromJson(JSONObject json) throws JSONException {
		taskNetId = json.getInt("RWID");
		workLocation = json.getString("GZDD");
		installLocation = json.getString("AZDD");
//		taskTitle = json.getString("RWBT");
		taskDescription = json.getString("RWMS");
		recipientsName = json.getString("TXR");
		recipientPhone = json.getString("TXRDH");
		planStartTime = Utils.parseDate(json.getString("JHKSSJ"));
		planEndTime = Utils.parseDate(json.getString("YJJSSJ"));
		saveTime = Utils.parseDate(json.getString("TXSJ"));
		equipmentNumber = json.getString("ZCBH");
		taskType = json.getString("RWGL");
		equipmentType = json.getString("CPLX");
		isSyncToServer = 1;
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
		taskContact = json.getString("LXR");
		contactPhone = json.getString("LXRDH");
		customerUnit = json.getString("KHDW");
		taskRemark = json.getString("RWBZ");
		equipmentFactory = json.getString("SBCJ");
		assetNumber = json.getString("ZCBH");
		logicalAddress = json.getString("LJDZ");
		equipmentRemark = json.getString("ZZBZ");
		taskInstallInfo = json.getString("AZDDDLXX");
		setTaskTitle();
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
		dest.writeString(equipmentNumber);
		dest.writeString(equipmentType);
		dest.writeLong(planStartTime);
		dest.writeLong(planEndTime);
		dest.writeLong(saveTime);
		dest.writeInt(taskState);
		dest.writeString(taskType);
		dest.writeInt(isSyncToServer);
		
		dest.writeString(taskContact);
		dest.writeString(contactPhone);
		dest.writeString(customerUnit);
		dest.writeString(taskRemark);
		dest.writeString(equipmentFactory);
		dest.writeString(assetNumber);
		dest.writeString(logicalAddress);
		dest.writeString(equipmentRemark);
		dest.writeString(taskInstallInfo);
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
			mBean.equipmentNumber = source.readString();
			mBean.equipmentType = source.readString();
			mBean.planStartTime = source.readLong();
			mBean.planEndTime = source.readLong();
			mBean.saveTime = source.readLong();
			mBean.taskState = source.readInt();
			mBean.taskType = source.readString();
			mBean.isSyncToServer = source.readInt();
			
			mBean.taskContact = source.readString();
			mBean.contactPhone = source.readString();
			mBean.customerUnit = source.readString();
			mBean.taskRemark = source.readString();
			mBean.equipmentFactory = source.readString();
			mBean.assetNumber = source.readString();
			mBean.logicalAddress = source.readString();
			mBean.equipmentRemark = source.readString();
			mBean.taskInstallInfo = source.readString();
			return mBean;
		}
	};

	public String getDetailAddress() {
		StringBuilder sb = new StringBuilder();
		return sb.append(workLocation)
				.append(installLocation).toString();
	}
	
	public JSONObject toJson() throws JSONException{
		JSONObject json = new JSONObject();
		json.put("RWID", "");
		json.put("GZDD", workLocation);
		json.put("AZDD", installLocation);
		json.put("XXDZ", getDetailAddress());
		json.put("RWBT", taskTitle);
		json.put("RWMS", taskDescription);
		json.put("LXR", taskContact);
		json.put("LXRDH", contactPhone);
		json.put("JHKSSJ", Utils.formatTime(planStartTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("YJJSSJ", Utils.formatTime(planEndTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("TXR", recipientsName);
		json.put("TXRDH", recipientPhone);
		if (saveTime == 0) {
			json.put("TXSJ", "");
		} else {
			json.put("TXSJ", Utils.formatTime(saveTime, "yyyy-MM-dd HH:mm:ss"));
		}
		json.put("TXFS", "shouji");
		json.put("RWZT", "");
		json.put("SFSC", "");
		json.put("SBCJ", equipmentFactory);
		json.put("CPLX", equipmentType);
		json.put("LJDZ", logicalAddress);
		json.put("CCBH", equipmentNumber);
		json.put("ZCBH", assetNumber);
		json.put("RWGL", taskType);
		json.put("TXID", "1");
		json.put("RZ_MKID", Utils.TASKMODULE);
		json.put("LJID", "");
		json.put("CLLJ", "");
		json.put("HTH", "");
		json.put("HTMC", "");
		json.put("PGR", "");
		json.put("KHDW", customerUnit);
		json.put("RWBZ", taskRemark);
		json.put("ZZBZ", equipmentRemark);
		json.put("PJXX", "");
		json.put("PJR", "");
		json.put("PJRSJ", "");
		json.put("AZDDDLXX", taskInstallInfo);
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
		json.put("LXR", taskContact);
		json.put("LXRDH", contactPhone);
		json.put("JHKSSJ", Utils.formatTime(planStartTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("YJJSSJ", Utils.formatTime(planEndTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("TXR", recipientsName);
		json.put("TXRDH", recipientPhone);
		json.put("TXSJ", Utils.formatTime(saveTime, "yyyy-MM-dd HH:mm:ss"));
		json.put("TXFS", "shouji");
		json.put("SBCJ", equipmentFactory);
		json.put("CPLX", equipmentType);
		json.put("LJDZ", logicalAddress);
		json.put("CCBH", equipmentNumber);
		json.put("ZCBH", assetNumber);
		json.put("RWGL", taskType);
		json.put("ZRR", "");
		json.put("ZRRDH", "");
		json.put("RZ_MKID", Utils.TASKMODULE);
		
		json.put("HTH", "");
		json.put("HTMC", "");
		json.put("PGR", "");
		json.put("KHDW", customerUnit);
		json.put("RWBZ", taskRemark);
		json.put("ZZBZ", equipmentRemark);
		json.put("PJXX", "");
		json.put("PJR", "");
		json.put("PJRSJ", "");
		json.put("AZDDDLXX", taskInstallInfo);
		return json;
	}
	
	public TaskListBean getTaskListBean(){
		TaskListBean mBean = new TaskListBean();
		setTaskListBean(mBean);
		return mBean;
	}
	
	public void setTaskListBean(TaskListBean mBean){
		mBean.createTime = saveTime;
		mBean.isSyncToServer = isSyncToServer;
		mBean.taskNetId = taskNetId;
		mBean.taskState = taskState;
		mBean.taskTitle = taskTitle;
		mBean.taskLocalId = taskLocalId;
	}

	public void setTaskTitle() {
		if(equipmentType.length() > 0 || taskType.length() > 0){
			this.taskTitle = equipmentType + taskType;
			return;   
		}
		if (taskDescription.length() <= 10) {
			this.taskTitle = taskDescription;
		} else {
			this.taskTitle = taskDescription.substring(0, 10);
		}
	}
}
