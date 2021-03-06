package com.kxf.cameramanager;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kxf.cameramanager.utils.LogUtil;

import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserAddActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_user_name, et_user_sex, et_user_tel, et_user_age, et_user_address;
    private TextView et_user_time;
    private Button btn_back, btn_user_add, btn_set_nan, btn_set_nv;
    private boolean isSexNan = true;
    private int type = 0;
    private User userM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);
        if (isWindowChanged){
            initView();
            ViewTreeObserver obs = et_user_name.getViewTreeObserver();
            obs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    et_user_name.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    LogUtil.d("et_user_name.getHeight()=" + et_user_name.getHeight());
                    int hight = et_user_name.getHeight()*9/10;
                    ViewGroup.LayoutParams laynan = btn_set_nan.getLayoutParams();
                    laynan.height = hight;
                    laynan.width = laynan.height;
                    btn_set_nan.setLayoutParams(laynan);
                    ViewGroup.LayoutParams laynv = btn_set_nv.getLayoutParams();
                    laynv.height = hight;
                    laynv.width = laynv.height;
                    btn_set_nv.setLayoutParams(laynv);

                    ViewGroup.LayoutParams layback = btn_back.getLayoutParams();
                    layback.height = hight;
                    layback.width = layback.height;
                    btn_back.setLayoutParams(layback);

                    ViewGroup.LayoutParams layadd = btn_user_add.getLayoutParams();
                    layadd.height = hight;
                    layadd.width = layadd.height;
                    btn_user_add.setLayoutParams(layadd);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            type = getIntent().getIntExtra("type", 0);
                            String userStr = getIntent().getStringExtra("userStr");
                            Gson gson = new Gson();
                            User u = gson.fromJson(userStr, User.class);
                            userM = u;
                            LogUtil.d("type=" + type);
                            LogUtil.d("u=" + u);


                            if (1==type && null != u){
                                LogUtil.d("修改 u=" + u);
                                et_user_name.setText(u.getName());
                                et_user_tel.setText(u.getTel());
                                et_user_age.setText(String.valueOf(u.getAge()));
                                et_user_address.setText(u.getAddress());
                                isSexNan = "男".equals(u.getSex());
                                btn_user_add.setText("修改");
                            }
                        }
                    });
                }
            });
        }
    }

    private void initView() {

        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_user_sex = (EditText) findViewById(R.id.et_user_sex);
        et_user_tel = (EditText) findViewById(R.id.et_user_tel);
        et_user_age = (EditText) findViewById(R.id.et_user_age);
        et_user_address = (EditText) findViewById(R.id.et_user_address);
        et_user_time = (TextView) findViewById(R.id.et_user_time);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_user_add = (Button) findViewById(R.id.btn_user_add);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        et_user_time.setText(sdf.format(new Date()));

        btn_set_nan = (Button) findViewById(R.id.btn_set_nan);
        btn_set_nv = (Button) findViewById(R.id.btn_set_nv);
        btn_set_nan.setOnClickListener(this);
        btn_set_nv.setOnClickListener(this);
        updateSexView();

        btn_back.setOnClickListener(this);
        btn_user_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_user_add:
                String name = et_user_name.getText().toString().trim();
                String tel = et_user_tel.getText().toString().trim();
                String age = et_user_age.getText().toString().trim();
                if (TextUtils.isEmpty(name)){
                    showDialogYes("姓名不能为空！");
                    return;
                }
                if (TextUtils.isEmpty(tel)){
                    showDialogYes("电话不能为空！");
                    return;
                }
                if (TextUtils.isEmpty(age)){
                    showDialogYes("年龄不能为空！");
                    return;
                }

                if (!isNum(age)){
                    showDialogYes("年龄不正确！");
                    return;
                }
                User u = new User();
                if (1==type){
                    u.setId(userM.getId());
                }
                u.setName(name);
                u.setAge(Integer.parseInt(age));
                u.setTel(tel);
                u.setSex(isSexNan? "男":"女");
                u.setAddress(et_user_address.getText().toString().trim());
                u.setInfo(et_user_time.getText().toString());
                try {
                    if (1==type){
                        MyApplication.db().saveOrUpdate(u);
                        showToast("修改用户成功！");
                    }else {
                        MyApplication.db().save(u);
                        showToast("添加用户成功！");
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                finish();
                break;
            case R.id.btn_set_nan:
                if (!isSexNan){
                    isSexNan = true;
                    updateSexView();
                }
                break;
            case R.id.btn_set_nv:
                if (isSexNan){
                    isSexNan = false;
                    updateSexView();
                }
                break;
        }
    }

    private void updateSexView(){
        if (isSexNan){
            btn_set_nan.setBackgroundResource(R.drawable.sex_choose);
            btn_set_nv.setBackgroundResource(R.drawable.sex);
            btn_set_nan.setTextColor(Color.BLACK);
            btn_set_nv.setTextColor(Color.WHITE);
        } else {
            btn_set_nv.setBackgroundResource(R.drawable.sex_choose);
            btn_set_nan.setBackgroundResource(R.drawable.sex);
            btn_set_nv.setTextColor(Color.BLACK);
            btn_set_nan.setTextColor(Color.WHITE);
        }
    }

    private boolean isNum(String str){
        boolean b = false;
        try {
            Integer.parseInt(str);
            b = true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return b;
    }
}
