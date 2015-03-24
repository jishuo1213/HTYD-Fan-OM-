package com.htyd.fan.om.util.fragment;

import java.lang.reflect.Field;

import com.htyd.fan.om.R;
import com.htyd.fan.om.login.LoginActivity;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.base.ThreadPool;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.netthread.ModifyPasswordThread;
import com.htyd.fan.om.util.ui.UItoolKit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ModifyPassword extends DialogFragment {

	private EditText oldEditPassword,newEditPassword;
	private DialogInterface dialog;
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
		builder.setTitle("修改密码");
		initView(v);
		builder.setView(v);
		builder.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ModifyPassword.this.dialog = dialog;
				isShowDialog(dialog,false);
				if(checkEmpty()){
					UItoolKit.showToastShort(getActivity(), "原密码或新密码不能为空");
					return;
				}
				if(!checkPassword()){
					UItoolKit.showToastShort(getActivity(), "原密码错误");
					return;
				}
				ModifyPasswordThread thread = new ModifyPasswordThread(getHandler(),
						getActivity(), oldEditPassword.getText().toString(),
						newEditPassword.getText().toString());
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

	private Runnable successRunnable = new Runnable() {
		@Override
		public void run() {
			UItoolKit.showToastShort(getActivity(), "密码修改成功，请重新登录");
			isShowDialog(dialog, true);
			dialog.cancel();
			Intent i = new Intent(getActivity(), LoginActivity.class);
			startActivity(i);
			OMUserDatabaseManager.getInstance(getActivity()).logoutDb();
			getActivity().finish();
		}
	};
	
	protected Handler getHandler(){
		return handler;
	}
	
	protected Runnable getSuccessRunnable(){
		return successRunnable;
	}
	
	private Runnable failRunnable = new Runnable() {
		@Override
		public void run() {
			UItoolKit.showToastShort(getActivity(), "修改密码失败，网络异常");
		}
	};
	
	protected Runnable getFailRunnable(){
		return failRunnable;
	}
	
	protected boolean checkPassword() {
		return oldEditPassword.getText().toString().equals(Preferences.getLastLoginPassword(getActivity()));
	}

	protected boolean checkEmpty() {
		return TextUtils.isEmpty(oldEditPassword.getText().toString()) || TextUtils.isEmpty(newEditPassword.getText().toString());
	}
	
	

	private void initView(View v) {
		TextView oldPassword,newPassword;
		oldPassword = (TextView) v.findViewById(R.id.tv_user_name);
		newPassword = (TextView) v.findViewById(R.id.tv_user_phone);
		oldEditPassword = (EditText) v.findViewById(R.id.edit_user_name);
		newEditPassword = (EditText) v.findViewById(R.id.edit_user_phone);
		oldEditPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		newEditPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		oldEditPassword.setHint("输入原密码");
		newEditPassword.setHint("输入新密码");
		oldPassword.setText("原密码");
		newPassword.setText("新密码");
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
