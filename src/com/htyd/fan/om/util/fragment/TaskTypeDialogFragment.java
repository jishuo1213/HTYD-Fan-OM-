package com.htyd.fan.om.util.fragment;

import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.CommonDataBean;
import com.htyd.fan.om.util.base.ThreadPool;
import com.htyd.fan.om.util.db.OMDatabaseHelper.CommonDataCursor;
import com.htyd.fan.om.util.db.OMDatabaseManager;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.https.Utility;
import com.htyd.fan.om.util.ui.UItoolKit;

public class TaskTypeDialogFragment extends DialogFragment {

	public static final String TYPENAME = "typename";
	public static final String CAT = "cat";
	
	private Spinner spinner;
	protected ArrayList<CommonDataBean> typeList;
	protected String cat;
	
	
	public static DialogFragment newInstance(String cat){
		DialogFragment fragment = new TaskTypeDialogFragment();
		Bundle args = new Bundle();
		args.putString(CAT, cat);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		typeList = new ArrayList<CommonDataBean>();
		cat = getArguments().getString(CAT);
		queryTaskType(typeList,cat);
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.task_type_layout, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setTitle("选择"+cat);
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
		i.putExtra(TYPENAME, ((CommonDataBean)spinner.getSelectedItem()));
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
	}

	private void initView(View v) {
		spinner = (Spinner) v.findViewById(R.id.spinner_tasktype);
		ImageView refresh = (ImageView) v.findViewById(R.id.img_refresh_tasktype);
		refresh.setOnClickListener(listener);
	}
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			new LoadTaskType().execute(cat);
		}
	};
	
	private class LoadTaskType extends AsyncTask<String, Void, Boolean>{

		private OMDatabaseManager mManager;
		
		public LoadTaskType() {
			mManager = OMDatabaseManager.getInstance(getActivity());
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				if (spinner.getAdapter() == null) {
					spinner.setAdapter(new TypeAdapter(typeList));
				} else {
					((TypeAdapter) spinner.getAdapter()).notifyDataSetChanged();
				}
			} else {
				UItoolKit.showToastShort(getActivity(), "加载数据失败");
			}
			cancel(false);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String result = "";
			JSONObject param = new JSONObject();
			try {
				param.put("FLMC", params[0]);
				result = NetOperating.getResultFromNet(getActivity(), param, Urls.COMMONDATA, "Operate=getNrxxByFlmc");
				mManager.deleteType(cat);
				typeList.clear();
				return Utility.handleTaskType(mManager, result,typeList);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	private class TypeAdapter extends BaseAdapter{

		private ArrayList<CommonDataBean> typeList;
		
		public TypeAdapter(ArrayList<CommonDataBean> typeList) {
			super();
			this.typeList = typeList;
		}

		@Override
		public int getCount() {
			return typeList.size();
		}

		@Override
		public Object getItem(int position) {
			return typeList.get(position);
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
			CommonDataBean mBean = (CommonDataBean) getItem(position);
			text.setText(mBean.typeName);
			return convertView;
		}
	}
	
	private void queryTaskType(final ArrayList<CommonDataBean> typeList,final String cat){
		ThreadPool.runMethod(new Runnable() {
			@Override
			public void run() {
				CommonDataCursor cursor = (CommonDataCursor) OMDatabaseManager
						.getInstance(getActivity()).queryTaskType(cat);
				if(cursor.getCount() > 0 && cursor.moveToFirst()){
					do{
						typeList.add(cursor.getTaskType());
					}while(cursor.moveToNext());
				}
				cursor.close();
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						setAdapter();
					}
				});
			}
		});
	}

	protected void setAdapter() {
		if(typeList.size() > 0){
			spinner.setAdapter(new TypeAdapter(typeList));
		}
	}
}
