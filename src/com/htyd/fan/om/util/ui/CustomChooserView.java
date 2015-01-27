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
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.CustomChooser;

/**
 * �Զ���
 * 
 * @author guilin
 * 
 */
public class CustomChooserView extends ViewGroup {

//	private OnItemChooserListener onItemChooserListener;

	/**
	 * An adapter for displaying the activities in an {@link AdapterView}.
	 */
	private final CustomViewAdapter mAdapter;

	/**
	 * Implementation of various interfaces to avoid publishing them in the
	 * APIs.
	 */
	private final Callbacks mCallbacks;

	/**
	 * The content of this view.
	 */
	private final LinearLayout mChooserContent;

	/**
	 * The expand activities action button;
	 */
	private final FrameLayout mExpandActivityOverflowButton;

	/**
	 * The image for the expand activities action button;
	 */
	private final ImageView mExpandActivityOverflowButtonImage;

	/**
	 * The maximal width of the list popup.
	 */
	private final int mListPopupMaxWidth;

	/**
	 * The ActionProvider hosting this view, if applicable.
	 */
	ActionProvider mProvider;

	private final OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
		@Override
		public void onGlobalLayout() {
			if (isShowingPopup()) {
				if (!isShown()) {
					getListPopupWindow().dismiss();
				} else {
					getListPopupWindow().show();
					if (mProvider != null) {
					}
				}
			}
		}
	};

	/**
	 * Popup window for showing the activity overflow list.
	 */
	private ListPopupWindow mListPopupWindow;

	/**
	 * Listener for the dismissal of the popup/alert.
	 */
	private PopupWindow.OnDismissListener mOnDismissListener;

	/**
	 * Flag whether this view is attached to a window.
	 */
	private boolean mIsAttachedToWindow;

	private final Context mContext;

	/**
	 * Create a new instance.
	 * 
	 * @param context
	 *            The application environment.
	 */
	public CustomChooserView(Context context) {
		this(context, null);
	}

	/**
	 * Create a new instance.
	 * 
	 * @param context
	 *            The application environment.
	 * @param attrs
	 *            A collection of attributes.
	 */
	public CustomChooserView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Create a new instance.
	 * 
	 * @param context
	 *            The application environment.
	 * @param attrs
	 *            A collection of attributes.
	 * @param defStyle
	 *            The default style to apply to this view.
	 */
	public CustomChooserView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		LayoutInflater inflater = LayoutInflater.from(mContext);
		inflater.inflate(R.layout.abs_custom_chooser_view, this, true);

		mCallbacks = new Callbacks();

		mChooserContent = (LinearLayout) findViewById(R.id.abs_custom_chooser_view_content);

		mExpandActivityOverflowButton = (FrameLayout) findViewById(R.id.abs_expand_custom_button);
		mExpandActivityOverflowButton.setOnClickListener(mCallbacks);
		mExpandActivityOverflowButtonImage = (ImageView) mExpandActivityOverflowButton
				.findViewById(R.id.abs__image);

		mAdapter = new CustomViewAdapter();
		mAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				updateAppearance();
			}
		});

		Resources resources = context.getResources();
		mListPopupMaxWidth = Math
				.max(resources.getDisplayMetrics().widthPixels / 2,
						resources
								.getDimensionPixelSize(R.dimen.abs__config_prefDialogWidth));
	}

	/**
	 * ��������
	 */
	public void setCustomChooserData(List<CustomChooser> list) {
		mAdapter.setData(list);
		if (isShowingPopup()) {
			dismissPopup();
			showPopup();
		}
	}

	/**
	 * �������
	 * 
	 * @param onItemChooserListener
	 */
	/*public void setOnItemChooserListener(
			OnItemChooserListener onItemChooserListener) {
		this.onItemChooserListener = onItemChooserListener;
	}*/

	/**
	 * Sets the background for the button that expands the activity overflow
	 * list.
	 * 
	 * <strong>Note:</strong> Clients would like to set this drawable as a clue
	 * about the action the chosen activity will perform. For example, if a
	 * share activity is to be chosen the drawable should give a clue that
	 * sharing is to be performed.
	 * 
	 * @param drawable
	 *            The drawable.
	 */
	public void setExpandActivityOverflowButtonResource(int resId) {
		mExpandActivityOverflowButtonImage.setImageResource(resId);
	}

	/**
	 * Sets the content description for the button that expands the activity
	 * overflow list.
	 * 
	 * description as a clue about the action performed by the button. For
	 * example, if a share activity is to be chosen the content description
	 * should be something like "Share with".
	 * 
	 * @param resourceId
	 *            The content description resource id.
	 */
	public void setExpandActivityOverflowButtonContentDescription(int resourceId) {
		CharSequence contentDescription = mContext.getString(resourceId);
		mExpandActivityOverflowButtonImage
				.setContentDescription(contentDescription);
	}

	/**
	 * Set the provider hosting this view, if applicable.
	 * 
	 * @hide Internal use only
	 */
	public void setProvider(ActionProvider provider) {
		mProvider = provider;
	}

	/**
	 * Shows the popup window with activities.
	 * 
	 * @return True if the popup was shown, false if already showing.
	 */
	public boolean showPopup() {
		if (isShowingPopup() || !mIsAttachedToWindow) {
			return false;
		}
		showPopupUnchecked();
		return true;
	}

	/**
	 * Shows the popup no matter if it was already showing.
	 * 
	 * @param maxActivityCount
	 *            The max number of activities to display.
	 */
	private void showPopupUnchecked() {
		if (mAdapter == null) {
			throw new IllegalStateException(
					"No data model. Did you call #setDataModel?");
		}

		getViewTreeObserver()
				.addOnGlobalLayoutListener(mOnGlobalLayoutListener);

		ListPopupWindow popupWindow = getListPopupWindow();
		if (!popupWindow.isShowing()) {
			final int contentWidth = Math.min(mAdapter.measureContentWidth(),
					mListPopupMaxWidth);
			popupWindow.setContentWidth(contentWidth);
			popupWindow.show();
			if (mProvider != null) {
			}
			popupWindow
					.getListView()
					.setContentDescription(
							mContext.getString(R.string.abs__activitychooserview_choose_application));
		}
	}

	/**
	 * Dismisses the popup window with activities.
	 * 
	 * @return True if dismissed, false if already dismissed.
	 */
	@SuppressWarnings("deprecation")
	public boolean dismissPopup() {
		if (isShowingPopup()) {
			getListPopupWindow().dismiss();
			ViewTreeObserver viewTreeObserver = getViewTreeObserver();
			if (viewTreeObserver.isAlive()) {
				viewTreeObserver
						.removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
			}
		}
		return true;
	}

	/**
	 * Gets whether the popup window with activities is shown.
	 * 
	 * @return True if the popup is shown.
	 */
	public boolean isShowingPopup() {
		return getListPopupWindow().isShowing();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mIsAttachedToWindow = true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		ViewTreeObserver viewTreeObserver = getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
			viewTreeObserver
					.removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
		}
		mIsAttachedToWindow = false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		View child = mChooserContent;
		// If the default action is not visible we want to be as tall as the
		// ActionBar so if this widget is used in the latter it will look as
		// a normal action button.
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(
				MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
		measureChild(child, widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(child.getMeasuredWidth(),
				child.getMeasuredHeight());
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		mChooserContent.layout(0, 0, right - left, bottom - top);
		if (getListPopupWindow().isShowing()) {
			showPopupUnchecked();
		} else {
			dismissPopup();
		}
	}

	/**
	 * Sets a listener to receive a callback when the popup is dismissed.
	 * 
	 * @param listener
	 *            The listener to be notified.
	 */
	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		mOnDismissListener = listener;
	}

	/**
	 * Gets the list popup window which is lazily initialized.
	 * 
	 * @return The popup.
	 */
	private ListPopupWindow getListPopupWindow() {
		if (mListPopupWindow == null) {
			mListPopupWindow = new ListPopupWindow(getContext());
			mListPopupWindow.setAdapter(mAdapter);
			mListPopupWindow.setAnchorView(CustomChooserView.this);
			mListPopupWindow.setModal(true);
			mListPopupWindow.setOnItemClickListener(mCallbacks);
			mListPopupWindow.setOnDismissListener(mCallbacks);
		}
		return mListPopupWindow;
	}

	/**
	 * Updates the buttons state.
	 */
	private void updateAppearance() {
		// Expand overflow button.
		if (mAdapter.getCount() > 0) {
			mExpandActivityOverflowButton.setEnabled(true);
		} else {
			mExpandActivityOverflowButton.setEnabled(false);
		}
		// Activity chooser content.
		mChooserContent.setBackgroundResource(0);
		// mChooserContent.setBackgroundDrawable(null);
		mChooserContent.setPadding(0, 0, 0, 0);
	}

	/**
	 * Interface implementation to avoid publishing them in the APIs.
	 */
	private class Callbacks implements AdapterView.OnItemClickListener,
			View.OnClickListener, View.OnLongClickListener,
			PopupWindow.OnDismissListener {

		// AdapterView#OnItemClickListener
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CustomViewAdapter adapter = (CustomViewAdapter) parent.getAdapter();
			final int itemViewType = adapter.getItemViewType(position);
			switch (itemViewType) {
			case CustomViewAdapter.ITEM_VIEW_TYPE_ACTIVITY:
				dismissPopup();
/*				if (onItemChooserListener != null) {
					onItemChooserListener.onItemChooser(position);
				}*/
				break;
			default:
				throw new IllegalArgumentException();
			}
		}

		// View.OnClickListener
		public void onClick(View view) {
			showPopupUnchecked();
		}

		// OnLongClickListener#onLongClick
		@Override
		public boolean onLongClick(View view) {
			return true;
		}

		// PopUpWindow.OnDismissListener#onDismiss
		public void onDismiss() {
			notifyOnDismissListener();
			if (mProvider != null) {
			}
		}

		private void notifyOnDismissListener() {
			if (mOnDismissListener != null) {
				mOnDismissListener.onDismiss();
			}
		}
	}

	/**
	 * Adapter for backing the list of activities shown in the popup.
	 */
	private class CustomViewAdapter extends BaseAdapter {

		private static final int ITEM_VIEW_TYPE_ACTIVITY = 0;
		private static final int ITEM_VIEW_TYPE_COUNT = 1;
		private List<CustomChooser> list = new ArrayList<CustomChooser>();

		public void setData(List<CustomChooser> list) {
			this.list.clear();
			this.list.addAll(list);
			notifyDataSetChanged();
		}

		@Override
		public int getItemViewType(int position) {
			return ITEM_VIEW_TYPE_ACTIVITY;
		}

		@Override
		public int getViewTypeCount() {
			return ITEM_VIEW_TYPE_COUNT;
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final int itemViewType = getItemViewType(position);
			switch (itemViewType) {
			case ITEM_VIEW_TYPE_ACTIVITY:
				if (convertView == null
						|| convertView.getId() != R.id.abs__list_item) {
					convertView = LayoutInflater.from(getContext()).inflate(
							R.layout.abs_custom_chooser_view_list_item, parent,
							false);
				}
				final CustomChooser customChooser = list.get(position);
				// Set the icon
				ImageView iconView = (ImageView) convertView
						.findViewById(R.id.abs__icon);
				iconView.setImageResource(customChooser.getIc_resource());
				// Set the title.
				TextView titleView = (TextView) convertView
						.findViewById(R.id.abs__title);
				titleView.setText(customChooser.getTitle());
				// set the other
				if (!TextUtils.isEmpty(customChooser.getOther())) {
					TextView otherView = (TextView) convertView
							.findViewById(R.id.abs__other);
					otherView.setText(customChooser.getOther());
				}
				return convertView;
			default:
				throw new IllegalArgumentException();
			}
		}

		public int measureContentWidth() {
			// The user may have specified some of the target not to be shown
			// but we
			// want to measure all of them since after expansion they should
			// fit.

			int contentWidth = 0;
			View itemView = null;

			final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
			final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
			final int count = getCount();

			for (int i = 0; i < count; i++) {
				itemView = getView(i, itemView, null);
				itemView.measure(widthMeasureSpec, heightMeasureSpec);
				contentWidth = Math.max(contentWidth,
						itemView.getMeasuredWidth());
			}
			return contentWidth;
		}
	}
}
