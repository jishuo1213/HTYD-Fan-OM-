package com.htyd.fan.om.attendmanage.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.attendmanage.AttentManagerActivity;
import com.htyd.fan.om.attendmanage.adapter.AttendCalendarGridAdapter;
import com.htyd.fan.om.model.AttendBean;
import com.htyd.fan.om.model.DateBean;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.AttendCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;
import com.htyd.fan.om.util.ui.TextViewWithBorder;
import com.htyd.fan.om.util.ui.UItoolKit;

public class AttendCalendarFragment extends Fragment {

	private static final String MONTHNUM = "monthnum";

	private List<DateBean> monthList;// 日历显示的内容
	private LoaderManager mLoadManager;
	List<AttendBean> attendList;// 签到的list
	private AttendLoaderCallback mCallback;
	private GridView monthGridView;
	private TextView attendTime, attendLocation, attendState;
	private Button signButton;
	public static int currentSelect;// 当前选中的天的位置
	private int firstDayPosition;// 这个月一号的位置
	private boolean isFinish;// 数据是否加载完成

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("fanjishuo____onCreate", "onCreate");
		isFinish = false;
		attendList = new ArrayList<AttendBean>();
		monthList = getMonth(Calendar.getInstance());
		mLoadManager = getActivity().getLoaderManager();
		mCallback = new AttendLoaderCallback();
		Bundle args = new Bundle();
		args.putInt(MONTHNUM, Calendar.getInstance().get(Calendar.MONTH) + 1);
		mLoadManager.initLoader(0, args, mCallback);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("fanjishuo____onCreateView", "onCreateView");
		View v = inflater
				.inflate(R.layout.fragment_main_page, container, false);
		intiView(v);
		return v;
	}

	@Override
	public void onDestroy() {
		Log.i("fanjishuo____onDestroy", "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onResume() {
		Log.i("fanjishuo____onResume", "onResume");
		super.onResume();
	}

	@Override
	public void onStop() {
		Log.i("fanjishuo____onStop", "onStop");
		super.onStop();
	}

	@Override
	public void onPause() {
		Log.i("fanjishuo____onPause", "onPause");
		super.onPause();
	}

	private void intiView(View v) {
		SigninClickListener mListener = new SigninClickListener();
		GridView mGridView = (GridView) v.findViewById(R.id.grid_week_chinese);
		monthGridView = (GridView) v.findViewById(R.id.grid_attend_calendar);
		mGridView.setAdapter(new MainPageGridAdapter());
		monthGridView.setAdapter(new AttendCalendarGridAdapter(monthList,
				getActivity()));
		monthGridView.setOnItemClickListener(new OnDayClickListener());
		TextView month = (TextView) v.findViewById(R.id.tv_month);
		month.setText(Calendar.getInstance().get(Calendar.YEAR) + "年"
				+ (Calendar.getInstance().get(Calendar.MONTH) + 1) + "月");
		attendTime = (TextView) v.findViewById(R.id.tv_attend_time);
		attendLocation = (TextView) v.findViewById(R.id.tv_attend_address);
		attendState = (TextView) v.findViewById(R.id.tv_attend_state);
		signButton = (Button) v.findViewById(R.id.btn_add_attend);
		signButton.setOnClickListener(mListener);
	}

	private class SigninClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_add_attend:
				Intent i = new Intent(getActivity(),AttentManagerActivity.class);
				startActivity(i);
			}
		}
	}

	private class MainPageGridAdapter extends BaseAdapter {
		private String[] itemArray;
		private Resources r;

		public MainPageGridAdapter() {
			r = getActivity().getResources();
			itemArray = r.getStringArray(R.array.week_chinese);
		}

		@Override
		public int getCount() {
			return itemArray.length;
		}

		@Override
		public Object getItem(int position) {
			return itemArray[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.week_item_layout, null);
				mHolder = new ViewHolder(convertView);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			mHolder.setText((String) getItem(position));
			return convertView;
		}
	}

	private class ViewHolder {

		private TextViewWithBorder itemTextView;

		public ViewHolder(View v) {
			itemTextView = (TextViewWithBorder) v
					.findViewById(R.id.tv_item_weekday);
		}

		public void setText(String text) {
			itemTextView.setText(text);
		}
	}

	private List<DateBean> getMonth(Calendar c) {
		List<DateBean> monthList;
		int currentMonth = c.get(Calendar.MONTH);
		int weeks = c.get(Calendar.WEEK_OF_MONTH);
		int currentDate = c.get(Calendar.DATE);
		int currentYear = c.get(Calendar.YEAR);
		c.add(Calendar.DATE, -currentDate);
		firstDayPosition = c.get(Calendar.DAY_OF_WEEK);
		currentSelect = firstDayPosition + currentDate - 1;
		c.add(Calendar.DATE, currentDate);
		Log.i("fanjishuo___getMonth", c.get(Calendar.DATE)+"");
		int week = 0;
		c.add(Calendar.DATE, -(c.get(Calendar.DAY_OF_WEEK) - 1));
		c.add(Calendar.DATE, -(weeks - 1) * 7);
		Log.i("fanjishuo___getMonth", "c.get(Calendar.DATE)"+c.get(Calendar.DATE)+"c.get(Calendar.MONTH)"+c.get(Calendar.MONTH)+"currentMonth"+currentMonth);
		while (c.get(Calendar.MONTH) <= currentMonth && c.get(Calendar.YEAR) == currentYear) {
			c.add(Calendar.DATE, 7);
			week++;
			Log.i("fanjishuo____c.date", c.get(Calendar.DATE)+"");
		}
		c.add(Calendar.DATE, -week * 7);
		int size = week * 7;
		Log.i("fanjishuo___getmonth", size+"");
		Log.i("fanjishuo____getmonth", c.get(Calendar.DATE)+"");
		monthList = new ArrayList<DateBean>(size);
		for (int i = 0; i < size; i++) {
			DateBean mBean = new DateBean();
			mBean.day = c.get(Calendar.DATE);
			if (c.get(Calendar.MONTH) != currentMonth) {
				mBean.state = 0;
			} else {
				mBean.state = 1;
			}
			monthList.add(mBean);
			c.add(Calendar.DATE, 1);
		}
		return monthList;
	}

	private class OnDayClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.i("fanjishuo____onItemClick", currentSelect + "");
			if (isFinish) {
				UItoolKit.showToastShort(getActivity(), "数据还没加载完!");
				return;
			}
			DateBean mBean = (DateBean) parent.getAdapter().getItem(position);
			if (mBean.state != 1) {
				UItoolKit.showToastShort(getActivity(), "看清楚日期在点啊浑淡!");
				return;
			}
			if (mBean.day > Calendar.getInstance().get(Calendar.DATE)) {
				UItoolKit.showToastShort(getActivity(), "穿越中。。。。");
				UItoolKit.showToastShort(getActivity(), "穿越失败。");
				return;
			}

			if (parent.getTag() != null) {
				((View) parent.getTag()).setBackgroundColor(getActivity()
						.getResources().getColor(R.color.activity_bg_color));
			}
			parent.setTag(view);
			view.setBackgroundColor(getActivity().getResources().getColor(
					R.color.orange));
			// setDetailView(attendList.get(mBean.day - 1));
			setDetailView(null);
			currentSelect = position;
		}
	}

	private static class AttendBeanCursorLoader extends SQLiteCursorLoader {

		private OMUserDatabaseManager mManager;
		private int monthNum;

		public AttendBeanCursorLoader(Context context, int monthNum) {
			super(context);
			this.monthNum = monthNum;
			mManager = OMUserDatabaseManager.getInstance(context);
		}

		@Override
		protected Cursor loadCursor() {
			mManager.openDb(0);
			return mManager.queryAttendCursor(monthNum);
		}

		@Override
		protected Cursor loadFromNet() {

			return loadCursor();
		}

	}

	private class AttendLoaderCallback implements LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new AttendBeanCursorLoader(getActivity(),
					args.getInt(MONTHNUM));
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			AttendCursor attendCursor = (AttendCursor) data;
			if (attendCursor != null && attendCursor.moveToFirst()) {
				int i = 0;
				do {
					attendList.add(attendCursor.getAttend());
					setGridView(attendList.get(i), i);
					i++;
				} while (attendCursor.moveToNext());
				isFinish = true;
				Date date = new Date(attendList.get(attendList.size() - 1).time);
				Calendar c = Calendar.getInstance();
				int currentDay = c.get(Calendar.DATE);
				c.setTime(date);
				if (currentDay != c.get(Calendar.DATE)) {
					setDetailView(null);
				} else {
					setDetailView(attendList.get(attendList.size() - 1));
				}
				OMUserDatabaseManager.getInstance(getActivity()).closeDb();
			} else {
				setDetailView(null);
				UItoolKit.showToastShort(getActivity(), "还没有可加载的数据");
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}

	}

	void setGridView(AttendBean attendBean, int position) {
		// v = monthGridView.getChildAt(firstDayPosition + position);
	}

	@SuppressLint("SimpleDateFormat")
	void setDetailView(AttendBean mBean) {
		if (mBean == null) {
			attendTime.setText("你今天还");
			attendLocation.setText("没有");
			attendState.setText("签到");
			return;
		}
		attendTime.setText(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
				.format(mBean.time));
		attendLocation.setText(mBean.getAddress());
		attendState.setText(mBean.addState);
		return;
	}
}
