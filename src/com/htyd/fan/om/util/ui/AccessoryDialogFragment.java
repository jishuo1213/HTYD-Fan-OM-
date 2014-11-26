package com.htyd.fan.om.util.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.htyd.fan.om.R;

public class AccessoryDialogFragment extends DialogFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.accessory_dialog_layout, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setTitle("添加附件");
		return  builder.create();
	}

	private void initView(View v) {
		TextView takePhoto,recoding,selectFile;
		takePhoto = (TextView) v.findViewById(R.id.tv_take_photo);
		recoding = (TextView) v.findViewById(R.id.tv_recording);
		selectFile = (TextView) v.findViewById(R.id.tv_select_files);
		takePhoto.setOnClickListener(AccessoryListener);
		recoding.setOnClickListener(AccessoryListener);
		selectFile.setOnClickListener(AccessoryListener);
	}

	private OnClickListener AccessoryListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
			case R.id.tv_take_photo:
				break;
			case R.id.tv_recording:
				break;
			case R.id.tv_select_files:
				break;
			}
		}
	};
}
