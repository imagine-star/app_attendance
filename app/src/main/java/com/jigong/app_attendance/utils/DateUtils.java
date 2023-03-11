package com.jigong.app_attendance.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @description 时间工具类
 * @author zhangliuming
 * @date 2019/4/26
 */
public class DateUtils {
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYYMMDD = "yyyyMMdd";
    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String FORMAT_yyMMddHHmmss = "yyMMddHHmmss";
    /**
     * 字符串转换成Date格式，如果转化失败，返回null
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static Date str2Date(String dateStr, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        try {
            return sf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转换成Date格式，如果转化失败，返回null
     *
     * @param dateStr
     * @return
     */
    public static String str2Date2(String dateStr) {
       String temp=dateStr.split(" ")[0];
        return temp;
    }
    /**
     * Date转String，如果转化失败，返回null
     *
     * @param date
     * @param format
     * @return
     */
    public static String date2Str(Date date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    /**
     * 获取某一天当前时间	2008-7-4
     * 
     * @Author: hailunZhao
     * @Date: 2019年5月5日 下午8:20:19
     * @Description: 
     * @param num
     * 			向前或向后滚动num天，正数向未来，负数向过去
     * @return
     */
	public static Date getNowOnOneDay(int num) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, num);
		return cal.getTime();
	}
    
    
    public static void main(String[] args){
//    	String date = date2Str(new Date(),FORMAT_YYYY_MM_DD);
//        System.out.println(date);
//        Date date1=str2Date("2019-04-26",FORMAT_YYYY_MM_DD);
//        System.out.println(date1);
        for (int i = 10; i >= 1; i--) {
        	String date = date2Str(getNowOnOneDay(-i),FORMAT_YYYY_MM_DD);
        	System.out.println(date);
		}
        
    }
}
