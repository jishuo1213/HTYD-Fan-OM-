package com.htyd.fan.om.util.fragment;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.util.ui.UItoolKit;

public class SetPhotoDescription extends DialogFragment {
	
	public static final String DESCRIPTION = "description";
	public static final String POSITION = "pos";
	
	protected EditText photoDescription;
	
	public static DialogFragment newInstace(int pos){
		SetPhotoDescription dialog = new SetPhotoDescription();
		Bundle args = new Bundle();
		args.putInt(POSITION, pos);
		dialog.setArguments(args);
		return dialog;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Builder builder = new AlertDialog.Builder(getActivity());
		View v = getActivity().getLayoutInflater().inflate(R.layout.set_net_dialog_layout, null);
		initView(v);
		builder.setView(v);
		builder.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (TextUtils.isEmpty(photoDescription.getText().toString())) {
					isShowDialog(dialog, true);
					UItoolKit.showToastShort(getActivity(), "文件描述不能为空");
				} else {
					String text = photoDescription.getText().toString();
					sendResult(text);
					isShowDialog(dialog, false);
				}
			}
		});
		
		builder.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				isShowDialog(dialog, false);
			}
		});
		builder.setTitle("文件描述");
		return builder.create();
	}

	protected void sendResult(String text) {
		if(getTargetFragment() == null){
			return;
		}
		Intent i = new Intent();
		i.putExtra(DESCRIPTION, text);
		i.putExtra(POSITION, getArguments().getInt(POSITION));
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
	}

	private void initView(View v) {
		TextView titleText = (TextView) v.findViewById(R.id.tv_dialog_title);
		titleText.setText("文件描述");
		photoDescription = (EditText) v.findViewById(R.id.edit_ip_address);
		photoDescription.setHint("请输入文件描述");
		photoDescription.setInputType(InputType.TYPE_CLASS_TEXT);
	}
	
	protected void isShowDialog(DialogInterface dialog, boolean isshow) {
		try {
			Field field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, !isshow);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	
}
