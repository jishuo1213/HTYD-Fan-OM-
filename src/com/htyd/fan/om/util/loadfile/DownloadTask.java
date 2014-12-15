package com.htyd.fan.om.util.loadfile;

import java.io.IOException;

import com.htyd.fan.om.util.ui.UItoolKit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

public class DownloadTask extends AsyncTask<String, Integer, Boolean> {

	private Context context;
	private String filePath;
	private ProgressDialog pd;
	private DownLoadManager mManager;
	
	public DownloadTask(Context context, String filePath) {
		this.context = context;
		this.filePath = filePath;
		pd = new ProgressDialog(context);
		mManager = DownLoadManager.getInstance();
	}

	
	@Override
	protected void onPreExecute() {
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
	protected Boolean doInBackground(String... params) {
		try {
			int allsize  = mManager.getFileSize(filePath);
			mManager.download(filePath, params[0]);
			while(!mManager.isComplete()){
				publishProgress((mManager.getDownLoadNum() / allsize) * 100);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if(result){
			UItoolKit.showToastShort(context, "下载成功");
		}
		this.cancel(false);
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		pd.setProgress(values[0]);
	}
}
