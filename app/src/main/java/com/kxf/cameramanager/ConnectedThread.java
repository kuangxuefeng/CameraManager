package com.kxf.cameramanager;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.text.TextUtils;

import com.kxf.cameramanager.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kxf on 2017/11/08.
 */

public class ConnectedThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler mHandler;
    private String cacheString;

    public void setMHandler(Handler handler) {
        mHandler = handler;
    }

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        mHandler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            LogUtil.e("IOException", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {

        int len; // len returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            byte[] buffer = new byte[1024];
            try {
                LogUtil.i("准备接收数据！");
                // Read from the InputStream
                len = mmInStream.read(buffer);
                // Send the obtained len to the UI activity
                byte[] bufferR = new byte[len];
                System.arraycopy(buffer, 0, bufferR, 0, len);
                String s = BluetoothUtils.byte2HexStr(bufferR);
                LogUtil.i("接收到数据["+ len +"]：" + s);
                LogUtil.i("mHandler=" + mHandler);
                if (null != mHandler && readSuccess(s)) {
                    LogUtil.i("readSuccess cacheString=" + cacheString);
                    mHandler.obtainMessage(BluetoothUtils.MESSAGE_READ, cacheString.length(), -1, cacheString)
                            .sendToTarget();
                    cacheString = null;
                }
            } catch (IOException e) {
                LogUtil.e("IOException", e);
                BluetoothUtils.btThreadInstance = null;
                try {
                    mmSocket.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                LogUtil.i("mHandler=" + mHandler);
                if (null != mHandler){
                    mHandler.obtainMessage(BluetoothUtils.MESSAGE_ERROR)
                            .sendToTarget();
                }
                break;
            }
        }
    }

    private boolean readSuccess(String s){
        LogUtil.i("cacheString=" + cacheString + ";s=" + s);
        boolean re = false;
        if (TextUtils.isEmpty(cacheString)){
            cacheString = s;
        }else {
            cacheString = cacheString + s;
        }
        if (!TextUtils.isEmpty(cacheString) && cacheString.length()>=18){
            re = true;
        }
        LogUtil.i("re=" + re);
        return re;
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(final byte[] bytes) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mmOutStream.write(bytes);
                    mmOutStream.flush();
                    LogUtil.i("发送成功，发送字节：" + bytes.length);
                } catch (IOException e) {
                    LogUtil.e("IOException", e);
                    BluetoothUtils.btThreadInstance = null;
                    try {
                        mmSocket.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    LogUtil.i("mHandler=" + mHandler);
                    if (null != mHandler){
                        mHandler.obtainMessage(BluetoothUtils.MESSAGE_ERROR)
                                .sendToTarget();
                    }
                }
            }
        }).start();
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }

}
