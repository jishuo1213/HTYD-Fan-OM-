package com.htyd.fan.om.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.os.Handler;

import com.htyd.fan.om.main.OMApp;
import com.htyd.fan.om.util.https.Urls;

public class UpLoadThread extends Thread {

	public static final int MESSAGEWHAT = 0x1213;
	public static final String SERVERRES = "result";
	public static final String FILEPATH = "filepath";
	public static final String THREADNUM = "threadnum";
	
	private String filePath;
	private Handler handler;
	private int num;
	
	
	public UpLoadThread() {
	}
	
	@Override
	public void run() {
		String serverResponse = null;
		HttpClient httpClient = OMApp.getInstance().getHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(Urls.UPLOADFILE);
		
		MultipartEntity multipartEntity = new MultipartEntity();
		StringBody sb1;
		Bundle bundle = new Bundle();
		bundle.putInt(THREADNUM, num);
		try {
			sb1 = new StringBody("",Charset.defaultCharset());
			StringBody sb2 = new StringBody("",Charset.defaultCharset());
			StringBody sb3 = new StringBody("",Charset.defaultCharset());
			StringBody sb4 = new StringBody("",Charset.defaultCharset());
			StringBody sb5 = new StringBody("11");
			multipartEntity.addPart("image", new FileBody(new File(filePath)));
			
			multipartEntity.addPart("yhid", sb1);
			multipartEntity.addPart("yhmc", sb2);
			multipartEntity.addPart("rwid", sb3);
			multipartEntity.addPart("rwbt", sb4);
			multipartEntity.addPart("rz_mkid", sb5);
			httpPost.setEntity(multipartEntity);
			
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			serverResponse = EntityUtils.toString(response.getEntity());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			bundle.putString(SERVERRES, "");
			handler.obtainMessage(MESSAGEWHAT,bundle).sendToTarget();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			bundle.putString(SERVERRES, "");
			handler.obtainMessage(MESSAGEWHAT,bundle).sendToTarget();
		} catch (IOException e) {
			e.printStackTrace();
			bundle.putString(SERVERRES, "");
			handler.obtainMessage(MESSAGEWHAT,bundle).sendToTarget();
		}
		bundle.putString(SERVERRES, serverResponse);
		handler.obtainMessage(MESSAGEWHAT, bundle).sendToTarget();
	}
	public void setParam(String filePath,Handler handler,int num){
		this.filePath = filePath;
		this.handler = handler;
		this.num = num;
	}
}
