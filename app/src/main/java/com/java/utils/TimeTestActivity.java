package com.java.utils;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.utils.other.TimeUtils;

import java.util.concurrent.TimeUnit;

public class TimeTestActivity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        tvResult = new TextView(this);
        tvResult.setTextSize(35);
        tvResult.setPadding(30, 30, 30, 30);
        root.addView(tvResult);

        long now = System.currentTimeMillis();
        long oneHourLater = now + TimeUnit.HOURS.toMillis(1);

        addBtn(root, "时间戳转字符串",
                () -> show(TimeUtils.timestampToString(now)));
        addBtn(root, "时间戳（毫秒）→ 指定格式字符串(yyyy-MM-dd)",
                () -> show(TimeUtils.timestampToString(now, TimeUtils.FORMAT_YMD)));
        addBtn(root, "时间字符串 → 时间戳（毫秒）",
                () -> show(String.valueOf(TimeUtils.stringToTimestamp("2023-11-13 15:30:00"))));
        addBtn(root, "获取当前时间字符串",
                () -> show(TimeUtils.getCurrentTimeString()));
        addBtn(root, "时间格式转换",
                () -> show(TimeUtils.convertFormat(
                        "2023-11-13 15:30:00",
                        TimeUtils.FORMAT_YMD_HMS,
                        TimeUtils.FORMAT_CHINESE_YMD_HM)));
        addBtn(root, "判断是否是今天",
                () -> show(String.valueOf(TimeUtils.isToday(now))));
        addBtn(root, "判断是否是昨天",
                () -> show(String.valueOf(TimeUtils.isYesterday(now - TimeUnit.DAYS.toMillis(1)))));
        addBtn(root, "获取可读的时间差",
                () -> show(TimeUtils.getReadableTimeDifference(now, oneHourLater)));
        addBtn(root, "获取相对时间",
                () -> show(TimeUtils.getRelativeTime(now)));
        addBtn(root, "getDayOfWeek",
                () -> show(TimeUtils.getDayOfWeek(now)));

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