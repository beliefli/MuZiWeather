package com.muzi.muziweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.muzi.muziweather.mode.City;
import com.muzi.muziweather.mode.County;
import com.muzi.muziweather.mode.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11630 on 2017/12/8.
 */

public class MuZiWeatherDB {
    public static final String DB_NAME = "MuZiWheatherSQ";//数据库名称
    public static final int SQ_VERSION = 1;//数据库版本号
    private SQLiteDatabase db;
    public static MuZiWeatherDB muZiWeatherDB;

    /**
     * 以单例模式创建该类的对象,私有构造方法
     */
    private MuZiWeatherDB(Context context) {
        MuZiWheatherOpenHelper helper = new MuZiWheatherOpenHelper(context, DB_NAME, null, SQ_VERSION);
        db = helper.getWritableDatabase();
    }

    public static MuZiWeatherDB getInstance(Context context) {
        if (muZiWeatherDB == null) {
            synchronized (MuZiWeatherDB.class) {
                if (muZiWeatherDB == null) {
                    muZiWeatherDB = new MuZiWeatherDB(context);
                }
            }
        }
        return muZiWeatherDB;
    }

    /**
     * 将全国省份信息保存到数据库
     */
    public void saveProvinceData(Province province) {
        if (province == null)
            return;
        ContentValues values = new ContentValues();
        values.put("province_name", province.getProviceName());
        values.put("province_code", province.getProviceCode());
        db.insert("Province", null, values);
    }

    /**
     * 获取数据库保存的全国所有省份信息
     */
    public List<Province> loadProvinces() {
        List<Province> provinceList = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setProviceId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProviceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProviceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinceList.add(province);
            } while (cursor.moveToNext());
        }
        return provinceList;
    }

    /**
     * 将每个省下面的城市保存到数据库
     */
    public void saveCityData(City city) {
        if (city == null)
            return;
        ContentValues values = new ContentValues();
        values.put("city_name", city.getCityName());
        values.put("city_code", city.getCityCode());
        values.put("province_id", city.getProvinceId());
        db.insert("City", null, values);
    }

    /**
     * 获取省份下面的城市
     */
    public List<City> loadCitys(int provinceId) {
        List<City> cityList = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                cityList.add(city);
            } while (cursor.moveToNext());
        }
        return cityList;
    }

    /**
     * 将城市下面的区县保存到数据库
     */
    public void saveCountyData(County county) {
        if (county == null)
            return;
        ContentValues values = new ContentValues();
        values.put("county_name", county.getCountyName());
        values.put("county_code", county.getCountyCode());
        values.put("city_id", county.getCityId());
        db.insert("County", null, values);
    }

    /**
     * 获取每个城市下面的区县
     */
    public List<County> loadCountys(int cityId) {
        List<County> countyList = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setCountyId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                countyList.add(county);
            } while (cursor.moveToNext());
        }
        return countyList;
    }
}
