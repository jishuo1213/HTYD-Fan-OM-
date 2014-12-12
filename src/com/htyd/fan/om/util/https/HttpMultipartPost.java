package com.htyd.fan.om.util.https;

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
import android.os.AsyncTask;
import android.util.Log;

import com.htyd.fan.om.util.https.CustomMultipartEntity.ProgressListener;
import com.htyd.fan.om.util.ui.UItoolKit;


public class HttpMultipartPost extends AsyncTask<String, Integer, String> {

	private Context context;
	private String filePath;
	private ProgressDialog pd;
	private long totalSize;

	public HttpMultipartPost(Context context, String filePath) {
		this.context = context;
		this.filePath = filePath;
	}

	@Override
	protected void onPreExecute() {
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("Uploading Picture...");
		pd.setCancelable(false);
		pd.show();
	}

	@Override
	protected String doInBackground(String... params) {
		String serverResponse = null;
		
		HttpClient httpClient = HttpHelper.getHttpClient(context);
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(Urls.UPLOADFILE);
		try {
			CustomMultipartEntity multipartContent = new CustomMultipartEntity(
					new ProgressListener() {
						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});

			// We use FileBody to transfer an image
			multipartContent.addPart("image", new FileBody(new File(
					filePath)));
			StringBody sb1 = new StringBody(params[0]);
			StringBody sb2 = new StringBody(params[1]);
			StringBody sb3 = new StringBody(params[2]);
			StringBody sb4 = new StringBody(params[3]);
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
		Log.i("fanjishuo____onPostExecute", result);
		try {
			JSONObject json = new JSONObject(result);
			if(json.getBoolean("RESULT")){
				UItoolKit.showToastShort(context, "保存成功");
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
