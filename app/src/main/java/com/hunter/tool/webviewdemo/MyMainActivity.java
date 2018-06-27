package com.hunter.tool.webviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.hunter.tool.webviewdemo.database.SearchInstance;
import com.hunter.tool.webviewdemo.util.BaseVOUtil;
import com.hunter.tool.webviewdemo.util.HtmlParse;
import com.hunter.tool.webviewdemo.util.MatchUtil;
import com.hunter.tool.webviewdemo.util.NotifacationUtil;
import com.hunter.tool.webviewdemo.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Administrator on 2017/12/12 0012.
 */

public class MyMainActivity extends Activity implements HtmlParse.ParseListener{
    private final static String TAG = "MyMainActivity";
    private final static int DEFALUT_SEARCH_TIME = 5;
    private final static int DEFALUT_BUY_TIME = 2;
    private static WebView mWebView;

    /**
     * 当前查询
     */
    private static String mCurrSearch = "";
    /**
     * 当前查询Item
     */
    private static int mCurrSearchItem = 0;

    //查询到的数据
    private ArrayList<BaseVO> mBaseArrLst = new ArrayList<BaseVO>();
    private HashMap<Integer, Integer> mBaseArrLstItemMps = new HashMap<>();

    private static boolean mStop = false;

    private ArrayList<SearchInstance> mSearchDatas = new ArrayList<>();
    private HashMap<Integer, ArrayList<BaseVO>> mSearchMaps = new HashMap<>();

