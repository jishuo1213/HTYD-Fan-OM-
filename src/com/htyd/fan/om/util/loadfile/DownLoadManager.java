package com.htyd.fan.om.util.loadfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;

import com.htyd.fan.om.util.https.HttpHelper;


public class DownLoadManager {
	
	/*private  int fileSize;
	private int downloadSum;*/
	private static DownLoadManager sDownUtil;
	private DataTransferListener mListener;
	
	private DownLoadManager(){
		
	}
	
	public interface DataTransferListener{
		public void OnDataTransfer(long length);
	}
	
	public static DownLoadManager getInstance(){
		if(sDownUtil == null){
			sDownUtil = new DownLoadManager();
		}
		return sDownUtil;
	}
	
	public boolean downLoadFromNet(Context context, JSONObject param, String Url,
			String operate, String targetFile) {
		NameValuePair nameParams = new BasicNameValuePair("Params",
				param.toString());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(nameParams);
		UrlEncodedFormEntity urlEncoded;
		FileOutputStream fos = null;
		InputStream in = null;
		long sum = 0;
		try {
			urlEncoded = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			HttpPost httpPost = new HttpPost(Url + operate);
			httpPost.setEntity(urlEncoded);
			HttpClient client = HttpHelper.getHttpClient(context);
			HttpResponse response = client.execute(httpPost);
			int res = response.getStatusLine().getStatusCode();
			if(res != HttpStatus.SC_OK){
				return false;
			}
			HttpEntity resEntity = response.getEntity();
			in = resEntity.getContent();
			File file = new File(targetFile);
			byte[] buffer = new byte[2048];
			fos = new FileOutputStream(file);
			int l = -1;
			while ((l = in.read(buffer)) != -1) {
				fos.write(buffer, 0, l);
				sum += l;
				mListener.OnDataTransfer(sum);
			}
			fos.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				fos.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public void setListener(DataTransferListener mListener){
		this.mListener = mListener;
	}
	
/*	public  void download(String fileUrl,String targetFile) throws IOException{
		getFileSize(fileUrl);
		int currentPartSize = 0;
		int moreByte = 0;
		int threadNum = 2;
		if (fileSize % threadNum == 0) {
			currentPartSize = fileSize / threadNum;
		}
		for (int i = 1; i < threadNum; i++) {
			if (fileSize % threadNum == i) {
				currentPartSize = (fileSize + threadNum - i) / threadNum;
				moreByte = threadNum - i;
				break;
			}
		}
		RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
		file.setLength(fileSize);
		file.close();
		for(int i = 0; i < threadNum;i++){
			int startPos = i * currentPartSize;
			RandomAccessFile currentPart = new RandomAccessFile(targetFile,"rw");
			currentPart.seek(startPos);
			if(i == threadNum - 1){
				new DownThread(fileUrl,startPos, currentPartSize - moreByte, currentPart,listener).start();
			}else{
				new DownThread(fileUrl,startPos, currentPartSize, currentPart,listener).start();
			}
		}
}
	
	public void upLoad(String fileUrl,String uploadFile){
		
	}
	
	public  int getFileSize(String fileUrl) throws IOException {
		if (fileSize == 0) {
			URL url = new URL(fileUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "image/jpeg,image/jpg");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Connection", "Keep-Alive");
			fileSize = conn.getContentLength();
			conn.disconnect();
			return fileSize;
		} else {
			return fileSize;
		}
	}
	
	public int getDownLoadNum(){
		return downloadSum;
	}

	public boolean isComplete(){
		return downloadSum >= fileSize;
	}
	
	private  DataTransferListener listener = new DataTransferListener() {
		
		@Override
		public void OnDataTransfer(int length) {
			downloadSum += length;
		}
	};*/
}
