/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htyd.fan.om.util.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.ActionProvider;
import android.view.View;
import android.widget.Toast;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.CustomChooser;
import com.htyd.fan.om.util.ui.CustomChooserView.OnItemChooserListener;

/**
 * 
 * @author guilin
 * 
 */
public class CustomMoreActionProvider extends ActionProvider implements
		OnItemChooserListener {

	/**
	 * Context for accessing resources.
	 */
	private final Context mContext;

	private List<CustomChooser> list;

	/**
	 * Creates a new instance.
	 * 
	 * @param context
	 *            Context for accessing resources.
	 */
	public CustomMoreActionProvider(Context context) {
		super(context);
		mContext = context;

		list = new ArrayList<CustomChooser>();
		CustomChooser customChooser = new CustomChooser();
		customChooser.setIc_resource(R.drawable.ofm_add_icon);
		customChooser.setTitle("我的任务");
		list.add(customChooser);
		customChooser = new CustomChooser();
		customChooser.setIc_resource(R.drawable.ofm_card_icon);
		customChooser.setTitle("领取任务");
		list.add(customChooser);
		customChooser = new CustomChooser();
		customChooser.setIc_resource(R.drawable.ofm_collect_icon);
		customChooser.setTitle("已完成任务");
		list.add(customChooser);
		customChooser = new CustomChooser();
		customChooser.setIc_resource(R.drawable.ofm_delete_icon);
		customChooser.setTitle("设置");
		list.add(customChooser);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateActionView() {
		// Create the view and set its data model.
		CustomChooserView customChooserView = new CustomChooserView(mContext);
		customChooserView.setCustomChooserData(list);
		customChooserView.setOnItemChooserListener(this);
		// Lookup and set the expand action icon.
		customChooserView
				.setExpandActivityOverflowButtonResource(R.drawable.actionbar_more_icon);
		customChooserView.setProvider(this);

		// Set content description.
		customChooserView
				.setExpandActivityOverflowButtonContentDescription(R.string.more_menu);

		return customChooserView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasSubMenu() {
		return true;
	}

	@Override
	public void onItemChooser(int position) {
		Toast.makeText(mContext, "choose item " + position, Toast.LENGTH_SHORT)
				.show();
	}
}
