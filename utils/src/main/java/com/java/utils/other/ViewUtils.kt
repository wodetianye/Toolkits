package com.java.utils.other

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.graphics.createBitmap
import com.java.utils.BaseUtils

/**
 * @Introduce: View 工具类.
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description: 动态修改 View 宽高、获取宽高、设置/获取常用属性、View 截图等.
 */
object ViewUtils : BaseUtils {

    override fun initUtils(context: Context) {

    }

    override fun unInitUtils() {

    }


    /**
     * 动态设置 View 的宽高（单位 px）。
     *
     * 例：setSize(view, 300, 150) -> view 宽 300px、高 150px
     */
    @JvmStatic
    fun setSize(view: View, widthPx: Int, heightPx: Int) {
        val params = view.layoutParams
            ?: ViewGroup.LayoutParams(widthPx, heightPx).also { view.layoutParams = it }
        params.width = widthPx
        params.height = heightPx
        view.layoutParams = params
    }

    /** 动态设置 View 的宽高（单位 dp）。 */
    @JvmStatic
    fun setSizeDp(view: View, widthDp: Float, heightDp: Float) {
        setSize(view, ScreenUtils.dp2px(widthDp), ScreenUtils.dp2px(heightDp))
    }

    /** 只设置宽度（单位 px）。 */
    @JvmStatic
    fun setWidth(view: View, widthPx: Int) {
        val params = view.layoutParams
            ?: ViewGroup.LayoutParams(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT).also {
                view.layoutParams = it
            }
        params.width = widthPx
        view.layoutParams = params
    }

    /** 只设置宽度（单位 dp）。 */
    @JvmStatic
    fun setWidthDp(view: View, widthDp: Float) {
        setWidth(view, ScreenUtils.dp2px(widthDp))
    }

