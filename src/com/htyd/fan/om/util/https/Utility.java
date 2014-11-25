package com.htyd.fan.om.util.https;

import android.text.TextUtils;

import com.htyd.fan.om.model.CityBean;
import com.htyd.fan.om.model.DistrictBean;
import com.htyd.fan.om.model.ProvinceBean;
import com.htyd.fan.om.util.db.OMDatabaseManager;

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
					mBean.provinceName = array[1];
					mBean.provinceCode = array[0];
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
}