package com.muzi.muziweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MuZi on 2017/12/8.
 */

public class MuZiWheatherOpenHelper extends SQLiteOpenHelper {
    /**
     * 省会表的创建语句
     */
    public static final String CREATE_PROVINCE = "create table Province("
            + "id integer primary key autoincrement,"
            + "province_name text,"
            + "province_code text)";
    /**
     * 城市表的创建语句
     */
    public static final String CITY_PROVINCE = "create table City("
            + "id integer primary key autoincrement,"
            + "city_name text,"
            + "city_code text,"
            + "province_id integer)";
    /**
     * 区域表的创建语句
     */
    public static final String COUNTY_PROVINCE = "create table County("
            + "id integer primary key autoincrement,"
            + "county_name text,"
            + "county_code text,"
            + "city_id integer)";

    public MuZiWheatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);//创建省会表
        sqLiteDatabase.execSQL(CITY_PROVINCE);//创建省会下城市表
        sqLiteDatabase.execSQL(COUNTY_PROVINCE);//创建城市下区县表
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
