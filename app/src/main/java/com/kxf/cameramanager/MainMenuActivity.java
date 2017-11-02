package com.kxf.cameramanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kxf.cameramanager.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class MainMenuActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_checkup, btn_control;

    private Button btn_an1, btn_an2, btn_an3, btn_an4, btn_time_add, btn_time_red, btn_qian1_add, btn_qian1_red, btn_qian2_add, btn_qian2_red, btn_stop, btn_start, btn_back;
    private TextView tv_time_tv, tv_qian1_tv, tv_qian2_tv;
    private static boolean isAlive;
    private static boolean BluetoothIsConnect;
    // 声明BluetoothAdapter类 BluetoothAdapter代表本地的蓝牙适配器设备让用户能执行基本的蓝牙任务
    private BluetoothAdapter localBluetoothAdapter = null;
    // 声明一个 BluetoothServerSocket类以监听其它设备对本机的连接请求等
    private BluetoothSocket btSocket = null;
    private BluetoothDevice btDevice;

    /**
     * 该方法是为了使用带有listenUsingRfcommWithServiceRecord(String, UUID)
     * 方法来进行对等的蓝牙应用而设计的。 如果你正试图连接蓝牙串口，那么使用众所周知的 SPP UUID
     * 00001101-0000-1000-8000-00805F9B34FB。
     * 但是你如果正试图连接Android设备那么请你生成你自己的专有UUID。
     **/
    private OutputStream outStream = null;
    private InputStream inStream = null;

    /**
     * 该方法是为了使用带有listenUsingRfcommWithServiceRecord(String, UUID)
     * 方法来进行对等的蓝牙应用而设计的。 如果你正试图连接蓝牙串口，那么使用众所周知的 SPP UUID
     * 00001101-0000-1000-8000-00805F9B34FB。
     * 但是你如果正试图连接Android设备那么请你生成你自己的专有UUID。
     **/
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BroadcastReceiver btConnectReceiver;
    private BroadcastReceiver btDisconnectReceiver;
    private IntentFilter connectIntentFilter;
    private IntentFilter disconnectIntentFilter;
    private boolean isConnect = false;
    private BroadcastReceiver mReceiver;

    private static String uuid = UUID.randomUUID().toString();

    @Override
    protected void onResume() {
        super.onResume();
//		checkBTCon();
        if (isWindowChanged && !isConnect) {
            initBT();
        }
    }

    private void initBT() {
        LogUtil.i("initBT........");
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtil.i("onReceive(Context context, Intent intent) BluetoothDevice.ACTION_FOUND");
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//					localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//					if (null == localBluetoothAdapter){
//						showDialog("当前蓝牙未连接，是否连接？", null, null, "是", new Runnable() {
//							@Override
//							public void run() {
//								finish();
//							}
//						});
//						return;
//					}
//					List<BluetoothDevice> bts = getBt();
//					if (null == bts || bts.size()<1){
//						showDialog("当前蓝牙未连接，是否连接？", "否", new Runnable() {
//							@Override
//							public void run() {
//								finish();
//							}
//						}, "是", new Runnable() {
//							@Override
//							public void run() {
//								startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
//							}
//						});
//					}else {
//						isConnect=true;
//						btDevice = bts.get(0);
//						connectInThread();
//					}
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, intentFilter);

        btConnectReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "--connected");
                isConnect = true;
            }
        };
        connectIntentFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(btConnectReceiver, connectIntentFilter);

        btDisconnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                // TODO Auto-generated method stub
                Log.e(TAG, "-- disconnected");
                isConnect = false;
                new TryToConnet().start();
            }
        };

        disconnectIntentFilter = new IntentFilter(
                BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(btDisconnectReceiver, disconnectIntentFilter);

        connectInThread();
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2000:
                    String re = (String) msg.obj;
                    if ("A15504800100010002".equals(re)) {
                        Intent intent = new Intent(mActivity, BTClientActivity.class);
                        startActivity(intent);
                    }
                    break;
                case 2001:
                    final String send = (String) msg.obj;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                outStream.write(getHexBytes(send));
                                outStream.flush();
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogUtil.e("发送出错！");
                                isConnect = false;
//								closeBT();
//								checkBTCon();
                            }
                        }
                    }).start();
                    break;
                case 2002:
                    break;
                default:
                    break;
            }
        }
    };

    private boolean isStartWith(String src, String startStr) {
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(startStr) || startStr.length() > src.length()) {
            return false;
        }
        return src.startsWith(startStr);
    }

    private void closeBT() {
        isConnect = false;
        if (outStream != null) {
            try {
                outStream.flush();
                outStream.close();
                outStream = null;
            } catch (IOException e) {
                e.printStackTrace();
                outStream = null;
            }
        }
        try {
            if (btSocket != null) {
                btSocket.close();
                btSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            btSocket = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeBT();
        if (isWindowChanged) {
            try {
                unregisterReceiver(mReceiver);
                unregisterReceiver(btConnectReceiver);
                unregisterReceiver(btDisconnectReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            uuid = "";
            if (isAlive) {
                isAlive = false;
            }
        }
    }

    private byte[] getHexBytes(String message) {
        int len = message.length() / 2;
        char[] chars = message.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        LogUtil.i("bytes=" + bytes);
        return bytes;
    }

    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
//            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }


    private List<BluetoothDevice> getBt() {
        LogUtil.d("getBt()......");
        List<BluetoothDevice> deviceList = new ArrayList<>();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {//得到连接状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);

            if (state == BluetoothAdapter.STATE_CONNECTED) {
                LogUtil.i("BluetoothAdapter.STATE_CONNECTED");
                Set<BluetoothDevice> devices = adapter.getBondedDevices();
                LogUtil.i("devices:" + devices.size());

                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) {
                        LogUtil.i("connected:" + device.getName());
                        deviceList.add(device);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.i("deviceList=" + deviceList);
        return deviceList;
    }

    private void sendByBT(String str) {
        LogUtil.i("str=" + str);
        if (TextUtils.isEmpty(str)) {
            LogUtil.e("发送数据为空！");
            return;
        }
        Message message = handler.obtainMessage(2001);
        message.obj = str;
        message.sendToTarget();
    }

    private void connectInThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (null == localBluetoothAdapter) {
                    showDialog("当前蓝牙未连接，是否连接？", null, null, "是", new Runnable() {
                        @Override
                        public void run() {
//                            finish();
                        }
                    });
                    return;
                }
                List<BluetoothDevice> bts = getBt();
                if (null == bts || bts.size() < 1) {
                    showDialog("当前蓝牙未连接，是否连接？", "否", new Runnable() {
                        @Override
                        public void run() {
//                            finish();
                        }
                    }, "是", new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                        }
                    });
                    return;
                } else {
                    isConnect = true;
                    btDevice = bts.get(0);
                }
                try {
                    btSocket = (BluetoothSocket) btDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(btDevice, 1);
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                }
                connect();
            }
        }).start();
    }

    private void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i("btDevice=" + btDevice);
