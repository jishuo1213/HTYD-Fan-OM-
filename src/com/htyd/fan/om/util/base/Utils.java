package com.htyd.fan.om.util.base;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;

import com.htyd.fan.om.util.https.Urls;


public class Utils {
	
	public static final int TASKMODULE = 7;
	public static final int ATTENDMODULE = 8;
	
	@SuppressLint("SimpleDateFormat")
	public static long parseDate(String source){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return	sdf.parse(source).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public static long parseDate(String source,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return	sdf.parse(source).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String formatTime(long time){
		return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
		.format(time);
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String formatTime(long time,String format){
		return new SimpleDateFormat(format).format(time);
	}
	
	public static int getCalendarField(long time, int field) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		return c.get(field);
	}
	
	public static String getAccessoryPath() {
		File sdCardDir = Environment.getExternalStorageDirectory();
		
		if (sdCardDir.exists()) {
			File mPictureDir = new File(sdCardDir + File.separator + "OmAccessory"+File.separator );
			if (!mPictureDir.exists()) {
				mPictureDir.mkdir();
			}
			return mPictureDir.getAbsolutePath();
		} else {
			File dir = Environment.getDataDirectory();
			File mPictureDir = new File(dir + File.separator  + "OmAccessory"+File.separator );
			if (!mPictureDir.exists()) {
				mPictureDir.mkdir();
			}
			return mPictureDir.getAbsolutePath();
		}	
	}
	
	public static String getCachePath() {
		File sdCardDir = Environment.getExternalStorageDirectory();
		
		if (sdCardDir.exists()) {
			File mPictureDir = new File(sdCardDir + File.separator + "OmCache"+File.separator);
			if (!mPictureDir.exists()) {
				mPictureDir.mkdir();
			}
			return mPictureDir.getAbsolutePath();
		} else {
			File dir = Environment.getDataDirectory();
			File mPictureDir = new File(dir + File.separator  + "OmCache"+File.separator );
			if (!mPictureDir.exists()) {
				mPictureDir.mkdir();
			}
			return mPictureDir.getAbsolutePath();
		}	
	}
	
	public static boolean deleteFile(String filePath){
		File file = new File(filePath);
		if(file.exists()){
			return file.delete();
		}
		return false;
	}
	
	public static boolean isAccessoryFileExist(String fileName){
		File file = new File(Urls.ACCESSORYFILEPATH +File.separator+ fileName);
	    return (file.exists() && file.isFile());
	}
	
	public static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
	
	public static boolean isNetWorkEnable() {
		return !Preferences.netType.equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT);
	}
	
	public static void back(){
		ThreadPool.runMethod(new Runnable() {
			@Override
			public void run() {
				finishActivity();
			}
		});
	}
	
	private static void finishActivity() {
		try {
			Instrumentation inst = new Instrumentation();
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
	//		fragment.getFragmentManager().beginTransaction().remove(fragment).commit();
		} catch (Exception e) {
			Log.e("Exception when onBack", e.toString());
		}
	}
		/**
		 * 通过设置Camera打开闪光灯
		 * @param mCamera
		 */
		public static void turnLightOn(Camera mCamera) {
			if (mCamera == null) {
				return;
			}
			Parameters parameters = mCamera.getParameters();
			if (parameters == null) {
				return;
			}
		List<String> flashModes = parameters.getSupportedFlashModes();
			// Check if camera flash exists
			if (flashModes == null) {
				// Use the screen as a flashlight (next best thing)
				return;
			}
			String flashMode = parameters.getFlashMode();
			if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
				// Turn on the flash
				if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
					parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
					mCamera.setParameters(parameters);
				} else {
				}
			}
		}
		/**
		 * 通过设置Camera关闭闪光灯
		 * @param mCamera
		 */
		public static void turnLightOff(Camera mCamera) {
			if (mCamera == null) {
				return;
			}
			Parameters parameters = mCamera.getParameters();
			if (parameters == null) {
				return;
			}
			List<String> flashModes = parameters.getSupportedFlashModes();
			String flashMode = parameters.getFlashMode();
			// Check if camera flash exists
			if (flashModes == null) {
				return;
			}
			if (!Parameters.FLASH_MODE_OFF.equals(flashMode)) {
				// Turn off the flash
				if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
					mCamera.setParameters(parameters);
				} 
			}
		}
}
