package com.java.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes

/**
 * @Introduce: Toast工具类.
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description :1. 支持子线程弹窗 2. 支持自定义弹窗View 3. 支持自定义弹窗显示时间 4. 支持随时取消弹窗 5. 支持使用Values资源
 */
@SuppressLint("StaticFieldLeak")
object ToastUtil : BaseUtils {

    private var currentToast: Toast? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private const val DEFAULT_DURATION = Toast.LENGTH_SHORT

    private lateinit var mContext: Context


    override fun initUtils(context: Context) {
        mContext = context.applicationContext
    }

    override fun unInitUtils() {

    }

    /**
     * 显示短时间Toast（字符串）.
     */
    @JvmStatic
    fun showShort(message: String) {
        show(message, Toast.LENGTH_SHORT)
    }

    /**
     * 显示长时间Toast（字符串）.
     */
    @JvmStatic
    fun showLong(message: String) {
        show(message, Toast.LENGTH_LONG)
    }

    /**
     * 显示短时间Toast（字符串资源）.
     */
    @JvmStatic
    fun showShort(@StringRes resId: Int) {
        show(mContext.getString(resId), Toast.LENGTH_SHORT)
    }

    /**
     * 显示长时间Toast（字符串资源）.
     */
    @JvmStatic
    fun showLong(@StringRes resId: Int) {
        show(mContext.getString(resId), Toast.LENGTH_LONG)
    }

    /**
     * 显示带格式的Toast.
     */
    @JvmStatic
    fun showShort(@StringRes resId: Int, vararg formatArgs: Any) {
        show(mContext.getString(resId, *formatArgs), Toast.LENGTH_SHORT)
    }

    /**
     * 显示自定义View的Toast.
     */
    @JvmStatic
    fun showCustomView(
        @LayoutRes layoutResId: Int,
        duration: Int = DEFAULT_DURATION
    ) {
        showCustomView(layoutResId, duration, Gravity.CENTER, 0, 0)
    }

    /**
     * 显示自定义时长的Toast.
     */
    @JvmStatic
    fun show(message: String, duration: Int = DEFAULT_DURATION) {
        show(message, duration, Gravity.CENTER, 0, 0)
    }

