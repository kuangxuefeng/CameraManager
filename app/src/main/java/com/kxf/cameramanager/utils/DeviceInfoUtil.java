package com.kxf.cameramanager.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * Created by kxf on 2017/7/21.
 */

public class DeviceInfoUtil {

    private static final String getDeviceIdErro = "出现异常导致无法获取设备ID";

    /*
    * 时间：2017/07/21 11:33:02 572

DeviceInfoUtil.getAndroidId(activity)：b9f6681f08feb498

DeviceInfoUtil.getIMEI(activity)：863835023100527

DeviceInfoUtil.getMacAddress(activity)：94:a1:a2:79:6e:84

DeviceInfoUtil.getBlueToothAddress()：22:22:9B:E7:75:3B

DeviceInfoUtil.getIdFromRom()：357912916722246

DeviceInfoUtil.getUuidFen()：1233322d-404e-4d9d-8f5c-4228bae2162d

DeviceInfoUtil.getUuid()：8b09a537e3914c888d0982c619c056f4
    * */

    public static String getIMEI(Context context) {
        String szImei = getDeviceIdErro;
        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            szImei = TelephonyMgr.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return szImei;
    }

    public static String getAndroidId(Context context) {
        String m_szAndroidID = getDeviceIdErro;
        try {
            m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m_szAndroidID;
    }

    public static String getMacAddress(Context context) {
        String m_szWLANMAC = getDeviceIdErro;
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m_szWLANMAC;
    }

    public static String getBlueToothAddress() {
        String m_szBTMAC = getDeviceIdErro;
        try {
            BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
            m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            m_szBTMAC = m_BluetoothAdapter.getAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m_szBTMAC;
    }

    public static String getIdFromRom() {
        String m_szDevIDShort = getDeviceIdErro; //13 digits
        try {
            m_szDevIDShort = "35" + //we make this look like a valid IMEI
                    Build.BOARD.length() % 10 +
                    Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 +
                    Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 +
                    Build.HOST.length() % 10 +
                    Build.ID.length() % 10 +
                    Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 +
                    Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 +
                    Build.TYPE.length() % 10 +
                    Build.USER.length() % 10;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m_szDevIDShort;
    }

    public static String getUuidFen() {
        String id_uu = getDeviceIdErro;
        try {
            UUID uuid = UUID.randomUUID();
            id_uu = uuid.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_uu;
    }

    public static String getUuid() {
        try {
            return getUuidFen().replace("-", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDeviceIdErro;
    }

    public static String getInfo(){
        String phoneInfo = "Product: " + android.os.Build.PRODUCT;
        phoneInfo += ",\n CPU_ABI: " + android.os.Build.CPU_ABI;
        phoneInfo += ",\n TAGS: " + android.os.Build.TAGS;
        phoneInfo += ",\n VERSION_CODES.BASE: " + android.os.Build.VERSION_CODES.BASE;
        phoneInfo += ",\n MODEL: " + android.os.Build.MODEL;
        phoneInfo += ",\n SDK: " + android.os.Build.VERSION.SDK;
        phoneInfo += ",\n VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE;
        phoneInfo += ",\n DEVICE: " + android.os.Build.DEVICE;
        phoneInfo += ",\n DISPLAY: " + android.os.Build.DISPLAY;
        phoneInfo += ",\n BRAND: " + android.os.Build.BRAND;
        phoneInfo += ",\n BOARD: " + android.os.Build.BOARD;
        phoneInfo += ",\n FINGERPRINT: " + android.os.Build.FINGERPRINT;
        phoneInfo += ",\n ID: " + android.os.Build.ID;
        phoneInfo += ",\n MANUFACTURER: " + android.os.Build.MANUFACTURER;
        phoneInfo += ",\n USER: " + android.os.Build.USER;
        return phoneInfo;
    }
}
