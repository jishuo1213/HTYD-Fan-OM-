package com.htyd.fan.om.util.fragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.util.fragment.DateTimePickerDialog.TimeAdapter;

public class SpendTimePickerDialog extends DialogFragment {

	public static final String ENDTIME = "endtime";
	private static final String STARTTIME = "starttime";
	
	protected Calendar mDate;
	private Spinner year, month, day, hour, minute;
	private TimeAdapter yearAdapter, monthAdapter, dayAdapter, hourAdapter,
			minuteAdapter;
	protected long startTime;
	
	public static DialogFragment  newInstance(long startTime){
		Bundle bundle = new Bundle();
		bundle.putLong(STARTTIME, startTime);
		DialogFragment fragment = new SpendTimePickerDialog();
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startTime = getArguments().getLong(STARTTIME);
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
		return builder.create();
	}

	private void initView(View v) {
		TextView halfday, oneday, twoday, threeday, threehour;

		halfday = (TextView) v.findViewById(R.id.tv_today);
		oneday = (TextView) v.findViewById(R.id.tv_tomorrow);
		twoday = (TextView) v.findViewById(R.id.tv_after_tomorrow);
		threeday = (TextView) v.findViewById(R.id.tv_am);
		threehour = (TextView) v.findViewById(R.id.tv_pm);

		halfday.setText("半天");
		oneday.setText("一天");
		twoday.setText("两天");
		threeday.setText("三天");
		threehour.setText("三小时");
		
		halfday.setOnClickListener(selectDateListener);
		oneday.setOnClickListener(selectDateListener);
		twoday.setOnClickListener(selectDateListener);
		threeday.setOnClickListener(selectDateListener);
		threehour.setOnClickListener(selectDateListener);

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

		setSpinner(mDate);
		
		year.setOnItemSelectedListener(timeSeclectListener);
		month.setOnItemSelectedListener(timeSeclectListener);
		day.setOnItemSelectedListener(timeSeclectListener);
		hour.setOnItemSelectedListener(timeSeclectListener);
		minute.setOnItemSelectedListener(timeSeclectListener);
	}

	private void initData() {
		Calendar c = Calendar.getInstance();
		mDate = new GregorianCalendar(c.get(Calendar.YEAR),
				c.get(Calendar.MONTH), c.get(Calendar.DATE),
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
		mDate.setTimeInMillis(startTime);
		yearAdapter = new TimeAdapter(2, c.get(Calendar.YEAR), getActivity());
		monthAdapter = new TimeAdapter(12, 1, getActivity());
		dayAdapter = new TimeAdapter(c.getActualMaximum(Calendar.DATE), 1,
				getActivity());
		hourAdapter = new TimeAdapter(24, 0, getActivity());
		minuteAdapter = new TimeAdapter(60, 0, getActivity());
	}

	private OnClickListener selectDateListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_today:// 半天
				mDate.setTimeInMillis(startTime);
				mDate.add(Calendar.HOUR_OF_DAY, 6);
				setSpinner(mDate);
				break;
			case R.id.tv_tomorrow:// 一天
				mDate.setTimeInMillis(startTime);
				mDate.add(Calendar.DATE, 1);
				setSpinner(mDate);
				break;
			case R.id.tv_after_tomorrow:// 两天
				mDate.setTimeInMillis(startTime);
				mDate.add(Calendar.DATE, 2);
				setSpinner(mDate);
				break;
			case R.id.tv_am:// 三天
				mDate.setTimeInMillis(startTime);
				mDate.add(Calendar.DATE, 3);
				setSpinner(mDate);
				break;
			case R.id.tv_pm:// 三小时
				mDate.setTimeInMillis(startTime);
				mDate.add(Calendar.HOUR_OF_DAY, 3);
				setSpinner(mDate);
				break;
			}
		}
	};

	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				if(mDate.getTimeInMillis() < startTime){
					mDate.setTimeInMillis(startTime);
				}
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
				if (position == 1) {
					mDate.set(Calendar.YEAR, mDate.get(Calendar.YEAR) + 1);
					dayAdapter.setCount(mDate.getActualMaximum(Calendar.DATE));
					dayAdapter.notifyDataSetChanged();
				} else {
					mDate.setTimeInMillis(startTime);
					dayAdapter.setCount(mDate.getActualMaximum(Calendar.DATE));
					dayAdapter.notifyDataSetChanged();
				}
				break;
			case R.id.spinner_month:
				mDate.set(Calendar.MONTH, position);
				dayAdapter.setCount(mDate.getActualMaximum(Calendar.DATE));
				dayAdapter.notifyDataSetChanged();
				break;
			case R.id.spinner_day:
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

	protected void setSpinner(Calendar date){
		year.setSelection(0);
		month.setSelection(date.get(Calendar.MONTH));
		day.setSelection(date.get(Calendar.DATE) - 1);
		hour.setSelection(date.get(Calendar.HOUR_OF_DAY));
		minute.setSelection(date.get(Calendar.MINUTE));
	}
	
	protected void sendResult() {
		if (getTargetFragment() == null) {
			return;
		}
		Intent i = new Intent();
		i.putExtra(ENDTIME, mDate.getTimeInMillis());
		Log.i("fanjishuo____sendResult", mDate.getTimeInMillis()+"");
		getTargetFragment().onActivityResult(getTargetRequestCode(),
				Activity.RESULT_OK, i);
	}
}