    /**
     * 显示Toast（核心方法）.
     */
    @JvmStatic
    fun show(
        message: String,
        duration: Int,
        gravity: Int = Gravity.CENTER,
        xOffset: Int = 0,
        yOffset: Int = 0
    ) {
        // 如果不在主线程，切换到主线程执行
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post {
                showOnMainThread(message, duration, gravity, xOffset, yOffset)
            }
        } else {
            showOnMainThread(message, duration, gravity, xOffset, yOffset)
        }
    }

    /**
     * 显示自定义时长的Toast（毫秒）.
     */
    @JvmStatic
    fun showWithDuration(message: String, durationMillis: Long) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post {
                showWithDurationOnMainThread(message, durationMillis)
            }
        } else {
            showWithDurationOnMainThread(message, durationMillis)
        }
    }

    /**
     * 显示自定义View的Toast.
     */
    @JvmStatic
    fun showCustomView(
        @LayoutRes layoutResId: Int,
        duration: Int,
        gravity: Int = Gravity.CENTER,
        xOffset: Int = 0,
        yOffset: Int = 0
    ) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post {
                showCustomViewOnMainThread(
                    layoutResId,
                    duration,
                    gravity,
                    xOffset,
                    yOffset
                )
            }
        } else {
            showCustomViewOnMainThread(layoutResId, duration, gravity, xOffset, yOffset)
        }
    }

    /**
     * 显示带图标和自定义样式的Toast.
     */
    /*    @JvmStatic
        fun showWithIcon(
            context: Context,
            message: String,
            @DrawableRes iconRes: Int,
            duration: Int = Toast.LENGTH_SHORT,
            backgroundColor: Int = android.R.color.black,
            textColor: Int = android.R.color.white
        ) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mainHandler.post {
                    showWithIconOnMainThread(message, iconRes, duration, backgroundColor, textColor)
                }
            } else {
                showWithIconOnMainThread(message, iconRes, duration, backgroundColor, textColor)
            }
        }*/

    /**
     * 取消当前显示的Toast.
     */
    @JvmStatic
    fun cancel() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post {
                cancelCurrentToast()
            }
        } else {
            cancelCurrentToast()
        }
    }

    /**
     * 设置Toast位置.
     */
    @JvmStatic
    fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        currentToast?.setGravity(gravity, xOffset, yOffset)
    }

    /**
     * 在主线程显示Toast.
     */
    private fun showOnMainThread(
        message: String,
        duration: Int,
        gravity: Int,
        xOffset: Int,
        yOffset: Int
    ) {
        cancelCurrentToast()

        currentToast = Toast.makeText(mContext, message, duration)
        if (gravity != Gravity.CENTER || xOffset != 0 || yOffset != 0) {
            currentToast?.setGravity(gravity, xOffset, yOffset)
        }
        currentToast?.show()
    }

    /**
     * 在主线程显示自定义时长的Toast.
     */
    private fun showWithDurationOnMainThread(
        message: String,
        durationMillis: Long
    ) {
        cancelCurrentToast()

        currentToast =
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).apply {
                show()
            }

        // 在指定时间后取消
        mainHandler.postDelayed({
            cancelCurrentToast()
        }, durationMillis)
    }

    /**
     * 在主线程显示自定义View的Toast.
     */
    private fun showCustomViewOnMainThread(
        @LayoutRes layoutResId: Int,
        duration: Int,
        gravity: Int,
        xOffset: Int,
        yOffset: Int
    ) {
        cancelCurrentToast()

        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(layoutResId, null)

        currentToast = Toast(mContext).apply {
            setView(view)
            setDuration(duration)
            if (gravity != Gravity.CENTER || xOffset != 0 || yOffset != 0) {
                setGravity(gravity, xOffset, yOffset)
            }
        }
        currentToast?.show()
    }

    /**
     * 在主线程显示带图标的Toast
     */
    /*    private fun showWithIconOnMainThread(
            context: Context,
            message: String,
            @DrawableRes iconRes: Int,
            duration: Int,
            backgroundColor: Int,
            textColor: Int
        ) {
            cancelCurrentToast()

            val view = LayoutInflater.from(context).inflate(R.layout.custom_toast_layout, null)
            view.apply {
                findViewById<TextView>(R.id.tv_message).text = message
                findViewById<ImageView>(R.id.iv_icon).setImageResource(iconRes)
                setBackgroundColor(ContextCompat.getColor(backgroundColor))
                findViewById<TextView>(R.id.tv_message).setTextColor(ContextCompat.getColor(textColor))
            }

            currentToast = Toast(context.applicationContext).apply {
                setView(view)
                setDuration(duration)
                show()
            }
        }*/

    /**
     * 取消当前Toast.
     */
    private fun cancelCurrentToast() {
        currentToast?.cancel()
        currentToast = null
    }


    /**
     * 链式调用构建模式.
     */
    class Builder(private val context: Context) {
        private var message: String = ""
        private var duration: Int = Toast.LENGTH_SHORT
        private var gravity: Int = Gravity.CENTER
        private var xOffset: Int = 0
        private var yOffset: Int = 0
        private var customView: View? = null
        private var customDuration: Long = 0L

        fun message(msg: String): Builder {
            this.message = msg
            return this
        }

        fun message(@StringRes resId: Int): Builder {
            this.message = context.getString(resId)
            return this
        }

        fun duration(duration: Int): Builder {
            this.duration = duration
            return this
        }

        fun gravity(gravity: Int, xOffset: Int = 0, yOffset: Int = 0): Builder {
            this.gravity = gravity
            this.xOffset = xOffset
            this.yOffset = yOffset
            return this
        }

        fun customView(@LayoutRes layoutResId: Int): Builder {
            this.customView = LayoutInflater.from(context).inflate(layoutResId, null)
            return this
        }

        fun customDuration(millis: Long): Builder {
            this.customDuration = millis
            return this
        }

        fun show() {
            when {
                customView != null -> {
                    showCustomView(
                        customView!!,
                        if (customDuration > 0) Toast.LENGTH_LONG else duration,
                        gravity,
                        xOffset,
                        yOffset
                    )
                    if (customDuration > 0) {
                        mainHandler.postDelayed({ cancel() }, customDuration)
                    }
                }

                customDuration > 0 -> {
                    showWithDuration(message, customDuration)
                }

                else -> {
                    show(message, duration, gravity, xOffset, yOffset)
                }
            }
        }

        private fun showCustomView(
            view: View,
            duration: Int,
            gravity: Int,
            xOffset: Int,
            yOffset: Int
        ) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mainHandler.post {
                    showCustomViewOnMainThread(view, duration, gravity, xOffset, yOffset)
                }
            } else {
                showCustomViewOnMainThread(view, duration, gravity, xOffset, yOffset)
            }
        }

        private fun showCustomViewOnMainThread(
            view: View,
            duration: Int,
            gravity: Int,
            xOffset: Int,
            yOffset: Int
        ) {
            cancelCurrentToast()
            currentToast = Toast(context).apply {
                setView(view)
                setDuration(duration)
                if (gravity != Gravity.CENTER || xOffset != 0 || yOffset != 0) {
                    setGravity(gravity, xOffset, yOffset)
                }
                show()
            }
        }
    }

    /**
     * 创建构建器.
     */
    @JvmStatic
    fun with(context: Context): Builder {
        return Builder(context.applicationContext)
    }
}