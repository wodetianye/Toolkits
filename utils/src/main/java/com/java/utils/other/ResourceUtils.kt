package com.java.utils.other

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.java.utils.BaseUtils

/**
 * @Introduce: 资源管理.
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description: 通过资源名字获取资源 id、读取常用资源.
 */
object ResourceUtils : BaseUtils {

    private var mApp: Application? = null

    // 常用资源类型常量，避免到处硬编码字符串
    const val TYPE_LAYOUT = "layout"
    const val TYPE_STRING = "string"
    const val TYPE_DRAWABLE = "drawable"
    const val TYPE_MIPMAP = "mipmap"
    const val TYPE_COLOR = "color"
    const val TYPE_DIMEN = "dimen"
    const val TYPE_ATTR = "attr"
    const val TYPE_STYLE = "style"
    const val TYPE_ANIM = "anim"
    const val TYPE_ARRAY = "array"
    const val TYPE_INTEGER = "integer"
    const val TYPE_BOOL = "bool"

    override fun initUtils(context: Context) {
        mApp = context.applicationContext as Application
    }

    override fun unInitUtils() {
        mApp = null
    }

    // 公开方法 —— 屏幕信息 =====================================================================

    /**
     * 获取屏幕信息。
     *
     * 例：displayMetrics -> DisplayMetrics(density=3.0, widthPixels=1080, ...)
     */
    @JvmStatic
    val displayMetrics: DisplayMetrics
        get() = resources.displayMetrics

    /**
     * 获取 Resources。
     *
     * 例：resources -> Resources
     */
    @JvmStatic
    val resources: Resources
        get() = requireContext().resources

    // 公开方法 —— 尺寸 =====================================================================

    /**
     * 获取 dimen 资源的值（带单位的浮点数）。
     *
     * 例：getDimension(R.dimen.text_size) -> 16.0f
     */
    @JvmStatic
    fun getDimension(@DimenRes resId: Int): Float = resources.getDimension(resId)

    /**
     * 获取 dimen 资源的像素值（四舍五入）。
     *
     * 例：getDimensionPixelSize(R.dimen.padding) -> 16
     */
    @JvmStatic
    fun getDimensionPixelSize(@DimenRes resId: Int): Int =
        resources.getDimensionPixelSize(resId)

    /**
     * 获取 dimen 资源的像素偏移（不四舍五入，取整）。
     *
     * 例：getDimensionPixelOffset(R.dimen.padding) -> 16
     */
    @JvmStatic
    fun getDimensionPixelOffset(@DimenRes resId: Int): Int =
        resources.getDimensionPixelOffset(resId)

    // 公开方法 —— Drawable =====================================================================

    /**
     * 获取 Drawable。
     *
     * 例：getDrawable(R.drawable.ic_logo) -> Drawable?
     */
    @JvmStatic
    fun getDrawable(@DrawableRes resId: Int): Drawable? {
        return ContextCompat.getDrawable(requireContext(), resId)
    }

    /**
     * 获取 Drawable（指定主题）。
     *
     * 例：getDrawable(R.drawable.ic_logo, theme) -> Drawable?
     */
    @JvmStatic
    fun getDrawable(@DrawableRes resId: Int, theme: Theme?): Drawable? {
        return ResourcesCompat.getDrawable(resources, resId, theme)
    }

    // 公开方法 —— 颜色 =====================================================================

    /**
     * 获取颜色值。
     *
     * 例：getColor(R.color.red) -> 0xFFFF0000
     */
    @JvmStatic
    fun getColor(@ColorRes resId: Int): Int {
        return ContextCompat.getColor(requireContext(), resId)
    }

    /**
     * 获取颜色值（指定主题）。
     *
     * 例：getColor(R.color.red, theme) -> 0xFFFF0000
     */
    @JvmStatic
    fun getColor(@ColorRes resId: Int, theme: Theme?): Int {
        return ResourcesCompat.getColor(resources, resId, theme)
    }

    /**
     * 获取 ColorStateList。
     *
     * 例：getColorStateList(R.color.selector_text) -> ColorStateList?
     */
    @JvmStatic
    fun getColorStateList(@ColorRes resId: Int): ColorStateList? {
        return ContextCompat.getColorStateList(requireContext(), resId)
    }

    // 公开方法 —— 字符串 =====================================================================

    /**
     * 获取字符串资源。
     *
     * 例：getString(R.string.app_name) -> "MyApp"
     */
    @JvmStatic
    fun getString(@StringRes resId: Int): String = resources.getString(resId)

    /**
     * 获取字符串资源（带格式化参数）。
     *
     * 例：getString(R.string.welcome, "张三") -> "欢迎 张三"
     */
    @JvmStatic
    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String =
        resources.getString(resId, *formatArgs)

    /**
     * 获取字符串资源（保留 Span 富文本）。
     *
     * 例：getText(R.string.styled_text) -> CharSequence
     */
    @JvmStatic
    fun getText(@StringRes resId: Int): CharSequence = resources.getText(resId)

