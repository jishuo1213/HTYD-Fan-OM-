package com.htyd.fan.om.util.fragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.htyd.fan.om.R;

public class DateTimePickerDialog extends DialogFragment {

	public static final String EXTRATIME = "extratime";
	private static final String LIMITSTARTTIME = "limitstarttime";

	protected Calendar mDate;
	protected long currentTime;
	private TimeAdapter yearAdapter, monthAdapter, dayAdapter, hourAdapter,
			minuteAdapter;
	protected int lastyearnum, lastmonthnum, lastdaynum, lasthournum, lastminutenum;
	private Spinner year, month, day, hour, minute;

	
	public static DialogFragment newInstance(boolean limitStartTime){
		Bundle bundle = new Bundle();
		bundle.putBoolean(LIMITSTARTTIME, limitStartTime);
		DialogFragment fragment = new DateTimePickerDialog();
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.date_picker_layout, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setTitle("请选择开始时间");
		builder.setPositiveButton("确定", dialogClickListener);
		builder.setNegativeButton("取消", dialogClickListener);
		return builder.create();
	}

	private void initView(View v) {

		TextView today, tomorrow, afterTomorrow, am, pm;
		
		today = (TextView) v.findViewById(R.id.tv_today);
		tomorrow = (TextView) v.findViewById(R.id.tv_tomorrow);
		afterTomorrow = (TextView) v.findViewById(R.id.tv_after_tomorrow);
		am = (TextView) v.findViewById(R.id.tv_am);
		pm = (TextView) v.findViewById(R.id.tv_pm);

		today.setOnClickListener(selectDateListener);
		tomorrow.setOnClickListener(selectDateListener);
		afterTomorrow.setOnClickListener(selectDateListener);
		am.setOnClickListener(selectDateListener);
		pm.setOnClickListener(selectDateListener);

		year = (Spinner) v.findViewById(R.id.spinner_year);
		month = (Spinner) v.findViewById(R.id.spinner_month);
		day = (Spinner) v.findViewById(R.id.spinner_day);
		hour = (Spinner) v.findViewById(R.id.spinner_hour);
		minute = (Spinner) v.findViewById(R.id.spinner_minute);
		year.setAdapter(yearAdapter);
		month.setAdapter(monthAdapter);
		day.setAdapter(dayAdapter);
		hour.setAdapter(hourAdapter);
		minute.setAdapter(minuteAdapter);
		setSpinner(mDate, 1);
		year.setOnItemSelectedListener(timeSeclectListener);
		month.setOnItemSelectedListener(timeSeclectListener);
		day.setOnItemSelectedListener(timeSeclectListener);
		hour.setOnItemSelectedListener(timeSeclectListener);
		minute.setOnItemSelectedListener(timeSeclectListener);
	}

	private void initData() {
		Calendar c = Calendar.getInstance();
		lastyearnum = -1;
		lastmonthnum = 1;
		lastdaynum = 1;
		lasthournum = 1;
		lastminutenum = 1;
		mDate = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY),
				c.get(Calendar.MINUTE));
		currentTime = mDate.getTimeInMillis();
		yearAdapter = new TimeAdapter(2, c.get(Calendar.YEAR),getActivity());
		monthAdapter = new TimeAdapter(12, 1,getActivity());
		dayAdapter = new TimeAdapter(c.getActualMaximum(Calendar.DATE), 1,getActivity());
		hourAdapter = new TimeAdapter(24, 0,getActivity());
		minuteAdapter = new TimeAdapter(60, 0,getActivity());
	}

	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				if(!getArguments().getBoolean(LIMITSTARTTIME,true)){
					sendResult();
					return;
				}
