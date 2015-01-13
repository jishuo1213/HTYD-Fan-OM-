package com.htyd.fan.om.util.https;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.text.TextUtils;

import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.model.AttendBean;
import com.htyd.fan.om.model.CityBean;
import com.htyd.fan.om.model.DistrictBean;
import com.htyd.fan.om.model.ProvinceBean;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.model.TaskProcessBean;
import com.htyd.fan.om.model.CommonDataBean;
import com.htyd.fan.om.util.db.OMDatabaseManager;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;

/**
 * 解析服务器返回的数据并保存存到数据库的类
 * @author fan
 *
 */

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
	 */
	public  static boolean handleProvincesResponse(
			OMDatabaseManager mManager, String response) {
		mManager.openDb(1);
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					ProvinceBean mBean = new ProvinceBean();
					mBean.provinceCode = array[0];
					if(array.length >= 1)
						mBean.provinceName = array[1];
					// 将解析出来的数据存储到Province表
					mManager.insertProvince(mBean);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public static boolean handleCitiesResponse(OMDatabaseManager mManager,
			String response, int provinceId) {
		mManager.openDb(1);
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					CityBean city = new CityBean();
					city.cityCode = array[0];
					if(array.length >= 1)
						city.cityName = array[1];
					city.provinceID = provinceId;
					// 将解析出来的数据存储到City表
					mManager.insertCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean handleCountiesResponse(OMDatabaseManager mManager,
			String response, int cityId) {
		mManager.openDb(1);
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					DistrictBean county = new DistrictBean();
					county.districtCode = array[0];
					if(array.length >= 1)
						county.districtName = array[1];
					county.cityID = cityId;
					// 将解析出来的数据存储到County表
					mManager.insertDistrict(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析返回的任务数据
	 * @param mManager
	 * @param response
	 * @return
	 * @throws JSONException
	 */
	public static  boolean handleTaskResponse(OMUserDatabaseManager mManager,
			String response) throws JSONException {
		mManager.openDb(1);
		if (!TextUtils.isEmpty(response)) {
			TaskDetailBean mBean = new TaskDetailBean();
			JSONObject resultJson = new JSONObject(response);
			JSONArray array = (JSONArray) new JSONTokener(resultJson.getString("Rows")).nextValue();
			for(int i = 0;i<array.length();i++){
				mBean.setFromJson(array.getJSONObject(i));
				mManager.insertTaskBean(mBean);
			}
				return true;
			}
		return false;
	}
	
	public static boolean handleTaskResponse(String response,
			ArrayList<TaskDetailBean> taskList) throws JSONException {
		if (!TextUtils.isEmpty(response)) {
			JSONObject resultJson = new JSONObject(response);
			JSONArray array = (JSONArray) new JSONTokener(
					resultJson.getString("Rows")).nextValue();
			for (int i = 0; i < array.length(); i++) {
				TaskDetailBean mBean = new TaskDetailBean();
				mBean.setFromJson(array.getJSONObject(i));
				taskList.add(mBean);
			}
			return true;
		}
		return false;
	}
	
	
	/**
	 * 解析返回的任务处理数据
	 * @param mManager
	 * @param response
	 * @return
	 * @throws JSONException
	 */
	
	public static boolean handleTaskProcessResponse(OMUserDatabaseManager mManager,
			String response) throws JSONException {
		mManager.openDb(1);
		if (!TextUtils.isEmpty(response)) {
			TaskProcessBean mBean = new TaskProcessBean();
			JSONObject resultJson = new JSONObject(response);
			JSONArray array = (JSONArray) new JSONTokener(resultJson.getString("Rows")).nextValue();
			for(int i = 0;i<array.length();i++){
				mBean.setFromJson(array.getJSONObject(i));
				mManager.insertTaskProcessBean(mBean);
			}
				return true;
			}
		return false;
	}
	
	public static boolean handleTaskProcessResponse(String response,ArrayList<TaskProcessBean> processList
			) throws JSONException {
		if (!TextUtils.isEmpty(response)) {
			JSONObject resultJson = new JSONObject(response);
			JSONArray array = (JSONArray) new JSONTokener(resultJson.getString("Rows")).nextValue();
			for(int i = 0;i<array.length();i++){
				TaskProcessBean mBean = new TaskProcessBean();
				mBean.setFromJson(array.getJSONObject(i));
				processList.add(mBean);
			}
				return true;
			}
		return false;
	}
	
	/**
	 * 解析返回的签到数据
	 * @param mManager
	 * @param response
	 * @throws Exception
	 */
	
	public static void handleAttend(OMUserDatabaseManager mManager,
			String response) throws Exception {
		mManager.openDb(1);
		if (!TextUtils.isEmpty(response)) {
			AttendBean mBean = new AttendBean();
			JSONObject resultJson = new JSONObject(response);
			JSONArray array = (JSONArray) new JSONTokener(
					resultJson.getString("Rows")).nextValue();
			for (int i = 0; i < array.length(); i++) {
				mBean.setFromJson(array.getJSONObject(i));
				mManager.insertAttendBean(mBean);
			}
		} else {
			throw new Exception("错误");
		}
	}
	
	/**
	 * 解析返回的附件列表数据
	 * @param mManager
	 * @param response
	 * @param taskId
	 * @throws Exception
	 */
	
	public static void handleAccessory(OMUserDatabaseManager mManager,
			String response,int taskId) throws Exception {
		mManager.openDb(1);
		if (!TextUtils.isEmpty(response)) {
			AffiliatedFileBean mBean = new AffiliatedFileBean();
			JSONArray array = (JSONArray) new JSONTokener(
					response).nextValue();
			for (int i = 0; i < array.length(); i++) {
				mBean.setFromJson(array.getJSONObject(i),taskId);
				mManager.insertTaskAccessoryBean(mBean);
			}
		} else {
			throw new Exception("错误");
		}
	}
	
	public static void handleAccessory(ArrayList<AffiliatedFileBean> accessoryList,
			String response,int taskId) throws Exception {
		if (!TextUtils.isEmpty(response)) {
			JSONArray array = (JSONArray) new JSONTokener(
					response).nextValue();
			for (int i = 0; i < array.length(); i++) {
				AffiliatedFileBean mBean = new AffiliatedFileBean();
				mBean.setFromJson(array.getJSONObject(i),taskId);
				accessoryList.add(mBean);
			}
		} else {
			throw new Exception("错误");
		}
	}
	
	public static boolean handleTaskType(OMDatabaseManager mManager,String result, ArrayList<CommonDataBean> typeList) throws JSONException{
		if(!TextUtils.isEmpty(result)){ 
			mManager.openDb(1);
			JSONObject resultJson = new JSONObject(result);
			JSONArray array = (JSONArray) new JSONTokener(
					resultJson.getString("Rows")).nextValue();
			for (int i = 0; i < array.length(); i++) {
				CommonDataBean mBean = new CommonDataBean();
				typeList.add(mBean);
				mBean.setFromJson(array.getJSONObject(i));
				mManager.insertTaskType(mBean);
			}
			return true;
		}else{
			return false;
		}
	}
}