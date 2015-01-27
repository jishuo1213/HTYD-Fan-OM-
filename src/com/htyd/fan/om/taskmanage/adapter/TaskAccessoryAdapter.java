package com.htyd.fan.om.taskmanage.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.util.ui.UItoolKit;

public class TaskAccessoryAdapter extends BaseAdapter {

	private List<AffiliatedFileBean> accessoryList;
	private Context context;
	private LoadListener mListener;
	
	public interface LoadListener{
		public void onUpLoadClick(AffiliatedFileBean mBean,int position);
		public void onDeleteClick(AffiliatedFileBean mBean,int position);
		public void onPreviewImg(String path);
		public void onSetPhotoDescription(int pos);
	}
	
	public TaskAccessoryAdapter(List<AffiliatedFileBean> accessoryList,Context context,LoadListener listener) {
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
		//mHolder.accessoryName.setText("附件" + (position + 1));
		if (mBean.fileSource == 0) {//本地创建的附件
			mHolder.accessoryState.setText(mBean.fileState == 0?"未上传":"已上传");
			mHolder.upLoad.setText("上传");
		}else{//网络上获取的附件
			mHolder.accessoryState.setText(mBean.fileState == 0?"未下载":"已下载");
			mHolder.upLoad.setText("下载");
		}
		mHolder.preViewImage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mBean.fileSource == 1 && mBean.fileState == 0){
					UItoolKit.showToastShort(context, "附件还未下载，无法预览");
					return;
				}
				mListener.onPreviewImg(mBean.filePath);
			}
		});
		mHolder.upLoad.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mBean.fileState == 1){
					return;
				}
				mListener.onUpLoadClick(mBean,position);
			}
		});
		mHolder.deleteFile.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mListener.onDeleteClick(mBean, position);
			}
		});
		mHolder.accessoryName.setText(mBean.fileDescription);
		mHolder.accessoryName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(mBean.fileDescription)) {
					return;
				}
				mListener.onSetPhotoDescription(position);
			}
		});
		return convertView;
	}

	private class ViewHolder {
		public TextView accessoryName,accessoryState,upLoad,deleteFile,preViewImage;
		public ViewHolder(View v) {
			preViewImage = (TextView) v.findViewById(R.id.tv_preview);
			accessoryName = (TextView) v.findViewById(R.id.tv_file_name);
			accessoryState = (TextView) v.findViewById(R.id.tv_file_state);
			upLoad = (TextView) v.findViewById(R.id.tv_upload);
			deleteFile = (TextView) v.findViewById(R.id.tv_delete);
		}
	}
}
