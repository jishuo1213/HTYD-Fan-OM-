package com.htyd.fan.om.util.loadfile;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.https.HttpHelper;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.loadfile.CustomMultipartEntity.ProgressListener;
import com.htyd.fan.om.util.ui.UItoolKit;


public class HttpMultipartPost extends AsyncTask<String, Integer, String> {

	private Context context;
	private String filePath;
	private ProgressDialog pd;
	private long totalSize;
	private AffiliatedFileBean mBean;
	private UpLoadFinishListener listener;
	private int position;
	
	public interface UpLoadFinishListener{
		public void onUpLoadFinish(AffiliatedFileBean mBean,int position);
	}

	public HttpMultipartPost(Context context, String filePath,UpLoadFinishListener listener) {
		this.context = context;
		this.filePath = filePath;
		this.listener = listener;
		position = -1;
	    mBean = new AffiliatedFileBean();
	}

	@Override
	protected void onPreExecute() {
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("Uploading Picture...");
		pd.setCancelable(true);
		pd.setOnCancelListener(loadCancelListener);
		pd.show();
	}

	private OnCancelListener loadCancelListener = new OnCancelListener(){
		@Override
		public void onCancel(DialogInterface dialog) {
			dialog.dismiss();
			HttpMultipartPost.this.cancel(false);
		}
	};
	
	@Override
	protected String doInBackground(String... params) {
		String serverResponse = null;
		HttpClient httpClient = HttpHelper.getHttpClient(context);
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(Urls.UPLOADFILE);
		Log.i("fanjishuo_____doInBackground", Urls.UPLOADFILE);
		position = Integer.parseInt(params[4]);
		try {
			CustomMultipartEntity multipartContent = new CustomMultipartEntity(
					new ProgressListener() {
						@Override
						public void transferred(long num) {
							Log.i("fanjishuo_____upload", num+"");
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});

			// We use FileBody to transfer an image
			Log.i("fanjishuo_____doInBackground", params[0]+params[1]+params[2]+params[3]);
			StringBody sb1 = new StringBody(params[0]);
			StringBody sb2 = new StringBody(params[1]);
			StringBody sb3 = new StringBody(params[2]);
			StringBody sb4 = new StringBody(params[3]);
			multipartContent.addPart("image", new FileBody(new File(
					filePath)));
			mBean.filePath = filePath;
			mBean.fileSource = 0;
			mBean.taskId = Integer.parseInt(params[2]);
			multipartContent.addPart("yhid", sb1);
			multipartContent.addPart("yhmc", sb2);
			multipartContent.addPart("rwid", sb3);
			multipartContent.addPart("rwbt", sb4);
			totalSize = multipartContent.getContentLength();
			
			// Send it
			httpPost.setEntity(multipartContent);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			serverResponse = EntityUtils.toString(response.getEntity());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return serverResponse;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		pd.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(String result) {
		if(result == null || result.length() == 0){
			UItoolKit.showToastShort(context, "网络异常");
			return;
		}
		try {
			JSONObject json = new JSONObject(result);
			if(json.getBoolean("RESULT")){
				UItoolKit.showToastShort(context, "保存成功");
				mBean.fileState = 1;
				mBean.netId = json.getInt("WJID");
				OMUserDatabaseManager.getInstance(context).insertTaskAccessoryBean(mBean);
				listener.onUpLoadFinish(mBean,position);
			}else{
				UItoolKit.showToastShort(context, "保存失败");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		pd.dismiss();
		this.cancel(false);
	}

	@Override
	protected void onCancelled() {
		System.out.println("cancle");
	}

}
