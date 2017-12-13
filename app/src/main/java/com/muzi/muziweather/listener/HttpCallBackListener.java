package com.muzi.muziweather.listener;

/**
 * Created by 11630 on 2017/12/8.
 */

public interface HttpCallBackListener {
    void succeed(String responseDate);
    void failed(String error);
}
