package com.kxf.cameramanager;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import com.kxf.cameramanager.utils.LogUtil;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


public class MyApplication extends Application {
    private static MyApplication instance;
    private static DbManager xdb = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        x.Ext.init(this);
        LogUtil.e("onCreate()");
        LogUtil.e("BuildConfig.buileDateTime=" + BuildConfig.buileDateTime);
        LogUtil.e("BuildConfig.VERSION_CODE=" + BuildConfig.VERSION_CODE);
        LogUtil.e("BuildConfig.VERSION_NAME=" + BuildConfig.VERSION_NAME);
        LogUtil.e("BuildConfig.needCheckDate=" + BuildConfig.needCheckDate);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                LogUtil.e("应用异常退出......");
//                LogUtil.e(ex.getMessage());
//                LogUtil.e(Arrays.toString(ex.getStackTrace()));
//                LogUtil.e(ex.toString());
//                StackTraceElement[] sts = ex.getStackTrace();
//                if (null != sts && sts.length>0){
//                    for (StackTraceElement st : sts){
//                        LogUtil.e("      "+st.toString());
//                    }
//                }
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                ex.printStackTrace(printWriter);
                Throwable cause = ex.getCause();
                while (cause != null) {
                    cause.printStackTrace(printWriter);
                    cause = cause.getCause();
                }
                printWriter.close();
                String result = writer.toString();
                LogUtil.e(result);
                Toast.makeText(getApplicationContext(), "程序发生故障退出，请稍后重试", Toast.LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        initDB();
    }


    public static MyApplication getInstance() {
        LogUtil.d("getInstance()  instance=" + instance);
        return instance;
    }

    //初始化xutil3 数据库
    private DbManager initDB() {
        LogUtil.e("initDB()");
        xdb = x.getDb(getDBConfig());
        return xdb;
    }

    public static DbManager db() {
        if (xdb == null) {
            xdb = x.getDb(getDBConfig());
        }
        LogUtil.e("xdb=" + xdb);
        return xdb;
    }

    private static DbManager.DaoConfig getDBConfig() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("MyApplication")
                .setDbVersion(1)
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager dbManager, int i, int i1) {
                        // TODO: ... 每次更新数据库的时候 增加版本号
//                        try {
//                            dbManager.addColumn(TransactionData.class,"test");
//                        } catch (DbException e) {
//                            e.printStackTrace();
//                        }
                        // db.dropTable(...);
                        // ...
                        // or
                        // db.dropDb();
                    }
                })
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            db.getDatabase().enableWriteAheadLogging();
                        }
                    }
                });
        return daoConfig;
    }

    private static final String SHARE_NAME = "appInfo";

    public static void saveShare(String key, String value) {
        SharedPreferences share = instance.getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        share.edit().putString(key, value).commit();
    }

    public static String getShare(String key, String def) {
        SharedPreferences share = instance.getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        return share.getString(key, def);
    }

    public static String getShare(String key) {
        return getShare(key, "");
    }

    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    public static String getSDCardPath() {
        File sdcardDir = null;
        // 判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }
}
