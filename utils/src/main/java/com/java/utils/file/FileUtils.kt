package com.java.utils.file

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.java.utils.BaseUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * @Introduce: 文件操作工具类.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 文件读写、路径检测、文件创建删除、Asset文件拷贝、存储检测等.
 */
class FileUtils private constructor() : BaseUtils {
    private lateinit var mContext: Context;

    companion object {
        private const val TAG = "FileUtils"
        val instance by lazy { FileUtils() }
    }

    override fun initUtils(context: Context) {
        this.mContext = context.applicationContext
    }

    override fun unInitUtils() {

    }


    // 1. 文件读写操作

    /**
     * 读取文件内容为字符串
     */
    fun readFileToString(filePath: String): String? {
        return readFileToString(File(filePath))
    }

    fun readFileToString(file: File): String? {
        if (!file.exists()) {
            Log.e(TAG, "File not exists: ${file.absolutePath}")
            return null
        }

        return try {
            file.readText(StandardCharsets.UTF_8)
        } catch (e: IOException) {
            Log.e(TAG, "Read file error: ${e.message}")
            null
        }
    }

    /**
     * 写入字符串到文件
     */
    fun writeStringToFile(content: String, filePath: String): Boolean {
        return writeStringToFile(content, File(filePath))
    }

    fun writeStringToFile(content: String, file: File): Boolean {
        // 确保父目录存在
        val parentDir = file.parentFile
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                Log.e(TAG, "Create parent directory failed: ${parentDir.absolutePath}")
                return false
            }
        }

        return try {
            file.writeText(content, StandardCharsets.UTF_8)
            true
        } catch (e: IOException) {
            Log.e(TAG, "Write file error: ${e.message}")
            false
        }
    }

    /**
     * 追加内容到文件
     */
    fun appendStringToFile(content: String, filePath: String): Boolean {
        val file = File(filePath)
        // 确保父目录存在
        val parentDir = file.parentFile
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                Log.e(TAG, "Create parent directory failed: ${parentDir.absolutePath}")
                return false
            }
        }

        return try {
            file.appendText(content, StandardCharsets.UTF_8)
            true
        } catch (e: IOException) {
            Log.e(TAG, "Append file error: ${e.message}")
            false
        }
    }

    // 2. 文件路径检测是否存在

    /**
     * 检测文件或目录是否存在
     */
    fun isPathExists(path: String): Boolean {
        return path.isNotEmpty() && File(path).exists()
    }

    /**
     * 检测是否是文件
     */
    fun isFile(path: String): Boolean {
        val file = File(path)
        return file.exists() && file.isFile
    }

    /**
     * 检测是否是目录
     */
    fun isDirectory(path: String): Boolean {
        val file = File(path)
        return file.exists() && file.isDirectory
    }

    // 3. 文件创建及删除

    /**
     * 创建文件
     */
    fun createFile(filePath: String): Boolean {
        val file = File(filePath)
        if (file.exists()) {
            return true
        }

        // 确保父目录存在
        val parentDir = file.parentFile
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                Log.e(TAG, "Create parent directory failed: ${parentDir.absolutePath}")
                return false
            }
        }

        return try {
            file.createNewFile()
        } catch (e: IOException) {
            Log.e(TAG, "Create file error: ${e.message}")
            false
        }
    }

    /**
     * 创建目录
     */
    fun createDirectory(dirPath: String): Boolean {
        val dir = File(dirPath)
        return if (dir.exists()) {
            dir.isDirectory
        } else {
            dir.mkdirs()
        }
    }

    /**
     * 删除文件
     */
    fun deleteFile(filePath: String): Boolean {
        val file = File(filePath)
        return if (!file.exists()) {
            true
        } else {
            file.delete()
        }
    }

    /**
     * 删除目录及其所有内容
     */
    fun deleteDirectory(dirPath: String): Boolean {
        val dir = File(dirPath)
        if (!dir.exists() || !dir.isDirectory()) {
            return false
        }

        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                deleteDirectory(file.absolutePath)
            } else {
                file.delete()
            }
        }
        return dir.delete()
    }

    // 4. Asset文件拷贝到内存卡

    /**
     * 拷贝Asset文件到指定路径
     */
    fun copyAssetToStorage(assetFileName: String, destPath: String): Boolean {
        return try {
            mContext.assets.open(assetFileName).use { input ->
                FileOutputStream(destPath).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: IOException) {
            Log.e(TAG, "Copy asset file error: ${e.message}")
            false
        }
    }

    /**
     * 拷贝Asset文件夹到指定目录
     */
    fun copyAssetFolderToStorage(assetFolder: String, destDir: String): Boolean {
        return try {
            // 创建目标目录
            createDirectory(destDir)

            // 获取asset文件夹下的所有文件
            val files = mContext.assets.list(assetFolder)
            if (files.isNullOrEmpty()) {
                return false
            }

            files.forEach { file ->
                val assetPath = "$assetFolder${File.separator}$file"
                val destPath = "$destDir${File.separator}$file"

                // 如果是子文件夹，递归拷贝
                if (isAssetDirectory(assetPath)) {
                    copyAssetFolderToStorage(assetPath, destPath)
                } else {
                    copyAssetToStorage(assetPath, destPath)
                }
            }
            true
        } catch (e: IOException) {
            Log.e(TAG, "Copy asset folder error: ${e.message}")
            false
        }
    }

    /**
     * 检测Asset路径是否是目录
     */
    private fun isAssetDirectory(assetPath: String): Boolean {
        return try {
            val files = mContext.assets.list(assetPath)
            !files.isNullOrEmpty()
        } catch (e: IOException) {
            false
        }
    }

    // 5. 存储分区是否可用检测

    /**
     * 检测外部存储是否可用
     */
    fun isExternalStorageAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * 检测外部存储是否可写
     */
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * 检测外部存储是否可读
     */
    fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return state == Environment.MEDIA_MOUNTED || state == Environment.MEDIA_MOUNTED_READ_ONLY
    }

    /**
     * 获取可用存储空间大小（字节）
     */
    fun getAvailableStorageSize(path: String): Long {
        return try {
            val stat = StatFs(path)
            val blockSize: Long
            val availableBlocks: Long

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.blockSizeLong
                availableBlocks = stat.availableBlocksLong
            } else {
                @Suppress("DEPRECATION")
                blockSize = stat.blockSize.toLong()
                @Suppress("DEPRECATION")
                availableBlocks = stat.availableBlocks.toLong()
            }

            availableBlocks * blockSize
        } catch (e: Exception) {
            Log.e(TAG, "Get available storage size error: ${e.message}")
            -1
        }
    }

    // 6. 获取存储分区路径获取

    /**
     * 获取外部存储根目录
     */
    fun getExternalStoragePath(): String {
        return Environment.getExternalStorageDirectory().absolutePath
    }

    /**
     * 获取应用内部存储目录
     */
    fun getInternalStoragePath(): String {
        return mContext.filesDir.absolutePath
    }

    /**
     * 获取应用缓存目录
     */
    fun getCacheStoragePath(): String {
        return mContext.cacheDir.absolutePath
    }

    /**
     * 获取外部存储中的应用私有目录
     */
    fun getExternalFilesDirPath(): String {
        return mContext.getExternalFilesDir(null)?.absolutePath ?: ""
    }

    /**
     * 获取外部存储中的应用缓存目录
     */
    fun getExternalCacheDirPath(): String {
        return mContext.externalCacheDir?.absolutePath ?: ""
    }

    // 7. 获取指定文件路径下的总文件大小

    /**
     * 获取文件或目录大小（字节）
     */
    fun getFileSize(path: String): Long {
        return getFileSize(File(path))
    }

    private fun getFileSize(file: File): Long {
        if (!file.exists()) {
            return 0
        }

        return if (file.isFile) {
            file.length()
        } else {
            var size = 0L
            file.listFiles()?.forEach { f ->
                size += getFileSize(f)
            }
            size
        }
    }

    /**
     * 获取格式化后的文件大小
     */
    fun getFormattedFileSize(path: String): String {
        val size = getFileSize(path)
        return formatFileSize(size)
    }

    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()

        return "%.2f %s".format(size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }

    // 8. 删除文件夹下所有文件

    /**
     * 删除文件夹下所有文件，但不删除文件夹本身
     */
    fun deleteAllFilesInDirectory(dirPath: String): Boolean {
        val dir = File(dirPath)
        if (!dir.exists() || !dir.isDirectory) {
            return false
        }

        var success = true
        dir.listFiles()?.forEach { file ->
            success = if (file.isDirectory) {
                success && deleteDirectory(file.absolutePath)
            } else {
                success && file.delete()
            }
        }
        return success
    }

    /**
     * 获取目录下所有文件列表
     */
    fun getAllFilesInDirectory(dirPath: String): List<File> {
        val fileList = mutableListOf<File>()
        val dir = File(dirPath)

        if (!dir.exists() || !dir.isDirectory) {
            return fileList
        }

        getAllFiles(dir, fileList)
        return fileList
    }

    private fun getAllFiles(dir: File, fileList: MutableList<File>) {
        dir.listFiles()?.forEach { file ->
            if (file.isFile) {
                fileList.add(file)
            } else if (file.isDirectory) {
                getAllFiles(file, fileList)
            }
        }
    }

    // 9. 设置文件夹权限为777

    /**
     * 设置文件或目录权限为777
     */
    fun setFullPermissions(path: String): Boolean {
        return try {
            val file = File(path)
            if (!file.exists()) {
                return false
            }

            // 设置读、写、执行权限给所有者、组和其他用户
            var success = true
            success = success && file.setReadable(true, false)  // 所有用户可读
            success = success && file.setWritable(true, false)  // 所有用户可写
            success = success && file.setExecutable(true, false) // 所有用户可执行

            // 如果是目录，递归设置子文件和子目录权限
            if (file.isDirectory) {
                file.listFiles()?.forEach { child ->
                    success = success && setFullPermissions(child.absolutePath)
                }
            }

            success
        } catch (e: Exception) {
            Log.e(TAG, "Set full permissions error: ${e.message}")
            false
        }
    }

    /**
     * 设置文件权限（更细粒度的控制）
     */
    fun setFilePermissions(
        path: String,
        readable: Boolean,
        writable: Boolean,
        executable: Boolean
    ): Boolean {
        return try {
            val file = File(path)
            if (!file.exists()) {
                return false
            }

            var success = true
            success = success && file.setReadable(readable, false)
            success = success && file.setWritable(writable, false)
            success = success && file.setExecutable(executable, false)

            success
        } catch (e: Exception) {
            Log.e(TAG, "Set file permissions error: ${e.message}")
            false
        }
    }

    /**
     * 获取文件权限信息
     */
    fun getFilePermissions(path: String): String {
        val file = File(path)
        if (!file.exists()) {
            return "File not exists"
        }

        val permissions = StringBuilder()
        permissions.append(if (file.canRead()) "r" else "-")
        permissions.append(if (file.canWrite()) "w" else "-")
        permissions.append(if (file.canExecute()) "x" else "-")

        return permissions.toString()
    }

    // 扩展函数：为File类添加扩展函数

    /**
     * 扩展函数：安全地读取文件内容
     */
    fun File.readTextSafely(charset: java.nio.charset.Charset = StandardCharsets.UTF_8): String? {
        return try {
            if (exists()) readText(charset) else null
        } catch (e: IOException) {
            Log.e(TAG, "Read file error: ${e.message}")
            null
        }
    }

    /**
     * 扩展函数：安全地写入文件内容
     */
    fun File.writeTextSafely(
        text: String,
        charset: java.nio.charset.Charset = StandardCharsets.UTF_8
    ): Boolean {
        return try {
            // 确保父目录存在
            parentFile?.mkdirs()
            writeText(text, charset)
            true
        } catch (e: IOException) {
            Log.e(TAG, "Write file error: ${e.message}")
            false
        }
    }

    /**
     * 扩展函数：递归计算文件大小
     */
    val File.recursiveSize: Long
        get() = getFileSize(this)
}

// 顶层扩展函数
/**
 * 将字节大小格式化为易读的字符串
 */
fun Long.formatFileSize(): String {
    if (this <= 0) return "0 B"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(toDouble()) / Math.log10(1024.0)).toInt()

    return "%.2f %s".format(this / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

/**
 * 检查路径是否存在
 */
fun String?.isPathExists(): Boolean {
    return !this.isNullOrEmpty() && File(this).exists()
}