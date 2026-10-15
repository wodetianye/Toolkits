package com.java.utils.file

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.java.utils.BaseUtils
import com.java.utils.log.Logger
import java.io.File

/**
 * @Introduce: 存储路径管理工具类.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 各类存储分区路径获取、分区挂载状态检测、可用空间查询。
 *               不涉及具体的文件读写，文件操作请使用 [FileUtils]。
 */
object StoragePath : BaseUtils {
    private const val TAG = "StoragePath"

    /** 可用空间最低要求：10MB。 */
    private const val MIN_REQUIRED_SPACE = 10 * 1024 * 1024L

    private var mApp: Application? = null

    override fun initUtils(context: Context) {
        mApp = context.applicationContext as Application
    }

    override fun unInitUtils() {
        mApp = null
    }

    /**
     * 应用内部缓存目录（/data/user/0/包名/cache/）。
     *
     * 例：-> "/data/user/0/com.xxx/cache/"
     */
    @JvmStatic
    fun getAppCachePath(): String =
        requireContext().cacheDir.absolutePath + File.separator

    /**
     * 应用内部文件目录（/data/user/0/包名/files/）。
     *
     * 例：-> "/data/user/0/com.xxx/files/"
     */
    @JvmStatic
    fun getAppFilePath(): String =
        requireContext().filesDir.absolutePath + File.separator

    /**
     * 应用外部文件目录（/storage/emulated/0/Android/data/包名/files/）。
     *
     * 例：-> "/storage/emulated/0/Android/data/com.xxx/files/"
     */
    @JvmStatic
    fun getExternalFilesPath(): String {
        val path = requireContext().getExternalFilesDir(null)?.absolutePath
            ?: return ""
        return path + File.separator
    }

    /**
     * 应用外部缓存目录（/storage/emulated/0/Android/data/包名/cache/）。
     *
     * 例：-> "/storage/emulated/0/Android/data/com.xxx/cache/"
     */
    @JvmStatic
    fun getExternalCachePath(): String {
        val path = requireContext().externalCacheDir?.absolutePath ?: return ""
        return path + File.separator
    }

    // 公共存储路径 =====================================================================

    /**
     * 外部存储根目录（Android 10 起指向内置存储公共目录）。
     *
     * 例：-> "/storage/emulated/0/"
     */
    @JvmStatic
    fun getExternalStoragePath(): String =
        Environment.getExternalStorageDirectory().absolutePath + File.separator

    /**
     * 外部存储公共文档目录。
     *
     * 例：-> "/storage/emulated/0/Documents/"
     */
    @JvmStatic
    fun getExternalDocumentsPath(): String =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            .absolutePath + File.separator

    // 分区状态 =====================================================================

    /**
     * 外部存储是否已挂载且可读写。
     *
     * 例：-> true
     */
    @JvmStatic
    fun isExternalStorageAvailable(): Boolean =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /**
     * 外部存储是否可读（挂载或只读挂载都算可读）。
     *
     * 例：-> true
     */
    @JvmStatic
    fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return state == Environment.MEDIA_MOUNTED ||
                state == Environment.MEDIA_MOUNTED_READ_ONLY
    }

    /**
     * 存储分区是否可用（已挂载 + 可写 + 可用空间充足）。
     *
     * 例：-> true
     *
     * 说明：
     * - 内部会检查挂载状态、可用空间（要求 >= [MIN_REQUIRED_SPACE]）。
     * - 从 Android 11（API 30）起，访问公共目录需要 MANAGE_EXTERNAL_STORAGE 权限，
     *   如果应用不需要该权限，请优先使用 [isExternalStorageAvailable] 判断。
     */
    @JvmStatic
    fun isStorageAreaAvailable(): Boolean {
        val state = Environment.getExternalStorageState()
        return when (state) {
            Environment.MEDIA_MOUNTED -> checkExternalStorageAvailable()
            Environment.MEDIA_MOUNTED_READ_ONLY -> {
                Logger.d("$TAG 存储介质已挂载，但处于只读状态，无法写入")
                false
            }

            Environment.MEDIA_REMOVED -> {
                Logger.d("$TAG 存储介质不存在")
                false
            }

            Environment.MEDIA_UNMOUNTED -> {
                Logger.d("$TAG 存储介质存在但没有挂载")
                false
            }

            Environment.MEDIA_BAD_REMOVAL -> {
                Logger.d("$TAG 存储介质在未正确卸载的情况下被直接移除")
                false
            }

            Environment.MEDIA_CHECKING -> {
                Logger.d("$TAG 存储介质正在被系统检查，例如刚插入SD卡时")
                false
            }

            Environment.MEDIA_SHARED -> {
                Logger.d("$TAG 存储介质未挂载，并正通过 USB 大容量存储共享给电脑")
                false
            }

            Environment.MEDIA_NOFS -> {
                Logger.d("$TAG 存储介质为空白或使用了不支持的文件系统")
                false
            }

            Environment.MEDIA_UNMOUNTABLE -> {
                Logger.d("$TAG 存储介质存在但无法挂载，可能已损坏")
                false
            }

            else -> false
        }
    }

    /**
     * 检查外部存储可用空间是否满足最低要求。
     *
     * 例：-> true（可用空间 >= 10MB）
     */
    @JvmStatic
    fun checkExternalStorageAvailable(): Boolean {
        val storageFile = Environment.getExternalStorageDirectory()
        if (!storageFile.exists() || !storageFile.isDirectory) {
            Logger.d("$TAG 外部存储目录不存在或不是目录")
            return false
        }
        @Suppress("DEPRECATION")
        val availableSpace = storageFile.usableSpace
        Logger.d("$TAG 外部存储可用空间: $availableSpace 字节")
        return availableSpace >= MIN_REQUIRED_SPACE
    }

    /**
     * 获取指定路径下总空间（字节）。
     *
     * 例：getTotalStorageSize("/storage/emulated/0") -> 64000000000L
     */
    @JvmStatic
    fun getTotalStorageSize(path: String): Long {
        return try {
            val stat = StatFs(path)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                stat.blockCountLong * stat.blockSizeLong
            } else {
                @Suppress("DEPRECATION")
                stat.blockCount.toLong() * stat.blockSize.toLong()
            }
        } catch (e: Exception) {
            Logger.e("$TAG getTotalStorageSize failed: ${e.message}")
            -1L
        }
    }

    /**
     * 获取指定路径下可用空间（字节）。
     *
     * 例：getAvailableStorageSize("/storage/emulated/0") -> 10737418240L
     */
    @JvmStatic
    fun getAvailableStorageSize(path: String): Long {
        return try {
            val stat = StatFs(path)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                stat.availableBlocksLong * stat.blockSizeLong
            } else {
                @Suppress("DEPRECATION")
                stat.availableBlocks.toLong() * stat.blockSize.toLong()
            }
        } catch (e: Exception) {
            Logger.e("$TAG getAvailableStorageSize failed: ${e.message}")
            -1L
        }
    }

    private fun requireContext(): Application {
        return mApp ?: throw IllegalStateException(
            "StoragePath 未初始化，请先调用 StoragePath.initUtils(context)"
        )
    }
}