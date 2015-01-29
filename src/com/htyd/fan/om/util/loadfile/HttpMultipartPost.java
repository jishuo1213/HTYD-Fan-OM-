package com.htyd.fan.om.util.loadfile;

import java.io.File;
import java.nio.charset.Charset;

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

import com.htyd.fan.om.main.OMApp;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.loadfile.CustomMultipartEntity.ProgressListener;
import com.htyd.fan.om.util.ui.UItoolKit;


public class HttpMultipartPost extends AsyncTask<String, Integer, String> {

	private Context context;
	private String filePath;
	private ProgressDialog pd;
	private long totalSize;
	private UpLoadFinishListener listener;
	private int position;
	
	public interface UpLoadFinishListener{
		public void onUpLoadFinish(int fileId,int position);
	}

	public HttpMultipartPost(Context context, String filePath,UpLoadFinishListener listener) {
		this.context = context;
		this.filePath = filePath;
		this.listener = listener;
		position = -1;
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
		HttpClient httpClient = OMApp.getInstance().getHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(Urls.UPLOADFILE);
		position = Integer.parseInt(params[4]);
		try {
			CustomMultipartEntity multipartContent = new CustomMultipartEntity(
					new ProgressListener() {
						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});

			// We use FileBody to transfer an image
			StringBody sb1 = new StringBody(params[0],Charset.defaultCharset());
			StringBody sb2 = new StringBody(params[1],Charset.defaultCharset());
			StringBody sb3 = new StringBody(params[2],Charset.defaultCharset());
			StringBody sb4 = new StringBody(params[3],Charset.defaultCharset());
			StringBody sb5 = new StringBody(Utils.TASKMODULE+"");
			StringBody sb6 = new StringBody(params[5],Charset.defaultCharset());
			StringBody sb7 = new StringBody(params[6]);
			
			multipartContent.addPart("image", new FileBody(new File(
					filePath)));
			multipartContent.addPart("yhid", sb1);
			multipartContent.addPart("yhmc", sb2);
			multipartContent.addPart("rwid", sb3);
			multipartContent.addPart("rwbt", sb4);
			multipartContent.addPart("rz_mkid", sb5);
			multipartContent.addPart("lbmc", sb6); 
			Log.i("fanjishuo____doInBackground", params[4]);
			multipartContent.addPart("tempid", sb7);
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
				listener.onUpLoadFinish(json.getInt("WJID"),position);
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
