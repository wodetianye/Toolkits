package com.java.utils.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.java.utils.log.Logger;

import java.io.File;

public class StoragePath {
    private static final String TAG = StoragePath.class.getSimpleName();

    /**
     * 应用内部缓存目录.
     * <p>
     * 内部沙箱位置：/data/user/0/your_package/cache
     * </P>
     */
    public static String getAPPCachePath(Context context) {
        if (context == null) return null;
        String appCachePath = context.getApplicationContext().getCacheDir().getAbsolutePath() + File.separator;
        Logger.i(TAG, "APP_CACHE_PATH", appCachePath);
        return appCachePath;
    }

    /**
     * 应用内部缓存目录.
     * <p>
     * 内部沙箱位置：/data/data/0/your_package/files
     * </P>
     */
    public static String getAppFilePath(Context context) {
        if (context == null) return null;
        String appFilePath = context.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator;
        Logger.i(TAG, "APP_FILE_PATH", appFilePath);
        return appFilePath;
    }

    /**
     * 应用外部缓存目录.
     * <p>
     * 外部沙箱位置：sdcard/Android/data/your_package/files
     * </P>
     */
    public static String getEmulatedFilePath(Context context) {
        if (context == null) return null;
        String sdAppFilePath = context.getExternalFilesDir(null) + File.separator;
        Logger.i(TAG, "SD_APP_FILE_PATH", sdAppFilePath);
        return sdAppFilePath;
    }

    /**
     * 应用外部缓存目录.
     * <p>
     * 外部沙箱位置：sdcard/Android/data/your_package/cache
     * </P>
     */
    public static String getEmulatedCachePath(Context context) {
        if (context == null) return null;
        String sdAppFilePath = context.getExternalCacheDir() + File.separator;
        Logger.i(TAG, "SD_APP_FILE_PATH", sdAppFilePath);
        return sdAppFilePath;
    }

    /**
     * 外部存储分区位置.
     * <p>
     * Android 10后外部存储通常指向内置存储的公共目录，并非SD卡：/storage/emulated/0
     * </P>
     */
    public static String getEmulatedSDPath() {
        String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        Logger.i(TAG, "SD_PATH", sdCardPath);
        return sdCardPath;
    }

    /**
     * SD卡存储分区位置.
     * <p>
     * Android 11后Environment.getExternalStorageDirectory()废弃：/storage/emulated/0
     * </P>
     */
    public static String getQEmulatedSDPath() {
        String sdCardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + File.separator;
        Logger.i(TAG, "SD_PATH", sdCardPath);
        return sdCardPath;
    }

    /**
     * 检测SDK是否可用.
     *
     * @return true：可用/false：不可用
     */
    public static boolean isStorageAreaAvailable() {
        if(!checkExternalStoragePermission()) return false;
        String state = Environment.getExternalStorageState();
        return switch (state) {
            case Environment.MEDIA_MOUNTED -> checkExternalStorageAvailable();
            case Environment.MEDIA_MOUNTED_READ_ONLY -> {
                Logger.d(TAG, "MNT_partition 存储介质已挂载，但处于只读状态，无法写入");
                yield false;
            }
            case Environment.MEDIA_REMOVED -> {
                Logger.d(TAG, "MNT_partition 存储介质不存在");
                yield false;
            }
            case Environment.MEDIA_UNMOUNTED -> {
                Logger.d(TAG, "MNT_partition 存储介质存在但没有挂载");
                yield false;
            }
            case Environment.MEDIA_BAD_REMOVAL -> {
                Logger.d(TAG, "MNT_partition 存储介质在未正确卸载的情况下被直接移除");
                yield false;
            }
            case Environment.MEDIA_CHECKING -> {
                Logger.d(TAG, "MNT_partition 存储介质正在被系统检查，例如刚插入SD卡时");
                yield false;
            }
            case Environment.MEDIA_SHARED -> {
                Logger.d(TAG, "MNT_partition 存储介质未挂载，并正通过 USB 大容量存储 共享给电脑");
                yield false;
            }
            case Environment.MEDIA_NOFS -> {
                Logger.d(TAG, "MNT_partition 存储介质为空白或使用了不支持的文件系统");
                yield false;
            }
            case Environment.MEDIA_UNMOUNTABLE -> {
                Logger.d(TAG, "MNT_partition 存储介质存在但无法挂载，可能已损坏");
                yield false;
            }
            default -> false;
        };
    }

    /**
     * 检测是否有读写权限.
     * <p>
     * Android API 版本<30不可用
     * </P>
     *
     * @return 是否有权限访问外部存储
     */
    private static boolean checkExternalStoragePermission() {
        boolean externalStoragePermissionCheck = Environment.isExternalStorageManager();
        Logger.i(TAG, "externalStoragePermissionCheck:", externalStoragePermissionCheck);
        return externalStoragePermissionCheck;
    }

    /**
     * 检测外部存储分区是否已挂载可用.
     *
     * @return 是否有内部存储 true:有 false:没有
     */
    public static boolean checkExternalStorageAvailable() {
        final long requiredSpace = 10 * 1024 * 1024; // 10MB
        File storageFile = Environment.getExternalStorageDirectory();
        if (FileUtils.checkFileDir(storageFile)) {
            @SuppressLint("UsableSpace") long availableSpace = Environment.getExternalStorageDirectory().getUsableSpace();
            Logger.i(TAG, "MNT partition was mounted successfully", "Storage size", availableSpace);
            return requiredSpace < availableSpace;
        } else {
            Logger.i(TAG, "One file path after partition mounting");
            return false;
        }
    }
}
