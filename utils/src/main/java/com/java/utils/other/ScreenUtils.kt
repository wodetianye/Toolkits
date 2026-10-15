package com.java.utils.other

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import com.java.utils.BaseUtils

/**
 * @Introduce: 屏幕尺寸管理.
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description: 读取屏幕信息、尺寸转换、截图等.
 */
@SuppressLint("StaticFieldLeak")
object ScreenUtils : BaseUtils {
    private var mApp: Context? = null

    override fun initUtils(context: Context) {
        mApp = context.applicationContext
    }

    override fun unInitUtils() {
        mApp = null
    }

    /**
     * dp → px（Float 版）。
     *
     * 例：dp2px(16f) -> 48（假设 density = 3.0）
     */
    @JvmStatic
    fun dp2px(dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal,
            requireContext().resources.displayMetrics
        ).toInt()
    }

    /**
     * dp → px（Int 版，内部委托 Float 版，保证算法一致）。
     *
     * 例：dp2px(16) -> 48
     */
    @JvmStatic
    fun dp2px(dpVal: Int): Int = dp2px(dpVal.toFloat())

    /**
     * sp → px。
     *
     * 例：sp2px(16f) -> 48
     */
    @JvmStatic
    fun sp2px(spVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spVal,
            requireContext().resources.displayMetrics
        ).toInt()
    }

    /**
     * px → dp（Float 版）。
     *
     * 例：px2dp(48f) -> 16f
     */
    @JvmStatic
    fun px2dp(pxVal: Float): Float {
        val density = requireContext().resources.displayMetrics.density
        return if (density == 0f) 0f else pxVal / density
    }

    /**
     * px → dp（Int 版）。
     *
     * 例：px2dp(48) -> 16
     */
    @JvmStatic
    fun px2dp(pxVal: Int): Int = px2dp(pxVal.toFloat()).toInt()

    /**
     * px → sp。
     *
     * 例：px2sp(48f) -> 16f
     */
    @JvmStatic
    fun px2sp(pxVal: Float): Float {
        val scaledDensity = requireContext().resources.displayMetrics.scaledDensity
        return if (scaledDensity == 0f) 0f else pxVal / scaledDensity
    }

    /**
     * 屏幕宽度（px，当前 App 可用区域）。
     *
     * 例：-> 1080
     */
    @JvmStatic
    val screenWidth: Int
        get() = requireContext().resources.displayMetrics.widthPixels

    /**
     * 屏幕高度（px，当前 App 可用区域）。
     *
     * 例：-> 2340
     */
    @JvmStatic
    val screenHeight: Int
        get() = requireContext().resources.displayMetrics.heightPixels

    /**
     * 屏幕密度（density）。
     *
     * 例：-> 3.0
     */
    @JvmStatic
    val screenDensity: Float
        get() = requireContext().resources.displayMetrics.density

    /**
     * 屏幕密度 DPI。
     *
     * 例：-> 480
     */
    @JvmStatic
    val screenDensityDpi: Int
        get() = requireContext().resources.displayMetrics.densityDpi

    private fun requireContext(): Context {
        return mApp ?: throw IllegalStateException(
            "ScreenUtils 未初始化，请先调用 ScreenUtils.initUtils(context)"
        )
    }
}