package com.lfx.okhttp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.lfx.okhttp.callback.DataCallBack;
import com.lfx.okhttp.callback.DownloadCallBack;
import com.lfx.okhttp.http.OkhttpManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView text;
    private static String URL = "https://www.apiopen.top/satinApi";
    private static String UPLOAD_URL = "http://10.18.33.247:8080/patrol-rest/image/multifileUpload";
    private static String DOWNLOAD_URL = "http://jkgs.gov.cn/appstore/jkgs_xiaomi.apk";

    private int PHOTO_REQUEST_CAREMA = 0;
    private int PHOTO_REQUEST_GALLERY = 1;
    private File tempFile;
    private String PHOTO_FILE_NAME = "output_image.jpg";
    private int REQUEST_WRITE_EXTERNAL_STORAGE = 3;

    private String PHOTO_NAME = "image.jpg";
    private File file;
    private Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在主线程中初始化Handler 对象
        handler = new Handler();
        setContentView(R.layout.activity_main);
        tv1 = findViewById(R.id.tv1);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get();
            }
        });
        tv2 = findViewById(R.id.tv2);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                post();
            }
        });
        tv3 = findViewById(R.id.tv3);
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        tv4 = findViewById(R.id.tv4);
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText(null);
            }
        });
        tv5 = findViewById(R.id.tv5);
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apkDir = getApplicationContext().getFilesDir().getAbsolutePath();
                download(DOWNLOAD_URL, apkDir, "jkgs.apk");
            }
        });
        text = findViewById(R.id.text);


    }

    private void get() {
        final LoadingDailog dialog;
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("加载中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();

        String url = URL + "?type=1&page=1";
        OkhttpManager.getAsync(url, new DataCallBack<String>() {
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);

                dialog.show();
                Log.e("OkHttp", "onBefore");
            }

            @Override
            public void onResponse(String response) {
                text.setText(response);
                Log.e("OkHttp", response);
            }

            @Override
            public void onError(Request request, Exception e) {
                Log.e("OkHttp", "onError");
            }

            @Override
            public void progress(long current, long total, boolean done) {
                Log.e("OkHttp", "progress");
            }

            @Override
            public void onAfter() {
                super.onAfter();
                dialog.dismiss();
                Log.e("OkHttp", "onAfter");
            }
        });
    }

    private void post() {
        final LoadingDailog dialog;
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("加载中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params.put("page", "1");
        OkhttpManager.postAsync(URL, params, new DataCallBack<String>() {
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                dialog.show();
                Log.e("OkHttp", "onBefore");
            }

            @Override
            public void onResponse(String response) {
                text.setText(response);
                Log.e("OkHttp", response);
            }

            @Override
            public void onError(Request request, Exception e) {
                Log.e("OkHttp", "onError");

            }

            @Override
            public void progress(long current, long total, boolean done) {
                Log.e("OkHttp", "progress");
            }

            @Override
            public void onAfter() {
                super.onAfter();
                dialog.dismiss();
                Log.e("OkHttp", "onAfter");
            }
        });
    }

    private void upload(Map<String, String> params, String image, List<String> fileList) {

        String url = UPLOAD_URL;
        OkhttpManager.postUploadAsync(url, params, image, fileList, new DataCallBack<Result>() {
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                Log.e("OkHttp", "onBefore");
            }

            @Override
            public void onResponse(Result response) {
                text.setText(response.getMessage());
                Toast.makeText(getApplicationContext(), "上传成功！", Toast.LENGTH_SHORT).show();
                Log.e("OkHttp", response.getMessage());
            }

            @Override
            public void onError(Request request, Exception e) {
                Log.e("OkHttp", "onError");

            }

            @Override
            public void progress(final long current, final long total, final boolean done) {
                text.setText("progress:current:" + current + "\n" + "total:" + total + "\n" + "done:" + done + "\n");
                Log.e("OkHttp", "progress:current:" + current + "\n" + "total:" + total + "\n" + "done:" + done + "\n");
            }

            @Override
            public void onAfter() {
                super.onAfter();
                Log.e("OkHttp", "onAfter");
            }
        });
    }

    private void download(String url, String dir, String fileName) {

        OkhttpManager.downloadAsync(url, dir, fileName, null, null, new DownloadCallBack() {
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                Log.e("OkHttp", "onBefore");
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "下载成功！", Toast.LENGTH_SHORT).show();
                Log.e("OkHttp", "onSuccess");
            }

            @Override
            public void onError(Request request, Exception e) {
                Log.e("OkHttp", "onError");

            }

            @Override
            public void progress(final long current, final long total, final boolean done) {
                text.setText("progress:current:" + current + "\n" + "total:" + total + "\n" + "done:" + done + "\n");
                Log.e("OkHttp", "progress:current:" + current + "\n" + "total:" + total + "\n" + "done:" + done + "\n");
            }

            @Override
            public void onAfter() {
                super.onAfter();
                Log.e("OkHttp", "onAfter");
            }
        });
    }

    /*
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                String getRealFilePath = getRealFilePath(this, uri);

                Map<String, String> params = new HashMap<>();
                params.put("userName", "李福星");
                params.put("desc", "一张美丽的图片");
                List<String> imageList = new ArrayList<>();
                imageList.add(getRealFilePath);

                upload(params, "fileName", imageList);

            }

        }
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            gallery();

        }


    }

    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
            gallery();
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.e("fasfa", "checkPermission: 已经授权！");
        }
    }
}
