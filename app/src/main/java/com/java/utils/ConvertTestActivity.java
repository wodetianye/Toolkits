package com.java.utils;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.utils.other.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

public class ConvertTestActivity extends AppCompatActivity {

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

        addBtn(root, "任意对象转字符串(123)",
                () -> show(ConvertUtils.convertString(123)));
        addBtn(root, "字符串转整数(\"123\")",
                () -> show(String.valueOf(ConvertUtils.str2Int("123"))));
        addBtn(root, "字符串转整数(\"abc\", -1)",
                () -> show(String.valueOf(ConvertUtils.str2Int("abc", -1))));
        addBtn(root, "字符串转整数(\"FF\", 16, 0)",
                () -> show(String.valueOf(ConvertUtils.str2Int("FF", 16, 0))));
        addBtn(root, "字符串忽略大小写比较(\"ABC\", \"abc\")",
                () -> show(String.valueOf(ConvertUtils.equalsIgnoreCase("ABC", "abc"))));
        addBtn(root, "判断字符串是否为空(\"\")",
                () -> show(String.valueOf(ConvertUtils.isEmpty(""))));
        addBtn(root, "判断集合是否为空(emptyList)",
                () -> show(String.valueOf(ConvertUtils.isEmpty(new ArrayList<>()))));
        addBtn(root, "判断字符串是否为整数(\"-123\")",
                () -> show(String.valueOf(ConvertUtils.isInteger("-123"))));
        addBtn(root, "比较两个数的大小(3L, 2L)",
                () -> show(String.valueOf(ConvertUtils.compare(3L, 2L))));
        addBtn(root, "计算两个数的差值(10, 3)",
                () -> show(String.valueOf(ConvertUtils.numberDiff(10, 3))));
        addBtn(root, "格式化小数(\"1.5\")",
                () -> show(ConvertUtils.stringFormatTwo("1.5")));
        addBtn(root, "米转千米(1500)",
                () -> show(String.valueOf(ConvertUtils.meters2Kilometers(1500.0))));
        addBtn(root, "克转斤(1000)",
                () -> show(String.valueOf(ConvertUtils.grams2Jin(1000.0))));
        addBtn(root, "分转元(150)",
                () -> show(String.valueOf(ConvertUtils.fen2Yuan(150))));
        addBtn(root, "push(list, 1, 2, 3)", () -> {
            List<Integer> list = new ArrayList<>();
            ConvertUtils.push(list, 1, 2, 3);
            show(list.toString());
        });
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