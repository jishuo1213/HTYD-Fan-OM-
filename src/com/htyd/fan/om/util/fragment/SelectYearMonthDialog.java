package com.htyd.fan.om.util.fragment;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.htyd.fan.om.R;

public class SelectYearMonthDialog extends DialogFragment {

	
	private Spinner yearSpinner,monthSpinner;
	private Calendar selectDate ;
	private SelectAttendDateListener listener;
	
	public interface SelectAttendDateListener{
		public void onSelectDate(Calendar c);
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(R.layout.select_year_month, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setTitle("请选择月份");
		builder.setPositiveButton("确定", dialogClickListener);
		builder.setNegativeButton("取消", dialogClickListener);
		return builder.create();
	}

	private OnClickListener dialogClickListener = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
			case DialogInterface.BUTTON_POSITIVE:
				selectDate.set(Calendar.YEAR, (int) yearSpinner.getSelectedItem());
				selectDate.set(Calendar.MONTH, (int) monthSpinner.getSelectedItem() -1);
				sendResult();
				break;
			}
		}
	};
	
	private View.OnClickListener monthClickListener =  new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.tv_pre_month:
				if (selectDate.get(Calendar.MONTH) == 0) {
					monthSpinner.setSelection(11);
				} else {
					monthSpinner.setSelection(selectDate.get(Calendar.MONTH) - 1);
				}
				yearSpinner.setSelection(1);
				break;
			case R.id.tv_current_month:
				yearSpinner.setSelection(1);
				monthSpinner.setSelection(selectDate.get(Calendar.MONTH));
				break;
			}
		}
	};
	
	private void sendResult(){
		listener.onSelectDate(selectDate);
	}
	
	private void initView(View v) {
		selectDate = Calendar.getInstance();
		yearSpinner = (Spinner) v.findViewById(R.id.spinner_calendar_year);
		monthSpinner = (Spinner) v.findViewById(R.id.spinner_calendar_month);
		TextView preMonth = (TextView) v.findViewById(R.id.tv_pre_month);
		TextView currentMonth = (TextView) v.findViewById(R.id.tv_current_month);
		preMonth.setOnClickListener(monthClickListener);
		currentMonth.setOnClickListener(monthClickListener);
		yearSpinner.setAdapter(new DateTimePickerDialog.TimeAdapter(2,
				selectDate.get(Calendar.YEAR) - 1, getActivity()));
		monthSpinner.setAdapter(new DateTimePickerDialog.TimeAdapter(12,1,getActivity()));
		monthSpinner.setSelection(selectDate.get(Calendar.MONTH));
		yearSpinner.setSelection(1);
	}
	
	public void setListener(SelectAttendDateListener listener){
		this.listener = listener;
	}
}
