package com.lfx.okhttp.callback;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;

/**
 * @package: com.lfx.okhttp.callback
 * @author: liufx
 * @date: 2018/11/7 11:59 AM
 * Copyright © 2018 中国电信甘肃万维公司. All rights reserved.
 * @description: 用于请求成功后的回调
 */
public abstract class DataCallBack<T> {

    //这是请求数据的返回类型，包含常见的（Bean，List等）
    public Type mType;

    public DataCallBack() {
        mType = getSuperclassTypeParameter(getClass());
    }

    /**
     * 通过反射想要的返回类型
     *
     * @param subclass
     * @return
     */
    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    /**
     * 在请求之前的方法，一般用于加载框展示
     *
     * @param request
     */
    public void onBefore(Request request) {
    }

    /**
     * @param response
     */
    public abstract void onResponse(T response);

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
