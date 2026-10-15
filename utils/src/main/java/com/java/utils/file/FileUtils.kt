package com.java.utils.file

import android.app.Application
import android.content.Context
import android.util.Log
import com.java.utils.BaseUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @Introduce: 文件操作工具类.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 文件读写、判断、创建删除、Asset 拷贝、大小计算、权限设置等.
 *               路径获取请使用 [StoragePath]，JSON 解析请使用 [ParseJsonUtils]。
 */
object FileUtils : BaseUtils {

    private const val TAG = "FileUtils"

    private var mApp: Application? = null

    override fun initUtils(context: Context) {
        mApp = context.applicationContext as Application
    }

    override fun unInitUtils() {
        mApp = null
    }

    private fun requireContext(): Application {
        return mApp ?: throw IllegalStateException(
            "FileUtils 未初始化，请先调用 FileUtils.initUtils(context)"
        )
    }
    // 公开方法 —— 读写 =====================================================================

    /**
     * 读取文件内容为字符串。
     *
     * 例：readFileToString("/sdcard/a.txt") -> "hello"
     */
    @JvmStatic
    @JvmOverloads
    fun readFileToString(
        filePath: String,
        charset: Charset = StandardCharsets.UTF_8
    ): String? = readFileToString(File(filePath), charset)

    /**
     * 读取文件内容为字符串。
     */
    @JvmStatic
    @JvmOverloads
    fun readFileToString(
        file: File,
        charset: Charset = StandardCharsets.UTF_8
    ): String? {
        if (!file.exists() || !file.isFile) {
            Log.e(TAG, "File not exists: ${file.absolutePath}")
            return null
        }
        return try {
            file.readText(charset)
        } catch (e: IOException) {
            Log.e(TAG, "Read file error: ${e.message}")
            null
        }
    }

    /**
     * 写入字符串到文件（覆盖）。
     *
     * 例：writeStringToFile("hi", "/sdcard/a.txt") -> true
     */
    @JvmStatic
    @JvmOverloads
    fun writeStringToFile(
        content: String,
        filePath: String,
        charset: Charset = StandardCharsets.UTF_8
    ): Boolean = writeStringToFile(content, File(filePath), charset)

    /**
     * 写入字符串到文件（覆盖）。
     */
    @JvmStatic
    @JvmOverloads
    fun writeStringToFile(
        content: String,
        file: File,
        charset: Charset = StandardCharsets.UTF_8
    ): Boolean {
        if (!ensureParentDir(file)) return false
        return try {
            file.writeText(content, charset)
            true
        } catch (e: IOException) {
            Log.e(TAG, "Write file error: ${e.message}")
            false
        }
    }

    /**
     * 追加内容到文件。
     *
     * 例：appendStringToFile("hi", "/sdcard/a.txt") -> true
     */
    @JvmStatic
    @JvmOverloads
    fun appendStringToFile(
        content: String,
        filePath: String,
        charset: Charset = StandardCharsets.UTF_8
    ): Boolean {
        val file = File(filePath)
        if (!ensureParentDir(file)) return false
        return try {
            file.appendText(content, charset)
            true
        } catch (e: IOException) {
            Log.e(TAG, "Append file error: ${e.message}")
            false
        }
    }

    // 公开方法 —— 判断 =====================================================================

    /** 路径是否存在。 */
    @JvmStatic
    fun isPathExists(path: String?): Boolean =
        !path.isNullOrEmpty() && File(path).exists()

    /** 是否是文件。 */
    @JvmStatic
    fun isFile(path: String?): Boolean {
        if (path.isNullOrEmpty()) return false
        val f = File(path)
        return f.exists() && f.isFile
    }

    /** 是否是目录。 */
    @JvmStatic
    fun isDirectory(path: String?): Boolean {
        if (path.isNullOrEmpty()) return false
        val f = File(path)
        return f.exists() && f.isDirectory
    }

    // 公开方法 —— 创建 / 删除 =====================================================================

    /**
     * 创建文件（父目录不存在会自动创建）。
     *
     * 例：createFile("/sdcard/a.txt") -> true
     */
    @JvmStatic
    fun createFile(filePath: String): Boolean {
        val file = File(filePath)
        if (file.exists()) return file.isFile
        if (!ensureParentDir(file)) return false
        return try {
            file.createNewFile()
        } catch (e: IOException) {
            Log.e(TAG, "Create file error: ${e.message}")
            false
        }
    }

    /**
     * 创建目录（支持多级）。
     *
     * 例：createDirectory("/sdcard/dir") -> true
     */
    @JvmStatic
    fun createDirectory(dirPath: String): Boolean {
        val dir = File(dirPath)
        return if (dir.exists()) dir.isDirectory else dir.mkdirs()
    }

    /**
     * 删除文件（不存在返回 true）。
     *
     * 例：deleteFile("/sdcard/a.txt") -> true
     */
    @JvmStatic
    fun deleteFile(filePath: String): Boolean {
        val file = File(filePath)
        return !file.exists() || file.delete()
    }

