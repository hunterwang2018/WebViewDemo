package com.hunter.tool.webviewdemo.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hunter.tool.webviewdemo.BaseVO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by Administrator on 2017/12/12 0012.
 */

public class HtmlParse {
    private final static String TAG = "HtmlParse";
    public final static int TYPE_BUY = 0x01;
    public final static int TYPE_SERARCHLIST = 0x02;
    public final static int TYPE_PRODUCT = 0x03;

    public interface ParseListener {
        public void parseReturn(int type, String data);
        public void parseReturn(int type, Set<BaseVO> list);
    }

    ParseListener mListener;
    public void setListener(ParseListener listener) {
        mListener = listener;
    }

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    sendListener(TYPE_BUY, "");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void parse(String html) {
        Document doc = Jsoup.parse(html);
        String docString = doc.toString();

        Log.i(TAG, "docString=" + docString);
        if(checkBuyPage(docString)) {
            //进入购买状态

            //延时2秒，继续发送
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendListener(TYPE_BUY, "");

                }
            }, 2000);

            return;
        }

        Element body = doc.body();
        Elements els = doc.select("div");
        Elements elss = els.attr("div","asynInfo");

        Set<BaseVO> lst = new HashSet<BaseVO>();
        for (Element el : els) {

            //判断是否产品界面，如果是直接退出，同时发送Desc
            if (checkProductPage(el)) {
                return;
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
                                    base.setName(linkText.split("￥")[0]);
                                    base.setPrice(Util.getPrice(linkText));

                                    Log.i(TAG, "set Name=" + base.getName());
                                    Log.i(TAG, "set Price=" + base.getPrice());

                                    lst.add(base); //集合
                                }
                            }
                        }
                    }
                }
            }
        }

//        sendListener(TYPE_SERARCHLIST, lst);
        if(lst!=null && lst.size()>0) {
            sendListener(TYPE_SERARCHLIST, lst);
        }
    }

    private boolean checkProductPage(Element el) {
        Elements miaoshu = el.getElementsByClass("miaoshu");
        if(miaoshu!=null && miaoshu.size()>0) {
            String desc = miaoshu.text();
            sendListener(TYPE_PRODUCT, desc);
            return true;
        }
        return false;
    }

    private boolean checkBuyPage(String docString) {
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
            return true;
        }

        return false;
    }

    private void sendListener(int type, String data) {
        if(mListener!=null)
            mListener.parseReturn(type, data);
    }
    private void sendListener(int type, Set<BaseVO> list) {
        if(mListener!=null)
            mListener.parseReturn(type, list);
    }
}