    /**
     * 获取字符串数组资源。
     *
     * 例：getStringArray(R.array.city_list) -> Array<String>
     */
    @JvmStatic
    fun getStringArray(@ArrayRes resId: Int): Array<String> =
        resources.getStringArray(resId)

    /**
     * 获取复数形式字符串（无格式化参数）。
     *
     * 例：getQuantityString(R.plurals.apples, 3) -> "3 apples"
     */
    @JvmStatic
    @SuppressLint("ResourceType")
    fun getQuantityString(@StringRes resId: Int, quantity: Int): String =
        resources.getQuantityString(resId, quantity)

    /**
     * 获取复数形式字符串（带格式化参数）。
     *
     * 例：getQuantityString(R.plurals.apples, 3, 3) -> "3 apples"
     */
    @JvmStatic
    @SuppressLint("ResourceType")
    fun getQuantityString(@StringRes resId: Int, quantity: Int, vararg formatArgs: Any): String =
        resources.getQuantityString(resId, quantity, *formatArgs)

    // 公开方法 —— 其他资源 =====================================================================

    /**
     * 获取整数资源。
     *
     * 例：getInteger(R.integer.max_count) -> 100
     */
    @JvmStatic
    fun getInteger(@IntegerRes resId: Int): Int = resources.getInteger(resId)

    /**
     * 获取布尔资源。
     *
     * 例：getBoolean(R.bool.is_debug) -> true
     */
    @JvmStatic
    fun getBoolean(@BoolRes resId: Int): Boolean = resources.getBoolean(resId)

    /**
     * 获取整数数组资源。
     *
     * 例：getIntArray(R.array.numbers) -> IntArray
     */
    @JvmStatic
    fun getIntArray(@ArrayRes resId: Int): IntArray = resources.getIntArray(resId)

    // 公开方法 —— 通过资源名获取 ID =====================================================================

    /**
     * 根据资源名获取资源 ID。
     *
     * 例：getResId("app_name", TYPE_STRING) -> R.string.app_name
     *
     * 注意：找不到时返回 0。
     */
    @JvmStatic
    fun getResId(resourceName: String?, defType: String?): Int {
        if (resourceName.isNullOrEmpty() || defType.isNullOrEmpty()) return 0
        return resources.getIdentifier(resourceName, defType, requireContext().packageName)
    }

    /**
     * 根据资源名获取 layout ID。
     *
     * 例：getLayoutId("activity_main") -> R.layout.activity_main
     */
    @JvmStatic
    fun getLayoutId(resourceName: String?): Int = getResId(resourceName, TYPE_LAYOUT)

    /**
     * 根据资源名获取 string ID。
     *
     * 例：getStringId("app_name") -> R.string.app_name
     */
    @JvmStatic
    fun getStringId(resourceName: String?): Int = getResId(resourceName, TYPE_STRING)

    /**
     * 根据资源名获取 drawable ID。
     *
     * 例：getDrawableId("ic_logo") -> R.drawable.ic_logo
     */
    @JvmStatic
    fun getDrawableId(resourceName: String?): Int = getResId(resourceName, TYPE_DRAWABLE)

    /**
     * 根据资源名获取 mipmap ID。
     *
     * 例：getMipmapId("ic_launcher") -> R.mipmap.ic_launcher
     */
    @JvmStatic
    fun getMipmapId(resourceName: String?): Int = getResId(resourceName, TYPE_MIPMAP)

    /**
     * 根据资源名获取 color ID。
     *
     * 例：getColorId("red") -> R.color.red
     */
    @JvmStatic
    fun getColorId(resourceName: String?): Int = getResId(resourceName, TYPE_COLOR)

    /**
     * 根据资源名获取 dimen ID。
     *
     * 例：getDimenId("text_size") -> R.dimen.text_size
     */
    @JvmStatic
    fun getDimenId(resourceName: String?): Int = getResId(resourceName, TYPE_DIMEN)

    /**
     * 根据资源名获取 attr ID。
     *
     * 例：getAttrId("colorPrimary") -> R.attr.colorPrimary
     */
    @JvmStatic
    fun getAttrId(resourceName: String?): Int = getResId(resourceName, TYPE_ATTR)

    /**
     * 根据资源名获取 style ID。
     */
    @JvmStatic
    fun getStyleId(resourceName: String?): Int = getResId(resourceName, TYPE_STYLE)

    /**
     * 根据资源名获取 anim ID。
     */
    @JvmStatic
    fun getAnimId(resourceName: String?): Int = getResId(resourceName, TYPE_ANIM)

    /**
     * 根据资源名获取 array ID。
     */
    @JvmStatic
    fun getArrayId(resourceName: String?): Int = getResId(resourceName, TYPE_ARRAY)

    /**
     * 根据资源名获取 integer ID。
     */
    @JvmStatic
    fun getIntegerId(resourceName: String?): Int = getResId(resourceName, TYPE_INTEGER)


    private fun requireContext(): Application {
        return mApp ?: throw IllegalStateException(
            "ResourceUtils 未初始化，请先调用 ResourceUtils.initUtils(context)"
        )
    }
}