package com.htyd.fan.om.util.base;

import java.util.List;

import android.content.Context;
import android.view.ActionProvider;
import android.view.View;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.CustomChooser;
import com.htyd.fan.om.util.ui.CustomChooserView;
import com.htyd.fan.om.util.ui.CustomChooserView.OnItemChooserListener;

public abstract class BaseOverflowMenu extends ActionProvider {

	private Context mContext;
	
	public abstract List<CustomChooser> getMenuList();
	public abstract OnItemChooserListener getChoseListener();
	
	public BaseOverflowMenu(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public View onCreateActionView() {
		CustomChooserView customChooserView = new CustomChooserView(mContext);
		customChooserView.setCustomChooserData(getMenuList());
		customChooserView.setOnItemChooserListener(getChoseListener());
		customChooserView.setExpandActivityOverflowButtonResource(getMenuIconId());
		customChooserView.setProvider(this);
		customChooserView
		.setExpandActivityOverflowButtonContentDescription(R.string.more_menu);
		return customChooserView;
	}

	public int getMenuIconId(){
		return R.drawable.actionbar_more_icon;
	}
}
