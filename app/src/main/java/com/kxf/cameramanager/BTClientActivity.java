package com.kxf.cameramanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kxf.cameramanager.utils.LogUtil;


public class BTClientActivity extends BaseActivity implements OnClickListener {

    private Button btn_an1, btn_an2, btn_an3, btn_an4, btn_time_add, btn_time_red, btn_qian1_add, btn_qian1_red, btn_qian2_add, btn_qian2_red, btn_stop, btn_start, btn_back;
    private TextView tv_time_tv, tv_qian1_tv, tv_qian2_tv, tv_bom_tv;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isWindowChanged) {
            setContentView(R.layout.activity_btclient); // 使用布局文件
            initView();
            initViewSize();
        }
    }

    private void initViewSize() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        final float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        LogUtil.i("height=" + height + ";width=" + width);
        LogUtil.i("density=" + density + ";densityDpi=" + densityDpi);
        //屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        final int screenWidth = (int) (width / density);//屏幕宽度(dp)
        final int screenHeight = (int) (height / density);//屏幕高度(dp)
        LogUtil.i("screenWidth=" + screenWidth + ";screenHeight=" + screenHeight);
        ViewTreeObserver obs = btn_an1.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                btn_an1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int h1 = btn_an1.getHeight() * screenHeight / 360;
                LogUtil.i("h1=" + h1);

                setHeightWeight(btn_an1, h1, h1);
                setHeightWeight(btn_an2, h1, h1);
                setHeightWeight(btn_an3, h1, h1);
                setHeightWeight(btn_an4, h1, h1);

                //btn_time_add, btn_time_red, btn_qian1_add, btn_qian1_red, btn_qian2_add, btn_qian2_red, btn_stop, btn_start, btn_back;
                setHeightWeight(btn_time_add, h1, h1);
                setHeightWeight(btn_time_red, h1, h1);
                setHeightWeight(btn_qian1_add, h1, h1);
                setHeightWeight(btn_qian1_red, h1, h1);
                setHeightWeight(btn_qian2_add, h1, h1);
                setHeightWeight(btn_qian2_red, h1, h1);

                RelativeLayout.LayoutParams lay1 = (RelativeLayout.LayoutParams) btn_stop.getLayoutParams();
                lay1.height = h1;
                lay1.width = h1;
                btn_stop.setLayoutParams(lay1);
                RelativeLayout.LayoutParams lay2 = (RelativeLayout.LayoutParams) btn_start.getLayoutParams();
                lay2.height = h1;
                lay2.width = h1;
                btn_start.setLayoutParams(lay2);
                RelativeLayout.LayoutParams lay3 = (RelativeLayout.LayoutParams) btn_back.getLayoutParams();
                lay3.height = h1;
                lay3.width = h1;
                btn_back.setLayoutParams(lay3);
            }
        });
