package com.kxf.cameramanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.kxf.cameramanager.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ShowPicActivity extends BaseActivity implements View.OnClickListener, FileChooseDialog.onFileChooseListener {

    private static final int FILE_SELECT_CODE = 500;
    private ImageView iv_left2, iv_left1, iv_center, iv_right1, iv_right2;
    private ImageView iv_left2_r, iv_left1_r, iv_center_r, iv_right1_r, iv_right2_r;
    private Button btn_out, btn_delete, btn_last, btn_back, btn_next;
    private Button btn_delete_r, btn_last_r, btn_next_r;
    private ProgressBar load_pb;
    private int index = 0;
    private int index_r = 0;
    private File[] fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isWindowChanged){
            setContentView(R.layout.activity_show_pic);
            initView();
        }
    }

    private void initView() {
        load_pb = (ProgressBar) findViewById(R.id.load_pb);
        iv_left2 = (ImageView) findViewById(R.id.iv_left2);
        iv_left1 = (ImageView) findViewById(R.id.iv_left1);
        iv_center = (ImageView) findViewById(R.id.iv_center);
        iv_right1 = (ImageView) findViewById(R.id.iv_right1);
        iv_right2 = (ImageView) findViewById(R.id.iv_right2);

        iv_left2_r = (ImageView) findViewById(R.id.iv_left2_r);
        iv_left1_r = (ImageView) findViewById(R.id.iv_left1_r);
        iv_center_r = (ImageView) findViewById(R.id.iv_center_r);
        iv_right1_r = (ImageView) findViewById(R.id.iv_right1_r);
        iv_right2_r = (ImageView) findViewById(R.id.iv_right2_r);

        btn_out = (Button) findViewById(R.id.btn_out);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_last = (Button) findViewById(R.id.btn_last);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_next = (Button) findViewById(R.id.btn_next);

        btn_delete_r = (Button) findViewById(R.id.btn_delete_r);
        btn_last_r = (Button) findViewById(R.id.btn_last_r);
        btn_next_r = (Button) findViewById(R.id.btn_next_r);

        btn_out.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_last.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        btn_delete_r.setOnClickListener(this);
        btn_last_r.setOnClickListener(this);
        btn_next_r.setOnClickListener(this);

        updateFiles();
        updateView();
        updateView_r();
    }

    private void updateFiles(){
        String SavePath = getBasePath();
        File path = new File(SavePath);
        fs = path.listFiles();
        if (null != fs && fs.length>0){
            List<File> ls;
            ls = java.util.Arrays.asList(fs);
            Collections.reverse(ls);
            fs = ls.toArray(new File[0]);
            ls = null;
        }
    }

    private void updateView() {
        if (null != fs && fs.length>0){
            iv_center.setImageBitmap(BitmapFactory.decodeFile(fs[index].getPath()));
            if ((index - 1)>=0){
                iv_left1.setImageBitmap(BitmapFactory.decodeFile(fs[index - 1].getPath()));
            }else {
                iv_left1.setImageBitmap(null);
            }
            if ((index - 2)>=0){
                iv_left2.setImageBitmap(BitmapFactory.decodeFile(fs[index - 2].getPath()));
            }else {
                iv_left2.setImageBitmap(null);
            }
            if ((index + 1)<fs.length){
                iv_right1.setImageBitmap(BitmapFactory.decodeFile(fs[index + 1].getPath()));
            }else {
                iv_right1.setImageBitmap(null);
            }
            if ((index + 2)<fs.length){
                iv_right2.setImageBitmap(BitmapFactory.decodeFile(fs[index + 2].getPath()));
            }else {
                iv_right2.setImageBitmap(null);
            }
        }else {
            iv_center.setImageBitmap(null);
        }
    }

    private void updateView_r() {
        if (null != fs && fs.length>0){
            iv_center_r.setImageBitmap(BitmapFactory.decodeFile(fs[index_r].getPath()));
            if ((index_r - 1)>=0){
                iv_left1_r.setImageBitmap(BitmapFactory.decodeFile(fs[index_r - 1].getPath()));
            }else {
                iv_left1_r.setImageBitmap(null);
            }
            if ((index_r - 2)>=0){
                iv_left2_r.setImageBitmap(BitmapFactory.decodeFile(fs[index_r - 2].getPath()));
            }else {
                iv_left2_r.setImageBitmap(null);
            }
            if ((index_r + 1)<fs.length){
                iv_right1_r.setImageBitmap(BitmapFactory.decodeFile(fs[index_r + 1].getPath()));
            }else {
                iv_right1_r.setImageBitmap(null);
            }
            if ((index_r + 2)<fs.length){
                iv_right2_r.setImageBitmap(BitmapFactory.decodeFile(fs[index_r + 2].getPath()));
            }else {
                iv_right2_r.setImageBitmap(null);
            }
        }else {
            iv_center_r.setImageBitmap(null);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_out:
                findUsb();
                FileChooseDialog fd = new FileChooseDialog(this, R.style.Dialog_Fullscreen);
                fd.setListener(this);
                fd.setPathRoot(MyApplication.getSDCardPath());
                fd.show();
                break;
            case R.id.btn_delete:
                if (null != fs && fs.length > 0 && index<fs.length){
                    fs[index].delete();
                    if (index>0){
                        index --;
                    }
                    updateFiles();
                    if (index_r>0 && null != fs && index_r >= fs.length){
                        index_r --;
                    }
                    updateView();
                    updateView_r();
                }
                break;
            case R.id.btn_delete_r:
                if (null != fs && fs.length > 0 && index_r<fs.length){
                    fs[index_r].delete();
                    if (index_r>0){
                        index_r --;
                    }
                    updateFiles();
                    if (index>0 && null != fs && index >= fs.length){
                        index --;
                    }
                    updateView();
                    updateView_r();
                }
                break;
            case R.id.btn_last:
                if ((index - 1)>-1){
                    index -= 1;
                    updateView();
                }
                break;
            case R.id.btn_last_r:
                if ((index_r - 1)>-1){
                    index_r -= 1;
                    updateView_r();
                }
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next:
                if (null != fs && (index + 1)<fs.length){
                    index += 1;
                    updateView();
                }
                break;
            case R.id.btn_next_r:
                if (null != fs && (index_r + 1)<fs.length){
                    index_r += 1;
                    updateView_r();
                }
                break;
        }
    }

    private String getBasePath() {
        String savePath = MyApplication.getSDCardPath() + "/DCIM/feng" + "/" + userLogin.getId();// /feng/ScreenImage
        return savePath;
    }

    @Override
    public void choose(final String path) {
        LogUtil.d("path=" + path);
        load_pb.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyFolder(getBasePath(), path);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        load_pb.setVisibility(View.GONE);
                        showDialogYes("复制完成！");
                    }
                });
            }
        }).start();
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    private void findUsb(){
        // 获取USB设备
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (manager == null) {
            return;
        } else {
            LogUtil.i("usb设备：" + String.valueOf(manager.toString()));
        }
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        LogUtil.i("usb设备：" + String.valueOf(deviceList.size()));
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        ArrayList<String> USBDeviceList = new ArrayList<String>(); // 存放USB设备的数量
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();

            USBDeviceList.add(String.valueOf(device.getVendorId()));
            USBDeviceList.add(String.valueOf(device.getProductId()));

            // 在这里添加处理设备的代码
            if (device.getVendorId() == 1155 && device.getProductId() == 22352) {
                LogUtil.i("找到设备 device=" + device);
            }
        }
        LogUtil.d("USBDeviceList=" + USBDeviceList);
    }
}
