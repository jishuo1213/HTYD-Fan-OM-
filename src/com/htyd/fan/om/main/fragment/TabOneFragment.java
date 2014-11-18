package com.htyd.fan.om.main.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.attendmanage.AttentManagerActivity;

public class TabOneFragment extends Fragment {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_main_page, container, false);
		intiView(v);
		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void intiView(View v) {
		GridView mGridView = (GridView) v.findViewById(R.id.grid_main_page);
		mGridView.setAdapter(new MainPageGridAdapter());
		mGridView.setOnItemClickListener(new GridItemClickListener());
	}

	private class GridItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch(position){
			case 0:
				Intent i = new Intent(getActivity(),AttentManagerActivity.class);
				startActivity(i);
			}
		}
	}
	
	private class MainPageGridAdapter extends BaseAdapter {
		private String[] itemArray;
		private int[] colorIdArray;
		private Resources r;

		public MainPageGridAdapter() {
			r = getActivity().getResources();
			itemArray = r.getStringArray(R.array.main_page_text);
			TypedArray colors = r
					.obtainTypedArray(R.array.main_page_item_color);
			colorIdArray = new int[colors.length()];
			for (int i = 0; i < colors.length(); i++) {
				colorIdArray[i] = r.getColor(colors.getResourceId(i, -1));
				Log.i("fanjishuo____colorIdArray", "colorIdArray" + i
						+ colorIdArray[i] + "");
			}
			colors.recycle();
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
						R.layout.main_page_item_layout, null);
				mHolder = new ViewHolder(convertView);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			mHolder.setText((String) getItem(position));

			if (colorIdArray[position] != -1) {
				mHolder.setBackground(colorIdArray[position]);
			}
			return convertView;
		}
	}

	private class ViewHolder {

		private TextView itemTextView;

		public ViewHolder(View v) {
			itemTextView = (TextView) v.findViewById(R.id.tv_item_menu);
		}

		public void setText(String text) {
			itemTextView.setText(text);
		}

		public void setBackground(int color) {
			itemTextView.setBackgroundColor(color);
		}
	}
}
