package com.java.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * @Introduce: 资源管理.
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description :可以通过资源名字来获取对应的资源id
 */
class ResourceUtils private constructor() : BaseUtils{
    private lateinit var mContext: Context
    private var mResources: Resources? = null

    companion object {
        private const val TAG = "ResourceUtils"
        val instance by lazy { ResourceUtils() }
    }

    override fun initUtils(context: Context) {
        mContext = context.applicationContext
        mResources = mContext.resources
    }

    override fun unInitUtils() {
        mResources = null
    }

    val displayMetrics: DisplayMetrics
        /**
         * 获取屏幕信息.
         * widthPixels 屏幕宽。当手机发生旋转时，之前的宽会变成高。
         * heightPixels 屏幕高。当手机发生旋转时，之前的高会变成宽
         * densityDpi 屏幕密度，即每英寸的屏幕中包含的像素数量，标准的屏幕密度为160
         * density  密度的比例（官方称它为显示的逻辑密度）= densityDpi/160，标准的屏幕密度为160，它的密度比例就是1，即1个dp就等于1个像素；
         * 手机的densityDpi为320，则它是标准屏幕密度的两倍（320 / 160 = 2），则density = 2，表示1个dp就等于2个像素
         *
         * @return
         */
        get() = mResources!!.displayMetrics

    /**
     * 是基于当前DisplayMetrics进行转换，获取指定资源id对应的尺寸，用于代码中设置尺寸.
     *
     * @param resId 资源id（必须时定义的dip资源，是px的话，则不会乘以屏幕密度）
     * @return 返回一个定义的dip值乘以屏幕密度（density 密度比例）
     */
    fun getDimension(@DimenRes resId: Int): Float {
        return mResources!!.getDimension(resId)
    }

    /**
     * 是基于当前DisplayMetrics进行转换，获取指定资源id对应的尺寸，且结果四舍五入，用于代码中设置尺寸.
     *
     * @param resId 资源id（必须时定义的dip资源，是px的话，则不会乘以屏幕密度）
     * @return 返回一个定义的dip值乘以屏幕密度（density 密度比例）
     */
    fun getDimensionPixelSize(@DimenRes resId: Int): Int {
        return mResources!!.getDimensionPixelSize(resId)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDrawable(@DrawableRes resId: Int): Drawable {
        return mResources!!.getDrawable(resId, null)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDrawable(@DrawableRes resId: Int, theme: Theme?): Drawable {
        return mResources!!.getDrawable(resId, theme)
    }

    /**
     * 获取颜色资源.
     *
     * @param resId 资源id
     * @return 颜色资源可用Id
     */
    fun getColor(@ColorRes resId: Int): Int {
        return getColor(resId, mContext.theme)
    }

    /**
     * 获取颜色资源，支持重设风格.
     *
     * @param resId 资源id
     * @param theme current theme
     * @return 颜色资源可用Id
     */
    fun getColor(@ColorRes resId: Int, theme: Theme?): Int {
        return mResources!!.getColor(resId, theme)
    }

    /**
     * 获取字符资源.
     *
     * @param resId
     * @return 返回一个字符串
     */
    fun getString(@StringRes resId: Int): String {
        return mResources!!.getText(resId).toString()
    }

    /**
     * 获取字符资源，支持拼接字符，
     *
     * @param resId
     * @return 返回一个字符串
     */
    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return mResources!!.getString(resId, *formatArgs)
    }

    /**
     * 获取一个字符对象，可以用来改变字体颜色、尺寸等操作.
     *
     * @param resId 资源id
     * @return 返回一个CharSequence
     */
    fun getText(@StringRes resId: Int): CharSequence {
        return mResources!!.getText(resId)
    }

    fun getLayoutId(resourceName: String?): Int {
        return getResId(resourceName, "layout")
    }

    fun getStringId(resourceName: String?): Int {
        return getResId(resourceName, "string")
    }

    fun getDrawableId(resourceName: String?): Int {
        return getResId(resourceName, "drawable")
    }

    fun getMipmapId(resourceName: String?): Int {
        return getResId(resourceName, "mipmap")
    }

    fun getColorId(resourceName: String?): Int {
        return getResId(resourceName, "color")
    }

    fun getDimenId(resourceName: String?): Int {
        return getResId(resourceName, "dimen")
    }

    fun getAttrId(resourceName: String?): Int {
        return getResId(resourceName, "attr")
    }

    fun getStyleId(resourceName: String?): Int {
        return getResId(resourceName, "style")
    }

    fun getAnimId(resourceName: String?): Int {
        return getResId(resourceName, "anim")
    }

    fun getArrayId(resourceName: String?): Int {
        return getResId(resourceName, "array")
    }

    fun getIntegerId(resourceName: String?): Int {
        return getResId(resourceName, "integer")
    }

    /**
     * 根据资源名字获取资源id.
     *
     * @param resourceName 资源名字
     * @param defType      资源类型
     * @return 资源id
     */
    fun getResId(resourceName: String?, defType: String?): Int {
        return mResources!!.getIdentifier(
            resourceName,
            defType,
            mContext.packageName
        )
    }
}