package com.htyd.fan.om.util.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.CityBean;
import com.htyd.fan.om.model.DistrictBean;
import com.htyd.fan.om.model.ProvinceBean;
import com.htyd.fan.om.util.db.OMDatabaseHelper.CityCursor;
import com.htyd.fan.om.util.db.OMDatabaseHelper.DistrictCursor;
import com.htyd.fan.om.util.db.OMDatabaseHelper.ProvinceCursor;
import com.htyd.fan.om.util.db.OMDatabaseManager;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;

public class SelectLocationDialogFragment extends DialogFragment {

	private static final String PARENTID = "parentid";
	private static final String PARENTCODE = "parentcode";
	public static final String LOCATION = "location";

	protected Spinner provinceSpinner, citySpinner, districtSpinner;
	private LoaderManager mLoaderManager;
	protected List<ProvinceBean> provinceList;
	protected List<CityBean> cityList;
	protected List<DistrictBean> districtList;
	private LocationLoaderCallBack mCallBack;
	private SelectLocationListener mListener;
	
	public interface SelectLocationListener{
		public void OnSelectLocation(String location);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLoaderManager = getLoaderManager();
		provinceList = new ArrayList<ProvinceBean>();
		cityList = new ArrayList<CityBean>();
		districtList = new ArrayList<DistrictBean>();
		mCallBack = new LocationLoaderCallBack();
		Bundle args = new Bundle();
		args.putInt(PARENTID, 0);
		args.putString(PARENTCODE, null);
		mLoaderManager.initLoader(0, args, mCallBack);
	}

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
				sendResult(Activity.RESULT_OK);
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return builder.create();
	}

	@Override
	public void onDestroy() {
		provinceList.clear();
		cityList.clear();
		districtList.clear();
		OMDatabaseManager.getInstance(getActivity()).closeDb();
		super.onDestroy();
	}

	public void setListener(SelectLocationListener listener){
		this.mListener = listener;
	}
	
	private void initView(View v) {
		provinceSpinner = (Spinner) v.findViewById(R.id.spinner_province);
		citySpinner = (Spinner) v.findViewById(R.id.spinner_city);
		districtSpinner = (Spinner) v.findViewById(R.id.spinner_district);
		provinceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				ProvinceBean mBean = (ProvinceBean) parent
						.getItemAtPosition(position);
				Bundle args = new Bundle();
				args.putInt(PARENTID, mBean.id);
				args.putString(PARENTCODE, mBean.provinceCode);
				if (mLoaderManager.getLoader(1) != null) {
					mLoaderManager.restartLoader(1, args, mCallBack);
				} else {
					mLoaderManager.initLoader(1, args, mCallBack);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				CityBean mBean = (CityBean) parent.getItemAtPosition(position);
				Bundle args = new Bundle();
				args.putInt(PARENTID, mBean.Id);
				args.putString(PARENTCODE, mBean.cityCode);
				if (mLoaderManager.getLoader(2) != null) {
					mLoaderManager.restartLoader(2, args, mCallBack);
				} else {
					mLoaderManager.initLoader(2, args, mCallBack);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	

	protected void sendResult(int reusltCode) {
		if (getTargetFragment() == null && mListener == null ) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		Intent i = new Intent();
		Pattern p = Pattern.compile("[1][1-2].+||[3][1].+||[5][0].+");
		ProvinceBean pBean = (ProvinceBean) provinceSpinner.getSelectedItem();
		CityBean cBean = (CityBean) citySpinner.getSelectedItem();
		DistrictBean dBean = (DistrictBean) districtSpinner.getSelectedItem();
		if(p.matcher(pBean.provinceCode).matches()){
			sb.append(pBean.provinceName).append(dBean.districtName);
		}else{
			sb.append(pBean.provinceName).append(cBean.cityName).append(dBean.districtName);
		}
		i.putExtra(LOCATION, sb.toString());
		if(mListener != null){
			mListener.OnSelectLocation(sb.toString());
			return;
		}
			getTargetFragment().onActivityResult(getTargetRequestCode(),
				reusltCode, i);
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
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			switch (type) {
			case 0:// 省
				ProvinceBean mProvinceBean = (ProvinceBean) getItem(position);
				mHolder.mTextView.setText(mProvinceBean.provinceName+"\t"+mProvinceBean.provinceCode.substring(0, 2));
				break;
			case 1:// 市
				CityBean mCityBean = (CityBean) getItem(position);
				mHolder.mTextView.setText(mCityBean.cityName
						+ "\t"
						+ mCityBean.cityCode.substring(2,4));
				break;
			case 2:// 县
				DistrictBean mDistrictBean = (DistrictBean) getItem(position);
				mHolder.mTextView
						.setText(mDistrictBean.districtName
								+ "\t"
								+ mDistrictBean.districtCode
										.substring(mDistrictBean.districtCode
												.length() - 2));
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

	private static class LocationCursorLoader extends SQLiteCursorLoader {

		private int type;// 0 省份 1 市 2 县区
		private String parentCode;
		private OMDatabaseManager mManger;

		public LocationCursorLoader(Context context, int type,String parentCode) {
			super(context);
			this.type = type;
			this.parentCode = parentCode;
			mManger = OMDatabaseManager.getInstance(getContext());
		}

		@Override
		public Cursor loadCursor() {
			mManger.openDb(0);
			return mManger.queryCursor(parentCode, type);
		}

		@Override
		protected Cursor loadFromNet() {
			return null;
		}
	}

	private class LocationLoaderCallBack implements LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new LocationCursorLoader(getActivity(), id, args.getString(PARENTCODE));

		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

			switch (loader.getId()) {
			case 0:
				ProvinceCursor provinceCursor = (ProvinceCursor) data;
				if (provinceCursor != null && provinceCursor.moveToFirst()) {
					do {
						provinceList.add(provinceCursor.getProvince());
					} while (provinceCursor.moveToNext());
					provinceSpinner
							.setAdapter(new SpinnerAdapter<ProvinceBean>(
									provinceList, 0));
				}
				break;
			case 1:
				CityCursor cityCursor = (CityCursor) data;
				cityList.clear();
				if (cityCursor != null && cityCursor.moveToFirst()) {
					do {
						cityList.add(cityCursor.getCity());
					} while (cityCursor.moveToNext());
					citySpinner.setAdapter(new SpinnerAdapter<CityBean>(
							cityList, 1));
				}
				break;
			case 2:
				DistrictCursor districtCursor = (DistrictCursor) data;
				districtList.clear();
				if (districtCursor != null && districtCursor.moveToFirst()) {
					do {
						districtList.add(districtCursor.getDistrict());
					} while (districtCursor.moveToNext());
					districtSpinner
							.setAdapter(new SpinnerAdapter<DistrictBean>(
									districtList, 2));
				}
				break;
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	}
}
