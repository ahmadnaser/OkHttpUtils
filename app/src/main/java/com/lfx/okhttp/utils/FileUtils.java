package com.lfx.okhttp.utils;

import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * @package: com.lfx.okhttp.callback
 * @author: liufx
 * @date: 2018/11/7 11:59 AM
 * Copyright © 2018 中国电信甘肃万维公司. All rights reserved.
 * @description: 获取文件 MimeType
 */
public class FileUtils {

    public static String getMimeType(String fileUrl) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(fileUrl);

        return type;
    }

    /**
     * @param path 文件夹路径
     */
    public static String isExistDir(String path) {
        File file = new File(path);
//判断文件夹是否存在,如果不存在则创建文件夹
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    public static String isExistDir2(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }
    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    @NonNull
    public  static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }



    public static void main(String args[]) throws Exception {
        System.out.println(FileUtils.getMimeType("file://c:/temp/test.TXT"));
        // output :  text/plain
    }
}