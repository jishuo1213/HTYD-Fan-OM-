package com.htyd.fan.om.login;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.htyd.fan.om.R;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.ui.UItoolKit;

public class SetNetDialog extends DialogFragment {

	protected EditText netEdit;
	private String address;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		address = Preferences.getServerAddress(getActivity());
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
				if (TextUtils.isEmpty(netEdit.getText().toString())) {
					isShowDialog(dialog, true);
					UItoolKit.showToastShort(getActivity(), "服务器地址不能为空");
				} else {
					Preferences.setServerAddress(getActivity(), netEdit
							.getText().toString());
					new Urls(Preferences.getServerAddress(getActivity()));
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
		builder.setTitle("网络设置");
		return builder.create();
	}

	private void initView(View v) {
		netEdit = (EditText) v.findViewById(R.id.edit_ip_address);
		if (address.length() > 0) {
			address = address.substring(7, address.length() - 11);
			netEdit.setText(address);
		}
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
