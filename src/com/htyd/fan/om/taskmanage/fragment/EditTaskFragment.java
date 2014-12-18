package com.htyd.fan.om.taskmanage.fragment;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.taskmanage.TaskViewPanel;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskAccessoryCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.fragment.DateTimePickerDialog;
import com.htyd.fan.om.util.fragment.SelectLocationDialogFragment;
import com.htyd.fan.om.util.fragment.SpendTimePickerDialog;
import com.htyd.fan.om.util.fragment.UploadFileDialog;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.https.Utility;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;
import com.htyd.fan.om.util.ui.UItoolKit;
import com.htyd.fan.om.util.zxing.CaptureActivity;

public class EditTaskFragment extends Fragment {

	private static final String SELECTTASK = "selecttask";
	private static final String TASKID = "taskid";
	private static final int LOADERID = 0x15;
	
	private static final int REQUESTSTARTTIME = 1;//开始时间
	private static final int REQUESTENDTIME = 2;//结束时间
	private static final int REQUESTLOCATION = 0;//工作地点
	private static final int REQUESTZXING = 0x09;//设备条码

	protected TaskViewPanel mPanel;
	protected TaskDetailBean mBean;
	private long startTime;
	protected ArrayList<AffiliatedFileBean> accessoryList;
	private LoaderManager mLoaderManager;
	private AccessoryLoaderCallback mCallback;
	
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
		mLoaderManager = getActivity().getLoaderManager();
		mBean = (TaskDetailBean) getArguments().get(SELECTTASK);
		Bundle args = new Bundle();
		args.putInt(TASKID, mBean.taskNetId);
		mCallback = new AccessoryLoaderCallback();
		mLoaderManager.initLoader(LOADERID, args, mCallback);
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
		mPanel.taskState.setFocusable(false);
		mPanel.taskLocation.setOnClickListener(dialogClickListener);
		mPanel.taskPlanStartTime.setOnClickListener(dialogClickListener);
		mPanel.taskPlanEndTime.setOnClickListener(dialogClickListener);
		mPanel.taskAccessory.setOnClickListener(dialogClickListener);
		mPanel.taskEquipment.setOnClickListener(dialogClickListener);
		mPanel.taskInstallLocation.addTextChangedListener(installLocationWatcher);
		getActivity().getActionBar().setTitle("编辑任务");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.task_edit_menu, menu);
	}

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

	private TextWatcher installLocationWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			mPanel.taskAddress.setText(mPanel.taskLocation.getText().toString()+s.toString());
		}
	};
	
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
				UploadFileDialog uploadDialog = (UploadFileDialog) UploadFileDialog
						.newInstance(accessoryList, mBean.taskNetId, mBean.taskTitle);
				uploadDialog.show(fm, null);
				break;
			case R.id.tv_task_equipment:
				Intent i = new Intent(getActivity(),CaptureActivity.class);
				startActivityForResult(i, REQUESTZXING);
				break;
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUESTLOCATION) {
				mPanel.taskLocation.setText(data
						.getStringExtra(SelectLocationDialogFragment.LOCATION));
				mPanel.taskAddress.setText(data
						.getStringExtra(SelectLocationDialogFragment.LOCATION)
						+ mPanel.taskInstallLocation.getText().toString());
			} else if (requestCode == REQUESTSTARTTIME) {
				startTime = data
						.getLongExtra(DateTimePickerDialog.EXTRATIME, 0);
				mPanel.taskPlanStartTime.setText(Utils.formatTime(startTime));
			} else if (requestCode == REQUESTENDTIME) {
				long endTime = data.getLongExtra(SpendTimePickerDialog.ENDTIME,
						0);
				mPanel.taskPlanEndTime.setText(Utils.formatTime(endTime));
			} else if (requestCode == REQUESTZXING) {
				mPanel.taskEquipment.setText(data.getStringExtra("result"));
			}
		}
	};

	

	private static class AccessoryLoader extends SQLiteCursorLoader {

		private OMUserDatabaseManager mManager;
		private int taskId;

		public AccessoryLoader(Context context, int taskId) {
			super(context);
			mManager = OMUserDatabaseManager.getInstance(context);
			this.taskId = taskId;
		}

		@Override
		protected Cursor loadCursor() {
			return mManager.queryAccessoryByTaskId(taskId);
		}

		@Override
		protected Cursor loadFromNet() {
			JSONObject param = new JSONObject();
			String result = "";
			try {
				param.put("JLID", taskId);
				result = NetOperating.getResultFromNet(getContext(), param,
						Urls.FILE, "Operate=getWjdz");
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			try {
				Utility.handleAccessory(mManager, result,taskId);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return loadCursor();
		}
	}
	
	private class AccessoryLoaderCallback implements LoaderCallbacks<Cursor>{

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new AccessoryLoader(getActivity(),args.getInt(TASKID));
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			TaskAccessoryCursor cursor = (TaskAccessoryCursor) data;
			if(cursor != null && cursor.moveToFirst()){
				if(accessoryList == null){
					accessoryList = new ArrayList<AffiliatedFileBean>();
				}
				do{
					accessoryList.add(cursor.getAccessory());
				}while(cursor.moveToNext());
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
		
	}
}
