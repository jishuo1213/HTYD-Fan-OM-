package com.htyd.fan.om.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.login.LoginActivity;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.fragment.CommonAddressDialog;

public class SettingFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.setting_layout, container, false);
		initView(v);
		return v;
	}

	private void initView(View v) {
		TextView userName,commonLocation,logOut;
		userName = (TextView) v.findViewById(R.id.tv_user_name);
		commonLocation = (TextView) v.findViewById(R.id.tv_common_address);
		logOut = (TextView) v.findViewById(R.id.tv_log_out);
		userName.setText(Preferences.getUserinfo(getActivity(), "YHMC"));
		commonLocation.setOnClickListener(settingListener);
		logOut.setOnClickListener(settingListener);
	}
	
	private OnClickListener settingListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.tv_common_address:
				CommonAddressDialog dialog = new CommonAddressDialog();
				dialog.show(getActivity().getFragmentManager(), null);
				break;
			case R.id.tv_log_out:
				Intent i = new Intent(getActivity(),LoginActivity.class);
				startActivity(i);
				OMUserDatabaseManager.getInstance(getActivity()).logoutDb();
				getActivity().finish();
				break;
			case R.id.tv_person_info:
				break;
			}
		}
	};
}
