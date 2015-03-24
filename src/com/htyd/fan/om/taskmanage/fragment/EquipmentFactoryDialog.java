package com.htyd.fan.om.taskmanage.fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.base.ThreadPool;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;

public class EquipmentFactoryDialog extends DialogFragment {

	public static final String FACTORYNAME = "factoryname";
	private Spinner spinner;
	protected Handler handler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
	}
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.task_type_layout, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setTitle("选择设备厂家");
		builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendResult();
			}
		});
		builder.setNegativeButton("取消", null);
		return builder.create();
	}

	protected void sendResult() {
		if(getTargetFragment() == null || spinner.getSelectedItem() == null)
			return;
		Intent i = new Intent();
		try {
			i.putExtra(FACTORYNAME,(((JSONObject) spinner.getSelectedItem()).getString("CJMC")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
	}

	private void initView(View v) {
		spinner = (Spinner) v.findViewById(R.id.spinner_tasktype);
		try {
			loadData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ImageView refresh = (ImageView) v.findViewById(R.id.img_refresh_tasktype);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ThreadPool.runMethod(new GetFactoryThread(getActivity(),handler));
			}
		});
	}

	
	private class GetFactoryThread implements  Runnable{
		
		private Context context;
		private Handler handler;
		
		public GetFactoryThread(Context context,Handler handler) {
			super();
			this.context = context;
			this.handler = handler;
		}

		@Override
		public void run() {
			JSONObject json = new JSONObject();
			try {
				json.put("CJMC", "");
				json.put("CJDZ", "");
				String result = NetOperating.getResultFromNet(context, json, Urls.EQUIPMENTFACTORYURL, "Operate=getAllCjxx");
				if(result.length() > 0 ){
					handler.post(new Runnable() {
						@Override
						public void run() {
							try {
								loadData();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						}
					});
				}
				saveResult(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void saveResult(String result) {
		BufferedWriter writer = null;
		try {
			JSONObject json = new JSONObject(result);
			result = json.getString("Rows");
			OutputStream out = getActivity().openFileOutput(
					Preferences.getUserId(getActivity()) + "factory.xml",
					Context.MODE_PRIVATE);
			writer = new BufferedWriter(new OutputStreamWriter(out));
			writer.write(result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}  finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadData() throws FileNotFoundException{
		BufferedReader reader = null;
		InputStream in = getActivity().openFileInput(Preferences.getUserId(getActivity()) + "factory.xml");
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
				spinner.setAdapter(new FactoryAdapter(array));
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
	}
	
	private class FactoryAdapter extends BaseAdapter{

		private JSONArray typeList;
		
		public FactoryAdapter(JSONArray array) {
			super();
			this.typeList = array;
		}

		@Override
		public int getCount() {
			return typeList.length();
		}

		@Override
		public Object getItem(int position) {
			try {
				return typeList.get(position);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.spinner_item_layout, null);
			}
			TextView text = (TextView) convertView;
			JSONObject json = (JSONObject) getItem(position);
			if (json != null) {
				try {
					text.setText(json.getString("CJMC"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return convertView;
		}
	}
}
