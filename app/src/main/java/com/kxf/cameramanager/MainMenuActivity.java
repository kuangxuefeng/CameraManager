package com.kxf.cameramanager;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kxf.cameramanager.utils.DeviceInfoUtil;
import com.kxf.cameramanager.utils.LogUtil;

import utils.CheckDateTime;

public class MainMenuActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1;
    private Button btn_checkup, btn_control;
    private TextView tv_info;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onResume() {
        super.onResume();
//		checkBTCon();
        if (isWindowChanged) {
            //回到主界面后检查是否已成功连接蓝牙设备
            if (BluetoothUtils.btThreadInstance != null) {
                BluetoothUtils.btThreadInstance.setMHandler(handler);
            }
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BluetoothUtils.MESSAGE_READ:
                    String re = (String) msg.obj;
                    if ("A15506830080010001".equals(re)) {
                        BluetoothUtils.btThreadInstance.setMHandler(null);
                        Intent intent = new Intent(mActivity, BTClientActivity.class);
                        startActivity(intent);
                    }
                    break;
                case BluetoothUtils.MESSAGE_WRITE:
                    String send = (String) msg.obj;
                    if (null != BluetoothUtils.btThreadInstance){
                        BluetoothUtils.btThreadInstance.write(BluetoothUtils.getHexBytes(send));
                    }
                    break;
                case BluetoothUtils.MESSAGE_ERROR:
                    //进入蓝牙设备连接界面
                    Intent intent = new Intent();
                    intent.setClass(mContext, DevicesListActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    private void sendByBT(String str) {
        LogUtil.i("str=" + str);
        if (TextUtils.isEmpty(str)) {
            LogUtil.e("发送数据为空！");
            return;
        }
        Message message = handler.obtainMessage(BluetoothUtils.MESSAGE_WRITE);
        message.obj = str;
        message.sendToTarget();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        if (isWindowChanged) {
            initView();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.i("DeviceInfoUtil.getMacAddress(mContext)=" + DeviceInfoUtil.getMacAddress(mContext));
                    LogUtil.i("DeviceInfoUtil.getBlueToothAddress()=" + DeviceInfoUtil.getBlueToothAddress());
                    LogUtil.i("DeviceInfoUtil.getInfo()=" + DeviceInfoUtil.getInfo());
                }
            }).start();

            // 获取蓝牙适配器
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(mContext, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            }

            //请求开启蓝牙
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            if (null == BluetoothUtils.getBluetoothSocket()){
                //进入蓝牙设备连接界面
                Intent intent = new Intent();
                intent.setClass(mContext, DevicesListActivity.class);
                startActivity(intent);
            }
        }
    }

    private void initView() {
        btn_checkup = (Button) findViewById(R.id.btn_checkup);
        btn_control = (Button) findViewById(R.id.btn_control);
        tv_info = (TextView) findViewById(R.id.tv_info);

        if (!CheckDateTime.isValidTime(BuildConfig.BUILD_TIME_LONG, 1, 0, 0, 0, 0, 0)){
            tv_info.setVisibility(View.VISIBLE);
        }

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
