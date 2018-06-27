package com.hunter.tool.webviewdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.hunter.tool.webviewdemo.adapter.ConditionVO;
import com.hunter.tool.webviewdemo.database.SearchInstance;
import com.hunter.tool.webviewdemo.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "WebView";

    private final static String url = "http://j2.58cdn.com.cn/zhuanzhuan/Mzhuanzhuan/m/js/entry/orderSure.js?_v=8403999";

    private final static String url11 = "//m.zhuanzhuan.58.com/Mzhuanzhuan/m/or_sure.html";
    //mWebView.loadUrl("javascript:$(\".fix_gozhuanzhuan\").click();");

    private final static String url1 = "http://m.58.com/sz/yingyou/?key=%E5%A5%B6%E7%B2%89&cmcskey=%E5%A5%B6%E7%B2%89&final=1&jump=1&sqa_type=0&newkey=%E5%A5%B6%E7%B2%89&formatsource=sou&from=list_yingyou_sou&keyfrom=list";

    private String msgTitle;//分享标题
    private String msgDesc;//分享描述
    private String msgImgUrl;//分享图片
    private String msgLink;//分享链接
    private boolean canShare = false;//是否支持分享


    private ArrayList<BaseVO> mBaseArrLst = new ArrayList<BaseVO>();
    private Set<BaseVO> mBaseLst = new HashSet<BaseVO>();
    private static int mBaseArrLstItem = 0;

    private static int mCurrStatus = 0x00;
    private final static int SEARCH_FINISH = 0x01;
    private final static int SIMPLE_START = 0x02;
    private final static int SIMPLE_FINISH = 0x03;
    private final static int SIMPLE_BUY = 0x04;

    private static String mSearch = "";

    private Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what) {
                case SEARCH_FINISH://搜索数据完成
                    mCurrStatus = SIMPLE_FINISH;
                    if(mBaseLst.size()>0) {
                        for(BaseVO base : mBaseLst) {
                            mBaseArrLst.add(base);
                            //mWebView.loadUrl(base.getUrl());
                        }
                    }

                    //加载第一个详细数据
                    BaseVO b = mBaseArrLst.get(mBaseArrLstItem);
                    mWebView.loadUrl(b.getUrl());

                    mCurrStatus = SIMPLE_START; //开始进入加载
                    break;

                case SIMPLE_FINISH:
                    if(mBaseArrLstItem <= mBaseArrLst.size()) {
                        //符合要求，进行购买
                        BaseVO one = mBaseArrLst.get(mBaseArrLstItem-1);
                        boolean flag = match(one, mSearch);
                        if(flag) {
                            //进入确认界面
                            mWebView.loadUrl("javascript:$(\".fix_gozhuanzhuan\").click();");
                        } else {
                            //开始下一个
                            BaseVO bNext = mBaseArrLst.get(mBaseArrLstItem);
                            mWebView.loadUrl(bNext.getUrl());

                            mCurrStatus = SIMPLE_START; //开始进入加载
                        }
                    }

                    break;

                case SIMPLE_BUY:

                    Buy();
                default:
                    break;
            }
        };
    };

    private boolean match(BaseVO bvo, String search) {
        //对比数据
        ArrayList<SearchInstance> arr = SettingActivity.readDataInstance(this);
        if(arr==null || arr.size()<=0)
            return false;

        for(SearchInstance instance : arr) {
            //判断 搜索 关键字是否相同
            if(match_Search(search, instance)) {
                //判断是否有多个条件符合
                if(match_Conditions(instance)) {
                    //先判断商品详情数据
                    if(match_Desc_Kick_Price(bvo, instance.getArrConditions())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }



    //判断 搜索 关键字是否相同
    private boolean match_Search(String search, SearchInstance instance) {
        boolean flag = false;
        if(instance.getSearch().equals(search)) {
            return true;
        }

        return flag;
    }

    //判断是否有多个条件符合
    private boolean match_Conditions(SearchInstance instance) {
        boolean flag = false;
        ArrayList<ConditionVO> cvo = instance.getArrConditions();
        if(cvo!=null && cvo.size()>0) {
            return true;
        }

        return flag;
    }

    //先判断商品详情数据是否与条件集数据匹配
    private boolean match_Desc_Kick_Price(BaseVO bvo, ArrayList<ConditionVO> cvo) {
        String bDesc = bvo.getDesc();
        if(bDesc==null || bDesc.equals(""))
            return false;

        for(ConditionVO cv : cvo) {
            //名称和条件集进行判断
            String condition = cv.getEtCondition();
            if(condition==null || condition.equals(""))
                return false;

            //进行Price判断
            boolean fPrice = match_Price(bvo, cv);
            if(!fPrice) {
                return false;
            }

            if(condition.contains("/")) {
                String[] cons = condition.split("/");
                for(String con : cons) {
                    boolean fDesc = bDesc.contains(con);
                    if(!fDesc) {
                        //匹配不成功
                        return false;
                    }
                }
            } else {
                boolean fDesc = bDesc.contains(condition);
                if(!fDesc) {
                    return false;
                }
            }

            //进行Kick判断
            boolean fKick = match_Desc_Kick(bDesc, cv);
            if (fKick)
                return false;
        }

        return true;
    }

    //进行Kick判断
    private boolean match_Desc_Kick(String bDesc, ConditionVO cv) {
        String kick = cv.getEtKick();
        if(kick.equals("")) {
            return false;
        }

        if(kick.contains("/")) {
            String[] kicks = kick.split("/");
            for(String ki : kicks) {
                if(bDesc.contains(ki)) {
                    //匹配成功
                    return true;
                }
            }
        } else {
            if(bDesc.contains(kick)) {
                return true;
            }
        }

        return false;
    }

    private boolean match_Price(BaseVO bvo, ConditionVO cv) {
        String bPrice = bvo.getPrice();
        String cPrice = cv.getEtPrice();
        try {
            if(Integer.valueOf(cPrice) <= Integer.valueOf(bPrice)) {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    String dataT = "￥ 95";

    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCurrStatus = SEARCH_FINISH;
        String dd= "全新原封美国进口雅培金盾go&grow三段奶粉 ￥150 南京|江宁";

        Button btnOpenSetting = (Button)findViewById(R.id.btnOpenSetting);
        btnOpenSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setClass(MainActivity.this, SettingActivity.class);
                MainActivity.this.startActivity(it);
            }
        });

        Button btnStartSearch = (Button)findViewById(R.id.btnStartSearch);
        btnStartSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<SearchInstance> datas = SettingActivity.readDataInstance(MainActivity.this);
                for(SearchInstance instance : datas) {
                    mSearch = instance.getSearch();
                    startSearch(mSearch);
                    break;
                }
            }
        });

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

//                /**这个是为了加载网页内容*/
//                view.loadUrl("javascript:window.local_obj.showSource1(document.getElementsByTagName('article')[0].innerText);");

//                msgDesc = stringByEvaluatingJavaScriptFromString("msg_desc");
//                msgTitle = stringByEvaluatingJavaScriptFromString("msg_title");
//                msgLink = stringByEvaluatingJavaScriptFromString("msg_link");
//                Log.d(TAG, msgTitle + "/" + msgDesc + "/" + msgLink);

                super.onPageFinished(view, url);
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("WebView","onPageStarted");
                super.onPageStarted(view, url, favicon);
            }


        });

        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
