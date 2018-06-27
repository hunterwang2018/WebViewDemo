package com.hunter.tool.webviewdemo.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/12/18 0018.
 */

public class PhoneUtil {

    public void call(final Context context, String phoneNumber) {
        try {

            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
//            if(ContextCompat.checkSelfPermission(context,
//                    android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {

                // 开始直接拨打电话
                Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);

                Toast.makeText(context, "拨打电话！", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        try {
                            // 延迟2秒后自动挂断电话
                            // 首先拿到TelephonyManager
                            TelephonyManager telMag = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                            Class<TelephonyManager> c = TelephonyManager.class;

                            // 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象
                            Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
                            //允许访问私有方法
                            mthEndCall.setAccessible(true);
                            final Object obj = mthEndCall.invoke(telMag, (Object[]) null);

                            // 再通过ITelephony对象去反射里面的endCall方法，挂断电话
                            Method mt = obj.getClass().getMethod("endCall");
                            //允许访问私有方法
                            mt.setAccessible(true);
                            mt.invoke(obj);
                            Toast.makeText(context, "挂断电话！", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 2 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
