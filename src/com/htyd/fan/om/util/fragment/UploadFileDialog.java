package com.htyd.fan.om.util.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.taskmanage.adapter.TaskAccessoryAdapter;
import com.htyd.fan.om.taskmanage.adapter.TaskAccessoryAdapter.UpLoadFileListener;
import com.htyd.fan.om.util.base.PictureUtils;
import com.htyd.fan.om.util.https.HttpMultipartPost;
import com.htyd.fan.om.util.ui.UItoolKit;

public class UploadFileDialog extends DialogFragment implements UpLoadFileListener{

	private static final String FILE = "file";
	private static final int REQUESTPHOTO = 1;
	private static final int REQUESTRECORDING = 2;

	protected ArrayList<AffiliatedFileBean> listAccessory;
	protected int state = 0;
	private ListView mListView;

	public static DialogFragment newInstance(ArrayList<AffiliatedFileBean> list) {
		Bundle args = new Bundle();
		if(list == null){
			list = new ArrayList<AffiliatedFileBean>();
		}
		args.putParcelableArrayList(FILE, list);
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
				Intent i = new Intent(getActivity(), CameraActivity.class);
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
		Log.i("fanjishuo_____onContextItemSelected", "takephoto");
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
				listAccessory.add(mBean);
				if(mListView.getAdapter() == null){
					mListView.setAdapter(new TaskAccessoryAdapter(listAccessory, getActivity(), this));
				}else{
					TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter) mListView.getAdapter();
					mAdapter.notifyDataSetChanged();
				}
				UItoolKit.showToastShort(getActivity(), data
						.getStringExtra(CameraFragment.EXTRA_PHOTO_FILENAME));
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
		for(int i = 0; i<listAccessory.size();i++){
			PictureUtils.cleanImageView((ImageView) mListView.getChildAt(i).findViewById(R.id.img_accessory_file));
		}
		super.onStop();
	}

	@Override
	public void onUpLoadClick(AffiliatedFileBean mBean) {
		HttpMultipartPost post = new HttpMultipartPost(getActivity(), mBean.filePath);
		post.execute();
	}

	@Override
	public void onDeleteClick(AffiliatedFileBean mBean, int position) {
		if (mBean.fileState == 0) {
			listAccessory.remove(position);
			TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter) mListView
					.getAdapter();
			mAdapter.notifyDataSetChanged();
			deleteAccessoryFromDb(mBean);
		} else {
			listAccessory.remove(position);
			TaskAccessoryAdapter mAdapter = (TaskAccessoryAdapter) mListView
					.getAdapter();
			mAdapter.notifyDataSetChanged();
			/**
			 * 从网上删除
			 */
			deleteAccessoryFromDb(mBean);
			deleteAccessoryFromNet(mBean);
		}
	}
	
	private void deleteAccessoryFromDb(AffiliatedFileBean mBean){
		
	}
	
	private void deleteAccessoryFromNet(AffiliatedFileBean mBean){
		
	}
}
