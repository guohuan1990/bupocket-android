package com.bupocket.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    private static long minute = 1000 * 60;
    private static long hour = minute * 60;
    private static long day = hour * 24;
    private static long halfamonth = day * 15;
    private static long month = day * 30;
    public  static String getDateDiff(long dateTimeStamp){
        dateTimeStamp = Long.parseLong((dateTimeStamp+"").substring(0,13));
        String result;
        long now = new Date().getTime();
        long diffValue = now - dateTimeStamp;
        if(diffValue < 0){
        }
        long monthC =diffValue/month;
        long weekC =diffValue/(7*day);
        long dayC =diffValue/day;
        long hourC =diffValue/hour;
        long minC =diffValue/minute;
        if(monthC>=1 || weekC >= 1){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            result= sdf.format(new Date(dateTimeStamp));
            return result;
        }
        else if(dayC>=1){
            result = Integer.parseInt(dayC+"") +"天前";
            return result;
        }
        else if(hourC>=1){
            result = Integer.parseInt(hourC+"") +"小时前";
            return result;
        }
        else if(minC>=1){
            result =  Integer.parseInt(minC+"") +"分钟前";
            return result;
        }else{
            result = "刚刚";
            return result;
        }
    }
    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }

}