//        ViewTreeObserver obs1 = btn_stop.getViewTreeObserver();
//        obs1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                btn_stop.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                int h1 = btn_stop.getHeight() * screenHeight / 360;
//                LogUtil.i("btn_stop h1=" + h1);
//                RelativeLayout.LayoutParams lay1 = (RelativeLayout.LayoutParams) btn_stop.getLayoutParams();
//                lay1.height = h1;
//                lay1.width = h1;
//                btn_stop.setLayoutParams(lay1);
////                setHeightWeightRl(btn_stop, h1, h1);
////                setHeightWeightRl(btn_start, h1, h1);
////                setHeightWeightRl(btn_back, h1, h1);
//            }
//        });
    }

    private void setHeightWeight(View v, int height, int weight){
        try {
            ViewGroup.LayoutParams lay = v.getLayoutParams();
            lay.height = height;
            lay.width = weight;
            btn_an1.setLayoutParams(lay);
            LogUtil.i("v.getId()=" + v.getId());
        } catch (Exception e) {
            LogUtil.e("v.getId()=" + v.getId(), e);
        }
    }

    private void setHeightWeightRl(View v, int height, int weight){
        try {
            RelativeLayout.LayoutParams lay = (RelativeLayout.LayoutParams) v.getLayoutParams();
            lay.height = height;
            lay.width = weight;
            btn_an1.setLayoutParams(lay);
            LogUtil.i("v.getId()=" + v.getId());
        } catch (Exception e) {
            LogUtil.e("v.getId()=" + v.getId(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isWindowChanged) {
            if (BluetoothUtils.btThreadInstance != null) {
                BluetoothUtils.btThreadInstance.setMHandler(handler);
            }
        }
    }

    private void initView() {
        btn_an1 = (Button) findViewById(R.id.btn_an1);
        btn_an1.setOnClickListener(this);
        btn_an2 = (Button) findViewById(R.id.btn_an2);
        btn_an2.setOnClickListener(this);
        btn_an3 = (Button) findViewById(R.id.btn_an3);
        btn_an3.setOnClickListener(this);
        btn_an4 = (Button) findViewById(R.id.btn_an4);
        btn_an4.setOnClickListener(this);

        btn_time_add = (Button) findViewById(R.id.btn_time_add);
        btn_time_add.setOnClickListener(this);
        btn_time_red = (Button) findViewById(R.id.btn_time_red);
        btn_time_red.setOnClickListener(this);
        btn_qian1_add = (Button) findViewById(R.id.btn_qian1_add);
        btn_qian1_add.setOnClickListener(this);
        btn_qian1_red = (Button) findViewById(R.id.btn_qian1_red);
        btn_qian1_red.setOnClickListener(this);
        btn_qian2_add = (Button) findViewById(R.id.btn_qian2_add);
        btn_qian2_add.setOnClickListener(this);
        btn_qian2_red = (Button) findViewById(R.id.btn_qian2_red);
        btn_qian2_red.setOnClickListener(this);

        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        tv_time_tv = (TextView) findViewById(R.id.tv_time_tv);
        tv_qian1_tv = (TextView) findViewById(R.id.tv_qian1_tv);
        tv_qian2_tv = (TextView) findViewById(R.id.tv_qian2_tv);
        tv_bom_tv = (TextView) findViewById(R.id.tv_bom_tv);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LogUtil.i("msg=" + msg);
            switch (msg.what) {
                case BluetoothUtils.MESSAGE_READ:
                    String re = (String) msg.obj;
                    if ("A15504800100010002".equals(re)) {
                        btn_an1.setSelected(true);
                        btn_an1.setEnabled(false);
                    } else if ("A15504800100020002".equals(re)) {
                        btn_an2.setSelected(true);
                        btn_an2.setEnabled(false);
                    } else if ("A15504800100030002".equals(re)) {
                        btn_an3.setSelected(true);
                        btn_an3.setEnabled(false);
                    } else if ("A15504800100040002".equals(re)) {
                        btn_an4.setSelected(true);
                        btn_an4.setEnabled(false);
                    }
//					else if ("A15504800100050002".equals(re)){
//						btn_time_add.setSelected(true);
//						btn_time_add.setEnabled(false);
//					} else if ("A15504800100070002".equals(re)){
//						btn_time_red.setSelected(true);
//						btn_time_red.setEnabled(false);
//					} else if ("A15504800100080002".equals(re)){
//						btn_qian1_add.setSelected(true);
//						btn_qian1_add.setEnabled(false);
//					} else if ("A155048001000A0002".equals(re)){
//						btn_qian1_red.setSelected(true);
//						btn_qian1_red.setEnabled(false);
//					} else if ("A155048001000B0002".equals(re)){
//						btn_qian2_add.setSelected(true);
//						btn_qian2_add.setEnabled(false);
//					} else if ("A155048001000D0002".equals(re)){
//						btn_qian2_red.setSelected(true);
//						btn_qian2_red.setEnabled(false);
//					}
                    else if (isStartWith(re, "A155058200310100")) {
                        tv_time_tv.setText(parseHex(re.substring(re.length() - 2)));
                    } else if (isStartWith(re, "A155058200320100")) {
                        tv_qian1_tv.setText(parseHex(re.substring(re.length() - 2)));
                    } else if (isStartWith(re, "A155058200330100")) {
                        tv_qian2_tv.setText(parseHex(re.substring(re.length() - 2)));
                    } else if (isStartWith(re, "A155058200340100")) {
                        tv_bom_tv.setText(parseHex(re.substring(re.length() - 2)));
                    } else if ("A15504800100140002".equals(re)) {
                        btn_start.setSelected(true);
                        btn_start.setEnabled(false);
                    } else if ("A15504800100150002".equals(re)) {
                        btn_stop.setSelected(true);
                        btn_stop.setEnabled(false);
                    } else if ("A155048001000F0002".equals(re)) {
                        startActivity(new Intent(mActivity, LoginActivity.class));
                        finish();
                    }
                    //, btn_stop, btn_start, btn_back;
                    //btn_time_add, btn_time_red
                    //	, btn_qian1_add, btn_qian1_red, btn_qian2_add, btn_qian2_red
                    //tv_time_tv, tv_qian1_tv, tv_qian2_tv
                    break;
                case BluetoothUtils.MESSAGE_WRITE:
                    String send = (String) msg.obj;
                    if (null != BluetoothUtils.btThreadInstance) {
                        BluetoothUtils.btThreadInstance.write(BluetoothUtils.getHexBytes(send));
                    }else {
                        handler.sendEmptyMessage(BluetoothUtils.MESSAGE_ERROR);
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

    private boolean isStartWith(String src, String startStr) {
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(startStr) || startStr.length() > src.length()) {
            return false;
        }
        return src.startsWith(startStr);
    }

    @Override
    public void onClick(View v) {
        LogUtil.d("v=" + v);
        switch (v.getId()) {
            case R.id.btn_an1:
                sendByBT("A15506830055010001");
                break;
            case R.id.btn_an2:
                sendByBT("A15506830055010002");
                break;
            case R.id.btn_an3:
                sendByBT("A15506830055010003");
                break;
            case R.id.btn_an4:
                sendByBT("A15506830055010004");
                break;
            case R.id.btn_time_add:
                sendByBT("A15504800100050001");
                break;
            case R.id.btn_time_red:
                sendByBT("A15504800100070001");
                break;
            case R.id.btn_qian1_add:
                sendByBT("A15506830055010005");
                break;
            case R.id.btn_qian1_red:
                sendByBT("A15506830055010007");
                break;
            case R.id.btn_qian2_add:
                sendByBT("A15506830055010006");
                break;
            case R.id.btn_qian2_red:
                sendByBT("A15506830055010008");
                break;
            case R.id.btn_stop:
                sendByBT("A1550683005B010002");
                break;
            case R.id.btn_start:
                sendByBT("A1550683005B010001");
                break;
            case R.id.btn_back:
                sendByBT("A1550683005B010003");
                break;
        }
    }

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
    public void finish() {
        super.finish();
        if (isWindowChanged) {
            //回到主界面后检查是否已成功连接蓝牙设备
            if (BluetoothUtils.btThreadInstance != null) {
                BluetoothUtils.btThreadInstance.setMHandler(null);
            }
        }
    }

    private String parseHex(String hex){
        String re = "00";
        try {
            int a = Integer.parseInt(hex, 16);
            re = String.format("%02d", a);
        } catch (Exception e) {
            LogUtil.e("parseHex(String hex) hex=" + hex, e);
        }
        return re;
    }
}
