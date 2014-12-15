package com.htyd.fan.om.util.loadfile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.htyd.fan.om.util.loadfile.DownThread.DataTransferListener;

public class DownLoadManager {
	
	private  int fileSize;
	private static DownLoadManager sDownUtil;
	private int downloadSum;
	
	private DownLoadManager(){
		
	}
	
	public static DownLoadManager getInstance(){
		if(sDownUtil == null){
			sDownUtil = new DownLoadManager();
		}
		return sDownUtil;
	}
	
	public  void download(String fileUrl,String targetFile) throws IOException{
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
	};
}
