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

import com.htyd.fan.om.main.OMApp;
import com.htyd.fan.om.util.https.Urls;

public class UpLoadThread extends Thread {

	private String filePath;

	public UpLoadThread(String filePath) {
		this.filePath = filePath;
	}
	@Override
	public void run() {
		String serverResponse = null;
		HttpClient httpClient = OMApp.getInstance().getHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(Urls.UPLOADFILE);
		
		MultipartEntity multipartEntity = new MultipartEntity();
		StringBody sb1;
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
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
