package com.kxf.cameramanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kxf.cameramanager.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by kxf on 2017/11/08.
 */

public class DevicesListActivity extends BaseActivity {

    private ProgressBar progressbarSearchDevices;

    private BluetoothAdapter mBluetoothAdapter;
    private List<String> mDevicesArray = new ArrayList<String>();
    private DevicesListAdapter<String> devicesListAdapter;
    private List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
    private List<String> deviceAddressList = new ArrayList<String>();
    private boolean isConnecting = false;
    private BluetoothSocket socket = null;
    private TextView tv_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isWindowChanged){
            setContentView(R.layout.listview_devices);
            ListView listView = (ListView) findViewById(R.id.listview_devices);
            tv_info = (TextView) findViewById(R.id.tv_info);
            progressbarSearchDevices = (ProgressBar) findViewById(R.id.progressbar_search_devices);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // 将已配对的设备添加到列表中
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (deviceAddressList.indexOf(device.getAddress())<0){
                        deviceAddressList.add(device.getAddress());
                        mDevicesArray.add(device.getName() + "\n" + device.getAddress());
                        deviceList.add(device);
                    }
                }
            }


            // 注册广播接收器，以获取蓝牙设备搜索结果
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
            // 搜索蓝牙设备
            mBluetoothAdapter.startDiscovery();
            progressbarSearchDevices.setVisibility(View.VISIBLE);

            // 为ListView控件设置适配器
            devicesListAdapter = new DevicesListAdapter<String>(getApplicationContext(), mDevicesArray);
            listView.setAdapter(devicesListAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isWindowChanged){
            unregisterReceiver(mReceiver);
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (deviceAddressList.indexOf(device.getAddress())<0){
                    deviceAddressList.add(device.getAddress());
                    deviceList.add(device);
                    // Add the name and address to an array adapter to show in a ListView
                    mDevicesArray.add(device.getName() + "\n" + device.getAddress());
//                    Toast.makeText(getApplicationContext(), device.getName() + "\n" + device.getAddress(), Toast.LENGTH_SHORT).show();
                    devicesListAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    class DevicesListAdapter<T> extends BaseAdapter {

        Context context;
        List<T> list;
        private LayoutInflater inflater;

        public DevicesListAdapter(Context context, List list) {
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public T getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = inflater.inflate(R.layout.item_listview_devices, null);
                holder.deviceName = (TextView) convertView.findViewById(R.id.item_device_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.deviceName.setText(list.get(position).toString());
            holder.deviceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.i("开始连接bt");
                    if (isConnecting){
                        return;
                    }
                    isConnecting = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            BluetoothDevice device = deviceList.get(position);
                            updateTvInfo("正在连接" + device.getName() + "...");
                            try {
                                // 蓝牙串口服务对应的UUID。如使用的是其它蓝牙服务，需更改下面的字符串
                                UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                            } catch (Exception e) {
                                LogUtil.e("获取Socket失败", e);
                                e.printStackTrace();
                                showToast("获取Socket失败");
                                updateTvInfo("连接" + device.getName() + "失败！");
                                isConnecting = false;
                                return;
                            }
                            mBluetoothAdapter.cancelDiscovery();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // Connect the device through the socket. This will block
                                        // until it succeeds or throws an exception
                                        socket.connect();
                                        LogUtil.i("连接成功");
                                        showToast("连接成功");
                                        BluetoothUtils.setBluetoothSocket(socket);
                                        mActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressbarSearchDevices.setVisibility(View.INVISIBLE);
                                                isConnecting = false;
                                                // 连接成功，返回主界面
                                                finish();
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        // Unable to connect; close the socket and get out
                                        LogUtil.e("连接失败", e);
                                        updateTvInfo("连接失败");
                                        try {
                                            socket.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                            LogUtil.e("IOException", e1);
                                        }
                                        isConnecting = false;
                                        return;
                                    }
                                    isConnecting = false;
                                }
                            }).start();
                        }
                    }).start();
                }
            });
            return convertView;
        }

        protected class Holder {
            TextView deviceName;
        }

    }

    private void updateTvInfo(final String s){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_info.setText(s);
            }
        });
    }
}
