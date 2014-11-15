package com.htyd.fan.om.attendmanage.fragment;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.htyd.fan.om.R;
import com.htyd.fan.om.attendmanage.model.LocationBean;

public class AttendManageFragment extends Fragment {

	private ViewPanel mPanel;
	private LocationBean mBean;
	private GeoCoder mCoder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBean = new LocationBean();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.attend_manage_fragment, container,
				false);
		intiView(v);
		return v;
	}

	private void intiView(View v) {
		mPanel = new ViewPanel(v);
	}

	private void initGeoCode() {
		mCoder = GeoCoder.newInstance();
		mCoder.setOnGetGeoCodeResultListener(mGeoCodeListener);
	}

	@SuppressLint("SimpleDateFormat")
	public void updateUI(Location loc) {
		mBean.time = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(loc
				.getTime());
		LatLng point;
		point = transCoordinate(new LatLng(loc.getLatitude(),
				loc.getLongitude()));
		initGeoCode();
		mCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));
	}

	private class ViewPanel {
		private TextView locTextView, timeTextView;

		public ViewPanel(View v) {
			locTextView = (TextView) v.findViewById(R.id.tv_loction);
			timeTextView = (TextView) v.findViewById(R.id.tv_time);
		}

		public void setLocation(String loc) {
			locTextView.setText(loc);
		}

		public void setTime(String time) {
			timeTextView.setText(time);
		}
	}

	private OnGetGeoCoderResultListener mGeoCodeListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
			ReverseGeoCodeResult.AddressComponent component = arg0
					.getAddressDetail();
			mBean.SetValueBean(component);
			mPanel.setLocation(mBean.getAddress());
			mPanel.setTime(mBean.time);
		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
		}
	};

	private LatLng transCoordinate(LatLng temppoint) {
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.GPS);
		converter.coord(temppoint);
		return converter.convert();
	}
}
