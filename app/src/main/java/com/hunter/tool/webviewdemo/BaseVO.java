package com.hunter.tool.webviewdemo;

/**
 * Created by Administrator on 2017/11/28 0028.
 */

public class BaseVO {

    private String url;
    private String name;
    private String price;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
//        if (obj instanceof BaseVO) {
//            BaseVO obj1 = (BaseVO) obj;
//            return (this.name == obj1.name);
//        }
//
//        return super.equals(obj);
        BaseVO a=(BaseVO)obj;
        return name.equals(a.name);
    }

}
