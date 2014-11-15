package com.htyd.fan.om.util.base;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.htyd.fan.om.R;

public class TitlePanel {

	private TextView backTextView, titleTextView;

	public TitlePanel(View v) {
		backTextView = (TextView) v.findViewById(R.id.tv_left_back);
		titleTextView = (TextView) v.findViewById(R.id.tv_title_text);
	}

	public TitlePanel(Activity a) {
		backTextView = (TextView) a.findViewById(R.id.tv_left_back);
		titleTextView = (TextView) a.findViewById(R.id.tv_title_text);
	}

	public void setTitle(String title) {
		titleTextView.setText(title);
	}

	public void setAllListener(OnClickListener listener) {
		backTextView.setOnClickListener(listener);
		titleTextView.setOnClickListener(listener);
	}

	public void setBackListner(OnClickListener listener) {
		backTextView.setOnClickListener(listener);
	}
}
