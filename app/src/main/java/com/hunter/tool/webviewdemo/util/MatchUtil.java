package com.hunter.tool.webviewdemo.util;

import android.content.Context;

import com.hunter.tool.webviewdemo.BaseVO;
import com.hunter.tool.webviewdemo.SettingActivity;
import com.hunter.tool.webviewdemo.adapter.ConditionVO;
import com.hunter.tool.webviewdemo.database.SearchInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/12/12 0012.
 */

public class MatchUtil {
    private final static String KEY = "g";
    private final static String CHANGE = "克";

    public boolean match(Context context, BaseVO bvo, String search) {
        //对比数据
        ArrayList<SearchInstance> arr = SettingActivity.readDataInstance(context);
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

        //阶,g的替换
        String name = bvo.getName();
        bDesc = name + bDesc;
        bDesc = splitValue(bDesc);
        bDesc = splitGtoK(bDesc);

        boolean flag = true;
        for(ConditionVO cv : cvo) {
            flag = true;
            //名称和条件集进行判断
            String condition = cv.getEtCondition();
            if(condition==null || condition.equals(""))
                flag = false;

            //进行Price判断
            boolean fPrice = match_Price(bvo, cv);
            if(!fPrice) {
                flag = false;
            }

            if(condition.contains("&")) {
                String[] cons = condition.split("&");
                for(String con : cons) {
                    boolean fDesc = bDesc.contains(con);
                    if(!fDesc) {
                        //匹配不成功
                        flag = false;
                    }
                }
            } else {
                boolean fDesc = bDesc.contains(condition);
                if(!fDesc) {
                    flag = false;
                }
            }

            //进行Kick判断
            boolean fKick = match_Desc_Kick(bDesc, cv);
            if (fKick)
                flag = false;

            if(flag) {
                break;
            }

        }

        return flag;
    }

    private String splitValue(String desc) {
        String[] str = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
        String[] str1 = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        Set<String> set = new HashSet<String>(Arrays.asList(str));

        StringBuffer sb = new StringBuffer(desc);

        String value1 = "阶";
        if(desc.contains(value1) || desc.contains("段")) {
            desc = desc.replaceAll("阶", "段");
            sb = new StringBuffer(desc);

            Integer[] locations = matchLocations(desc, "段");
            for(Integer location : locations) {
                if(location!=0) {
                    String strValue = desc.substring(location-1, location);
                    if(set.contains(strValue)) {
                        for (int i = 0; i < str.length; i++) {
                            if(strValue.equals(str[i])) {
                                sb.replace(location-1, location, str1[i]);
                            }
                        }
                    }
                }
            }
        }

        return sb.toString();
    }

    private String splitGtoK(String desc) {
        String[] str1 = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        Set<String> set = new HashSet<String>(Arrays.asList(str1));

        desc = replaceG(desc);
        StringBuffer sb = new StringBuffer(desc);

        Integer[] locations = matchLocations(desc, KEY);
        for(Integer location : locations) {
            if(location!=0) {
                String strValue = desc.substring(location-1, location);
                if(set.contains(strValue)) {
                    sb.replace(location, location+1, CHANGE);
                }
            }
        }

        return sb.toString();
    }

    private String replaceG(String desc) {
        if(desc.contains("G")) {
            desc = desc.replaceAll("G", KEY);
            return desc;
        } else {
            return desc;
        }
    }

    private Integer[] matchLocations(String desc, String value) {
        ArrayList<Integer> iarr = new ArrayList<>();
        Matcher slashMatcher = Pattern.compile(value).matcher(desc);
        while (slashMatcher.find()) {
            iarr.add(slashMatcher.start());
        }

        return (Integer[])iarr.toArray(new Integer[0]);
    }

    //进行Kick判断
    private boolean match_Desc_Kick(String bDesc, ConditionVO cv) {
        String kick = cv.getEtKick();
        if(kick.equals("")) {
            return false;
        }

        if(kick.contains("&")) {
            String[] kicks = kick.split("&");
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

        if(cPrice.contains("-")) {
            String[] ps = cPrice.split("-");
            int beP = Integer.valueOf(ps[0]);
            int neP = Integer.valueOf(ps[1]);
            if(beP<=Integer.valueOf(bPrice)
                    && neP>=Integer.valueOf(bPrice)) {
                return true;
            }
        } else {
            try {
                if (Integer.valueOf(cPrice) >= Integer.valueOf(bPrice)) {
                    return true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }
}
