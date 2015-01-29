package com.htyd.fan.om.util.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.htyd.fan.om.R;

public class InputDialogFragment extends DialogFragment {

	private static final String TITLE = "title";
	private static final String HINT = "hint";
	public static final String INPUTTEXT = "inputtext";
	
	private EditText editText;
	private InputDoneListener listener;
	
	public interface InputDoneListener{
		public void onInputDone(String text);
	}
	
	public static DialogFragment newInstance(String title,String hint){
		DialogFragment fragment = new InputDialogFragment();
		Bundle args = new Bundle();
		args.putString(TITLE, title);
		args.putString(HINT, hint);
		fragment.setArguments(args);
		return fragment;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder = new AlertDialog.Builder(getActivity());
		View v = getActivity().getLayoutInflater().inflate(R.layout.set_net_dialog_layout, null);
		initView(v);
		builder.setView(v);
		builder.setTitle("输入"+getArguments().getString(TITLE));
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendResult();
			}
		});
		builder.setNegativeButton("取消", null);
		return builder.create();
	}

	protected void sendResult() {
		if(getTargetFragment() == null && listener == null){
			return;
		}
		if(listener != null){
			listener.onInputDone(editText.getText().toString());
			return;
		}
		Intent i = new Intent();
		i.putExtra(INPUTTEXT, editText.getText().toString());
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
	}

	private void initView(View v) {
		TextView textView = (TextView) v.findViewById(R.id.tv_dialog_title);
		editText = (EditText) v.findViewById(R.id.edit_ip_address);
		textView.setText(getArguments().getString(TITLE));
		editText.setHint(getArguments().getString(HINT));
	}

	public void setListener(InputDoneListener listener){
		this.listener = listener;
	}
	
}
