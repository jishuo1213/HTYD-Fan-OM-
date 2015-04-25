package com.htyd.fan.om.main;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.https.Urls;

public class OMApp extends Application {
	

	private  static HttpClient omHttpClient;
	private static OMApp sOMApp;
	
	public static OMApp getInstance(){
		return sOMApp;
	}
	
	@Override
	public void onCreate() {
		if (getCurProcessName(this).equals("com.htyd.fan.om")) {
			super.onCreate();
			Log.e("fanjishuo____onCreate", "appcreate----"
					+ getCurProcessName(this));
			sOMApp = this;
			initOmHttpClient();
			initBaseUrl();
		}
	}
	
private	String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}
	
	public  HttpClient getHttpClient(){
		if(omHttpClient == null){
			initOmHttpClient();
		}
		return omHttpClient;
	}
	
	private void initBaseUrl(){
		if (!TextUtils.isEmpty(Preferences.getServerAddress(this))) {
			new Urls(Preferences.getServerAddress(this));
		}
	}
	
	private  void initOmHttpClient() {

		if (null == omHttpClient) {
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams
					.setUserAgent(
							params,
							"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
									+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			// 超时设置
			/* 从连接池中取连接的时间 */
			ConnManagerParams.setTimeout(params, 1000);
			/* 连接超时 */
			int ConnectionTimeOut = 5000;

			HttpConnectionParams
					.setConnectionTimeout(params, ConnectionTimeOut);
			/* 请求超时 */
			HttpConnectionParams.setSoTimeout(params, 4000);
			// 设置httpClient支持http和https两种模式
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));

			// 使用线程安全的连接管理来创建httpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
					params, schReg);
			omHttpClient = new DefaultHttpClient(conMgr, params);
		}
	}
	
}
