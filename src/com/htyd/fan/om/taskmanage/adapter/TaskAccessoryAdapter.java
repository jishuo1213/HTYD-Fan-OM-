package com.htyd.fan.om.taskmanage.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.util.base.PictureUtils;

public class TaskAccessoryAdapter extends BaseAdapter {

	private List<AffiliatedFileBean> accessoryList;
	private Context context;
	private UpLoadFileListener mListener;
	
	public interface UpLoadFileListener{
		public void onUpLoadClick(AffiliatedFileBean mBean);
		public void onDeleteClick(AffiliatedFileBean mBean,int position);
	}
	
	public TaskAccessoryAdapter(List<AffiliatedFileBean> accessoryList,Context context,UpLoadFileListener listener) {
		this.accessoryList = accessoryList;
		this.context = context;
		this.mListener = listener;
	}

	@Override
	public int getCount() {
		return accessoryList.size();
	}

	@Override
	public Object getItem(int position) {
		return accessoryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.upload_file_layout, null);
			mHolder = new ViewHolder(convertView);
			convertView.setTag(mHolder);
		}else{
			mHolder = (ViewHolder) convertView.getTag();
		}
		final AffiliatedFileBean mBean = (AffiliatedFileBean) getItem(position);
		mHolder.accessoryImg.setImageDrawable(PictureUtils.getScaledDrawable((Activity) context, mBean.filePath));
		mHolder.accessoryName.setText("附件"+(position+1));
		mHolder.accessoryState.setText(mBean.fileState+"");
		mHolder.upLoad.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mListener.onUpLoadClick(mBean);
			}
		});
		mHolder.deleteFile.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mListener.onDeleteClick(mBean, position);
			}
		});
		return convertView;
	}

	private class ViewHolder {
		public ImageView accessoryImg;
		public TextView accessoryName,accessoryState,upLoad,deleteFile;
		public ViewHolder(View v) {
			accessoryImg = (ImageView) v.findViewById(R.id.img_accessory_file);
			accessoryName = (TextView) v.findViewById(R.id.tv_file_name);
			accessoryState = (TextView) v.findViewById(R.id.tv_file_state);
			upLoad = (TextView) v.findViewById(R.id.tv_upload);
			deleteFile = (TextView) v.findViewById(R.id.tv_delete);
		}
		
	}
}
