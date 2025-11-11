package com.java.utils

import android.content.Context
import com.java.utils.file.FileUtils
import com.java.utils.log.Logger
import com.java.utils.thread.ThreadManager

fun init(context: Context) {
    Logger.initLogUtils(true, Logger.DEBUG)
    FileUtils.instance.initUtils(context)
    ThreadManager.getInstance().initUtils(context)
    NetWorkUtils.initUtils(context)
    ResourceUtils.instance.initUtils(context)
    ScreenUtils.instance.initUtils(context)
    ToastUtil.initUtils(context)
}

fun clearCache() {
    FileUtils.instance.unInitUtils()
    ThreadManager.getInstance().unInitUtils()
    NetWorkUtils.unInitUtils()
    ResourceUtils.instance.unInitUtils()
    ScreenUtils.instance.unInitUtils()
    ToastUtil.unInitUtils()
}