    /**
     * 删除目录及其所有内容。
     *
     * 例：deleteDirectory("/sdcard/dir") -> true
     */
    @JvmStatic
    fun deleteDirectory(dirPath: String): Boolean {
        val dir = File(dirPath)
        if (!dir.exists() || !dir.isDirectory) return false
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) deleteDirectory(file.absolutePath) else file.delete()
        }
        return dir.delete()
    }

    /**
     * 删除目录下所有内容，保留目录本身。
     *
     * 例：deleteAllFilesInDirectory("/sdcard/dir") -> true
     */
    @JvmStatic
    fun deleteAllFilesInDirectory(dirPath: String): Boolean {
        val dir = File(dirPath)
        if (!dir.exists() || !dir.isDirectory) return false
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

    // 公开方法 —— Asset 拷贝 =====================================================================

    /**
     * 拷贝 Asset 文件到指定路径。
     *
     * 例：copyAssetToStorage("config.json", "/sdcard/config.json") -> true
     */
    @JvmStatic
    fun copyAssetToStorage(assetFileName: String, destPath: String): Boolean {
        return try {
            val destFile = File(destPath)
            if (!ensureParentDir(destFile)) return false
            requireContext().assets.open(assetFileName).use { input ->
                FileOutputStream(destFile).use { output ->
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
     * 拷贝 Asset 文件夹到指定目录（递归）。
     *
     * 例：copyAssetFolderToStorage("config", "/sdcard/config") -> true
     */
    @JvmStatic
    fun copyAssetFolderToStorage(assetFolder: String, destDir: String): Boolean {
        return try {
            createDirectory(destDir)
            val files = requireContext().assets.list(assetFolder)
            if (files.isNullOrEmpty()) return false
            files.forEach { file ->
                val assetPath = "$assetFolder${File.separator}$file"
                val destPath = "$destDir${File.separator}$file"
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

    // 公开方法 —— 大小 =====================================================================

    /**
     * 获取文件或目录大小（字节）。
     *
     * 例：getFileSize("/sdcard/dir") -> 102400L
     */
    @JvmStatic
    fun getFileSize(path: String): Long = getFileSize(File(path))

    /**
     * 获取格式化后的文件大小。
     *
     * 例：getFormattedFileSize("/sdcard/dir") -> "100.00 KB"
     */
    @JvmStatic
    fun getFormattedFileSize(path: String): String = formatFileSize(getFileSize(path))

    /**
     * 获取目录下所有文件（递归）。
     *
     * 例：getAllFilesInDirectory("/sdcard/dir") -> List<File>
     */
    @JvmStatic
    fun getAllFilesInDirectory(dirPath: String): List<File> {
        val result = mutableListOf<File>()
        val dir = File(dirPath)
        if (!dir.exists() || !dir.isDirectory) return result
        collectAllFiles(dir, result)
        return result
    }

    // 公开方法 —— 权限 =====================================================================

    /**
     * 设置文件或目录权限为 777（递归）。
     *
     * 例：setFullPermissions("/sdcard/dir") -> true
     *
     * 注意：Linux 权限模型下 777 表示所有用户可读写执行。
     * 出于安全考虑，非必要不建议使用。
     */
    @JvmStatic
    fun setFullPermissions(path: String): Boolean {
        return try {
            val file = File(path)
            if (!file.exists()) return false
            var success = true
            success = success && file.setReadable(true, false)
            success = success && file.setWritable(true, false)
            success = success && file.setExecutable(true, false)
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
     * 设置文件权限（细粒度）。
     *
     * 例：setFilePermissions("/sdcard/a.txt", true, true, false) -> true
     */
    @JvmStatic
    fun setFilePermissions(
        path: String,
        readable: Boolean,
        writable: Boolean,
        executable: Boolean
    ): Boolean {
        return try {
            val file = File(path)
            if (!file.exists()) return false
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
     * 获取文件权限字符串，例如 "rwx" / "rw-"。
     *
     * 例：getFilePermissions("/sdcard/a.txt") -> "rw-"
     */
    @JvmStatic
    fun getFilePermissions(path: String): String {
        val file = File(path)
        if (!file.exists()) return "File not exists"
        return buildString {
            append(if (file.canRead()) "r" else "-")
            append(if (file.canWrite()) "w" else "-")
            append(if (file.canExecute()) "x" else "-")
        }
    }

    // 公开方法 —— 格式化（顶层扩展函数也复用这里） =====================================================================

    /**
     * 字节大小 → 易读字符串。
     *
     * 例：formatFileSize(102400L) -> "100.00 KB"
     */
    @JvmStatic
    fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
            .coerceIn(0, units.size - 1)
        return "%.2f %s".format(
            size / Math.pow(1024.0, digitGroups.toDouble()),
            units[digitGroups]
        )
    }

    /** 确保父目录存在。 */
    private fun ensureParentDir(file: File): Boolean {
        val parent = file.parentFile ?: return true
        if (parent.exists()) return true
        if (parent.mkdirs()) return true
        Log.e(TAG, "Create parent directory failed: ${parent.absolutePath}")
        return false
    }

    private fun getFileSize(file: File): Long {
        if (!file.exists()) return 0
        if (file.isFile) return file.length()
        var size = 0L
        file.listFiles()?.forEach { size += getFileSize(it) }
        return size
    }

    private fun collectAllFiles(dir: File, result: MutableList<File>) {
        dir.listFiles()?.forEach { file ->
            if (file.isFile) result.add(file)
            else if (file.isDirectory) collectAllFiles(file, result)
        }
    }

    private fun isAssetDirectory(assetPath: String): Boolean {
        return try {
            !requireContext().assets.list(assetPath).isNullOrEmpty()
        } catch (e: IOException) {
            false
        }
    }
}