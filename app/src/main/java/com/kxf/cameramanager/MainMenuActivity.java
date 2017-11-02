package com.kxf.cameramanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_checkup, btn_control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        if (isWindowChanged){
            initView();
        }
    }

    private void initView() {
        btn_checkup = (Button) findViewById(R.id.btn_checkup);
        btn_control = (Button) findViewById(R.id.btn_control);

        btn_checkup.setOnClickListener(this);
        btn_control.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        btn_checkup.setEnabled(false);
        btn_control.setEnabled(false);
        Intent intent;
        switch (v.getId()){
            case R.id.btn_checkup:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_control:
                intent = new Intent(this, BTClientActivity.class);
                startActivity(intent);
                break;
        }
        btn_checkup.setEnabled(true);
        btn_control.setEnabled(true);
    }
}
