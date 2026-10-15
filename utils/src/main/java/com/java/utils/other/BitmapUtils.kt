package com.java.utils.other

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.ColorInt
import androidx.core.graphics.createBitmap
import com.java.utils.BaseUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * @Introduce: Bitmap 工具类.
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description: 图片压缩、裁剪、缩放、旋转、透明度、高斯模糊、圆形头像等.
 */
object BitmapUtils : BaseUtils {

    private var mApp: Application? = null

    override fun initUtils(context: Context) {
        mApp = context.applicationContext as Application
    }

    override fun unInitUtils() {
        mApp = null
    }

    /**
     * 按质量压缩（只影响文件大小，不改变像素尺寸）。
     *
     * 例：compressByQuality(bitmap, 80) -> JPEG 字节数组（约缩小到 80% 质量）
     */
    @JvmStatic
    fun compressByQuality(
        bitmap: Bitmap,
        quality: Int = 80,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(format, quality.coerceIn(0, 100), baos)
        return baos.toByteArray()
    }

    /**
     * 按尺寸压缩（等比缩放到指定最大宽/高以内）。
     *
     * 例：compressBySize(bitmap, 800, 600) -> 最大边不超过 800/600 的 Bitmap
     */
    @JvmStatic
    fun compressBySize(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxWidth && height <= maxHeight) return bitmap