/*				if (mDate.getTimeInMillis() < currentTime) {
					mDate.setTimeInMillis(currentTime);
					if(mDate.get(Calendar.MINUTE) <= 30){
						mDate.set(Calendar.MINUTE, 30);
					}else{
						mDate.add(Calendar.HOUR_OF_DAY, 1);
						mDate.set(Calendar.MINUTE, 0);
					}
				}*/
				sendResult();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				break;
			}
		}
	};

	private OnItemSelectedListener timeSeclectListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			switch (parent.getId()) {
			case R.id.spinner_year:
				if(lastyearnum == -1){
					lastyearnum = (int) parent.getSelectedItem();
					return;
				}
				if(lastyearnum == (int) parent.getSelectedItem()){
					return;
				}
				if (position == 1) {
					mDate.set(Calendar.YEAR,mDate.get(Calendar.YEAR) + 1);
					dayAdapter.setCount(mDate.getActualMaximum(Calendar.DATE));
					dayAdapter.notifyDataSetChanged();
				} else {
					mDate.setTimeInMillis(currentTime);
					dayAdapter.setCount(mDate.getActualMaximum(Calendar.DATE));
					dayAdapter.notifyDataSetChanged();
					
				}
				break;
			case R.id.spinner_month:
				if(lastmonthnum == -1){
					lastmonthnum = (int) parent.getSelectedItem();
					return;
				}
				if(lastmonthnum == (int) parent.getSelectedItem()){
					return;
				}
				mDate.set(Calendar.MONTH, position);
				dayAdapter.setCount(mDate.getActualMaximum(Calendar.DATE));
				dayAdapter.notifyDataSetChanged();
				break;
			case R.id.spinner_day:
				if(lastdaynum == -1){
					lastdaynum = (int) parent.getSelectedItem();
					return;
				}
				if(lastdaynum == (int)parent.getSelectedItem()){
					return;
				}
				mDate.set(Calendar.DATE, position + 1);
				break;
			case R.id.spinner_hour:
				mDate.set(Calendar.HOUR_OF_DAY, position);
				break;
			case R.id.spinner_minute:
				mDate.set(Calendar.MINUTE, position);
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};

	private OnClickListener selectDateListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_today:
				mDate.setTimeInMillis(currentTime);
				
				if(mDate.get(Calendar.MINUTE) > 30){
					mDate.add(Calendar.HOUR_OF_DAY, 1);
					mDate.set(Calendar.MINUTE,0);
				}else{
					mDate.set(Calendar.MINUTE,30);
				}
				setSpinner(mDate, 0);
				break;
			case R.id.tv_tomorrow:
				mDate.setTimeInMillis(currentTime);
				mDate.add(Calendar.DATE, 1);
				mDate.set(Calendar.HOUR_OF_DAY, 8);
				mDate.set(Calendar.MINUTE,0);
				setSpinner(mDate, 0);
				break;
			case R.id.tv_after_tomorrow:
				mDate.setTimeInMillis(currentTime);
				mDate.add(Calendar.DATE, 2);
				mDate.set(Calendar.HOUR_OF_DAY, 8);
				mDate.set(Calendar.MINUTE,0);
				setSpinner(mDate, 0);
				break;
			case R.id.tv_am:
				mDate.set(Calendar.HOUR_OF_DAY, 8);
				mDate.set(Calendar.MINUTE,0);
				setSpinner(mDate, 0);
				break;
			case R.id.tv_pm:
				mDate.set(Calendar.HOUR_OF_DAY, 14);
				mDate.set(Calendar.MINUTE,0);
				setSpinner(mDate, 0);
				break;
			}
		}
	};

	protected void sendResult() {
		if (getTargetFragment() == null) {
			return;
		}
		Intent i = new Intent();
		i.putExtra(EXTRATIME, mDate.getTimeInMillis());
		getTargetFragment().onActivityResult(getTargetRequestCode(),
				Activity.RESULT_OK, i);
	}

	protected void setSpinner(Calendar date, int what) {
		switch (what) {
		case 0://所有
			year.setSelection(0);
			month.setSelection(date.get(Calendar.MONTH));
			day.setSelection(date.get(Calendar.DATE) - 1);
			hour.setSelection(date.get(Calendar.HOUR_OF_DAY));
			minute.setSelection(date.get(Calendar.MINUTE));
		case 1:// 年
			month.setSelection(date.get(Calendar.MONTH));
			day.setSelection(date.get(Calendar.DATE) - 1);
			hour.setSelection(date.get(Calendar.HOUR_OF_DAY));
			minute.setSelection(date.get(Calendar.MINUTE));
			break;
		case 2:// 月
			day.setSelection(date.get(Calendar.DATE) - 1);
			hour.setSelection(date.get(Calendar.HOUR_OF_DAY));
			minute.setSelection(date.get(Calendar.MINUTE));
			break;
		case 3:// 日
			hour.setSelection(date.get(Calendar.HOUR_OF_DAY));
			minute.setSelection(date.get(Calendar.MINUTE));
			break;
		case 4:// 点
			minute.setSelection(date.get(Calendar.MINUTE));
			break;
		}
	}

	public static class TimeAdapter extends BaseAdapter {

		private int count;
		private int start;
		private Context context;

		public TimeAdapter(int count, int start,Context context) {
			this.count = count;
			this.start = start;
			this.context = context;
		}

		@Override
		public int getCount() {
			return this.count;
		}

		@Override
		public Object getItem(int position) {
			return start + position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView mTextView;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						R.layout.spinner_item_layout, null);
			}
			mTextView = (TextView) convertView;
			mTextView.setText((int) getItem(position) + "");
			return convertView;
		}

		public void setCount(int count) {
			this.count = count;
		}
	}
}
