package com.kxf.cameramanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kxf.cameramanager.utils.LogUtil;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private int screenWidth;
    private int screenHeight;
    private LinearLayout ll_user_list;
    private RelativeLayout rl_bottom;
    private static final int itemCount = 5;
    private int page = 1;
    private List<User> usAll = null;
    private List<User> us = null;
    private ImageView iv_user_add, iv_user_last, iv_user_next, iv_bg, iv_people;
    private boolean isCanClick = true;
    private ProgressBar load_pb;
    private Button btn_bt;
    private ImageButton ib_query;
    private EditText et_query;

    private OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int itemId) {
            LogUtil.d("itemId=" + itemId);
            final User u = us.get(itemId);
            switch (v.getId()){
                case R.id.iv_user_sure:
                    if (isCanClick){
                        isCanClick = false;
                        load_pb.setVisibility(View.VISIBLE);
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                String ustr = gson.toJson(u);
                                MyApplication.saveShare(KEY_USER_LOGIN, ustr);
                                Intent intent = new Intent(mActivity, MainActivity.class);
                                startActivity(intent);
                                finish();
                                isCanClick = true;
                            }
                        }, 500);
                    }
                    break;
                case R.id.iv_user_delete:
                    showDialog("是否删除？", "否", null, "是", new Runnable() {
                        @Override
                        public void run() {
                            WhereBuilder wb = WhereBuilder.b("id", "=", u.getId());
                            try {
                                MyApplication.db().delete(User.class, wb);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initUser();
                                        if ((page-1)*itemCount>=us.size()&&page>1){
                                            page--;
                                        }
                                        initUserView();
                                    }
                                });
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case R.id.iv_people:
                    Gson gson = new Gson();
                    String userStr = gson.toJson(u);
                    Intent intent = new Intent(mActivity, UserAddActivity.class);
                    intent.putExtra("userStr", userStr);
                    intent.putExtra("type", 1);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isWindowChanged){
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
        LogUtil.d("height=" + height + ";width=" + width);
        LogUtil.d("density=" + density + ";densityDpi=" + densityDpi);
        //屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        screenWidth = (int) (width/density);//屏幕宽度(dp)
        screenHeight = (int)(height/density);//屏幕高度(dp)
        LogUtil.d("screenWidth=" + screenWidth + ";screenHeight=" + screenHeight);
        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        ViewTreeObserver obs = iv_bg.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                iv_bg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = (int) (iv_bg.getHeight()/density);
                int width = (int) (iv_bg.getWidth()/density);
                LogUtil.d("height=" + height + ";width=" + width);

                ViewGroup.LayoutParams rlLay = rl_bottom.getLayoutParams();
                int h = rlLay.height * width / 540;
                rlLay.height = h;
                LogUtil.d("h=" + h);
                rl_bottom.setLayoutParams(rlLay);

                LogUtil.d("rl_bottom " + "rl_bottom.getHeight()=" + rl_bottom.getHeight() + ";rl_bottom.getWidth()=" + rl_bottom.getWidth());
                LogUtil.d("iv_user_add " + "height=" + iv_user_add.getHeight()/density + ";width=" + iv_user_add.getWidth()/density);
                LogUtil.d("iv_user_last " + "height=" + iv_user_last.getHeight()/density + ";width=" + iv_user_last.getWidth()/density);
                LogUtil.d("iv_user_next " + "height=" + iv_user_next.getHeight()/density + ";width=" + iv_user_next.getWidth()/density);

                RelativeLayout.LayoutParams addLay = (RelativeLayout.LayoutParams) iv_user_add.getLayoutParams();
                RelativeLayout.LayoutParams nextLay = (RelativeLayout.LayoutParams) iv_user_next.getLayoutParams();
                //screenWidth=640;screenHeight=360     height=560;width=1080
                RelativeLayout.LayoutParams lastLay = (RelativeLayout.LayoutParams) iv_user_last.getLayoutParams();
                LogUtil.d("lastLay.rightMargin=" + lastLay.rightMargin);
                int margin = (int) ((int) (width/2.0*(215.0/270.0) - iv_user_add.getWidth()/density/2 - iv_user_last.getWidth()/density/2)*density);
                lastLay.rightMargin = margin;
                nextLay.leftMargin = margin;
                LogUtil.d("lastLay.rightMargin=" + lastLay.rightMargin);
                iv_user_last.setLayoutParams(lastLay);
                iv_user_next.setLayoutParams(nextLay);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isWindowChanged){
            et_query.setText(null);
            initUser();
            initUserView();
        }
    }

    private void initUserView() {
        ll_user_list.removeAllViews();
        if (us != null && us.size() > 0) {
            for (int i = 0; i < itemCount; i++) {
                int index = (page-1) * itemCount + i;
                if (index < us.size()) {
                    addItem(ll_user_list, us.get(index), index);
                }else {
                    return;
                }
            }
        }
    }

    private void initUser() {
        try {
            usAll = MyApplication.db().findAll(User.class);
            us = usAll;
            LogUtil.e("usAll=" + usAll);
            LogUtil.e("us=" + us);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        iv_user_add = (ImageView) findViewById(R.id.iv_user_add);
        iv_user_last = (ImageView) findViewById(R.id.iv_user_last);
        iv_user_next = (ImageView) findViewById(R.id.iv_user_next);
        iv_user_add.setOnClickListener(this);
        iv_user_last.setOnClickListener(this);
        iv_user_next.setOnClickListener(this);

        ib_query = (ImageButton) findViewById(R.id.ib_query);
        et_query = (EditText) findViewById(R.id.et_query);

        ib_query.setOnClickListener(this);
        et_query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    ib_query.callOnClick();
                }
            }
        });

        btn_bt = (Button) findViewById(R.id.btn_bt);
        btn_bt.setOnClickListener(this);

        ll_user_list = (LinearLayout) findViewById(R.id.ll_user_list);
        load_pb = (ProgressBar) findViewById(R.id.load_pb);
        load_pb.setVisibility(View.GONE);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
    }

    private void addItem(ViewGroup parent, User u, int itemId) {
        View v = LayoutInflater.from(this).inflate(R.layout.activity_login, null);
        LinearLayout ll_user_item = (LinearLayout) v.findViewById(R.id.ll_user_item);
        LinearLayout ll_user_list = (LinearLayout) v.findViewById(R.id.ll_user_list);
        ((TextView) ll_user_item.findViewById(R.id.tv_user_name)).setText("姓名:" + u.getName());
        ((TextView) ll_user_item.findViewById(R.id.tv_user_age)).setText("年龄:" + u.getAge());
        ((TextView) ll_user_item.findViewById(R.id.tv_user_tel)).setText("电话:" + u.getTel());
        ll_user_item.findViewById(R.id.iv_user_sure).setOnClickListener(getLister(listener, itemId));
        ll_user_item.findViewById(R.id.iv_user_delete).setOnClickListener(getLister(listener, itemId));
        ll_user_item.findViewById(R.id.iv_people).setOnClickListener(getLister(listener, itemId));
        ll_user_list.removeAllViews();
        parent.addView(ll_user_item);
    }

    private View.OnClickListener getLister(final OnItemClickListener listener, final int itemId) {
        View.OnClickListener lis = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                LogUtil.d("v=" + v);
                if (null != listener) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onItemClick(v, itemId);
                        }
                    });
                }
            }
        };
        return lis;
    }

    @Override
    public void onClick(View v) {
        LogUtil.d("v=" + v);
        switch (v.getId()){
            case R.id.iv_user_add:
                Intent intent = new Intent(this, UserAddActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_user_last:
                if (page>1){
                    page--;
                    initUserView();
                }
                break;
            case R.id.iv_user_next:
                if (null != us && page*itemCount<us.size()){
                    page++;
                    initUserView();
                }
                break;
            case R.id.btn_bt:
                LogUtil.e("intent = new Intent(this, BTClientActivity.class);");
                intent = new Intent(this, BTClientActivity.class);
                startActivity(intent);
                break;
            case R.id.ib_query:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String str = et_query.getText().toString().trim();
                        if (!TextUtils.isEmpty(str) && null != usAll && usAll.size() > 0){
                            us = new ArrayList<User>();
                            for (int i = 0; i < usAll.size(); i++){
                                LogUtil.e("usAll.size()=" + usAll.size() + "; i=" + i + ";  usAll.get(i)=" + usAll.get(i));
                                if (Pattern.matches("^(.*" + str + ".*)$", usAll.get(i).getName())){
                                    us.add(usAll.get(i));
                                }
                            }
                            page = 1;
                            initUserView();
                        } else if(TextUtils.isEmpty(str)){
                            us = usAll;
                            page = 1;
                            initUserView();
                        }
                    }
                });
                break;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int itemId);
    }

}
