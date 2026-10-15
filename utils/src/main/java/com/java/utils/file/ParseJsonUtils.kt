package com.java.utils.file;

import android.content.Context;

import com.java.utils.log.Logger;

import java.nio.charset.StandardCharsets;

/**
 * @Introduce: 对象比较及转换工具类.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description:
 */
public class ParseJsonUtils {

    private ParseJsonUtils() throws Exception {
        throw new Exception("该对象不允许私自创建");
    }

    /**
     * 解析字节数组为json字符串.
     *
     * @param result 字节数组
     * @return Json字符串
     */
    public static String parseJsonFile(byte[] result) {
        if (result == null) {
            return null;
        }
        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * 解析该路径下的文件为json字符串.
     *
     * @param jsonFilePath 必须是asset目录下
     * @return Json字符串
     */
    public static String parseJsonFile(Context context, String jsonFilePath) {
        if(!FileUtils.isAssetFile(context, jsonFilePath)) {
            Logger.w("jsonFilePath is not asset file");
            return null;
        }
        byte[] result = FileUtils.getAssetFileContent(context, jsonFilePath);
        return new String(result, StandardCharsets.UTF_8);
    }
}
