package com.htyd.fan.om.util.fragment;

import java.lang.reflect.Field;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import com.htyd.fan.om.R;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.base.ThreadPool;
import com.htyd.fan.om.util.netthread.ModifyPersonalInfoThread;
import com.htyd.fan.om.util.ui.UItoolKit;

public class ModifyPersonalInfo extends DialogFragment {

	private EditText name,phone;
	private Handler handler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
	}
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder = new AlertDialog.Builder(getActivity());
		View v = getActivity().getLayoutInflater().inflate(R.layout.modify_personal_info_layout, null);
		builder.setTitle("修改个人资料");
		builder.setView(v);
		initView(v);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				isShowDialog(dialog, false);
				if (checkIsChanged()) {
					isShowDialog(dialog, true);
					dialog.cancel();
					return;
				}
				ModifyPersonalInfoThread thread = new ModifyPersonalInfoThread(
						getHandler(), getUserName(), getUserPhone(),
						getActivity());
				thread.setRunnable(getSuccessRunnable(), getFailRunnable());
				ThreadPool.runMethod(thread);
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				isShowDialog(dialog, true);
				dialog.cancel();
			}
		});
		return builder.create();
	}

	protected boolean checkIsChanged() {
		return name.getText().toString()
				.equals(Preferences.getUserinfo(getActivity(), "YHMC"))
				&& phone.getText().toString()
				.equals(Preferences.getUserinfo(getActivity(), "SHOUJ"));
	}

	protected String getUserPhone() {
		return phone.getText().toString();
	}

	protected String getUserName() {
		return name.getText().toString();
	}

	protected Handler getHandler() {
		return handler;
	}

	private void initView(View v) {
		name = (EditText) v.findViewById(R.id.edit_user_name);
		phone = (EditText) v.findViewById(R.id.edit_user_phone);
		name.setText(Preferences.getUserinfo(getActivity(), "YHMC"));
		phone.setText(Preferences.getUserinfo(getActivity(), "SHOUJ"));
	}
	
	private Runnable successRunnable = new Runnable() {
		
		@Override
		public void run() {
			UItoolKit.showToastShort(getActivity(), "修改个人资料成功");
			updateUserInfo();
			isShowDialog(getDialog(), true);
			getDialog().cancel();
		}
	};
	
	public Runnable getSuccessRunnable() {
		return successRunnable;
	}
	
	protected void updateUserInfo() {
		String userInfo = Preferences.getUserinfo(getActivity());
		try {
			JSONObject json = new JSONObject(userInfo);
			json.put("YHMC", name.getText().toString());
			json.put("SHOUJ", phone.getText().toString());
			Preferences.setUserInfo(getActivity(), json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private Runnable failRunnable = new Runnable() {
		
		@Override
		public void run() {
			UItoolKit.showToastShort(getActivity(), "修改个人资料失败，网络异常");
		}
	};
	
	public Runnable getFailRunnable() {
		return failRunnable;
	}
	
	
	
	protected void isShowDialog(DialogInterface dialog, boolean isshow) {
		try {
			Field field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, isshow);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
}
