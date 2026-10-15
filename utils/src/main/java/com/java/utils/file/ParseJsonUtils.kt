package com.java.utils.file

import android.app.Application
import android.content.Context
import com.java.utils.BaseUtils
import com.java.utils.log.Logger
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * @Introduce: 文件解析工具类.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 解析 JSON 文件（字节数组 / 本地文件 / Asset 文件）.
 */
object ParseJsonUtils : BaseUtils {

    private const val TAG = "ParseJsonUtils"

    private var mApp: Application? = null

    override fun initUtils(context: Context) {
        mApp = context.applicationContext as Application
    }

    override fun unInitUtils() {
        mApp = null
    }

    /**
     * 字节数组 → JSON 字符串（UTF-8）。
     *
     * 例：parseJsonFile(bytes) -> "{\"name\":\"Tom\"}"
     */
    @JvmStatic
    fun parseJsonFile(result: ByteArray?): String? {
        if (result == null) return null
        return try {
            String(result, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            Logger.e("$TAG parseJsonFile(byte[]) failed: ${e.message}")
            null
        }
    }

    /**
     * 本地文件 → JSON 字符串。
     *
     * 例：parseJsonFile("/sdcard/a.json") -> "{\"name\":\"Tom\"}"
     */
    @JvmStatic
    fun parseJsonFile(filePath: String?): String? {
        if (filePath.isNullOrEmpty()) return null
        return FileUtils.readFileToString(filePath)
    }

    /**
     * Asset 文件 → JSON 字符串。
     *
     * 例：parseJsonFile("config.json") -> "{\"name\":\"Tom\"}"
     */
    @JvmStatic
    fun parseJsonFromAsset(assetFileName: String?): String? {
        if (assetFileName.isNullOrEmpty()) return null
        return try {
            requireContext().assets.open(assetFileName).use { input ->
                val bytes = input.readBytes()
                String(bytes, StandardCharsets.UTF_8)
            }
        } catch (e: IOException) {
            Logger.e("$TAG parseJsonFromAsset failed: ${e.message}")
            null
        }
    }

    private fun requireContext(): Application {
        return mApp ?: throw IllegalStateException(
            "ParseJsonUtils 未初始化，请先调用 ParseJsonUtils.initUtils(context)"
        )
    }
}