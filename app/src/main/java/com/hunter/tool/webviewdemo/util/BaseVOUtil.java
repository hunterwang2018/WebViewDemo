package com.hunter.tool.webviewdemo.util;

import com.hunter.tool.webviewdemo.BaseVO;

import java.util.ArrayList;


public class BaseVOUtil {

	public static ArrayList<BaseVO> addBV(ArrayList<BaseVO> arr, BaseVO bv) {
		if(arr.size()==0) {
			arr.add(bv);
			return arr;
		}
		
		String buffer = bv.getName()+bv.getPrice();
		
		boolean flag = true;
		for(BaseVO b : arr) {
			String value = b.getName()+bv.getPrice();
			if(value.equals(buffer)) {
				flag = false;
				break;
			}
		}
		
		if(flag) {
			arr.add(bv);
		}
		
		return arr;
	}
}
