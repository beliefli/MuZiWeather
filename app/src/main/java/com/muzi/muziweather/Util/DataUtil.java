package com.muzi.muziweather.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.muzi.muziweather.db.MuZiWeatherDB;
import com.muzi.muziweather.mode.City;
import com.muzi.muziweather.mode.County;
import com.muzi.muziweather.mode.Province;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 11630 on 2017/12/8.
 */

public class DataUtil {
    /**
     * 解析并存储网络请求回来的省会信息数据
     */
    public synchronized static boolean isSuccessSaveProvinceDate(MuZiWeatherDB db, String response) {
        if (TextUtils.isEmpty(response))
            return false;
        String[] provinces = response.split(",");
        if (provinces == null || provinces.length == 0)
            return false;
        for (String province : provinces) {
            String[] proviceInfo = province.split("\\|");
            String provinceCode = proviceInfo[0];
            String provinceName = proviceInfo[1];
            Province pc = new Province();
            pc.setProviceName(provinceName);
            pc.setProviceCode(provinceCode);
            db.saveProvinceData(pc);
        }
        return true;
    }

    /**
     * 解析并存储网络请求回来的城市信息数据
     */
    public synchronized static boolean isSuccessSaveCityDate(MuZiWeatherDB db, String response, int provinceId) {
        if (TextUtils.isEmpty(response))
            return false;
        String[] citys = response.split(",");
        if (citys == null || citys.length == 0)
            return false;
        for (String city : citys) {
            String[] cityInfo = city.split("\\|");
            String cityCode = cityInfo[0];
            String cityName = cityInfo[1];
            City cy = new City();
            cy.setCityCode(cityCode);
            cy.setCityName(cityName);
            cy.setProvinceId(provinceId);
            db.saveCityData(cy);
        }
        return true;
    }

    /**
     * 解析并存储网络请求回来的区县信息数据
     */
    public synchronized static boolean isSuccessSaveCountyDate(MuZiWeatherDB db, String response, int cityId) {
        if (TextUtils.isEmpty(response))
            return false;
        String[] countys = response.split(",");
        if (countys == null || countys.length == 0)
            return false;
        for (String county : countys) {
            String[] countyInfo = county.split("\\|");
            String countyCode = countyInfo[0];
            String countyName = countyInfo[1];
            County cy = new County();
            cy.setCountyCode(countyCode);
            cy.setCountyName(countyName);
            cy.setCityId(cityId);
            db.saveCountyData(cy);
        }
        return true;
    }

    /**
     * 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        } catch (Exception e) {
        }
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
