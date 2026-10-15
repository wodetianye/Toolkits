package com.java.utils;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.utils.file.FileUtils;
import com.java.utils.file.ParseJsonUtils;
import com.java.utils.file.StoragePath;

import java.io.File;

public class FileTestActivity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 外层：结果 + 可滚动按钮区
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        tvResult = new TextView(this);
        tvResult.setTextSize(18f);
        tvResult.setPadding(30, 30, 30, 30);
        root.addView(tvResult);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(container);
        root.addView(scrollView);

        File testFile = new File(StoragePath.getAppCachePath(), "test.txt");

        // 存储路径
        addBtn(container, "应用内部缓存目录",
                () -> show(StoragePath.getAppCachePath()));
        addBtn(container, "应用内部文件目录",
                () -> show(StoragePath.getAppFilePath()));
        addBtn(container, "应用外部文件目录",
                () -> show(StoragePath.getExternalFilesPath()));
        addBtn(container, "应用外部缓存目录",
                () -> show(StoragePath.getExternalCachePath()));
        addBtn(container, "应用外部存储目录",
                () -> show(StoragePath.getExternalStoragePath()));
        addBtn(container, "应用外部文档目录",
                () -> show(StoragePath.getExternalDocumentsPath()));

        // 分区状态
        addBtn(container, "外部存储是否已挂载且可读写",
                () -> show(String.valueOf(StoragePath.isExternalStorageAvailable())));
        addBtn(container, "外部存储是否可读",
                () -> show(String.valueOf(StoragePath.isExternalStorageReadable())));
        addBtn(container, "可用存储空间大小",
                () -> show(String.valueOf(StoragePath.getAvailableStorageSize(
                        StoragePath.getAppCachePath()))));
        addBtn(container, "总存储空间大小",
                () -> show(String.valueOf(StoragePath.getTotalStorageSize(
                        StoragePath.getAppCachePath()))));

        // 文件读写
        addBtn(container, "向文件写入字符串",
                () -> show("writeStringToFile -> " +
                        FileUtils.writeStringToFile("Hello FileUtils", testFile.getAbsolutePath())));
        addBtn(container, "向文件追加字符串",
                () -> show("appendStringToFile -> " +
                        FileUtils.appendStringToFile("\nAppended line", testFile.getAbsolutePath())));
        addBtn(container, "读取文件内容为字符串",
                () -> show(FileUtils.readFileToString(testFile.getAbsolutePath())));

        // 文件判断
        addBtn(container, "路径是否存在",
                () -> show(String.valueOf(FileUtils.isPathExists(testFile.getAbsolutePath()))));
        addBtn(container, "是否是文件",
                () -> show(String.valueOf(FileUtils.isFile(testFile.getAbsolutePath()))));
        addBtn(container, "是否是目录",
                () -> show(String.valueOf(FileUtils.isDirectory(testFile.getAbsolutePath()))));

        // 文件大小
        addBtn(container, "文件大小",
                () -> show(String.valueOf(FileUtils.getFileSize(testFile.getAbsolutePath()))));
        addBtn(container, "格式化文件大小",
                () -> show(FileUtils.getFormattedFileSize(testFile.getAbsolutePath())));
        addBtn(container, "目录下所有文件",
                () -> show(String.valueOf(FileUtils.getAllFilesInDirectory(
                        StoragePath.getAppCachePath()).size())));

        // 文件权限
        addBtn(container, "文件权限",
                () -> show(FileUtils.getFilePermissions(testFile.getAbsolutePath())));
        addBtn(container, "设置文件权限为 rw-",
                () -> show(String.valueOf(FileUtils.setFilePermissions(
                        testFile.getAbsolutePath(), true, true, false))));

        // Asset 拷贝
        addBtn(container, "拷贝 Asset 文件到存储",
                () -> {
                    String dest = StoragePath.getAppCachePath() + "asset_copy.txt";
                    show("copyAssetToStorage -> " +
                            FileUtils.copyAssetToStorage("test.txt", dest));
                });

        // JSON 解析
        addBtn(container, "从 byte[] 解析 JSON 文件",
                () -> {
                    byte[] bytes = "{\"name\":\"Tom\"}".getBytes();
                    show(ParseJsonUtils.parseJsonFile(bytes));
                });
        addBtn(container, "从路径解析 JSON 文件",
                () -> show(ParseJsonUtils.parseJsonFile(testFile.getAbsolutePath())));
        addBtn(container, "从 Asset 解析 JSON",
                () -> show(ParseJsonUtils.parseJsonFromAsset("test.json")));

        // 删除
        addBtn(container, "删除文件",
                () -> show(String.valueOf(FileUtils.deleteFile(testFile.getAbsolutePath()))));
        addBtn(container, "删除目录下所有文件",
                () -> show(String.valueOf(FileUtils.deleteAllFilesInDirectory(
                        StoragePath.getAppCachePath()))));

        setContentView(root);
    }

    private void addBtn(LinearLayout parent, String text, Runnable action) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextSize(25f);
        btn.setPadding(30, 30, 30, 30);
        btn.setOnClickListener(v -> action.run());
        parent.addView(btn);
    }

    private void show(String text) {
        tvResult.setText(text);
    }
}