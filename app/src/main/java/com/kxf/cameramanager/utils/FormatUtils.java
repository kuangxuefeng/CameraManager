package com.kxf.cameramanager.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kuangxf on 2017/9/14.
 */

public class FormatUtils {
    public static final String FORMAT_COMMODITY_YMD = "yyyyMMdd";
    public static final String FORMAT_COMMODITY_HMSS = "HHmmssSSS";
    public static final String FORMAT_COMMODITY_SHOW = "yyyy/MM/dd HH:mm:ss SSS";
    public enum ALIGN{
        LEFT, RIGHT, CENTER
    }
    public static String FormatStringLen(String src, int len, ALIGN align, String filling){
        if (TextUtils.isEmpty(filling)){
            return src;
        }
        if (null == src){
            src = "";
        }
        switch (align){
            case LEFT:
                while (src.length() < len){
                    src = filling + src;
                }
                break;
            case RIGHT:
                while (src.length() < len){
                    src = src + filling;
                }
                break;
            case CENTER:
                while (src.length() < len){
                    src = filling + src + filling;
                }
                break;
        }
        return src;
    }

    public static String getTimeByFormat(String format){
        return new SimpleDateFormat(format).format(new Date());
    }

    public static String FormatTime(String src, String formatSrc, String formatRe){
        try {
            return new SimpleDateFormat(formatRe).format(new SimpleDateFormat(formatSrc).parse(src));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
