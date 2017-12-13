package com.muzi.muziweather.Util;

import android.text.TextUtils;

import com.muzi.muziweather.db.MuZiWeatherDB;
import com.muzi.muziweather.mode.City;
import com.muzi.muziweather.mode.County;
import com.muzi.muziweather.mode.Province;

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
}
