package com.kxf.cameramanager.utils;


import android.text.TextUtils;
import android.util.Log;

import com.kxf.cameramanager.MyApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

    private static boolean isOpenLog = true;
    public final static String KeyLogFileName = "KeyLogFileName";
    private static String logFile;
    private static final long logFileSizeKB = 5 * 1024;//50*1024  为50m

    static {
        getLogFilePath(false);
        File f = new File(logFile);
        double fLen = f.length() / 1024.00;
        if (!f.exists() || fLen > logFileSizeKB) {
            try {
                if (f.exists()){
                    f.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            getLogFilePath(true);
        }
    }

    public static void e(String msg) {
        if (isOpenLog) {
            String tag = getModule();
            Log.e(tag, msg);
            writeFile(tag, msg);
        }
    }

    public static void i(String msg) {
        if (isOpenLog) {
            String tag = getModule();
            Log.i(tag, msg);
            writeFile(tag, msg);
        }
    }

    public static void d(String msg) {
        if (isOpenLog) {
            String tag = getModule();
            Log.d(tag, msg);
            writeFile(tag, msg);
        }
    }

    private static String getModule() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        int stackOffset = -1;
        int methodCount = 2;
        for (int i = 3; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(LogUtil.class.getName())) {
                stackOffset = i;
                break;
            }
        }
        for (int i = methodCount; i > 0; i--) {
            int stackIndex = stackOffset;
            String simpleClassName = getSimpleClassName(trace[stackIndex]
                    .getClassName());
            if (simpleClassName.startsWith("TLog")) {
                continue;
            } else {
                i = 0;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(") [")
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".").append(trace[stackIndex].getMethodName())
                    .append("]");
            return builder.toString();
        }
        return "-----";
    }

    private static void getLogFilePath(boolean isForceUpdate) {
        logFile = MyApplication.getShare(KeyLogFileName, "");
        if (TextUtils.isEmpty(logFile) || isForceUpdate) {
            logFile = MyApplication.getSDCardPath() + File.separator + "log_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".main";
            File f = new File(logFile);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            MyApplication.saveShare(KeyLogFileName, logFile);
        }
    }

    private static String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private static boolean writeFile(String TAG, String value) {
        String str = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS").format(new Date()) + "     " + TAG + " : " + value;
        return writeFile(str);
//        return false;
    }

    private static boolean writeFile(String str) {
        boolean re = false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(logFile), true));
            bw.write(str);
            bw.newLine();
            bw.flush();
            bw.close();
            re = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return re;
    }

//    public static void readFile() {
//        BufferedReader br = null;
//        try {
//            FileReader fr = new FileReader(new File(logFile));
//            br = new BufferedReader(fr);
//            String line;
//            while ((line = br.readLine()) != null) {
//                Log.e("line = ", EncUtil.desEncryptAsString(MyDesKeyUtil.get3DesKeyDef(), line));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
