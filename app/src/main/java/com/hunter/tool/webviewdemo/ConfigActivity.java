package com.hunter.tool.webviewdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/12/12 0012.
 */

public class ConfigActivity extends Activity {
    private final static String SP_KEY_INTERVAL = "interval";
    private final static String SP_KEY_BUY = "phone";
    private final static String SP_KEY_Phone = "phone222";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.config);

        final EditText etTime = (EditText)findViewById(R.id.etSearchIntervalTimer);
        etTime.setText(getIntervalTimer(this)+"");

//        final EditText etBuy = (EditText)findViewById(R.id.etBuy);
//        etBuy.setText(getBuy(this) + "");

        final EditText ePhone = (EditText)findViewById(R.id.etPhone);
        ePhone.setText(getPhone(this));

        Button btnEnter = (Button)findViewById(R.id.btnConfigEnter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = etTime.getText().toString();
                int iv = Integer.valueOf(value);

//                String buy = etBuy.getText().toString();
//                int iBuy = Integer.valueOf(buy);

                String phone = ePhone.getText().toString();

                SharedPreferences sp = getSharedPreferences("WEB_SP", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(SP_KEY_INTERVAL, iv);
//                editor.putInt(SP_KEY_BUY, iBuy);
                editor.putString(SP_KEY_Phone, phone);
                editor.commit();

                Toast.makeText(ConfigActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static int getIntervalTimer(Context context) {
        SharedPreferences sp = context.getSharedPreferences("WEB_SP", Context.MODE_PRIVATE);
        int value = sp.getInt(SP_KEY_INTERVAL, 0);

        return value;
    }

//    public static int getBuy(Context context) {
//        SharedPreferences sp = context.getSharedPreferences("WEB_SP", Context.MODE_PRIVATE);
//        int buy = sp.getInt(SP_KEY_BUY, 0);
//
//        return buy;
//    }

    public static String getPhone(Context context) {
        SharedPreferences sp = context.getSharedPreferences("WEB_SP", Context.MODE_PRIVATE);
        String phone = sp.getString(SP_KEY_Phone, "");

        return phone;
    }

}
