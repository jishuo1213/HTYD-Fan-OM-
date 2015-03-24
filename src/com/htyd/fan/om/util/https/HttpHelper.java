package com.htyd.fan.om.util.https;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
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
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.htyd.fan.om.main.OMApp;
import com.htyd.fan.om.util.ui.UItoolKit;

public class HttpHelper {

	private static final String CHARSET_UTF8 = HTTP.UTF_8;
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 1024;
	private static final int DEFAULT_SOCKET_TIMEOUT = 8000;
	private static final int DEFAULT_HOST_CONNECTIONS = 2;
	private static final int DEFAULT_MAX_CONNECTIONS = 2;

	private HttpHelper() {

	}

	/**
	 * 
	 * @param context
	 * @param url
	 * @param nameValuePairs
	 * @return
	 */

	public static String GetResponse(final Context context, final String url,
			final NameValuePair... nameValuePairs){
		String strResult;

		strResult = "";

		FutureTask<String> task = new FutureTask<String>(
				new Callable<String>() {
					@Override
					public String call() throws Exception {
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						if (nameValuePairs != null) {
							for (int i = 0; i < nameValuePairs.length; i++) {
								params.add(nameValuePairs[i]);
							}
							Log.d("fanjishuo_____GetResponse", params.toString());
						}
						
						UrlEncodedFormEntity urlEncoded = new UrlEncodedFormEntity(
								params, CHARSET_UTF8);
						
						Log.d("fanjishuo____GetResponse", url);
						HttpPost httpPost = new HttpPost(url);
						httpPost.setEntity(urlEncoded);

						if (isNetWorkAvailable(context) == true) {
							HttpClient client = OMApp.getInstance().getHttpClient();// >>>
							HttpResponse response = client.execute(httpPost);// >>>
							int res = response.getStatusLine().getStatusCode();
							if (res != HttpStatus.SC_OK) {
//								throw new RuntimeException("请求失败");
								return "false";
							}
							HttpEntity resEntity = response.getEntity();
							return (resEntity == null) ? "false" : EntityUtils
									.toString(resEntity, CHARSET_UTF8);
						} else {
							UItoolKit.showToastShort(context, "检查网络连接先");
							return "false";
						}
					}
				});
		new Thread(task).start();
		try {
			strResult = task.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return "false";
		} catch (ExecutionException e) {
			e.printStackTrace();
			if (e.getCause().getClass().equals(ConnectTimeoutException.class)) {
				UItoolKit.showToastShort(context, "网络连接超时");
				return "false";
			} else {
				return "false";
			}
		}
		Log.d("fanjishuo____GetResponse", strResult);
		return strResult;
	}
	
	public static String GetResponsenew(Context context, String url,
			NameValuePair... nameValuePairs) {
		String strResult;
		strResult = "";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (nameValuePairs != null) {
			for (int i = 0; i < nameValuePairs.length; i++) {
				params.add(nameValuePairs[i]);
			}
		}
		UrlEncodedFormEntity urlEncoded = null;
		try {
			urlEncoded = new UrlEncodedFormEntity(params, CHARSET_UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			strResult = "false";
		}

		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(urlEncoded);

		if (isNetWorkAvailable(context) == true) {
			HttpClient client = OMApp.getInstance().getHttpClient();// >>>
			HttpResponse response = null;
			try {
				response = client.execute(httpPost);
			} catch (IOException e) {
				e.printStackTrace();
				strResult = "false";
			}// >>>
			int res = response.getStatusLine().getStatusCode();
			if (res != HttpStatus.SC_OK) {
				// throw new RuntimeException("请求失败");
				strResult = "false";
			}
			HttpEntity resEntity = response.getEntity();
			try {
				strResult = (resEntity == null) ? "false" : EntityUtils
						.toString(resEntity, CHARSET_UTF8);
			} catch (ParseException | IOException e) {
				e.printStackTrace();
				strResult = "false";
			}
		} else {
			strResult = "false";
		}
		return strResult;
	}

/*	private static HttpClient getHttpClient(Context context) {
		if (null == customerHttpClient) {
			Log.e("fanjishuo____getHttpClient", "new client");
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, CHARSET_UTF8);
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams
					.setUserAgent(
							params,
							"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
									+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			// 超时设置
			 从连接池中取连接的时间 
			ConnManagerParams.setTimeout(params, 1000);
			 连接超时 
			int ConnectionTimeOut = 3000;

			HttpConnectionParams
					.setConnectionTimeout(params, ConnectionTimeOut);
			 请求超时 
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
			customerHttpClient = new DefaultHttpClient(conMgr, params);
		}
		return customerHttpClient;
	}*/

	public static boolean isNetWorkAvailable(Context context) {
		// ConnectivityManager:主要管理和网络连接相关的操作
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
		// 获取代表联网状态的NetWorkInfo对象
		NetworkInfo info = cm.getActiveNetworkInfo();
		boolean status = info != null && info.isConnected();
		return status;
	}
	
	private static HttpClient httpClient;
	
	@SuppressWarnings("unused")
	private static synchronized HttpClient getHttpClient() {
		if (httpClient == null) {
			final HttpParams httpParams = new BasicHttpParams();

			// timeout: get connections from connection pool
			ConnManagerParams.setTimeout(httpParams, 1000);
			// timeout: connect to the server
			HttpConnectionParams.setConnectionTimeout(httpParams,
					DEFAULT_SOCKET_TIMEOUT);
			// timeout: transfer data from server
			HttpConnectionParams.setSoTimeout(httpParams,
					DEFAULT_SOCKET_TIMEOUT);

			// set max connections per host
			ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
					new ConnPerRouteBean(DEFAULT_HOST_CONNECTIONS));
			// set max total connections
			ConnManagerParams.setMaxTotalConnections(httpParams,
					DEFAULT_MAX_CONNECTIONS);

			// use expect-continue handshake
			HttpProtocolParams.setUseExpectContinue(httpParams, true);
			// disable stale check
			HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);

			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);

			HttpClientParams.setRedirecting(httpParams, false);

			// set user agent
			String userAgent = 	"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
					+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1";
			HttpProtocolParams.setUserAgent(httpParams, userAgent);

			// disable Nagle algorithm
			HttpConnectionParams.setTcpNoDelay(httpParams, true);

			HttpConnectionParams.setSocketBufferSize(httpParams,DEFAULT_SOCKET_BUFFER_SIZE);

			// scheme: http and https
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

			ClientConnectionManager manager = new ThreadSafeClientConnManager(
					httpParams, schemeRegistry);
			httpClient = new DefaultHttpClient(manager, httpParams);
		}
		return httpClient;
	}
	
	
}
