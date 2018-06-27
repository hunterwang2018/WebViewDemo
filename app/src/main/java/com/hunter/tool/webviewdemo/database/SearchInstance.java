package com.hunter.tool.webviewdemo.database;

import com.hunter.tool.webviewdemo.adapter.ConditionVO;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/6 0006.
 */

public class SearchInstance {
    private String search;
    private ArrayList<ConditionVO> arrConditions = new ArrayList<ConditionVO>();

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public ArrayList<ConditionVO> getArrConditions() {
        return arrConditions;
    }

//    public void setArrConditions(ArrayList<ConditionVO> arrConditions) {
//        this.arrConditions = arrConditions;
//    }
}
