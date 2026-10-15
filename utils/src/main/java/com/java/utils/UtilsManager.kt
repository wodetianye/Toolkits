package com.java.utils

import android.content.Context
import com.java.utils.file.FileUtils
import com.java.utils.file.ParseJsonUtils
import com.java.utils.file.StoragePath
import com.java.utils.other.BitmapUtils
import com.java.utils.other.NetWorkUtils
import com.java.utils.other.ResourceUtils
import com.java.utils.other.ScreenUtils
import com.java.utils.other.ToastUtils
import com.java.utils.thread.ThreadManager

fun init(context: Context) {
    FileUtils.initUtils(context)
    ParseJsonUtils.initUtils(context)
    StoragePath.initUtils(context)
    ThreadManager.getInstance().initUtils(context)
    NetWorkUtils.initUtils(context)
    ResourceUtils.initUtils(context)
    ScreenUtils.initUtils(context)
    ToastUtils.initUtils(context)
    BitmapUtils.initUtils(context);
}

fun clearCache() {
    FileUtils.unInitUtils()
    ParseJsonUtils.unInitUtils()
    StoragePath.unInitUtils()
    ThreadManager.getInstance().unInitUtils()
    NetWorkUtils.unInitUtils()
    ResourceUtils.unInitUtils()
    ScreenUtils.unInitUtils()
    ToastUtils.unInitUtils()
    BitmapUtils.unInitUtils()
}