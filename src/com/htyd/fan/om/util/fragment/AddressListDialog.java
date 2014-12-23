package com.htyd.fan.om.util.fragment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import com.htyd.fan.om.R;
import com.htyd.fan.om.util.ui.UItoolKit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AddressListDialog extends DialogFragment {

	public static final String ADDRESS = "common_address";
	public static final String FILENAME = "address_file";
	
	private ArrayList<String> addressList;
	private ChooseAddressListener mListener;
	
	public interface ChooseAddressListener{
		public void onAddressChoose(String address);
	}
	
	public AddressListDialog(ChooseAddressListener listener) {
		super();
		this.mListener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			addressList = loadAddress();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("选择常用地点");
		if (addressList.size() > 0) {
			builder.setAdapter(new AddressAdapter(addressList), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onAddressChoose(addressList.get(which));
					dialog.dismiss();
				}
			});
		}else{
			UItoolKit.showToastShort(getActivity(), "还没有常用地点，快去设置里添加吧");
		}
		return builder.create();
	}

	private class AddressAdapter extends BaseAdapter{

		private ArrayList<String>  addressList;
		
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
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.choose_address_item_layout, null);
			}
			TextView text = (TextView) convertView;
			text.setText((position+1)+"   "+(String)getItem(position));
			return convertView;
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
}
