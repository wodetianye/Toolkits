package com.java.utils.other

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
import com.java.utils.BaseUtils

/**
 * @Introduce: Toast工具类.
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description :1. 支持子线程弹窗 2. 支持自定义弹窗View 3. 支持自定义弹窗显示时间 4. 支持随时取消弹窗 5. 支持使用Values资源
 */

@SuppressLint("StaticFieldLeak")
object  ToastUtils : BaseUtils {
    private var currentToast: Toast? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private const val DEFAULT_DURATION = Toast.LENGTH_SHORT
    private var mContext: Context? = null

    override fun initUtils(context: Context) {
        mContext = context.applicationContext
    }

    override fun unInitUtils() {
        mainHandler.removeCallbacksAndMessages(null)
        cancelCurrentToast()
        mContext = null
    }
    @JvmStatic
    fun showShort(message: String) = show(message, Toast.LENGTH_SHORT)

    @JvmStatic
    fun showLong(message: String) = show(message, Toast.LENGTH_LONG)

    @JvmStatic
    fun showShort(@StringRes resId: Int) {
        show(requireContext().getString(resId), Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun showLong(@StringRes resId: Int) {
        show(requireContext().getString(resId), Toast.LENGTH_LONG)
    }

    @JvmStatic
    fun showShort(@StringRes resId: Int, vararg formatArgs: Any) {
        show(requireContext().getString(resId, *formatArgs), Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun showLong(@StringRes resId: Int, vararg formatArgs: Any) {
        show(requireContext().getString(resId, *formatArgs), Toast.LENGTH_LONG)
    }

    /**
     * 显示 Toast（核心方法）。
     *
     * @param duration 仅支持 [Toast.LENGTH_SHORT] 或 [Toast.LENGTH_LONG]。
     *                 需要自定义时长请用 [showWithDuration]。
     */
    @JvmStatic
    fun show(
        message: String,
        duration: Int = DEFAULT_DURATION,
        gravity: Int = Gravity.CENTER,
        xOffset: Int = 0,
        yOffset: Int = 0
    ) {
        runOnMain { showOnMainThread(message, duration, gravity, xOffset, yOffset) }
    }

    /**
     * 显示自定义时长的 Toast（毫秒）。
     *
     * 实现方式：先以 LENGTH_LONG 显示，再延时取消。
     * 在部分定制 ROM（MIUI、EMUI 等）上，系统可能接管时长，取消不一定精确生效。
     */
    @JvmStatic
    fun showWithDuration(message: String, durationMillis: Long) {
        runOnMain { showWithDurationOnMainThread(message, durationMillis) }
    }

    @JvmStatic
    fun showCustomView(
        @LayoutRes layoutResId: Int,
        duration: Int = DEFAULT_DURATION
    ) {
        showCustomView(layoutResId, duration, Gravity.CENTER, 0, 0)
    }

    @JvmStatic
    fun showCustomView(
        @LayoutRes layoutResId: Int,
        duration: Int,
        gravity: Int,
        xOffset: Int = 0,
        yOffset: Int = 0
    ) {
        runOnMain {
            showCustomViewOnMainThread(layoutResId, duration, gravity, xOffset, yOffset)
        }
    }

    @JvmStatic
    fun cancel() {
        runOnMain { cancelCurrentToast() }
    }

    private fun showOnMainThread(
        message: String,
        duration: Int,
        gravity: Int,
        xOffset: Int,
        yOffset: Int
    ) {
        cancelCurrentToast()
        val toast = Toast.makeText(requireContext(), message, duration)
        if (gravity != Gravity.CENTER || xOffset != 0 || yOffset != 0) {
            toast.setGravity(gravity, xOffset, yOffset)
        }
        currentToast = toast
        toast.show()
    }

    private fun showWithDurationOnMainThread(message: String, durationMillis: Long) {
        cancelCurrentToast()
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
        currentToast = toast
        toast.show()
        // 只取消自己，避免误杀这段时间内新弹出的 Toast。
        mainHandler.postDelayed({
            if (currentToast === toast) {
                cancelCurrentToast()
            }
        }, durationMillis)
    }

    private fun showCustomViewOnMainThread(
        @LayoutRes layoutResId: Int,
        duration: Int,
        gravity: Int,
        xOffset: Int,
        yOffset: Int
    ) {
        cancelCurrentToast()
        val view = LayoutInflater.from(requireContext()).inflate(layoutResId, null)
        val toast = Toast(requireContext()).apply {
            this.view = view
            this.duration = duration
            if (gravity != Gravity.CENTER || xOffset != 0 || yOffset != 0) {
                setGravity(gravity, xOffset, yOffset)
            }
        }
        currentToast = toast
        toast.show()
    }

    private fun cancelCurrentToast() {
        currentToast?.cancel()
        currentToast = null
    }

    /** 统一的主线程调度。 */
    private inline fun runOnMain(crossinline block: () -> Unit) {
//    private fun runOnMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post { block() }
        }
    }

    private fun requireContext(): Context {
        return mContext ?: throw IllegalStateException(
            "ToastUtil 未初始化，请先调用 ToastUtil.initUtils(context)"
        )
    }

    class Builder internal constructor(private val context: Context) {

        private var message: String = ""
        private var duration: Int = Toast.LENGTH_SHORT
        private var gravity: Int = Gravity.CENTER
        private var xOffset: Int = 0
        private var yOffset: Int = 0
        private var customView: View? = null
        private var customDuration: Long = 0L

        fun message(msg: String): Builder = apply { this.message = msg }

        fun message(@StringRes resId: Int): Builder =
            apply { this.message = context.getString(resId) }

        fun message(@StringRes resId: Int, vararg formatArgs: Any): Builder =
            apply { this.message = context.getString(resId, *formatArgs) }

        fun duration(duration: Int): Builder = apply { this.duration = duration }

        @JvmOverloads
        fun gravity(gravity: Int, xOffset: Int = 0, yOffset: Int = 0): Builder = apply {
            this.gravity = gravity
            this.xOffset = xOffset
            this.yOffset = yOffset
        }

        fun customView(@LayoutRes layoutResId: Int): Builder = apply {
            this.customView = LayoutInflater.from(context).inflate(layoutResId, null)
        }

        fun customView(view: View): Builder = apply { this.customView = view }

        fun customDuration(millis: Long): Builder = apply { this.customDuration = millis }

        fun show() {
            val view = customView
            when {
                view != null -> {
                    val realDuration =
                        if (customDuration > 0) Toast.LENGTH_LONG else duration
                    showCustomViewInternal(view, realDuration, gravity, xOffset, yOffset)
                    if (customDuration > 0) {
                        // 同样只取消自己对应的 Toast。
                        val shown = currentToast
                        mainHandler.postDelayed({
                            if (currentToast === shown) {
                                cancel()
                            }
                        }, customDuration)
                    }
                }

                customDuration > 0 -> showWithDuration(message, customDuration)

                else -> show(message, duration, gravity, xOffset, yOffset)
            }
        }

        private fun showCustomViewInternal(
            view: View,
            duration: Int,
            gravity: Int,
            xOffset: Int,
            yOffset: Int
        ) {
            runOnMain {
                cancelCurrentToast()
                val toast = Toast(context).apply {
                    this.view = view
                    this.duration = duration
                    if (gravity != Gravity.CENTER || xOffset != 0 || yOffset != 0) {
                        setGravity(gravity, xOffset, yOffset)
                    }
                }
                currentToast = toast
                toast.show()
            }
        }
    }

    @JvmStatic
    fun with(context: Context): Builder = Builder(context.applicationContext)
}