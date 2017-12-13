package com.muzi.muziweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muzi.muziweather.Util.DataUtil;
import com.muzi.muziweather.Util.HttpUtil;
import com.muzi.muziweather.db.MuZiWeatherDB;
import com.muzi.muziweather.listener.HttpCallBackListener;
import com.muzi.muziweather.mode.City;
import com.muzi.muziweather.mode.County;
import com.muzi.muziweather.mode.Province;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    TextView textView2;
    ListView lv_view;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> dataList = new ArrayList<>();
    private MuZiWeatherDB muZiWeatherDB;
    private int currentLevel;//当前选中的级别
    private int provinceLevel = 0;
    private int cityLevel = 1;
    private int countyLevel = 2;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City seletedCity;
    private County seletedCounty;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        lv_view=findViewById(R.id.lv_view);
        textView2=findViewById(R.id.textView2);
        muZiWeatherDB = MuZiWeatherDB.getInstance(this);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        lv_view.setAdapter(arrayAdapter);
        lv_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == provinceLevel) {
                    selectedProvince = provinceList.get(i);
                    queryCitysData();
                }
            }
        });
        queryProvincesData(); // 加载省级数据
    }

    /**
     * 查询某个全国所有的省份，优先从本地查询，查询不到则从网络进行查询
     */
    private void queryProvincesData() {
        provinceList = muZiWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProviceName());
            }
            arrayAdapter.notifyDataSetChanged();
            lv_view.setSelection(0);
            textView2.setText("中国");
            currentLevel = provinceLevel;
        } else {
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询某个省下面所有的市，优先从本地数据库查询，若本地没有则从网络查询
     */
    private void queryCitysData() {
        cityList = muZiWeatherDB.loadCitys(selectedProvince.getProviceId());
        if (cityList != null && cityList.size() > 0) {
            dataList.clear();
            for (City province : cityList) {
                String cityName = province.getCityName();
                dataList.add(cityName);
            }
            arrayAdapter.notifyDataSetChanged();
            lv_view.setSelection(0);
            currentLevel = cityLevel;
            return;
        }
        queryFromServer(selectedProvince.getProviceCode(), "city");
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCountiesData() {
        countyList = muZiWeatherDB.loadCountys(seletedCity.getCityId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            arrayAdapter.notifyDataSetChanged();
            lv_view.setSelection(0);
            textView2.setText(seletedCity.getCityName());
            currentLevel = countyLevel;
        } else {
            queryFromServer(seletedCity.getCityCode(), "county");
        }
    }

    private void queryFromServer(String code, final String leleveType) {
        String address;
        if (TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void succeed(String responseDate) {
                boolean result = false;
                if ("province".equals(leleveType)) {
                    result = DataUtil.isSuccessSaveProvinceDate(muZiWeatherDB, responseDate);
                } else if ("city".equals(leleveType)) {
                    result = DataUtil.isSuccessSaveCityDate(muZiWeatherDB, responseDate, selectedProvince.getProviceId());
                } else if ("county".equals(leleveType)) {
                    result = DataUtil.isSuccessSaveCountyDate(muZiWeatherDB, responseDate, seletedCity.getCityId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(leleveType)) {
                                queryProvincesData();
                            } else if ("city".equals(leleveType)) {
                                queryCitysData();
                            } else if ("county".equals(leleveType)) {
                                queryCountiesData();
                            }
                        }
                    });
                }
            }

            @Override
            public void failed(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentLevel == countyLevel) {
            queryCitysData();
        } else if (currentLevel == cityLevel) {
            queryProvincesData();
        } else {
            finish();
        }

    }
}