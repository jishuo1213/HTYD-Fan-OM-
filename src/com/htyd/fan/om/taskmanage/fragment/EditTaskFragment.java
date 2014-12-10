package com.htyd.fan.om.taskmanage.fragment;

import java.text.ParseException;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.fragment.DateTimePickerDialog;
import com.htyd.fan.om.util.fragment.SelectLocationDialogFragment;
import com.htyd.fan.om.util.fragment.SpendTimePickerDialog;
import com.htyd.fan.om.util.fragment.UploadFileDialog;
import com.htyd.fan.om.util.ui.UItoolKit;

public class EditTaskFragment extends Fragment {

	private static final String SELECTTASK = "selecttask";
/*	private static final int REQUESTPHOTO = 3;//照片
	private static final int REQUESTRECORDING = 4;//录音
*/	
	private static final int REQUESTSTARTTIME = 1;//开始时间
	private static final int REQUESTENDTIME = 2;//结束时间
	private static final int REQUESTLOCATION = 0;//工作地点

	private TaskViewPanel mPanel;
	protected TaskDetailBean mBean;
	private long startTime;

	public static Fragment newInstance(Parcelable mBean) {
		Bundle args = new Bundle();
		args.putParcelable(SELECTTASK, mBean);
		Fragment fragment = new EditTaskFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mBean = (TaskDetailBean) getArguments().get(SELECTTASK);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.task_detail_fragment_layout,
				container, false);
		initView(v);
		return v;
	}

	private void initView(View v) {
		mPanel = new TaskViewPanel(v);
		mPanel.setTaskShow(mBean);
		// mPanel.setViewEnable();
		mPanel.taskLocation.setOnClickListener(dialogClickListener);
		mPanel.taskAddress.setOnClickListener(dialogClickListener);
		mPanel.taskPlanStartTime.setOnClickListener(dialogClickListener);
		mPanel.taskPlanEndTime.setOnClickListener(dialogClickListener);
		mPanel.taskAccessory.setOnClickListener(dialogClickListener);
		getActivity().getActionBar().setTitle("编辑任务");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.task_edit_menu, menu);
	}

