package com.htyd.fan.om.taskmanage.fragment;

import com.htyd.fan.om.R;
import com.htyd.fan.om.util.ui.CustomChooserView.OnItemChooserListener;
import com.htyd.fan.om.util.ui.UItoolKit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TaskListFragment extends Fragment implements OnItemChooserListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tasklist_layout, container, false);
		initView(v);
		return v;
	}

	private void initView(View v) {
		
	}

	@Override
	public void onItemChooser(int position) {
		UItoolKit.showToastShort(getActivity(), "position"+position);
	}
}
