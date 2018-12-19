# OkHttpUtils
okhttp3封装网络请求 get post 文件上传 下载


[![](https://img.shields.io/badge/QQ-547166147-orange.svg)](https://github.com/liufx/OkHttpUtils)


- **GitHub地址：** [https://github.com/liufx/OkHttpUtils](https://github.com/liufx/OkHttpUtils)

**（开源不易，如果喜欢的话希望给个小星星，谢谢~）**

《OkHttpUtils》 是使用okhttp3进行封装 提供get、post、文件上传、文件下载等方法，代码结构清晰有详细注释，如有任何疑问和建议请提 Issue或联系[![](https://img.shields.io/badge/QQ:-547166147@qq.com-blue.svg)]()



## 前言

前段时间学习了 okhttp，闲了进行了简单封装，简单易懂，注释清晰方便大家查看！

## 项目截图

- 截图

![](https://github.com/liufx/OkHttpUtils/blob/master/screenshot/code.png)

- gif

   <img src="/screenshot/radio.gif" width = "240" height = "360" alt="效果">

## 具体实现
```Java
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

```
