package com.htyd.fan.om.attendmanage.fragment;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.htyd.fan.om.model.AttendBean;
import com.htyd.fan.om.util.ui.SelectLocationDialogFragment;
import com.htyd.fan.om.util.ui.UItoolKit;

public class AttendManageFragment extends Fragment {

	private ViewPanel mPanel;
	private AttendBean mBean;
	private GeoCoder mCoder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBean = new AttendBean();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.attend_manage_fragment, container,
				false);
		intiView(v);
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 0) {
				UItoolKit.showToastShort(getActivity(), data
						.getStringExtra(SelectLocationDialogFragment.LOCATION));
			}
		}
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
		mBean.time = loc.getTime();
		mBean.latitude = loc.getLatitude();
		mBean.longitude = loc.getLongitude();
		LatLng point;
		point = transCoordinate(new LatLng(loc.getLatitude(),
				loc.getLongitude()));
		initGeoCode();
		mCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));
	}

	private class ViewPanel {
		private TextView locTextView, timeTextView;
		private Button mButton;

		public ViewPanel(View v) {
			locTextView = (TextView) v.findViewById(R.id.tv_loction);
			timeTextView = (TextView) v.findViewById(R.id.tv_time);
			mButton = (Button) v.findViewById(R.id.btn_sign_in);
			mButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentManager fm = getActivity().getFragmentManager();
					SelectLocationDialogFragment dialog = new SelectLocationDialogFragment();
					dialog.setTargetFragment(AttendManageFragment.this, 0);
					dialog.show(fm, null);
				}
			});
		}

		public void setLocation(String loc) {
			locTextView.setText(loc);
		}

		public void setTime(String time) {
			timeTextView.setText(time);
		}
	}

	private OnGetGeoCoderResultListener mGeoCodeListener = new OnGetGeoCoderResultListener() {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
			ReverseGeoCodeResult.AddressComponent component = arg0
					.getAddressDetail();
			mBean.SetValueBean(component);
			mPanel.setLocation(mBean.getAddress());
			mPanel.setTime(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
					.format(mBean.time));
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
