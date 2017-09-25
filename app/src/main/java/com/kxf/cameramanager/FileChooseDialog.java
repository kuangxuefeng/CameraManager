package com.kxf.cameramanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kxf.cameramanager.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuangxf on 2017/9/25.
 */

public class FileChooseDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {
    private String pathRoot = ".";
    private TextView tv_file_path;
    private ImageButton ib_file_back;
    private ListView lv_file_list;
    private Button btn_file_choose;
    private Context context;
    private List<String> paths;
    private onFileChooseListener listener;

    public onFileChooseListener getListener() {
        return listener;
    }

    public void setListener(onFileChooseListener listener) {
        this.listener = listener;
    }

    public String getPathRoot() {
        return pathRoot;
    }

    public void setPathRoot(String pathRoot) {
        this.pathRoot = pathRoot;
    }

    public FileChooseDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public FileChooseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected FileChooseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_file_choose);

        initView();
    }

    private void initView() {
        tv_file_path = (TextView) findViewById(R.id.tv_file_path);
        ib_file_back = (ImageButton) findViewById(R.id.ib_file_back);
        lv_file_list = (ListView) findViewById(R.id.lv_file_list);
        btn_file_choose = (Button) findViewById(R.id.btn_file_choose);

        ib_file_back.setOnClickListener(this);
        btn_file_choose.setOnClickListener(this);

        File f = new File(pathRoot);
        if (f.exists() && f.isDirectory()){
            updatePath(f.getPath());
        }
        lv_file_list.setOnItemClickListener(this);
    }

    private void updatePath(String path) {
        File f = new File(path);
        paths = new ArrayList();
        if (f.exists() && f.isDirectory()){
            tv_file_path.setText(path);
            File[] fs = f.listFiles();
            if (null != fs && fs.length>0){
                for (File f1:fs){
                    if (f1.isDirectory()){
                        paths.add(f1.getName());
                    }
                }
            }

            if (paths.size()>0){
                ListAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, paths);
                lv_file_list.setAdapter(adapter);
            }else {
                lv_file_list.setAdapter(null);
            }
            lv_file_list.deferNotifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_file_back:
                File f = new File(tv_file_path.getText().toString());
                File pFile = f.getParentFile();
                if ((!f.equals(pathRoot)) && null != pFile && pFile.exists()){
                    updatePath(pFile.getPath());
                }
                break;
            case R.id.btn_file_choose:
                if (null != listener){
                    listener.choose(tv_file_path.getText().toString());
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File f = new File(tv_file_path.getText().toString(), paths.get(position));
        String path = f.getPath();
        LogUtil.d("path=" + path);
        updatePath(path);
    }

    public interface onFileChooseListener {
        void choose(String path);
    }
}
