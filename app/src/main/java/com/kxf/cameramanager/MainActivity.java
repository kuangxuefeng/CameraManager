package com.kxf.cameramanager;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kxf.cameramanager.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, Camera.AutoFocusCallback {
    Button btn_dingge, btn_save, btn_next, btn_back;
    ImageView iv_show;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/ddHH:mm:ss");
    SimpleDateFormat sdfFile = new SimpleDateFormat("MMddHHmmss");

    // (176*144,320*240,352*288,480*360,640*480)
    private int imageW = 640;
    private int imageH = 480;

    MySurfaceView mySurface;// surfaceView声明
    SurfaceHolder holder;// surfaceHolder声明
    Camera myCamera;// 相机声明
    boolean isSavePic = false;
    boolean isStopPreview = false;
    Bitmap bmDG;

    // 创建jpeg图片回调数据对象
    Camera.PictureCallback jpeg = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {// 获得图片
                bmDG = BitmapFactory.decodeByteArray(data, 0, data.length);
                LogUtil.e("bmDG=" + bmDG);
                if (isSavePic){
                    saveImage(bmDG);
                }else {
                    myCamera.stopPreview();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private void initImage(String filePath) {
        if (TextUtils.isEmpty(filePath)){
            String dirPath = getBasePath();
            File path = new File(dirPath);
            File[] fs = path.listFiles();
            if (null != fs && fs.length > 0){
                iv_show.setImageURI(Uri.fromFile(fs[fs.length - 1]));
            }
        }else {
            iv_show.setImageURI(Uri.fromFile(new File(filePath)));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySurface = (MySurfaceView) findViewById(R.id.my_camera);
        iv_show = (ImageView) findViewById(R.id.iv_show);
        initImage(null);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_dingge = (Button) findViewById(R.id.btn_dingge);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_next = (Button) findViewById(R.id.btn_next);

        btn_dingge.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        mySurface.setOnClickListener(this);
        mySurface.setMyCallBack(new MySurfaceView.MyCallBack() {

            @Override
            public void mySurfaceChanged(SurfaceHolder holder, int format,
                                         int width, int height) {
                if (true) {
                    return;
                }
                // 已经获得Surface的width和height，设置Camera的参数
                Camera.Parameters parameters = myCamera.getParameters();
                parameters.setPreviewSize(width, height);

                List<Camera.Size> vSizeList = parameters.getSupportedPictureSizes();

                for (int num = 0; num < vSizeList.size(); num++) {
                    Camera.Size vSize = vSizeList.get(num);
                }
                if (MainActivity.this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    // 如果是竖屏
                    // parameters.set("orientation", "portrait");
                    // 在2.2以上可以使用
                    myCamera.setDisplayOrientation(90);
                } else {
                    // parameters.set("orientation", "landscape");
                    // 在2.2以上可以使用
                    myCamera.setDisplayOrientation(0);
                }
            }

            @Override
            public void mySurfaceCreated(Camera myCamera) {

                if (MainActivity.this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    // 如果是竖屏
                    // parameters.set("orientation", "portrait");
                    // 在2.2以上可以使用
                    myCamera.setDisplayOrientation(90);
                } else {
                    // parameters.set("orientation", "landscape");
                    // 在2.2以上可以使用
                    myCamera.setDisplayOrientation(0);
                }
                MainActivity.this.myCamera = myCamera;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.my_camera:
            case R.id.btn_dingge:
                if (isStopPreview){
                    isStopPreview = false;
                    myCamera.startPreview();
                }else {
                    isStopPreview = true;
                    isSavePic = false;
                    myCamera.autoFocus(this);
                }
                break;
            case R.id.btn_save:
                if (isStopPreview){
                    saveImage(bmDG);
                }else {
                    myCamera.autoFocus(this);
                }
                isSavePic = true;
                isStopPreview = false;
                break;
            case R.id.btn_back:
                break;
            case R.id.btn_next:
                Intent intent = new Intent(this, ShowPicActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        LogUtil.e("对焦结果：" + success);
        if (success || (!mySurface.getIsBackCamera()) || true) {
            Toast.makeText(this, "定格", Toast.LENGTH_SHORT).show();
            // 设置参数,并拍照
            Camera.Parameters params = myCamera.getParameters();
            params.setPictureFormat(PixelFormat.JPEG);
            params.setPreviewSize(imageW, imageH);
            myCamera.setParameters(params);
            myCamera.takePicture(null, null, jpeg);
        } else {
            Toast.makeText(this, "对焦失败，请重新拍照！", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage(Bitmap bmp) {
        // 定义矩阵对象
        Matrix matrix = new Matrix();
        // 缩放原图
        matrix.postScale(1f, 1f);
        // bmp.getWidth(), 500分别表示重绘后的位图宽高
        Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                bmp.getHeight(), matrix, true);

        String SavePath = getBasePath();

        String filepath = null;
        // 3.保存Bitmap
        try {
            File path = new File(SavePath);
            // 文件
            filepath = SavePath + "/img_" + sdfFile.format(new Date())
                    + ".jpg";
            File file = new File(filepath);
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                dstbmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                Toast.makeText(this, "截屏文件已保存至" + getBasePath() + "下",
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        initImage(filepath);
        myCamera.startPreview();
    }

    private String getBasePath() {
        String savePath = MyApplication.getSDCardPath() + "/DCIM/feng";// /feng/ScreenImage
        return savePath;
    }

}