//		try {
//			btSocket=btDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
                try {
                    btSocket.connect();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                LogUtil.i("btSocket.isConnected()=" + btSocket.isConnected());
                if (!btSocket.isConnected()) {
                    return;
                }
                try {
                    outStream = btSocket.getOutputStream();
                    inStream = btSocket.getInputStream();
                    LogUtil.i("outStream=" + outStream);
                    LogUtil.i("inStream=" + inStream);
                    isConnect = true;
                    showToast("连接成功");
                    new Thread(new Connect()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("连接失败");
                }
            }
        }).start();
    }

    private class TryToConnet extends Thread {
        public void run() {
            /* 此处必须重新创建一个socket，否则重新连接后无法传输数据，个人猜想是Rfcomm通道已经改变 */
            try {
                btSocket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "-- failed to create btSocket");
            }
            while (true) {
                try {
                    btSocket.connect();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (isConnect) {
                    Log.e(TAG, "-- Connect again,ending the TryToConnet thread");
                    break;
                }
            }
            try {
                outStream = btSocket.getOutputStream();
                inStream = btSocket.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class Connect implements Runnable {
        @Override
        public void run() {
            uuid = UUID.randomUUID().toString();
            String uuidRun = uuid;
            LogUtil.i("uuidRun=" + uuidRun + "  ;uuidRun.equals(uuid)=" + uuidRun.equals(uuid));
            byte[] buffer = new byte[25];
            while (uuidRun.equals(uuid)) {
                LogUtil.i("Thread.currentThread().getId()=" + Thread.currentThread().getId());
                try {
                    inStream.read(buffer);
                    LogUtil.i("inStream.read(buffer)=" + byte2HexStr(buffer));
                    showToast("接收到：" + byte2HexStr(buffer));
                    handler.obtainMessage(2000, byte2HexStr(buffer)).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        if (isWindowChanged) {
            initView();
        }
    }

    private void initView() {
        btn_checkup = (Button) findViewById(R.id.btn_checkup);
        btn_control = (Button) findViewById(R.id.btn_control);

        btn_checkup.setOnClickListener(this);
        btn_control.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        btn_checkup.setEnabled(false);
        btn_control.setEnabled(false);
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_checkup:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_control:
//                intent = new Intent(this, BTClientActivity.class);
//                startActivity(intent);
                sendByBT("A15506830080010001");
                break;
        }
        btn_checkup.setEnabled(true);
        btn_control.setEnabled(true);
    }
}