        val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        val targetWidth = (width * ratio).toInt().coerceAtLeast(1)
        val targetHeight = (height * ratio).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    /**
     * 从文件路径按采样率压缩加载（避免 OOM）。
     *
     * 例：compressFromFile("/sdcard/a.jpg", 800, 600) -> 解码后的 Bitmap
     */
    @JvmStatic
    fun compressFromFile(path: String, maxWidth: Int, maxHeight: Int): Bitmap? {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return try {
            BitmapFactory.decodeFile(path, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 从 InputStream 按采样率压缩加载。
     *
     * 例：compressFromStream(inputStream, 800, 600) -> 解码后的 Bitmap
     *
     * 注意：InputStream 会被读取两次，请确保支持 mark/reset，或传入新的流。
     */
    @JvmStatic
    fun compressFromStream(input: InputStream, maxWidth: Int, maxHeight: Int): Bitmap? {
        val bytes = input.readBytes()
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return try {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 按目标宽高等比缩放。
     *
     * 例：scale(bitmap, 200, 100) -> 宽 200、高 100 的 Bitmap
     */
    @JvmStatic
    fun scale(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    /**
     * 按比例缩放。
     *
     * 例：scaleByRatio(bitmap, 0.5f) -> 缩小到一半
     */
    @JvmStatic
    fun scaleByRatio(bitmap: Bitmap, ratio: Float): Bitmap {
        val w = (bitmap.width * ratio).toInt().coerceAtLeast(1)
        val h = (bitmap.height * ratio).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, w, h, true)
    }

    /**
     * 裁剪 Bitmap（指定矩形区域）。
     *
     * 例：crop(bitmap, 100, 100, 300, 300) -> 从 (100,100) 开始，宽高 300x300 的区域
     *
     * 注意：如果区域超出原图范围，会自动 clamp 到有效范围。
     */
    @JvmStatic
    fun crop(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int): Bitmap {
        val safeX = x.coerceIn(0, bitmap.width - 1)
        val safeY = y.coerceIn(0, bitmap.height - 1)
        val safeWidth = width.coerceIn(1, bitmap.width - safeX)
        val safeHeight = height.coerceIn(1, bitmap.height - safeY)
        return Bitmap.createBitmap(bitmap, safeX, safeY, safeWidth, safeHeight)
    }

    /**
     * 居中裁剪为正方形。
     *
     * 例：cropCenterSquare(bitmap) -> 以中心为基准的正方形 Bitmap
     */
    @JvmStatic
    fun cropCenterSquare(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2
        return Bitmap.createBitmap(bitmap, x, y, size, size)
    }

    /**
     * 生成圆形头像。
     *
     * 例：toCircle(bitmap) -> 圆形 Bitmap
     *
     * 内部会先居中裁剪为正方形，再裁成圆形。
     */
    @JvmStatic
    fun toCircle(bitmap: Bitmap): Bitmap {
        val square = cropCenterSquare(bitmap)
        val size = square.width
        val output = createBitmap(size, size)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, size, size)
        val rectF = RectF(rect)

        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(square, rect, rectF, paint)
        paint.xfermode = null
        return output
    }

    /**
     * 生成圆角图片。
     *
     * 例：toRoundCorner(bitmap, 20f) -> 圆角半径 20px 的 Bitmap
     */
    @JvmStatic
    fun toRoundCorner(bitmap: Bitmap, radius: Float): Bitmap {
        val output = createBitmap(bitmap.width, bitmap.height)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

        canvas.drawRoundRect(rect, radius, radius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        paint.xfermode = null
        return output
    }

    /**
     * 生成圆形图片，并加边框。
     *
     * 例：toCircleWithBorder(bitmap, 4f, Color.WHITE) -> 带白色 4px 边框的圆形图
     */
    @JvmStatic
    fun toCircleWithBorder(bitmap: Bitmap, borderWidth: Float, @ColorInt borderColor: Int): Bitmap {
        val circle = toCircle(bitmap)
        val size = circle.width
        val output = createBitmap(size, size)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // 先画圆（作为边框底色）
        paint.color = borderColor
        paint.style = Paint.Style.FILL
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        // 再把圆形头像画上去，半径缩小 borderWidth
        val innerRadius = size / 2f - borderWidth
        val rect = RectF(
            size / 2f - innerRadius,
            size / 2f - innerRadius,
            size / 2f + innerRadius,
            size / 2f + innerRadius
        )
        val srcRect = Rect(0, 0, size, size)
        canvas.drawBitmap(circle, srcRect, rect, paint)
        return output
    }

    // 公开方法 —— 旋转、翻转、透明度、模糊

    /**
     * 旋转 Bitmap。
     *
     * 例：rotate(bitmap, 90f) -> 顺时针旋转 90°
     */
    @JvmStatic
    fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        if (degrees % 360f == 0f) return bitmap
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * 水平翻转。
     *
     * 例：flipHorizontal(bitmap) -> 左右镜像
     */
    @JvmStatic
    fun flipHorizontal(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply { postScale(-1f, 1f) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * 垂直翻转。
     *
     * 例：flipVertical(bitmap) -> 上下镜像
     */
    @JvmStatic
    fun flipVertical(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply { postScale(1f, -1f) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * 改变 Bitmap 整体透明度。
     *
     * 例：setAlpha(bitmap, 0.5f) -> 半透明 Bitmap
     *
     * @param alpha 0f（全透明）~ 1f（不透明）
     */
    @JvmStatic
    fun setAlpha(bitmap: Bitmap, alpha: Float): Bitmap {
        val safeAlpha = (alpha.coerceIn(0f, 1f) * 255).toInt()
        val result = createBitmap(bitmap.width, bitmap.height)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.alpha = safeAlpha
        }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }

    /**
     * 高斯模糊（RenderScript 实现，API 17+）。
     *
     * 例：blur(bitmap, 15f) -> 模糊半径 15 的 Bitmap
     *
     * @param radius 0f ~ 25f，值越大越模糊。
     *               注意：RenderScript 的 ScriptIntrinsicBlur 限制 radius ≤ 25。
     */
    @JvmStatic
    fun blur(bitmap: Bitmap, radius: Float): Bitmap {
        if (radius <= 0f) return bitmap
        val safeRadius = radius.coerceIn(0f, 25f)
        val output = createBitmap(bitmap.width, bitmap.height)
        val rs = RenderScript.create(requireContext())
        val input = Allocation.createFromBitmap(rs, bitmap)
        val out = Allocation.createFromBitmap(rs, output)
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)).apply {
            setRadius(safeRadius)
            setInput(input)
            forEach(out)
        }
        out.copyTo(output)
        rs.destroy()
        blurScript.destroy()
        input.destroy()
        out.destroy()
        return output
    }

    /**
     * 先缩小再模糊再放大，模拟大半径模糊（性能更好）。
     *
     * 例：blurFast(bitmap, 20f) -> 模糊半径 20 的 Bitmap
     */
    @JvmStatic
    @JvmOverloads
    fun blurFast(bitmap: Bitmap, radius: Float, scale: Float = 0.5f): Bitmap {
        if (radius <= 0f) return bitmap
        val small = scaleByRatio(bitmap, scale)
        val blurred = blur(small, radius)
        return scale(blurred, bitmap.width, bitmap.height)
    }

    /**
     * Bitmap → Drawable。
     *
     * 例：bitmap -> Drawable
     */
    @JvmStatic
    fun toDrawable(bitmap: Bitmap): Drawable {
        return BitmapDrawable(requireContext().resources, bitmap)
    }

    /**
     * Drawable → Bitmap。
     *
     * 例：drawable -> Bitmap
     */
    @JvmStatic
    fun toBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            drawable.bitmap?.let { return it }
        }
        val width = drawable.intrinsicWidth.coerceAtLeast(1)
        val height = drawable.intrinsicHeight.coerceAtLeast(1)
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 将 Bitmap 保存到文件。
     *
     * 例：saveToFile(bitmap, File("/sdcard/a.jpg"), 90) -> true
     */
    @JvmStatic
    fun saveToFile(
        bitmap: Bitmap,
        file: File,
        quality: Int = 90,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): Boolean {
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(format, quality.coerceIn(0, 100), out)
                out.flush()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 计算 BitmapFactory 的 inSampleSize。
     *
     * 例：options(2000x1000), 800x600 -> 2
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun requireContext(): Application {
        return mApp ?: throw IllegalStateException(
            "BitmapUtils 未初始化，请先调用 BitmapUtils.initUtils(context)"
        )
    }
}