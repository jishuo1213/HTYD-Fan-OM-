package com.htyd.fan.om.util.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.CustomChooser;
import com.htyd.fan.om.util.base.BaseOverflowMenu;
import com.htyd.fan.om.util.ui.CustomChooserView.OnItemChooserListener;

public class TaskOvewflowMenu extends BaseOverflowMenu {

	private Context context;

	public TaskOvewflowMenu(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public List<CustomChooser> getMenuList() {
		List<CustomChooser> mList = new ArrayList<CustomChooser>();
		mList = new ArrayList<CustomChooser>();
		CustomChooser customChooser = new CustomChooser();
		customChooser.setIc_resource(R.drawable.ofm_add_icon);
		customChooser.setTitle("在处理任务");
		mList.add(customChooser);
		customChooser = new CustomChooser();
		customChooser.setIc_resource(R.drawable.ofm_card_icon);
		customChooser.setTitle("待领取任务");
		mList.add(customChooser);
		customChooser = new CustomChooser();
		customChooser.setIc_resource(R.drawable.ofm_collect_icon);
		customChooser.setTitle("已完成任务");
		mList.add(customChooser);
		customChooser = new CustomChooser();
		customChooser.setIc_resource(R.drawable.ofm_delete_icon);
		customChooser.setTitle("新建任务");
		mList.add(customChooser);
		return mList;
	}

	@Override
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
	};
}
