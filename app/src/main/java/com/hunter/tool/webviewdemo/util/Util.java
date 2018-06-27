package com.hunter.tool.webviewdemo.util;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/12/7 0007.
 */

public class Util {
    private final static String TAG = "Util";
    String urlBase = "http://m.58.com/sz/yingyou/?key=%E5%A5%B6%E7%B2%89&cmcskey=%E5%A5%B6%E7%B2%89&final=1&jump=1&sqa_type=0&newkey=%E5%A5%B6%E7%B2%89&formatsource=sou&from=list_yingyou_sou&keyfrom=list";

    private static String url0 = "http://m.58.com/sz/yingyou/?key=";
    private static String url1 = "&cmcskey=";
    private static String url2 = "&final=1&jump=1&sqa_type=0&newkey=";
    private static String url3 = "&formatsource=sou&from=list_yingyou_sou&keyfrom=list";
    public static String getSearchURL(String data) {
        String value = "";
        try {
            value = java.net.URLEncoder.encode(data, "utf-8");
            Log.i(TAG, "value1 = " + value);
//            String value =  java.net.URLDecoder.decode("%E5%A5%B6%E7%B2%89",   "utf-8");
//            Log.i(TAG, "value = " + value);
        } catch (Exception ex) {

        }

        value = url0 + value + url1 + value + url2 + value + url3;

        return value;
    }

    public static String getPrice(String data) {
        String[] aaa = data.split("￥");
        String price = aaa[1].split("\\s+")[0];
        return price;
    }



//    public static String priceUtil(String data) {
//        if(!data.contains("￥"))
//            return "";
//
//        String d1 = data.replaceAll(" ", "");
//        String value = d1.substring(d1.indexOf("￥")+1, d1.length());
//
//        return value;
//    }
}