//        mWebView.loadUrl(url);

    }

    private void startSearch(String search) {
        String url = Util.getSearchURL(search);
        mWebView.loadUrl(url);
    }

    private void Buy() {
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

        setMouseClick(w, h);
    }

    public void setMouseClick(int x, int y) {
        MotionEvent evenDownt = MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis() + 100, MotionEvent.ACTION_DOWN, x, y, 0);
        dispatchTouchEvent(evenDownt);
        MotionEvent eventUp = MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis() + 100, MotionEvent.ACTION_UP, x, y, 0);
        dispatchTouchEvent(eventUp);
        evenDownt.recycle();
        eventUp.recycle();
    }

    final class InJavaObj {
        @JavascriptInterface
        public void showSource() {

        }
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            Log.i(TAG, html);

            Document doc = Jsoup.parse(html);

            String docString = doc.toString();

            boolean dFlag = false;
            if(docString.contains("手机号") && docString.contains("发送验证码")
                    && docString.contains("请输入手机验证码")
                    && docString.contains("收货人")
                    && docString.contains("请输入收货人姓名")
                    && docString.contains("收货地区")
                    && docString.contains("选择地区")
                    && docString.contains("详细地址")
                    && docString.contains("请输入详细的地址信息")
                    && docString.contains("合计:")
                    && docString.contains("确认")) {
                dFlag = true;
                mCurrStatus = SIMPLE_BUY;
                handler.sendEmptyMessage(mCurrStatus);
                return;
            }
//            else {
//                Buy();
//
//                return;
//            }

            Element body = doc.body();
            Elements els = doc.select("div");

            Elements elss = els.attr("div","asynInfo");

            createItems(els);


        }

        private void createItems(Elements els) {
            for (Element el : els) {

                Elements miaoshu = el.getElementsByClass("miaoshu");
                if(miaoshu!=null && miaoshu.size()>0) {
                    String msText = miaoshu.text();
                    Log.i(TAG, "miaoshu = " + msText);
                    mBaseArrLst.get(mBaseArrLstItem++).setDesc(msText);
                    mCurrStatus = SIMPLE_FINISH;
                    break;
                }

                Elements asynInfo = el.getElementsByClass("asynInfo");

                for(Element e : asynInfo) {
                    String className = e.className();

                    if("asynInfo".equals(className)) {
                        Elements pros =  e.children();
                        for(Element pro : pros) {
                            Elements zhuanzhuan = pro.getElementsByClass("zhuanzhuan");

                            for(Element li : zhuanzhuan) {
                                Elements zzitem = li.getElementsByClass("zzitem");

                                for (Element item : zzitem) {
                                    Elements links = item.getElementsByTag("a");
                                    for (Element link : links) {
                                        String linkHref = link.attr("href");
                                        String linkText = link.text();
                                        Log.i(TAG, "linkHref="+linkHref);
                                        Log.i(TAG, "linkText="+linkText);

                                        //Save Coll
                                        BaseVO base = new BaseVO();
                                        base.setUrl(linkHref);
//                                        base.setName(linkText.split("\\s+")[0]);
//                                        base.setPrice(linkText.split("\\s+")[1]);
                                        base.setName(linkText.split("￥")[0]);
                                        base.setPrice(Util.getPrice(linkText));

                                        Log.i(TAG, "set Name=" + base.getName());
                                        Log.i(TAG, "set Price=" + base.getPrice());

                                        mBaseLst.add(base);
                                    }
                                }
                            }
                        }

                    }

                }

            }

            Log.i(TAG, "BaseLst size" + mBaseLst.size());
            handler.sendEmptyMessage(mCurrStatus);
        }

        @JavascriptInterface
        public void showSource1(String html) {
            Log.e("", "______"+html+"____");
        }


    }

    public String stringByEvaluatingJavaScriptFromString(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                Field mp = WebView.class.getDeclaredField("mProvider");
                mp.setAccessible(true);
                Object webViewObject = mp.get(this);
                Field wc = webViewObject.getClass().getDeclaredField("mWebViewCore");
                wc.setAccessible(true);
                Object webViewCore = wc.get(webViewObject);
                Field bf = webViewCore.getClass().getDeclaredField("mBrowserFrame");
                bf.setAccessible(true);
                Object browserFrame = bf.get(webViewCore);
                Method stringByEvaluatingJavaScriptFromString = browserFrame.getClass()
                        .getDeclaredMethod("stringByEvaluatingJavaScriptFromString",
                                String.class);
                stringByEvaluatingJavaScriptFromString.setAccessible(true);
                Object obj_value = stringByEvaluatingJavaScriptFromString.invoke(
                        browserFrame, script);
                return String.valueOf(obj_value);
            } catch (Exception e) {
                Log.e("!!!", "stringByEvaluatingJavaScriptFromString", e);
            }
            return null;
        } else {
            try {
                Field[] fields = WebView.class.getDeclaredFields();
                // 由webview取到webviewcore
                Field field_webviewcore = WebView.class.getDeclaredField("mWebViewCore");
                field_webviewcore.setAccessible(true);
                Object obj_webviewcore = field_webviewcore.get(this);
                // 由webviewcore取到BrowserFrame
                Field field_BrowserFrame = obj_webviewcore.getClass().getDeclaredField(
                        "mBrowserFrame");
                field_BrowserFrame.setAccessible(true);
                Object obj_frame = field_BrowserFrame.get(obj_webviewcore);
                // 获取BrowserFrame对象的stringByEvaluatingJavaScriptFromString方法
                Method method_stringByEvaluatingJavaScriptFromString = obj_frame.getClass()
                        .getMethod("stringByEvaluatingJavaScriptFromString", String.class);
                // 执行stringByEvaluatingJavaScriptFromString方法
                Object obj_value = method_stringByEvaluatingJavaScriptFromString.invoke(
                        obj_frame,
                        script);
                // 返回执行结果
                return String.valueOf(obj_value);
            } catch (Exception e) {
                Log.e("!!!", "stringByEvaluatingJavaScriptFromString", e);
            }
            return null;
        }
    }

}
