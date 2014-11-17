package com.htyd.fan.om.util.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.CityBean;
import com.htyd.fan.om.model.DistrictBean;
import com.htyd.fan.om.model.ProvinceBean;

public class SelectLocationDialogFragment extends DialogFragment {

	private Spinner provinceSpinner, citySpinner, districtSpinner;

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_select_location, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setTitle("请选择地点");
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return builder.create();
	}

	private void initView(View v) {
		provinceSpinner = (Spinner) v.findViewById(R.id.spinner_province);
		citySpinner = (Spinner) v.findViewById(R.id.spinner_city);
		districtSpinner = (Spinner) v.findViewById(R.id.spinner_district);
		provinceSpinner.setAdapter(new SpinnerAdapter<ProvinceBean>(null,0));
	}

	private class SpinnerAdapter<T extends Parcelable> extends BaseAdapter {

		private List<T> dataList;
		private int type;

		public SpinnerAdapter(List<T> dataList, int type) {
			this.dataList = dataList;
			this.type = type;
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("unchecked")
		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.spinner_item_layout, null);
				mHolder = new ViewHolder(convertView);
				convertView.setTag(mHolder);
			}else{
				mHolder = (ViewHolder) convertView.getTag();
			}
			switch(type){
			case 0://省
				ProvinceBean mProvinceBean = (ProvinceBean) getItem(position);
				mHolder.mTextView.setText(mProvinceBean.provinceName);
				break;
			case 1://市
				CityBean mCityBean = (CityBean) getItem(position);
				mHolder.mTextView.setText(mCityBean.cityName);
				break;
			case 2://县
				DistrictBean mDistrictBean = (DistrictBean) getItem(position);
				mHolder.mTextView.setText(mDistrictBean.districtName);
				break;
			}
			
			return convertView;
		}

		private class ViewHolder {
			public TextView mTextView;

			public ViewHolder(View v) {
				mTextView = (TextView) v.findViewById(R.id.tv_spinner);
			}
		}
	}
}