    private static boolean mCallPhoneFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try {
//            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:10086"));
//            startActivity(intent);
//        } catch (SecurityException ex) {
//            ex.printStackTrace();
//        }

//        String data1 = "一段900G置 请走转转担保交易，喜欢的话就赶快联系我吧。";
//        //String dd = "3段&900克&KOTI";
//
//        BaseVO bv = new BaseVO();
//        bv.setName("启赋");
//        bv.setDesc(data1);
//        bv.setPrice("200");
//
//        boolean flag = new MatchUtil().match(this, bv, "启赋");
//
//        System.out.print(flag);

        webViewInit();

        Button btnOpenSetting = (Button)findViewById(R.id.btnOpenSetting);
        btnOpenSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSetting();
            }
        });

        Button btnStartSearch = (Button)findViewById(R.id.btnStartSearch);
        btnStartSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStop = false;
                mBaseArrLst.clear();
                mBaseArrLstItemMps.clear();
                mSearchDatas.clear();
                mSearchMaps.clear();

                mSearchDatas = SettingActivity.readDataInstance(MyMainActivity.this);
                if(mSearchDatas==null && mSearchDatas.size()==0) {
                    Toast.makeText(MyMainActivity.this, "设置数据为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                mCurrSearchItem=0;
                searchForSetting(mCurrSearchItem);
            }
        });

        Button btnConfig = (Button)findViewById(R.id.btnConfigSetting);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoConfig();
            }
        });

        Button btnStop = (Button)findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStop = true;
                Log.i(TAG, "mStop = " + mStop);

                mBaseArrLst.clear();
                mBaseArrLstItemMps.clear();
                mSearchDatas.clear();
                mSearchMaps.clear();

            }
        });

    }

    private void loadUrl(final String url) {
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(url);
            }
        });
    }

    private void loadSearchUrl(String value) {
        if(value==null && value.equals("")) {
            Toast.makeText(this, "查询数据为空", Toast.LENGTH_SHORT).show();
            return;
        }

        mCurrSearch = value;    //设置当前查询内容
        String url = Util.getSearchURL(value);
//        mWebView.loadUrl(url);
        loadUrl(url);
    }

    private void searchForSetting(int item) {
        if(item >= mSearchDatas.size()) {
            //搜索完毕
            Toast.makeText(this, "本轮搜索完毕，继续下一轮。。。", Toast.LENGTH_SHORT).show();
            mCurrSearchItem=0;

            if(mCallPhoneFlag) {
                mCallPhoneFlag = false;
                //拨打电话
                new NotifacationUtil().do1(this);
            }
        }

        SearchInstance data = mSearchDatas.get(mCurrSearchItem);

        String search = data.getSearch();
        loadSearchUrl(search);
    }

    private void gotoSetting() {
        Intent it = new Intent();
        it.setClass(this, SettingActivity.class);
        startActivity(it);
    }

    private void gotoConfig() {
        Intent it = new Intent();
        it.setClass(this, ConfigActivity.class);
        startActivity(it);
    }

    private void webViewInit() {
        mWebView = (WebView)findViewById(R.id.wv);
        // 设置与Js交互的权限
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.requestFocus();
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);

        mWebView.setWebViewClient(new WebViewClient(){
            //重写方法在这里。。。
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "url = " + url);

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                CookieManager cookieManager = CookieManager.getInstance();
                String CookieStr = cookieManager.getCookie(url);
                if(CookieStr!=null)
                {
                    Log.i(TAG, "CookieStr = " + CookieStr);
                }

                view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
                        "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

                super.onPageFinished(view, url);
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("WebView","onPageStarted");
                super.onPageStarted(view, url, favicon);
            }


        });

        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");

        mWebView.loadUrl("http://m.58.com/sz/");
    }

    @Override
    public void parseReturn(int type, String data) {
        if(mStop)
            return;

        switch (type) {
            case HtmlParse.TYPE_PRODUCT:
                //将对应的产品Desc进行赋值
                int item = mBaseArrLstItemMps.get(mCurrSearchItem);
                mBaseArrLst.get(item).setDesc(data);
                productPage();

                break;

            case HtmlParse.TYPE_BUY:
                if(mStop)
                    return;

//                try {
//                    int buy = ConfigActivity.getBuy(this);
//                    if(buy==0)
//                        buy = DEFALUT_BUY_TIME;
//                    Thread.sleep(1000*buy);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                String phone = ConfigActivity.getPhone(this);
                if(!phone.equals("")) {
                  loadUrl("javascript:$(\"#mobile\").val(\"" + phone + "\");");
                }

                DisplayMetrics dm = new DisplayMetrics();
                //取得窗口属性
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                //窗口的宽度
                int screenWidth = dm.widthPixels;
                //窗口高度
                int screenHeight = dm.heightPixels;

                int w = screenWidth /2 + 150;
                Log.i(TAG, "Buy screenWidth point:" + w);
                int h = screenHeight - (screenHeight/8)/2;
                Log.i(TAG, "Buy screenHeight point:" + h);

                setMouseClick(w, h+50);

                //save


                //延时2秒，继续发送
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(mStop)
                            return;

                        int item = mBaseArrLstItemMps.get(mCurrSearchItem);
                        if(item >= mBaseArrLst.size()) {
                            nextSearch();
                        } else {
                            BaseVO bNext = mBaseArrLst.get(item);//mBaseArrLst.get(mCurrSearchItem);
                            loadUrl(bNext.getUrl());
                        }
                    }
                }, 2000);
                break;
        }
    }

    private void productPage() {
        int item = mBaseArrLstItemMps.get(mCurrSearchItem);
        if(item < mBaseArrLst.size()) {
            BaseVO bv = mBaseArrLst.get(item);
            mBaseArrLstItemMps.put(mCurrSearchItem, item+1);

            boolean flag = new MatchUtil().match(this, bv, mCurrSearch);
            if(flag) {
                //进入确认界面
                loadUrl("javascript:$(\".fix_gozhuanzhuan\").click();");
                mCallPhoneFlag = true;

            } else {
                //开始下一个
                item = mBaseArrLstItemMps.get(mCurrSearchItem);
                if(item >= mBaseArrLst.size()) {
                    nextSearch();
                } else {
                    BaseVO bNext = mBaseArrLst.get(item);//mBaseArrLst.get(mCurrSearchItem);
                    loadUrl(bNext.getUrl());
                }
            }
        } else {
            //继续下一轮查询
            nextSearch();
        }
    }

    private void nextSearch() {
        //加入延时机制
        int time = ConfigActivity.getIntervalTimer(this);
        if(time==0) {
            time = DEFALUT_SEARCH_TIME;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCurrSearchItem++;
                searchForSetting(mCurrSearchItem);
            }
        }, time * 1000);
    }

    private void setMouseClick(int x, int y) {
        MotionEvent evenDownt = MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis() + 100, MotionEvent.ACTION_DOWN, x, y, 0);
        dispatchTouchEvent(evenDownt);
        MotionEvent eventUp = MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis() + 100, MotionEvent.ACTION_UP, x, y, 0);
        dispatchTouchEvent(eventUp);
        evenDownt.recycle();
        eventUp.recycle();
    }

    @Override
    public void parseReturn(int type, Set<BaseVO> list) {
        if(mStop)
            return;

        if(type!=HtmlParse.TYPE_SERARCHLIST)
            return;

        if(list!=null && list.size()<=0) {
            mCurrSearchItem++;
            searchForSetting(mCurrSearchItem);
        } else {
            ArrayList<BaseVO> lstAll =  mSearchMaps.get(mCurrSearchItem);
            if(lstAll==null) {
                lstAll = new ArrayList<>();
            } else {
                for(BaseVO vo : lstAll) {
                    Log.i("TEST", "lstAll name="+vo.getName());
                }
            }

            for(BaseVO bv : list) {
                //lstAll.add(bv);
                lstAll = BaseVOUtil.addBV(lstAll, bv);
                Log.i("TEST", "lstAll add lstALL size="+lstAll.size() + " url = " + bv.getName());
            }
            mSearchMaps.put(mCurrSearchItem, lstAll);

            ArrayList<BaseVO> bvs = mSearchMaps.get(mCurrSearchItem);


            mBaseArrLst.clear();
            for(BaseVO base : bvs) {
                mBaseArrLst.add(base);
            }

//            for(BaseVO vo : mBaseArrLst) {
//                Log.i("TEST", "NAME="+vo.getName());
//            }
//
//            for(BaseVO vo : mBaseArrLst) {
//                Log.i("TEST", "URL="+vo.getUrl());
//            }

            //加载一个详细数据
            if(mBaseArrLstItemMps.size() == 0) {
                mBaseArrLstItemMps.put(mCurrSearchItem, 0);
            } else if(mBaseArrLstItemMps.size() == mCurrSearchItem) {
                mBaseArrLstItemMps.put(mCurrSearchItem, 0);
            }

            int item = mBaseArrLstItemMps.get(mCurrSearchItem);
            if(item >= mBaseArrLst.size()) {
                nextSearch();
            } else {
                BaseVO bNext = mBaseArrLst.get(item);//mBaseArrLst.get(mCurrSearchItem);
                loadUrl(bNext.getUrl());
            }
        }
    }

    private void htmlParse(String html) {
        HtmlParse hp = new HtmlParse();
        hp.setListener(this);

        hp.parse(html);
    }

    final class InJavaScriptLocalObj {

        @JavascriptInterface
        public void showSource(String html) {
            try {
                htmlParse(html);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
