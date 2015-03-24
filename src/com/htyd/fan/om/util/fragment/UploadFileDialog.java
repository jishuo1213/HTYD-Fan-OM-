package com.htyd.fan.om.util.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.map.LocationReceiver;
import com.htyd.fan.om.map.OMLocationManager;
import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.model.OMLocationBean;
import com.htyd.fan.om.taskmanage.adapter.TaskAccessoryAdapter;
import com.htyd.fan.om.taskmanage.adapter.TaskAccessoryAdapter.LoadListener;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskAccessoryCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.db.SQLSentence;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.https.Utility;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;
import com.htyd.fan.om.util.loadfile.DownloadTask;
import com.htyd.fan.om.util.loadfile.DownloadTask.DownLoadFinishListener;
import com.htyd.fan.om.util.loadfile.HttpMultipartPost;
import com.htyd.fan.om.util.loadfile.HttpMultipartPost.UpLoadFinishListener;
import com.htyd.fan.om.util.ui.UItoolKit;

public class UploadFileDialog extends DialogFragment implements
		LoadListener, UpLoadFinishListener, DownLoadFinishListener {

	private static final String FILE = "file";
	private static final String TASKNETID = "taskid";
	private static final String TASKTITLE = "taskTitle";
	private static final String IMGPATH = "imagepath";
	private static final String QUERYVIEW = "queryview";
	private static final String TASKLOCALID = "tasklocalid";
	
	private static final int REQUESTPHOTO = 1;
	private static final int REQUESTRECORDING = 2;
	private static final int REQUESTPHOTODESCRIPTION = 0x03;
	private static final int LOADERID = 0x04;

	protected ArrayList<AffiliatedFileBean> accessoryList;
	protected int state = 0;
	private ListView mListView;
	protected ImageView refreshView;
	private Uri imageFileUri;
	private LoaderManager mLoaderManager;
	private AccessoryLoaderCallback callBacks;
	protected boolean isViewQuery;
	protected double longitude,latitude;
	

	public static DialogFragment newInstance(int taskNetId,String taskTitle, boolean isViewQuery, int taskLocalId) {
		Bundle args = new Bundle();
		args.putInt(TASKNETID, taskNetId);
		args.putString(TASKTITLE, taskTitle);
		args.putBoolean(QUERYVIEW, isViewQuery);
		args.putInt(TASKLOCALID, taskLocalId);
		DialogFragment fragment = new UploadFileDialog();
		fragment.setArguments(args);
		return fragment;
	}
	
	public static DialogFragment newQueryTaskInstance(
			ArrayList<AffiliatedFileBean> accessoryList, int taskNetId,
			String taskTitle, boolean isViewQuery, int taskLocalId){
		Bundle args = new Bundle();
		args.putParcelableArrayList(FILE, accessoryList);
		args.putInt(TASKNETID, taskNetId);
		args.putString(TASKTITLE, taskTitle);
		args.putBoolean(QUERYVIEW, isViewQuery);
		args.putInt(TASKLOCALID, taskLocalId);
		DialogFragment fragment = new UploadFileDialog();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isViewQuery = getArguments().getBoolean(QUERYVIEW);
		if (!isViewQuery) {
			mLoaderManager = getLoaderManager();
			Bundle args = new Bundle();
			args.putInt(TASKNETID, getArguments().getInt(TASKNETID));
			args.putInt(TASKLOCALID, getArguments().getInt(TASKLOCALID));
			callBacks = new AccessoryLoaderCallback();
			mLoaderManager.initLoader(LOADERID, args, callBacks);
			accessoryList = new ArrayList<AffiliatedFileBean>();
		} else {
			accessoryList = getArguments().getParcelableArrayList(FILE);
		}
		if(savedInstanceState != null){
			imageFileUri = (Uri) savedInstanceState.get(IMGPATH); 
		}
	}
	
	@Override
	public void onStart() {
		getActivity().registerReceiver(locationReceiver, new IntentFilter(OMLocationManager.ACTION_LOCATION));
		super.onStart();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(locationReceiver);
		OMLocationManager.get(getActivity()).stopLocationUpdate();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(IMGPATH, imageFileUri);
	}
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(Utils.isNetWorkEnable() && !isViewQuery){
			OMLocationManager.get(getActivity()).startLocationUpdate();
		}
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.accessory_layout, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setTitle("查看附件");
		builder.setPositiveButton("确定", dialogClickListener);
		return builder.create();
	}

	private BroadcastReceiver locationReceiver = new LocationReceiver() {
		
		@Override
		protected void onNetWorkLocationReceived(Context context,
				OMLocationBean loc) {
			longitude = loc.longitude;
			latitude = loc.latitude;
			OMLocationManager.get(getActivity()).stopLocationUpdate();
		}

		@Override
		protected void onNetDisableReceived(Context context) {
			UItoolKit.showToastShort(getActivity(), "网络连接失败");
			OMLocationManager.get(getActivity()).stopLocationUpdate();
		}
	};
	
	private void initView(View v) {
		mListView = (ListView) v.findViewById(R.id.list_accessory);
		refreshView = (ImageView) v.findViewById(R.id.tv_refresh_accessory);
		TextView createAccessory = (TextView) v
				.findViewById(R.id.tv_add_accessory);
		if (isViewQuery) {
			createAccessory.setVisibility(View.GONE);
			refreshView.setVisibility(View.GONE);
		}
		createAccessory.setOnClickListener(accessoryListener);
		refreshView.setOnClickListener(accessoryListener);
		if(isCanSetAdapter()){
			mListView.setAdapter(new TaskAccessoryAdapter(accessoryList, getActivity(), this));
		}
	}

	protected boolean isCanSetAdapter() {
		return accessoryList != null && accessoryList.size() > 0 && mListView.getAdapter() == null;
	}

	private OnClickListener accessoryListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()){
			case R.id.tv_add_accessory:
				Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				imageFileUri = getOutputPictureFileUri();
				i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
				startActivityForResult(i, REQUESTPHOTO);
				break;
			case R.id.tv_refresh_accessory:
				new RefreshAccessoryTask().execute(getArguments().getInt(TASKNETID),getArguments().getInt(TASKLOCALID));
				refreshView.setEnabled(false);
				break;
			}
		}
	};
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater()
				.inflate(R.menu.add_accessory_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_take_photo:
			Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			imageFileUri = getOutputPictureFileUri();
			i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
			startActivityForResult(i, REQUESTPHOTO);
			return true;
		case R.id.menu_select_file:
			return true;
		case R.id.menu_recoring:
			FragmentManager fm = getActivity().getFragmentManager();
			RecodingDialogFragment dialog = new RecodingDialogFragment();
			dialog.setTargetFragment(UploadFileDialog.this, REQUESTRECORDING);
			dialog.show(fm, null);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUESTPHOTO) {
				AffiliatedFileBean mBean  = new AffiliatedFileBean();
				if(accessoryList == null)
					accessoryList = new ArrayList<AffiliatedFileBean>();
				mBean.filePath = imageFileUri.getPath();
				mBean.fileSource = 0;
				mBean.taskId = getArguments().getInt(TASKNETID);
				mBean.fileState = 0;
				mBean.taskLocalId = getArguments().getInt(TASKLOCALID);
				mBean.longitude = longitude;
				mBean.latitude = latitude;
				accessoryList.add(mBean);
				OMUserDatabaseManager.getInstance(getActivity()).openDb(1);
				OMUserDatabaseManager.getInstance(getActivity()).insertTaskAccessoryBean(mBean);
				if (mListView.getAdapter() == null) {
					mListView.setAdapter(new TaskAccessoryAdapter(
							accessoryList, getActivity(), this));
				} else {
					TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter) mListView.getAdapter();
					mAdapter.notifyDataSetChanged();
				}
			} else if (requestCode == REQUESTRECORDING) {
				UItoolKit.showToastShort(getActivity(),data.getStringArrayExtra(RecodingDialogFragment.FILEPATHARRAY)[0]);
			} else if (requestCode == REQUESTPHOTODESCRIPTION) {
				int pos = data.getIntExtra(SetPhotoDescription.POSITION, -1);
				//View v = mListView.getChildAt(pos);
				//TextView descriptionView = (TextView) v.findViewById(R.id.tv_file_name);
				//descriptionView.setText(data.getStringExtra(SetPhotoDescription.DESCRIPTION));
				TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter) mListView.getAdapter();
				AffiliatedFileBean mBean = (AffiliatedFileBean) mAdapter.getItem(pos);
				mBean.fileDescription = data.getStringExtra(SetPhotoDescription.DESCRIPTION);
				mAdapter.notifyDataSetChanged();
				OMUserDatabaseManager.getInstance(getActivity()).updateUploadAccessoryBean(mBean);
			}
		}
	}

	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				break;
			}
		}
	};

	@Override
	public void onUpLoadClick(AffiliatedFileBean mBean,int position) {
		if(mBean.taskId == 0){
			UItoolKit.showToastShort(getActivity(), "任务还未同步至服务器，不能上传附件");
			return;
		}
		if (mBean.fileSource == 0 && mBean.fileState == 0) {
			if(mBean.fileDescription == null || mBean.fileDescription.length() == 0){
				UItoolKit.showToastShort(getActivity(), "请输入文件描述信息");
				DialogFragment dialog = SetPhotoDescription.newInstace(position);
				dialog.setTargetFragment(UploadFileDialog.this, REQUESTPHOTODESCRIPTION);
				dialog.show(getFragmentManager(), null);
				return;
			}
			HttpMultipartPost post = new HttpMultipartPost(getActivity(),
					mBean.filePath, this);
			StringBuilder sb = new StringBuilder();
			sb.append(mBean.latitude).append("|").append(mBean.longitude);
			post.execute(Preferences.getUserinfo(getActivity(), "YHID"),
					Preferences.getUserinfo(getActivity(), "YHMC"),
					mBean.taskId + "", getArguments().getString(TASKTITLE),
					position + "",mBean.fileDescription,sb.toString());
		} else if (mBean.fileSource == 1 && mBean.fileState == 0) {
			String [] array = mBean.filePath.split("\\\\" +Preferences.getUserinfo(getActivity(), "YHID") + "\\\\");
			String sdCardDir = "";
			if (!isViewQuery) {
				sdCardDir = Utils.getAccessoryPath();
			} else {
				sdCardDir = Utils.getCachePath();
			}
			Log.i("fanjishuo_____onUpLoadClick", sdCardDir);
			String targetFile = sdCardDir +File.separator+ array[1];
			new DownloadTask(getActivity(), targetFile,this).execute(mBean);
		} else {
			UItoolKit.showToastShort(getActivity(), "该附件已经上传或下载，请预览");
		}
	}

	@Override
	public void onDeleteClick(AffiliatedFileBean mBean, int position) {
			accessoryList.remove(position);
			TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter) mListView
					.getAdapter();
			mAdapter.notifyDataSetChanged();
			if(mBean.fileSource == 0 && mBean.fileState == 0){
				deleteAccessoryFromDb(mBean);
				return;
			}
			deleteAccessoryFromNet(mBean);
	}
	
	@Override
	public void onPreviewImg(String path) {
		Intent i = new Intent(getActivity(),PhotoViewer.class);
		i.putExtra(PhotoViewer.PICTUREPATH, path);
		startActivity(i);
	}
	
	protected void deleteAccessoryFromDb(AffiliatedFileBean mBean){
		OMUserDatabaseManager.getInstance(getActivity()).detelteTaskAccessory(mBean);
		File file = new File(mBean.filePath);
		if(file.exists()){
			file.delete();
		}
	}
	
	private void deleteAccessoryFromNet(AffiliatedFileBean mBean) {
		new DeleteAccessoryTask().execute(mBean);
	}

	@Override
	public void onUpLoadFinish(int fileId,int position) {
		if(position == -1){
			return;
		}
		accessoryList.get(position).fileState = 1;
		accessoryList.get(position).netId = fileId;
		Log.i("fanjishuo____onUpLoadFinish", accessoryList.get(position).taskLocalId+"");
		TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter)mListView.getAdapter();
		mAdapter.notifyDataSetChanged();
		OMUserDatabaseManager.getInstance(getActivity()).updateUploadAccessoryBean(accessoryList.get(position));
	}
	
	@Override
	public void onDownLoadFinish(AffiliatedFileBean mBean) {
		TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter) mListView
				.getAdapter();
		mAdapter.notifyDataSetChanged();
		if (!isViewQuery) {
			OMUserDatabaseManager.getInstance(getActivity()).updateDownloadAccessoryBean(mBean);
		} /*else {
			OMUserDatabaseManager.getInstance(getActivity()).detelteTaskAccessory(mBean.netId);
		}*/
	}

	private class DeleteAccessoryTask extends AsyncTask<AffiliatedFileBean, Void, Boolean>{

		private AffiliatedFileBean mBean;
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				deleteAccessoryFromDb(mBean);
				UItoolKit.showToastShort(getActivity(), "删除成功");
			} else {
				UItoolKit.showToastShort(getActivity(), "删除失败，请重试");
			}
			this.cancel(false);
		}

		@Override
		protected Boolean doInBackground(AffiliatedFileBean... params) {
			JSONObject param = new JSONObject();
			String result = "";
			mBean = params[0];
			try {
				param.put("WJID", mBean.netId);
				param.put("RZ_MKID", 11);
				result = NetOperating.getResultFromNet(getActivity(), param,
						Urls.FILE, "Operate=deleteWjxxByWjid");
				return new JSONObject(result).getBoolean("RESULT");
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	private Uri getOutputPictureFileUri() {
		String filename = UUID.randomUUID().toString() + ".jpg";
		File file = new File(Utils.getAccessoryPath() + File.separator + filename);
		return Uri.fromFile(file);
	}

	@Override
	public void onSetPhotoDescription(int pos) {
		DialogFragment dialog = SetPhotoDescription.newInstace(pos);
		dialog.setTargetFragment(UploadFileDialog.this, REQUESTPHOTODESCRIPTION);
		dialog.show(getFragmentManager(), null);
	}
	
	private class RefreshAccessoryTask extends AsyncTask<Integer,Void, Boolean>{

		private int taskNetId;
		private int taskLocalId;

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				if(mListView.getAdapter() != null)
					((TaskAccessoryAdapter) mListView.getAdapter()).notifyDataSetChanged();
				UItoolKit.showToastShort(getActivity(), "刷新成功");
			}
			refreshView.setEnabled(true);
			cancel(false);
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			JSONObject param = new JSONObject();
			taskNetId = params[0];
			taskLocalId = params[1];
			String result = "";
			try {
				param.put("JLID", taskNetId);
				result = NetOperating.getResultFromNet(getActivity(), param,
						Urls.FILE, "Operate=getWjdz");
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			refreshAccessoryList(result,taskNetId,accessoryList,taskLocalId);
			return true;
		}
	}
	
	public void refreshAccessoryList(String result,int taskId,ArrayList<AffiliatedFileBean> listAccessory,int taskLocalId) {
		OMUserDatabaseManager mManager = OMUserDatabaseManager.getInstance(getActivity());
		if(result.length() == 0){
			if (listAccessory.size() == 0) {
			} else {
				Iterator<AffiliatedFileBean> it = listAccessory.iterator();
				AffiliatedFileBean mBean;
				while (it.hasNext()) {
					mBean = it.next();
					if (mBean.fileState == 1) {
						Utils.deleteFile(mBean.filePath);
						it.remove();
						mManager.detelteTaskAccessory(mBean);
					}
				}
			}
			return;
		}
		if(result.equals("[]") || result.equals("false")){
			if (listAccessory.size() == 0) {
			} else {
				Iterator<AffiliatedFileBean> it = listAccessory.iterator();
				AffiliatedFileBean mBean;
				while (it.hasNext()) {
					mBean = it.next();
					if (mBean.fileState == 1) {
						Utils.deleteFile(mBean.filePath);
						it.remove();
						mManager.detelteTaskAccessory(mBean);
					}
				}
			}
			return;
		}
		try {
			JSONArray array = (JSONArray) new JSONTokener(
					result).nextValue();
			mManager.clearFeedTable(SQLSentence.TABLE_TASK_ACCESSORY);
			deleteAccessory(array);
			listAccessory.clear();
			for (int i = 0; i < array.length(); i++) {
				AffiliatedFileBean mBean = new AffiliatedFileBean();
				mBean.setFromJson(array.getJSONObject(i), taskId,taskLocalId);
				String [] fileName = mBean.filePath.split("\\\\" +Preferences.getUserinfo(getActivity(), "YHID") + "\\\\");
				if(Utils.isAccessoryFileExist(fileName[1])){
					mBean.fileState = 1;
					mBean.filePath = Urls.ACCESSORYFILEPATH +File.separator+ fileName[1];
				}
				mManager.insertTaskAccessoryBean(mBean);
				listAccessory.add(mBean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void deleteAccessory(JSONArray array) {
		int length  = array.length();
		int [] accessoryId = new int[length];
		for(int i = 0; i < length; i++){
			try {
				accessoryId[i] = array.getJSONObject(i).getInt("WJID");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Arrays.sort(accessoryId);
		Iterator<AffiliatedFileBean> it = accessoryList.iterator();
		AffiliatedFileBean mBean;
		while(it.hasNext()){
			mBean = it.next();
			if(!dichotomyCheck(accessoryId,mBean.netId) && mBean.fileState == 1){
					Utils.deleteFile(mBean.filePath);
			}
		}
	}

	private boolean dichotomyCheck(int[] accessoryId, int netId) {
		int start,end,middle;
		Log.i("fanjishuo____dichotomyCheck", "netId" + netId);
		start = 0;
		end = accessoryId.length - 1;
		while (start <= end) {
			middle = (start + end) / 2;
			if (netId > accessoryId[middle]) {
				start = middle + 1;
			} else if (netId < accessoryId[middle]) {
				end = middle - 1;
			} else {
				return true;
			}
		}
		return false;
	}
	private static class AccessoryLoader extends SQLiteCursorLoader {

		private OMUserDatabaseManager mManager;
		private int taskNetId;
		private int taskLocalId;

		public AccessoryLoader(Context context, int taskNetId,int taskLocalId) {
			super(context);
			mManager = OMUserDatabaseManager.getInstance(context);
			this.taskNetId = taskNetId;
			this.taskLocalId = taskLocalId;
		}

		@Override
		protected Cursor loadCursor() {
				mManager.openDb(0);
				return mManager.queryAccessoryByTaskLocalId(taskLocalId);
		}

		@Override
		protected Cursor loadFromNet() {
			JSONObject param = new JSONObject();
			String result = "";
			try {
				param.put("JLID", taskNetId);
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
				Utility.handleAccessory(mManager, result,taskNetId,taskLocalId);
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
			return new AccessoryLoader(getActivity(),args.getInt(TASKNETID),args.getInt(TASKLOCALID));
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			TaskAccessoryCursor cursor = (TaskAccessoryCursor) data;
			if(cursor != null && cursor.moveToFirst()){
				Log.i("fanjishuo____onLoadFinished", cursor.getCount()+"");
				if (accessoryList == null) {
					accessoryList = new ArrayList<AffiliatedFileBean>();
				} else {
					accessoryList.clear();
				}
				do{
					accessoryList.add(cursor.getAccessory());
				}while(cursor.moveToNext());
				if(mListView != null && mListView.getAdapter() == null){
					mListView.setAdapter(new TaskAccessoryAdapter(accessoryList, getActivity(), UploadFileDialog.this));
				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	}
}
