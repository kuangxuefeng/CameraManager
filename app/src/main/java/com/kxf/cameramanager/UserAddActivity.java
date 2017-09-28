package com.kxf.cameramanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.xutils.ex.DbException;

public class UserAddActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_user_name, et_user_sex, et_user_tel, et_user_age, et_user_address;
    private Button btn_back, btn_user_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);
        if (isWindowChanged){
            initView();
        }
    }

    private void initView() {
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_user_sex = (EditText) findViewById(R.id.et_user_sex);
        et_user_tel = (EditText) findViewById(R.id.et_user_tel);
        et_user_age = (EditText) findViewById(R.id.et_user_age);
        et_user_address = (EditText) findViewById(R.id.et_user_address);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_user_add = (Button) findViewById(R.id.btn_user_add);
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
                u.setName(name);
                u.setAge(Integer.parseInt(age));
                u.setTel(tel);
                u.setSex(et_user_sex.getText().toString().trim());
                u.setAddress(et_user_address.getText().toString().trim());
                try {
                    MyApplication.db().save(u);
                    showToast("添加用户成功！");
                } catch (DbException e) {
                    e.printStackTrace();
                }
                finish();
                break;
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
