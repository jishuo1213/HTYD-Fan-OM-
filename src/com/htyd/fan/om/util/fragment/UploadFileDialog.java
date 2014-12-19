package com.htyd.fan.om.util.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.taskmanage.adapter.TaskAccessoryAdapter;
import com.htyd.fan.om.taskmanage.adapter.TaskAccessoryAdapter.UpLoadFileListener;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.loadfile.DownloadTask;
import com.htyd.fan.om.util.loadfile.DownloadTask.DownLoadFinishListener;
import com.htyd.fan.om.util.loadfile.HttpMultipartPost;
import com.htyd.fan.om.util.loadfile.HttpMultipartPost.UpLoadFinishListener;
import com.htyd.fan.om.util.ui.UItoolKit;

public class UploadFileDialog extends DialogFragment implements
		UpLoadFileListener, UpLoadFinishListener, DownLoadFinishListener {

	private static final String FILE = "file";
	private static final String TASKID = "taskid";
	private static final String TASKTITLE = "taskTitle";
	private static final String IMGPATH = "imagepath";
	
	private static final int REQUESTPHOTO = 1;
	private static final int REQUESTRECORDING = 2;

	protected ArrayList<AffiliatedFileBean> listAccessory;
	protected int state = 0;
	private ListView mListView;
	private Uri imageFileUri;

	public static DialogFragment newInstance(ArrayList<AffiliatedFileBean> list,int taskNetId,String taskTitle) {
		Bundle args = new Bundle();
		if(list == null){
			list = new ArrayList<AffiliatedFileBean>();
		}
		args.putParcelableArrayList(FILE, list);
		args.putInt(TASKID, taskNetId);
		args.putString(TASKTITLE, taskTitle);
		DialogFragment fragment = new UploadFileDialog();
		fragment.setArguments(args);
		return fragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listAccessory = (ArrayList<AffiliatedFileBean>) getArguments()
				.get(FILE);
		if(savedInstanceState != null){
			imageFileUri = (Uri) savedInstanceState.get(IMGPATH); 
		}
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
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.accessory_layout, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setTitle("查看附件");
		builder.setPositiveButton("确定", dialogClickListener);
		return builder.create();
	}

	private void initView(View v) {
		mListView = (ListView) v.findViewById(R.id.list_accessory);
		TextView createAccessory = (TextView) v
				.findViewById(R.id.tv_add_accessory);
		createAccessory.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				imageFileUri = getOutputPictureFileUri();
				Log.i("fanjishuo_____imageFileUri", (imageFileUri == null)+"");
				i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
				startActivityForResult(i, REQUESTPHOTO);
			}
		});
		if(listAccessory.size() > 0){
			mListView.setAdapter(new TaskAccessoryAdapter(listAccessory, getActivity(), this));
		}
	}

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
				if(listAccessory == null)
					listAccessory = new ArrayList<AffiliatedFileBean>();
				mBean.filePath = imageFileUri.getPath();
				mBean.fileSource = 0;
				mBean.taskId = getArguments().getInt(TASKID);
				mBean.fileState = 0;
				listAccessory.add(mBean);
				OMUserDatabaseManager.getInstance(getActivity()).openDb(1);
				OMUserDatabaseManager.getInstance(getActivity()).insertTaskAccessoryBean(mBean);
				if(mListView.getAdapter() == null){
					mListView.setAdapter(new TaskAccessoryAdapter(listAccessory, getActivity(), this));
				}else{
					TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter) mListView.getAdapter();
					mAdapter.notifyDataSetChanged();
				}
			} else if (requestCode == REQUESTRECORDING) {
				UItoolKit.showToastShort(getActivity(),data.getStringArrayExtra(RecodingDialogFragment.FILEPATHARRAY)[0]);
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
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onUpLoadClick(AffiliatedFileBean mBean,int position) {
		if (mBean.fileSource == 0 && mBean.fileState == 0) {
			HttpMultipartPost post = new HttpMultipartPost(getActivity(),
					mBean.filePath, this);
			post.execute(Preferences.getUserinfo(getActivity(), "YHID"),
					Preferences.getUserinfo(getActivity(), "YHMC"),
					mBean.taskId + "", getArguments().getString(TASKTITLE),
					position + "");
			Log.i("fanjishuo_____onUpLoadClick", Preferences.getUserinfo(getActivity(), "YHMC"));
		} else if (mBean.fileSource == 1 && mBean.fileState == 0) {
			String [] array = mBean.filePath.split("\\\\" +Preferences.getUserinfo(getActivity(), "YHID") + "\\\\");
			String sdCardDir = Utils.getAccessoryPath();
			String targetFile = sdCardDir +"/"+array[1];
			new DownloadTask(getActivity(), targetFile,this).execute(mBean);
		} else {
			UItoolKit.showToastShort(getActivity(), "该附件已经上传或下载，请预览");
		}
	}

	@Override
	public void onDeleteClick(AffiliatedFileBean mBean, int position) {
			listAccessory.remove(position);
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
		FragmentManager fm = getActivity().getFragmentManager();
		ImageFragment fragment = ImageFragment.newInstance(path);
		fragment.show(fm, null);
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
	public void onUpLoadFinish(AffiliatedFileBean mBean,int position) {
		if(position == -1){
			return;
		}
		listAccessory.set(position, mBean);
		TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter)mListView.getAdapter();
		mAdapter.notifyDataSetChanged();
		OMUserDatabaseManager.getInstance(getActivity()).updateAccessoryBean(mBean);
	}
	
	@Override
	public void onDownLoadFinish(AffiliatedFileBean mBean) {
		TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter)mListView.getAdapter();
		mAdapter.notifyDataSetChanged();
		OMUserDatabaseManager.getInstance(getActivity()).updateTaskAccessoryBean(mBean);
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
				result = NetOperating.getResultFromNet(getActivity(), param,
						Urls.FILE, "Operate=deleteWjxxByWjid");
				return new JSONObject(result).getBoolean("RESULT");
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	private  Uri getOutputPictureFileUri() {
		String  filename = UUID.randomUUID().toString() + ".jpg";
		File file = new File(Utils.getAccessoryPath()+File.separator+filename);
		Log.i("fanjishuo____getOutputPictureFileUri", file.getAbsolutePath());
		return Uri.fromFile(file);
	}
}
