package com.htyd.fan.om.service;

import com.htyd.fan.om.util.base.NetWorkUtils;
import com.htyd.fan.om.util.base.Preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Preferences.netType = NetWorkUtils.getNetWorkType(context);
	}
}
