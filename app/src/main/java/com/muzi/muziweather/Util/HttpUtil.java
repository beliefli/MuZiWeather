package com.muzi.muziweather.Util;

import com.muzi.muziweather.listener.HttpCallBackListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 11630 on 2017/12/8.
 */

public class HttpUtil {
    public static void sendHttpRequest(final String url, final HttpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL requestUrl = new URL(url);
                    connection = (HttpURLConnection) requestUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(30000);
                    connection.setReadTimeout(10000);
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    inputStream.close();
                    if (listener != null)
                        listener.succeed(stringBuffer.toString());

                } catch (Exception e) {
                    if (listener != null)
                        listener.failed(e.getMessage());
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }
        }).start();

    }
}
