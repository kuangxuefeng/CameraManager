package com.kxf.cameramanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.kxf.cameramanager.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class BTClientActivity extends BaseActivity implements OnClickListener {

	private static boolean isAlive;
	private static boolean BluetoothIsConnect;
	// 声明BluetoothAdapter类 BluetoothAdapter代表本地的蓝牙适配器设备让用户能执行基本的蓝牙任务
	private BluetoothAdapter localBluetoothAdapter = null;
	// 声明一个 BluetoothServerSocket类以监听其它设备对本机的连接请求等
	private BluetoothSocket btSocket = null;

	/**
	 * 该方法是为了使用带有listenUsingRfcommWithServiceRecord(String, UUID)
	 * 方法来进行对等的蓝牙应用而设计的。 如果你正试图连接蓝牙串口，那么使用众所周知的 SPP UUID
	 * 00001101-0000-1000-8000-00805F9B34FB。
	 * 但是你如果正试图连接Android设备那么请你生成你自己的专有UUID。
	 **/
	private OutputStream outStream = null;

	/**
	 * 该方法是为了使用带有listenUsingRfcommWithServiceRecord(String, UUID)
	 * 方法来进行对等的蓝牙应用而设计的。 如果你正试图连接蓝牙串口，那么使用众所周知的 SPP UUID
	 * 00001101-0000-1000-8000-00805F9B34FB。
	 * 但是你如果正试图连接Android设备那么请你生成你自己的专有UUID。
	 **/
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_btclient); // 使用布局文件
	}

	@Override
	protected void onResume() {
		super.onResume();

		checkBTCon();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 2000:
					sendByBT("A155058103020000");
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								InputStream is = btSocket.getInputStream();
								while(true){
									LogUtil.i("接收轮询");
									byte[] buffer =new byte[1024];
									int count = is.read(buffer);
									String strGet = byte2HexStr(buffer);
									LogUtil.i("strGet=" + strGet);
									if ("A155058103020000".equals(strGet)){
										sendByBT("A155058103020000");
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
								LogUtil.e("接收出错！");
								checkBTCon();
							}
						}
					}).start();
					break;
				case 2001:
					final String send = (String) msg.obj;
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								outStream.write(getHexBytes(send));
								outStream.flush();
							} catch (IOException e) {
								e.printStackTrace();
								LogUtil.e("发送出错！");
								checkBTCon();
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

	@Override
	public void onPause() {
		super.onPause();
		if (outStream != null) {
			try {
				outStream.flush();
				outStream.close();
				outStream = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			if (btSocket != null) {
				btSocket.close();
				btSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isAlive) {
			isAlive = false;
		}
	}

	/*
	 * 判断并控制小车的运行方向
	 */
	public void carMove(int vId) {
		LogUtil.i("carMove(int vId)...");
		String message = "A55A04B3B7AA"; // message为我们要发送的十六进制数值,默认为停止
		byte[] msgBuffer;
		if (btSocket == null) {
			LogUtil.e("btSocket为空");
			Toast.makeText(this, "发送消息失败！", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			outStream = btSocket.getOutputStream(); // 打开IO流，从而获得各自的InputStream和OutputStream对象
		} catch (IOException e) {
			// LogUtil.e("ON RESUME: Output stream creation failed.", e);
			LogUtil.e("获取输出流对象失败！！");
			Toast.makeText(this, "获取输出流对象失败！", Toast.LENGTH_SHORT).show();
		}
//		switch (vId) {
//			case R.id.btnF:
//				LogUtil.i("前进 ");
//				message = "A55A04B1B5AA"; // 为前进， 赋值给message
//				break;
//			case R.id.btnL:
//				LogUtil.i("向左");
//				message = "A55A04B5B9AA"; // 为向左，赋值给message
//				break;
//			case R.id.btnB:
//				LogUtil.i("后退");
//				message = "A55A04B2B6AA"; // 为后退，赋值给message
//				break;
//			case R.id.btnR:
//				LogUtil.i("向右");
//				message = "A55A04B4B8AA"; // 为向右，赋值给message  A55A04B4B8AA
//				break;
//			case R.id.btnS:
//				LogUtil.i("停止");
//				message = "A55A04B3B7AA"; // 为停止，赋值给message
//				break;
//			default:
//
//				break;
//		}
//		msgBuffer = getHexBytes(message); // 讲字符型数据message 装换成字节数组
//		// 赋值给msgBuffer
//		try {
//			outStream.write(msgBuffer); // 讲msgBuffer 数据写入io流
//			outStream.flush();
//		} catch (IOException e) {
//			LogUtil.e("发送消息失败！！");
//			Toast.makeText(this, "发送消息失败！", Toast.LENGTH_SHORT).show();
//		}
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
		return bytes;
	}

	public static String byte2HexStr(byte[] b)
	{
		String stmp="";
		StringBuilder sb = new StringBuilder("");
		for (int n=0;n<b.length;n++)
		{
			stmp = Integer.toHexString(b[n] & 0xFF);
			sb.append((stmp.length()==1)? "0"+stmp : stmp);
//            sb.append(" ");
		}
		return sb.toString().toUpperCase().trim();
	}

	@Override
	public void onClick(View v) {

	}

	private List<BluetoothDevice> getBt(){
		LogUtil.d("getBt()......");
		List<BluetoothDevice> deviceList = new ArrayList<>();
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
		try {//得到连接状态的方法
			Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
			//打开权限
			method.setAccessible(true);
			int state = (int) method.invoke(adapter, (Object[]) null);

			if(state == BluetoothAdapter.STATE_CONNECTED){
				LogUtil.i("BluetoothAdapter.STATE_CONNECTED");
				Set<BluetoothDevice> devices = adapter.getBondedDevices();
				LogUtil.i("devices:"+devices.size());

				for(BluetoothDevice device : devices){
					Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
					method.setAccessible(true);
					boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
					if(isConnected){
						LogUtil.i("connected:"+device.getName());
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

	public void checkBTCon() {
		if (null == btSocket || null == outStream){
			new Thread(new Runnable() {
				@Override
				public void run() {
					LogUtil.i("checkBTCon  run...");
					localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					if (null == localBluetoothAdapter){
						showDialog("当前蓝牙未连接，是否连接？", null, null, "是", new Runnable() {
							@Override
							public void run() {
								finish();
							}
						});
						return;
					}
					List<BluetoothDevice> bts = getBt();
					if (null == bts || bts.size()<1){
						showDialog("当前蓝牙未连接，是否连接？", "否", new Runnable() {
							@Override
							public void run() {
								finish();
							}
						}, "是", new Runnable() {
							@Override
							public void run() {
								startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
							}
						});
					}else {
						BluetoothDevice bluetoothDevice = localBluetoothAdapter
								.getRemoteDevice(bts.get(0).getAddress());
						try {
							Method m = bluetoothDevice.getClass()
									.getMethod("createRfcommSocket",
											new Class[] { int.class });
							btSocket = (BluetoothSocket) m.invoke(
									bluetoothDevice, Integer.valueOf(1)); // 1==RFCOMM
							btSocket.connect();
							outStream = btSocket.getOutputStream();
							handler.sendEmptyMessage(2000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					LogUtil.i("outStream=" + outStream);
				}
			}).start();
		}
	}

	private void sendByBT(String str){
		if (TextUtils.isEmpty(str)){
			LogUtil.e("发送数据为空！");
			return;
		}
		Message message = handler.obtainMessage(2001);
		message.obj = str;
		message.sendToTarget();
	}
}
