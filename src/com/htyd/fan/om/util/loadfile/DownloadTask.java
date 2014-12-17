package com.htyd.fan.om.util.loadfile;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.loadfile.DownLoadManager.DataTransferListener;
import com.htyd.fan.om.util.ui.UItoolKit;

public class DownloadTask extends AsyncTask<AffiliatedFileBean, Float, Boolean> {

	private Context context;
	private String filePath;
	private ProgressDialog pd;
	private DownLoadManager mManager;
	private DownLoadFinishListener mListener;
	private AffiliatedFileBean mBean;
	
	public DownloadTask(Context context, String filePath,DownLoadFinishListener mListener) {
		this.context = context;
		this.filePath = filePath;
		pd = new ProgressDialog(context);
		mManager = DownLoadManager.getInstance();
		this.mListener = mListener;
	}
	
	public interface DownLoadFinishListener{
		public void onDownLoadFinish(AffiliatedFileBean mBean);
	}
	
	@Override
	protected void onPreExecute() {
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Downloading Picture...");
		pd.setCancelable(true);
		pd.setOnCancelListener(loadCancelListener);
		pd.show();
	}

	private OnCancelListener loadCancelListener = new OnCancelListener(){
		@Override
		public void onCancel(DialogInterface dialog) {
			dialog.dismiss();
			DownloadTask.this.cancel(false);
		}
	};

	@Override
	protected Boolean doInBackground(AffiliatedFileBean... params) {
		
		final long fileSize = params[0].fileSize;
		mBean = params[0];
		mManager.setListener(new DataTransferListener() {
			@Override
			public void OnDataTransfer(long length) {
				publishProgress((length / (float)fileSize) * 100);
			}
		});
		JSONObject param = new JSONObject();
		try {
			param.put("WJID", mBean.netId);
			param.put("WJDZ", mBean.filePath);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return mManager.downLoadFromNet(context, param, Urls.FILE,
				"Operate=downLoad", filePath);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if(result){
			UItoolKit.showToastShort(context, "下载成功");
			mBean.fileState = 1;
			mBean.filePath = filePath;
			mListener.onDownLoadFinish(mBean);
		}else{
			UItoolKit.showToastShort(context, "下载失败");
		}
		pd.dismiss();
		this.cancel(false);
	}
	
	@Override
	protected void onProgressUpdate(Float... values) {
		//pd.setProgress(values[0]);
		pd.setMessage("Downloading Picture..."+values[0]+"%");
	}
	@Override
	protected void onCancelled(Boolean result) {
		super.onCancelled(result);
	}
}
