package com.kxf.cameramanager;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ShowPicActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_left2, iv_left1, iv_center, iv_right1, iv_right2;
    private Button btn_out, btn_delete, btn_last, btn_back, btn_next;
    private int index = 0;
    private File[] fs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pic);

        initView();
    }

    private void initView() {
        iv_left2 = (ImageView) findViewById(R.id.iv_left2);
        iv_left1 = (ImageView) findViewById(R.id.iv_left1);
        iv_center = (ImageView) findViewById(R.id.iv_center);
        iv_right1 = (ImageView) findViewById(R.id.iv_right1);
        iv_right2 = (ImageView) findViewById(R.id.iv_right2);

        btn_out = (Button) findViewById(R.id.btn_out);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_last = (Button) findViewById(R.id.btn_last);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_next = (Button) findViewById(R.id.btn_next);

        btn_out.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_last.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        updateView();
    }

    private void updateView() {
        String SavePath = getBasePath();
        File path = new File(SavePath);
        fs = path.listFiles();
        if (null != fs && fs.length>0){
            List<File> ls;
            ls = java.util.Arrays.asList(fs);
            Collections.reverse(ls);
            fs = ls.toArray(new File[0]);
            ls = null;
            iv_center.setImageURI(Uri.fromFile(fs[index]));
            if ((index - 1)>=0){
                iv_left1.setImageURI(Uri.fromFile(fs[index - 1]));
            }else {
                iv_left1.setImageURI(null);
            }
            if ((index - 2)>=0){
                iv_left2.setImageURI(Uri.fromFile(fs[index - 2]));
            }else {
                iv_left2.setImageURI(null);
            }
            if ((index + 1)<fs.length){
                iv_right1.setImageURI(Uri.fromFile(fs[index + 1]));
            }else {
                iv_right1.setImageURI(null);
            }
            if ((index + 2)<fs.length){
                iv_right2.setImageURI(Uri.fromFile(fs[index + 2]));
            }else {
                iv_right2.setImageURI(null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_out:
                break;
            case R.id.btn_delete:
                break;
            case R.id.btn_last:
                if ((index - 1)>-1){
                    index -= 1;
                    updateView();
                }
                break;
            case R.id.btn_back:
                break;
            case R.id.btn_next:
                if (null != fs && (index + 1)<fs.length){
                    index += 1;
                    updateView();
                }
                break;
        }
    }

    private String getBasePath() {
        String savePath = MyApplication.getSDCardPath() + "/DCIM/feng";// /feng/ScreenImage
        return savePath;
    }
}