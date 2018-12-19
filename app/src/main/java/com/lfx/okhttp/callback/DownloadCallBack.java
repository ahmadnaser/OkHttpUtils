package com.lfx.okhttp.callback;

import okhttp3.Request;

/**
 * @package: com.lfx.okhttp.callback
 * @author: liufx
 * @date: 2018/11/7 11:59 AM
 * Copyright © 2018 中国电信甘肃万维公司. All rights reserved.
 * @description: 用于请求成功后的回调
 */
public abstract class DownloadCallBack {

    /**
     * 在请求之前的方法，一般用于加载框展示
     *
     * @param request
     */
    public void onBefore(Request request) {
    }


    public abstract void onSuccess();

    /**
     * 请求失败的时候
     *
     * @param request
     * @param e
     */
    public abstract void onError(Request request, Exception e);

    /**
     * 在请求之后的方法，一般用于加载框隐藏
     */
    public void onAfter() {

    }

    /**
     * 实现图片进度监听
     *
     * @param current
     * @param total
     * @param done
     */
    public void progress(long current, long total, boolean done) {
    }
}
