package com.kxf.cameramanager;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kxf.cameramanager.utils.FormatUtils;
import com.kxf.cameramanager.utils.LogUtil;


public class BaseActivity extends Activity {

    public static final String KEY_USER_LOGIN = "key_user_login";
    private String tag = "";
    protected Activity mActivity;
    protected Context mContext;
    protected User userLogin;
    protected AlertDialog dialog;

    protected ImageView top_iv_left_icon;
    protected TextView top_tv_title, top_tv_right;

    private boolean isNeedAdapta = true;
    public static final int msg_show_dialog = 2000;
    protected final Handler handlerBase = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LogUtil.d("msg.what=" + msg.what);
            switch (msg.what) {
                case msg_show_dialog:
                    String str = (String) msg.obj;
                    showDialogYes(str);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mActivity = this;
        mContext = this;
        tag = this.getPackageName() + "." + this.getLocalClassName();
        tag = "do in " + tag;
        LogUtil.e(tag);
        String userStr = MyApplication.getShare(KEY_USER_LOGIN);
        Gson gson = new Gson();
        userLogin = gson.fromJson(userStr, User.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e(tag);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e(tag);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e(tag);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e(tag);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e(tag);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e(tag);
    }

    @Override
    public void finish() {
        super.finish();
        LogUtil.e(tag);
    }

    protected void showDialog(final String msg, final String leftButtonTitle, final Runnable leftRun, final String rightButtonTitle, final Runnable rightRun) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != dialog && dialog.isShowing()){
                    dialog.dismiss();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(FormatUtils.FormatStringLen(msg, 50, FormatUtils.ALIGN.CENTER, " ")).setCancelable(false).setTitle("提示");
                if (!TextUtils.isEmpty(leftButtonTitle)){
                    builder.setPositiveButton(leftButtonTitle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(null != leftRun){
                                mActivity.runOnUiThread(leftRun);
                            }
                        }
                    });
                }
                if (!TextUtils.isEmpty(rightButtonTitle)){
                    builder.setNeutralButton(rightButtonTitle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(null != rightRun){
                                mActivity.runOnUiThread(rightRun);
                            }
                        }
                    });
                }
                dialog = builder.create();
                dialog.show();
            }
        });
    }

    protected void showDialogYes(String msg){
        showDialog(msg, "确定", null, null, null);
    }

    protected void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
