package com.htyd.fan.om.util.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.CustomChooser;
import com.htyd.fan.om.util.base.BaseOverflowMenu;

public class AttendOverflowMenu extends BaseOverflowMenu {

	//private Context context;

	public AttendOverflowMenu(Context context) {
		super(context);
	//	this.context = context;
	}

	@Override
	public List<CustomChooser> getMenuList() {
		List<CustomChooser> list = new ArrayList<CustomChooser>();
		CustomChooser customChooser = new CustomChooser();
		customChooser.setIc_resource(R.drawable.ofm_add_icon);
		customChooser.setTitle("考勤统计");
		list.add(customChooser);
		customChooser = new CustomChooser();
		customChooser.setIc_resource(R.drawable.ofm_card_icon);
		customChooser.setTitle("签到");
		list.add(customChooser);
		return list;
	}

/*	@Override
	public OnItemChooserListener getChoseListener() {
		return mListener;
	}

	public void setListener(OnItemChooserListener listener) {
		this.mListener = listener;
	}

	private OnItemChooserListener mListener = new OnItemChooserListener() {

		@Override
		public void onItemChooser(int position) {
			UItoolKit.showToastShort(context, position + "");
		}
	};*/
}
