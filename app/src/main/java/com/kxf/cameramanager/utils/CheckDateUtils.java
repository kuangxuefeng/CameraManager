package com.kxf.cameramanager.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import com.kxf.cameramanager.BuildConfig;

import utils.CheckDateTime;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by kuangxf on 2017/11/20.
 */

public class CheckDateUtils {
    private static final String KEY_DATA_OUT = "key_data_out";
    private static final String KEY_DATA_OUT_BUILD_TIME = "key_data_out_build_time";
    private static final int dateOut_day = 15;//1  1天
    private static final int dateOut_minutes = 0;//1  1分钟

    public static void checkDate(final Activity activity, final DateOutListen dateOutListen) {
        final SharedPreferences sp = activity.getPreferences(MODE_PRIVATE);
        long bt = sp.getLong(KEY_DATA_OUT_BUILD_TIME, 0);
        LogUtil.e("判断到期 保存的程序编译时间 bt=" + bt);
        LogUtil.e("判断到期 当前程序编译时间 BuildConfig.BUILD_TIME_LONG=" + BuildConfig.BUILD_TIME_LONG);
        if (sp.getBoolean(KEY_DATA_OUT, false) && bt == BuildConfig.BUILD_TIME_LONG) {
            LogUtil.e("已判断到期！");
            if (null != dateOutListen){
                dateOutListen.out();
            }
            return;
        }else if (bt != BuildConfig.BUILD_TIME_LONG){
            sp.edit().putBoolean(KEY_DATA_OUT, false).commit();
            sp.edit().putLong(KEY_DATA_OUT_BUILD_TIME, BuildConfig.BUILD_TIME_LONG).commit();
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!CheckDateTime.isValidTime(BuildConfig.BUILD_TIME_LONG, 0, 0, dateOut_day, 0, dateOut_minutes, 0)){
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            sp.edit().putBoolean(KEY_DATA_OUT, true).commit();
                            sp.edit().putLong(KEY_DATA_OUT_BUILD_TIME, BuildConfig.BUILD_TIME_LONG).commit();
                            if (null != dateOutListen){
                                dateOutListen.out();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public interface DateOutListen{
        void out();
    }
}
