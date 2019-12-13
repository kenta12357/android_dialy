package com.example.diary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTime {

    private static Date date;

    public static String getNowYear(){
        final DateFormat dateFormat = getDateFormat("yyyy");
        updateDate();
        String dateStr = dateFormat.format(date);
        return dateStr;
    }

    private static DateFormat getDateFormat(String pattern){
        final DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat;
    }

    private static void updateDate(){
        date = new Date(System.currentTimeMillis());
    }
}
