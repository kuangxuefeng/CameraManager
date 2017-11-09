package com.kxf.cameramanager;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

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

    public static ConnectedThread instance;

    public void setMHandler(Handler handler){
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

        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            byte[] buffer = new byte[100];
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                if (null != mHandler){
                    mHandler.obtainMessage(BluetoothUtils.MESSAGE_READ, bytes, -1, BluetoothUtils.byte2HexStr(buffer))
                            .sendToTarget();
                }
            } catch (IOException e) {
                LogUtil.e("IOException", e);
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
            LogUtil.i("发送成功，发送字节：" + bytes.length);
        } catch (IOException e) {
            LogUtil.e("IOException", e);
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

}
