package com.java.utils;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.utils.log.Logger;

import java.util.ArrayList;
import java.util.List;

public class LoggerTestActivity extends AppCompatActivity {

    private TextView tvResult;

    public static class User {
        public String name;
        public int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // 基础日志
        addBtn(container, "Logger.v(\"verbose\")",
                () -> Logger.v("verbose message"));
        addBtn(container, "Logger.d(\"debug\")",
                () -> Logger.d("debug message"));
        addBtn(container, "Logger.i(\"info\")",
                () -> Logger.i("info message"));
        addBtn(container, "Logger.w(\"warn\")",
                () -> Logger.w("warn message"));
        addBtn(container, "Logger.e(\"error\")",
                () -> Logger.e("error message"));

        // 带 Tag
        addBtn(container, "Logger.d(\"MyTag\", \"msg\")",
                () -> Logger.d("MyTag", "with tag"));
        addBtn(container, "Logger.i(\"Net\", \"用户登录\", user)",
                () -> Logger.i("Net", "用户登录", new User("Tom", 18)));

        // 多参数
        addBtn(container, "Logger.d(\"a\", 1, true)",
                () -> Logger.d("a", 1, true));
        addBtn(container, "Logger.d(list)",
                () -> {
                    List<String> list = new ArrayList<>();
                    list.add("a");
                    list.add("b");
                    Logger.d("list =", list);
                });

        // 异常
        addBtn(container, "Logger.e(exception)",
                () -> {
                    try {
                        int i = 1 / 0;
                    } catch (Exception e) {
                        Logger.e("异常", e);
                    }
                });

        // 超长日志
        addBtn(container, "Logger.d(5000 chars)",
                () -> {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 5000; i++) sb.append("A");
                    Logger.d(sb.toString());
                });

        // 堆栈
        addBtn(container, "Logger.printStackTrace",
                () -> Logger.printStackTrace("Stack", true));

        // 配置
        addBtn(container, "setLogLevel(WARN)",
                () -> {
                    Logger.setLogLevel(Logger.WARN);
                    show("已切换到 WARN");
                });
        addBtn(container, "setLogLevel(VERBOSE)",
                () -> {
                    Logger.setLogLevel(Logger.VERBOSE);
                    show("已切换到 VERBOSE");
                });
        addBtn(container, "switchLog(false)",
                () -> {
                    Logger.switchLog(false);
                    show("已关闭日志输出");
                });
        addBtn(container, "switchLog(true)",
                () -> {
                    Logger.switchLog(true);
                    show("已开启日志输出");
                });
        addBtn(container, "setDefaultTag(\"NewTag\")",
                () -> {
                    Logger.setDefaultTag("NewTag");
                    show("默认 Tag 已改成 NewTag");
                });
        addBtn(container, "setStackTraceFilter",
                () -> {
                    Logger.setStackTraceFilter("com.java.utils");
                    show("堆栈过滤已设置");
                });

        // 状态查询
        addBtn(container, "isDebugLevel",
                () -> show(String.valueOf(Logger.isDebugLevel())));

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