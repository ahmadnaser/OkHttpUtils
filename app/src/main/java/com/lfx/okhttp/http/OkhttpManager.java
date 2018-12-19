package com.lfx.okhttp.http;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.lfx.okhttp.callback.DataCallBack;
import com.lfx.okhttp.callback.DownloadCallBack;
import com.lfx.okhttp.progress.CmlRequestBody;
import com.lfx.okhttp.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @package: com.lfx.okhttp.http
 * @author: liufx
 * @date: 2018/12/7 12:11 PM
 * Copyright © 2018 中国电信甘肃万维公司. All rights reserved.
 * @description: 简要描述
 */
public class OkhttpManager {
    private OkHttpClient client;
    private static OkhttpManager okhttpManager;
    private Handler mHandler;
    private Gson mGson;

    /**
     * 单例模式 OKhttpManager实例
     */
    private static OkhttpManager getInstance() {
        if (okhttpManager == null) {
            okhttpManager = new OkhttpManager();
        }
        return okhttpManager;
    }

    private OkhttpManager() {
        client = new OkHttpClient();
        mHandler = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    //默认的请求回调类
    private final DataCallBack<String> DEFAULT_RESULT_CALLBACK = new DataCallBack<String>() {
        @Override
        public void onError(Request request, Exception e) {
        }

        @Override
        public void onResponse(String response) {
        }
    };

    //******************  内部逻辑处理方法  ******************/
    private Response p_getSync(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response;
    }

    private String p_getSyncAsString(String url) throws IOException {
        return p_getSync(url).body().string();
    }

    private void p_getAsync(String url, DataCallBack dataCallBack) {
        if (dataCallBack == null)
            dataCallBack = DEFAULT_RESULT_CALLBACK;
        final DataCallBack callBack = dataCallBack;
        final Request request = new Request.Builder().url(url).build();
        //UI thread
        callBack.onBefore(request);
        client.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
                callBack.onAfter();
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String result = response.body().string();
                    if (callBack.mType == String.class) {
                        deliverDataSuccess(result, callBack);
                    } else {
                        Object o = mGson.fromJson(result, callBack.mType);
                        deliverDataSuccess(result, callBack);
                    }
                    callBack.onAfter();
                } catch (IOException e) {
                    e.printStackTrace();
                    deliverDataFailure(request, e, callBack);
                    callBack.onAfter();
                }
            }
        });
    }

    private void p_postAsync(String url, Map<String, String> params, DataCallBack dataCallBack) {
        if (dataCallBack == null)
            dataCallBack = DEFAULT_RESULT_CALLBACK;
        final DataCallBack callBack = dataCallBack;
        RequestBody requestBody = null;
        if (params == null) {
            params = new HashMap<String, String>();
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey().toString();
            String value = null;
            if (entry.getValue() == null) {
                value = "";
            } else {
                value = entry.getValue().toString();
            }
            builder.add(key, value);
        }
        requestBody = builder.build();
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        //UI thread
        callBack.onBefore(request);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
                callBack.onAfter();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    if (callBack.mType == String.class) {
                        deliverDataSuccess(result, callBack);
                    } else {
                        Object o = mGson.fromJson(result, callBack.mType);
                        deliverDataSuccess(result, callBack);
                    }
                    callBack.onAfter();
                } catch (IOException e) {
                    e.printStackTrace();
                    deliverDataFailure(request, e, callBack);
                    callBack.onAfter();
                }
            }
        });
    }

    private void p_postUploadAsync(String url, Map<String, String> params, String fileRequestName, List<String> fileList, DataCallBack dataCallBack) {
        if (dataCallBack == null)
            dataCallBack = DEFAULT_RESULT_CALLBACK;
        final DataCallBack callBack = dataCallBack;
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey().toString();
            String value = null;
            if (entry.getValue() == null) {
                value = "";
            } else {
                value = entry.getValue().toString();
            }
            builder.addFormDataPart(key, value);
        }
        for (int i = 0; i < fileList.size(); i++) { //对文件进行遍历
            File file = new File(fileList.get(i)); //生成文件
            //根据文件的后缀名，获得文件类型
            String fileType = FileUtils.getMimeType(file.getName());
            builder.addFormDataPart( //给Builder添加上传的文件
                    fileRequestName,  //请求的名字
                    file.getName(), //文件的文字，服务器端用来解析的
                    RequestBody.create(MediaType.parse(fileType), file) //创建RequestBody，把上传的文件放入
            );
        }
        final Request request = new Request.Builder().url(url)
                .post(new CmlRequestBody(builder.build()) {
                    @Override
                    public void loading(long current, long total, boolean done) {
                        deliverUpdateProgress(current, total, done, callBack);
                    }
                }).build();
        //UI thread
        callBack.onBefore(request);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
                callBack.onAfter();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    if (callBack.mType == String.class) {
                        deliverDataSuccess(result, callBack);
                    } else {
                        Object o = mGson.fromJson(result, callBack.mType);
                        deliverDataSuccess(o, callBack);
                    }
                    callBack.onAfter();
                } catch (IOException e) {
                    e.printStackTrace();
                    deliverDataFailure(request, e, callBack);
                    callBack.onAfter();
                }
            }
        });
    }

    /**
     * @param url          下载连接
     * @param target       储存下载文件的SDCard目录
     * @param fileName     文件名称
     * @param params       url携带参数
     * @param extraHeaders 请求携带其他的要求的headers
     * @param callBack     下载监听
     */
    public void download(final String url, final String target, final String fileName, HashMap<String, String> params, HashMap<String, String> extraHeaders, final DownloadCallBack callBack) {
        //构造请求Url
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for (String key : params.keySet()) {
                if (params.get(key) != null) {
                    urlBuilder.setQueryParameter(key, params.get(key));//非必须
                }
            }
        }
        //构造请求request
        final Request request = new Request.Builder()
                .url(urlBuilder.build())
                .headers(extraHeaders == null ? new Headers.Builder().build() : Headers.of(extraHeaders))//headers非必须
                .get()
                .build();
        //UI thread
        callBack.onBefore(request);
        //异步执行请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                deliverDownloadFailure(request, e, callBack);
                callBack.onAfter();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //非主线程
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = FileUtils.isExistDir(target);
                try {
                    //获取响应的字节流
                    is = response.body().byteStream();
                    //文件的总大小
                    long total = response.body().contentLength();
                    File file = new File(savePath, fileName);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    //循环读取输入流
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        if (callBack != null) {
                            deliverDownloadProgress(progress, total, false, callBack);
                        }

                    }
                    fos.flush();
                    // 下载完成
                    if (callBack != null) {
                        deliverDownloadProgress(100, total, true, callBack);
                        deliverDownloadSuccess(callBack);
                        callBack.onAfter();
                    }

                } catch (Exception e) {
                    if (callBack != null) {
                        callBack.onError(request, e);
                        callBack.onAfter();
                    }

                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    //******************  数据分发的方法  ******************/
    private void deliverDataFailure(final Request request, final IOException e, final DataCallBack callBack) {
        mHandler.post(new Runnable() {//发送到主线程
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onError(request, e);
                }
            }
        });
    }

    private void deliverDownloadFailure(final Request request, final IOException e, final DownloadCallBack callBack) {
        mHandler.post(new Runnable() {//发送到主线程
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onError(request, e);
                }
            }
        });
    }

    /**
     * 数据请求成功之后分发
     *
     * @param result
     * @param callBack
     */
    private void deliverDataSuccess(final Object result, final DataCallBack callBack) {
        mHandler.post(new Runnable() {//同样 发送到主线程
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onResponse(result);
                }

            }
        });
    }

    /**
     * 下载成功之后分发
     *
     * @param callBack
     */
    private void deliverDownloadSuccess(final DownloadCallBack callBack) {
        mHandler.post(new Runnable() {//同样 发送到主线程
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onSuccess();
                }

            }
        });
    }

    /**
     * 更新数据上传进度
     *
     * @param current
     * @param total
     * @param done
     * @param callBack
     */
    private void deliverUpdateProgress(final long current, final long total, final boolean done, final DataCallBack callBack) {
        mHandler.post(new Runnable() {//同样 发送到主线程
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.progress(current, total, done);
                }

            }
        });
    }

    /**
     * 更新数据下载进度
     *
     * @param current
     * @param total
     * @param done
     * @param callBack
     */
    private void deliverDownloadProgress(final long current, final long total, final boolean done, final DownloadCallBack callBack) {
        mHandler.post(new Runnable() {//同样 发送到主线程
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.progress(current, total, done);
                }

            }
        });
    }

    //******************  对外公布的方法  ******************/
    public static Response getSync(String url) throws IOException {
        return getInstance().p_getSync(url);//同步GET，返回Response类型数据
    }

    public static String getSyncAsString(String url) throws IOException {
        return getInstance().p_getSyncAsString(url);//同步GET，返回String类型数据（和上面getSync方法只是返回的数据类型不同而已）
    }

    public static void getAsync(String url, DataCallBack callBack) {
        getInstance().p_getAsync(url, callBack);//异步GET 调用方法
    }

    public static void postAsync(String url, Map<String, String> params, DataCallBack callBack) {
        getInstance().p_postAsync(url, params, callBack);//POST提交表单 调用方法
    }

    public static void postUploadAsync(String url, Map<String, String> params, String fileRequestName, List<String> fileList, DataCallBack callBack) {
        getInstance().p_postUploadAsync(url, params, fileRequestName, fileList, callBack);//POST提交表单 调用方法
    }

    public static void downloadAsync(String url, String saveDir, String fileName, HashMap<String, String> params, HashMap<String, String> extraHeaders, final DownloadCallBack callBack) {
        getInstance().download(url, saveDir, fileName, params, extraHeaders, callBack);//POST提交表单 调用方法
    }

    /**
     * 根据tag取消请求
     *
     * @param tag
     */
    public void cancelTag(Object tag) {
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    //取消所有请求
    public static void cancelAll() {
        getInstance().client.dispatcher().cancelAll();//取消所有请求
    }

}