    /** 只设置高度（单位 px）。 */
    @JvmStatic
    fun setHeight(view: View, heightPx: Int) {
        val params = view.layoutParams
            ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, heightPx).also {
                view.layoutParams = it
            }
        params.height = heightPx
        view.layoutParams = params
    }

    /** 只设置高度（单位 dp）。 */
    @JvmStatic
    fun setHeightDp(view: View, heightDp: Float) {
        setHeight(view, ScreenUtils.dp2px(heightDp))
    }

    /** 设置 Margin（单位 px）。 */
    @JvmStatic
    fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        val params = view.layoutParams as? ViewGroup.MarginLayoutParams ?: return
        params.setMargins(left, top, right, bottom)
        view.layoutParams = params
    }

    /** 设置 Margin（单位 dp）。 */
    @JvmStatic
    fun setMarginsDp(view: View, leftDp: Float, topDp: Float, rightDp: Float, bottomDp: Float) {
        setMargins(
            view,
            ScreenUtils.dp2px(leftDp),
            ScreenUtils.dp2px(topDp),
            ScreenUtils.dp2px(rightDp),
            ScreenUtils.dp2px(bottomDp)
        )
    }

    /** 获取 View 当前宽度（px），未测量返回 0。 */
    @JvmStatic
    fun getWidth(view: View): Int = view.width

    /** 获取 View 当前高度（px），未测量返回 0。 */
    @JvmStatic
    fun getHeight(view: View): Int = view.height

    /** 获取宽度，未测量返回默认值。 */
    @JvmStatic
    fun getWidthOr(view: View, default: Int): Int =
        if (view.width > 0) view.width else default

    /** 获取高度，未测量返回默认值。 */
    @JvmStatic
    fun getHeightOr(view: View, default: Int): Int =
        if (view.height > 0) view.height else default

    /** 是否已完成测量/布局。 */
    @JvmStatic
    fun isMeasured(view: View): Boolean = view.width > 0 && view.height > 0

    /**
     * 布局完成后回调真实宽高（Java 友好版）。
     *
     * Java 用法：
     * ViewUtils.getSizeAfterLayout(view, (w, h) -> { ... });
     */
    @JvmStatic
    fun getSizeAfterLayout(view: View, listener: OnSizeReadyListener) {
        if (isMeasured(view)) {
            listener.onSizeReady(view.width, view.height)
            return
        }
        view.post {
            if (isMeasured(view)) {
                listener.onSizeReady(view.width, view.height)
            } else {
                view.viewTreeObserver.addOnGlobalLayoutListener(
                    object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            if (view.viewTreeObserver.isAlive) {
                                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }
                            if (isMeasured(view)) {
                                listener.onSizeReady(view.width, view.height)
                            }
                        }
                    }
                )
            }
        }
    }

    /** 获取 LayoutParams 中声明的宽度（可能是 MATCH_PARENT=-1 / WRAP_CONTENT=-2）。 */
    @JvmStatic
    fun getLayoutWidth(view: View): Int = view.layoutParams?.width ?: 0

    /** 获取 LayoutParams 中声明的高度。 */
    @JvmStatic
    fun getLayoutHeight(view: View): Int = view.layoutParams?.height ?: 0

    /**
     * 强制测量 View 并返回 measuredWidth（px）。
     *
     * 例：measureWidth(view) -> 1080
     */
    @JvmStatic
    fun measureWidth(view: View): Int {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(ScreenUtils.screenWidth, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(ScreenUtils.screenHeight, View.MeasureSpec.AT_MOST)
        )
        return view.measuredWidth
    }

    /**
     * 强制测量 View 并返回 measuredHeight（px）。
     *
     * 例：measureHeight(view) -> 480
     */
    @JvmStatic
    fun measureHeight(view: View): Int {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(ScreenUtils.screenWidth, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(ScreenUtils.screenHeight, View.MeasureSpec.AT_MOST)
        )
        return view.measuredHeight
    }

    /** 设置透明度（0f ~ 1f）。 */
    @JvmStatic
    fun setAlpha(view: View, alpha: Float) {
        view.alpha = alpha.coerceIn(0f, 1f)
    }

    /** 获取透明度。 */
    @JvmStatic
    fun getAlpha(view: View): Float = view.alpha

    /** 设置内边距（单位 px）。 */
    @JvmStatic
    fun setPadding(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        view.setPadding(left, top, right, bottom)
    }

    /** 设置内边距（单位 dp）。 */
    @JvmStatic
    fun setPaddingDp(view: View, leftDp: Float, topDp: Float, rightDp: Float, bottomDp: Float) {
        view.setPadding(
            ScreenUtils.dp2px(leftDp),
            ScreenUtils.dp2px(topDp),
            ScreenUtils.dp2px(rightDp),
            ScreenUtils.dp2px(bottomDp)
        )
    }

    /** 将 View 绘制成 Bitmap（宽高必须 > 0）。 */
    @JvmStatic
    fun captureViewBitmap(view: View): Bitmap {
        require(view.width > 0 && view.height > 0) {
            "View 尚未完成测量，宽高为 0，无法截图"
        }
        val bitmap = createBitmap(view.width, view.height)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    /** 将 Activity 的 DecorView 绘制成 Bitmap。 */
    @JvmStatic
    fun captureActivityBitmap(activity: Activity): Bitmap {
        return captureViewBitmap(activity.window.decorView)
    }

    /** 字符串中的数字和冒号转为粗体。 */
    @JvmStatic
    fun digitToBold(str: String?): SpannableString {
        if (TextUtils.isEmpty(str)) return SpannableString("")
        val spanStr = SpannableString(str)
        var start = -1
        for (i in 0..str!!.length) {
            val isBold = i < str.length &&
                    (Character.isDigit(str[i]) || str[i] == ':')
            if (isBold && start == -1) {
                start = i
            } else if (!isBold && start != -1) {
                spanStr.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start, i,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                start = -1
            }
        }
        return spanStr
    }

    /**
     * 布局完成回调（Java 可直接用 lambda）。
     *
     * Java 用法：
     * ViewUtils.getSizeAfterLayout(view, (w, h) -> {
     *     Log.d("TAG", "w=" + w + ", h=" + h);
     * });
     */
    fun interface OnSizeReadyListener {
        fun onSizeReady(width: Int, height: Int)
    }
}