/*	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater()
		.inflate(R.menu.add_accessory_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_take_photo:
			Intent i = new Intent(getActivity(), CameraActivity.class);
			startActivityForResult(i, REQUESTPHOTO);
			return true;
		case R.id.menu_select_file:
			return true;
		case R.id.menu_recoring:
			FragmentManager fm = getActivity().getFragmentManager();
			RecodingDialogFragment dialog = new RecodingDialogFragment();
			dialog.setTargetFragment(EditTaskFragment.this, REQUESTRECORDING);
			dialog.show(fm, null);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}*/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_or_receive:
			TaskDetailBean tempBean = null;
			try {
				tempBean = mPanel.getTaskBean();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (tempBean.equals(mBean)) {
				UItoolKit.showToastShort(getActivity(), "保存成功");
				return true;
			}
			/*
			 * 保存至网络逻辑 AsycTask
			 */
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private OnClickListener dialogClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			FragmentManager fm = getActivity().getFragmentManager();
			switch (v.getId()) {
			case R.id.tv_task_location:
				SelectLocationDialogFragment locationDialog = new SelectLocationDialogFragment();
				locationDialog.setTargetFragment(EditTaskFragment.this,
						REQUESTLOCATION);
				locationDialog.show(fm, null);
				break;
			case R.id.edit_task_plan_starttime:
				DateTimePickerDialog dateDialog = (DateTimePickerDialog) DateTimePickerDialog
						.newInstance(true);
				dateDialog.setTargetFragment(EditTaskFragment.this,
						REQUESTSTARTTIME);
				dateDialog.show(fm, null);
				break;
			case R.id.edit_task_plan_endtime:
				if (startTime == 0) {
					UItoolKit.showToastShort(getActivity(), "请先选择开始时间");
					return;
				}
				SpendTimePickerDialog spendDialog = (SpendTimePickerDialog) SpendTimePickerDialog
						.newInstance(startTime);
				spendDialog.setTargetFragment(EditTaskFragment.this,
						REQUESTENDTIME);
				spendDialog.show(fm, null);
				break;
			case R.id.tv_task_accessory:
				UploadFileDialog uploadDialog = (UploadFileDialog) UploadFileDialog.newInstance(null);
				uploadDialog.show(fm, null);
				break;
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUESTLOCATION) {
				mPanel.taskLocation.setText(data
						.getStringExtra(SelectLocationDialogFragment.LOCATION));
			} else if (requestCode == REQUESTSTARTTIME) {
				startTime = data
						.getLongExtra(DateTimePickerDialog.EXTRATIME, 0);
				mPanel.taskPlanStartTime.setText(Utils.formatTime(startTime));
			} else if (requestCode == REQUESTENDTIME) {
				long endTime = data.getLongExtra(SpendTimePickerDialog.ENDTIME,
						0);
				mPanel.taskPlanEndTime.setText(Utils.formatTime(endTime));
			}/*else if (requestCode == REQUESTPHOTO) {
				UItoolKit.showToastShort(getActivity(), data
						.getStringExtra(CameraFragment.EXTRA_PHOTO_FILENAME));
			} else if (requestCode == REQUESTRECORDING) {
				UItoolKit.showToastShort(getActivity(),data.getStringArrayExtra(RecodingDialogFragment.FILEPATHARRAY)[0]);
			}*/
		}
	};

	protected static class TaskViewPanel {

		public TextView taskLocation, taskAddress, taskPlanStartTime,
				taskPlanEndTime, taskAccessory ;
		public EditText taskInstallLocation, taskTitle, taskDescription,
				taskEquipment, taskProductType, taskState, taskType,
				taskRecipient, taskRecipientPhone;
		private int taskId;

		public TaskViewPanel(View v) {
			taskLocation = (TextView) v.findViewById(R.id.tv_task_location);
			taskAddress = (TextView) v.findViewById(R.id.tv_task_address);
			taskAccessory = (TextView) v.findViewById(R.id.tv_task_accessory);
			taskInstallLocation = (EditText) v
					.findViewById(R.id.edit_task_install_location);
			taskTitle = (EditText) v.findViewById(R.id.edit_task_title);
			taskDescription = (EditText) v
					.findViewById(R.id.edit_task_description);
			taskPlanStartTime = (TextView) v
					.findViewById(R.id.edit_task_plan_starttime);
			taskPlanEndTime = (TextView) v
					.findViewById(R.id.edit_task_plan_endtime);
			taskEquipment = (EditText) v.findViewById(R.id.edit_task_equipment);
			taskProductType = (EditText) v
					.findViewById(R.id.edit_task_product_type);
			taskState = (EditText) v.findViewById(R.id.edit_task_state);
			taskType = (EditText) v.findViewById(R.id.edit_task_type);
			taskRecipient = (EditText) v.findViewById(R.id.edit_task_recipient);
			taskRecipientPhone = (EditText) v
					.findViewById(R.id.edit_task_recipient_phone);

		}

		public void setTaskShow(TaskDetailBean mBean) {
			taskLocation.setText(mBean.workLocation);
			taskAddress.setText(mBean.getDetailAddress());
			taskInstallLocation.setText(mBean.installLocation);
			taskTitle.setText(mBean.taskTitle);
			taskDescription.setText(mBean.taskDescription);
			taskPlanStartTime.setText(Utils.formatTime(mBean.planStartTime));
			taskPlanEndTime.setText(Utils.formatTime(mBean.planEndTime));
			taskEquipment.setText(mBean.equipment);
			taskProductType.setText(mBean.productType);
			taskState.setText(mBean.taskState + "");
			taskType.setText(mBean.taskType + "");
			taskRecipient.setText(mBean.recipientsName);
			taskRecipientPhone.setText(mBean.recipientPhone);
			taskId = mBean.taskNetId;
		}

		public void setViewEnable() {
			taskInstallLocation.setFocusable(false);
			taskTitle.setFocusable(false);
			taskPlanStartTime.setFocusable(false);
			taskPlanEndTime.setFocusable(false);
			taskEquipment.setFocusable(false);
			taskProductType.setFocusable(false);
			taskState.setFocusable(false);
			taskType.setFocusable(false);
			taskRecipient.setFocusable(false);
			taskRecipientPhone.setFocusable(false);
		}

		public TaskDetailBean getTaskBean() throws ParseException {
			TaskDetailBean taskBean = new TaskDetailBean();
			taskBean.taskNetId = taskId;
			taskBean.workLocation = taskLocation.getText().toString();
			taskBean.installLocation = taskInstallLocation.getText().toString();
			taskBean.taskTitle = taskTitle.getText().toString();
			taskBean.taskDescription = taskDescription.getText().toString();
			taskBean.planStartTime = Utils.parseDate(taskPlanStartTime
					.getText().toString(), "yyyy年MM月dd日 HH:mm:ss");
			taskBean.planEndTime = Utils.parseDate(taskPlanEndTime.getText()
					.toString(), "yyyy年MM月dd日 HH:mm:ss");
			taskBean.equipment = taskEquipment.getText().toString();
			taskBean.productType = taskProductType.getText().toString();
			taskBean.taskState = Integer.parseInt(taskState.getText()
					.toString());
			taskBean.taskType = Integer.parseInt(taskType.getText().toString());
			taskBean.recipientsName = taskRecipient.getText().toString();
			taskBean.recipientPhone = taskRecipientPhone.getText().toString();
			return taskBean;
		}
	}

}
