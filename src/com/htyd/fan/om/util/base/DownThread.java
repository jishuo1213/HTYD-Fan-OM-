package com.htyd.fan.om.util.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownThread extends Thread{

	private int startPos; //当前线程的下载开始位置
	private int currentPartSize;//当前线程负责下载的文件大小
	private RandomAccessFile currentPart;//当前线程需要现在的文件块
	public int length;//该线程已下载的字节数
	private String fileUrl;
	private DataTransferListener listener;
	private int threadNum;
	
	public interface DataTransferListener{
		public void OnDataTransfer(int length,int threadNum);
	}
	
	public DownThread(String fileUrl, int startPos, int currentPartSize,
			RandomAccessFile currentPart, DataTransferListener listener,int threadNum) {
		this.startPos = startPos;
		this.currentPartSize = currentPartSize;
		this.currentPart = currentPart;
		this.fileUrl = fileUrl;
		this.listener = listener;
		this.threadNum = threadNum;
	}
	@Override
	public void run() {
		URL url;
		InputStream inputStream = null;
		try {
			 url = new URL(fileUrl);
			 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			 conn.setConnectTimeout(10000);
			 conn.setRequestMethod("GET");
			 conn.setRequestProperty("Accept", "image/jpeg,image/jpg");
			 conn.setRequestProperty("Accept-Language", "zh-CN,en-US");
			 conn.setRequestProperty("Charset", "UTF-8");
			 inputStream = conn.getInputStream();
			 inputStream.skip(startPos);
			 byte[] buffer = new byte[2048];
			 int hasRead = 0;
			 while (length < currentPartSize && (hasRead = inputStream.read(buffer)) > 0){
				 currentPart.write(buffer, 0, hasRead);
				 length += hasRead;
				 listener.OnDataTransfer(hasRead,threadNum);
			 }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				currentPart.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
