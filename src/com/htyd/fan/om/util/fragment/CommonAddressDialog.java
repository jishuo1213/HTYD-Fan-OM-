package com.htyd.fan.om.util.fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.util.fragment.SelectLocationDialogFragment.SelectLocationListener;

public class CommonAddressDialog extends DialogFragment implements SelectLocationListener {

	public static final String ADDRESS = "common_address";
	public static final String FILENAME = "address_file";
	
	private ArrayList<String> addressList;
	protected ListView addressListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			addressList = loadAddress();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(R.layout.common_address_layout, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setTitle("管理常用地点");
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(addressList != null)
					saveAddress(addressList);
			}
		});
		return builder.create();
	}

	private void initView(View v) {
		TextView addAddress = (TextView) v.findViewById(R.id.tv_add_address);
		addAddress.setOnClickListener(addClickListener);
		addressListView = (ListView) v.findViewById(R.id.list_address);
		if(addressList != null){
			addressListView.setAdapter(new AddressAdapter(addressList));
		}
	}
	
	private View.OnClickListener addClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			SelectLocationDialogFragment dialog = new SelectLocationDialogFragment();
			dialog.setListener(CommonAddressDialog.this);
			dialog.show(getActivity().getFragmentManager(), null);
		}
	};
	
	private void saveAddress(ArrayList<String> address){
		JSONArray array = new JSONArray();
		int length = address.size();
		for(int i = 0; i < length;i++){
			JSONObject json = new JSONObject();
			try {
				json.put(ADDRESS, address.get(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(json);
		}
		BufferedWriter writer = null;
		try {
			OutputStream out = getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE);
			writer = new BufferedWriter(new OutputStreamWriter(out));
			writer.write(array.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private ArrayList<String> loadAddress() throws FileNotFoundException {
		BufferedReader reader = null;
		ArrayList<String> listAddress = new ArrayList<String>();
		InputStream in = getActivity().openFileInput(FILENAME);
		reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			if (sb.toString().length() > 0) {
				JSONArray array = (JSONArray) new JSONTokener(sb.toString())
						.nextValue();
				for (int i = 0; i < array.length(); i++) {
					listAddress.add(array.getJSONObject(i).getString(ADDRESS));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return listAddress;
	}
	
	private class AddressAdapter extends BaseAdapter {

		private ArrayList<String> addressList = new ArrayList<String>();
		
		
		public AddressAdapter(ArrayList<String> addressList) {
			super();
			this.addressList = addressList;
		}

		@Override
		public int getCount() {
			return addressList.size();
		}

		@Override
		public Object getItem(int position) {

			return addressList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder mHolder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.address_item_layout, null);
				mHolder = new ViewHolder(convertView);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			mHolder.address.setText((position+1)+"   "+(String) getItem(position));
			mHolder.delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					addressList.remove(position);
					notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}

	protected class ViewHolder {
		public TextView address, delete;

		public ViewHolder(View v) {
			address = (TextView) v.findViewById(R.id.tv_address);
			delete = (TextView) v.findViewById(R.id.tv_delete_address);
		}
	}

	@Override
	public void OnSelectLocation(String location) {
		if (addressListView.getAdapter() == null) {
			addressList = new ArrayList<String>();
			addressList.add(location);
			addressListView.setAdapter(new AddressAdapter(addressList));
		} else {
			if (!addressList.contains(location)) {
				addressList.add(location);
				AddressAdapter mAdapter = (AddressAdapter) addressListView
						.getAdapter();
				mAdapter.notifyDataSetChanged();
			}
		}
	}
}
