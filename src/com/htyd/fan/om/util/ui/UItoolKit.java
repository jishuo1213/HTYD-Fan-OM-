package com.htyd.fan.om.util.ui;

import android.content.Context;
import android.widget.Toast;

public class UItoolKit {

	public static void showToastShort(Context context, String info) {
		if (context == null || info == null || "".equals(info))
			return;

		Toast mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
		mToast.show();
	}
}
