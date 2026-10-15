package com.java.utils;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.utils.other.ViewUtils;

public class ViewTestActivity extends AppCompatActivity {

    private TextView target;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        target = new TextView(this);
        target.setText("测试 View");
        target.setBackgroundColor(Color.LTGRAY);
        root.addView(target);

        tvResult = new TextView(this);
        tvResult.setTextSize(35);
        tvResult.setPadding(30, 30, 30, 30);
        root.addView(tvResult);

        addBtn(root, "动态设置 View 的宽高(100, 50)",
                () -> ViewUtils.setSizeDp(target, 100f, 50f));
        addBtn(root, "只设置宽度(200)",
                () -> ViewUtils.setWidthDp(target, 200f));
        addBtn(root, "只设置高度(80)",
                () -> ViewUtils.setHeightDp(target, 80f));
        addBtn(root, "设置 Margin(16, 8, 16, 8)",
                () -> ViewUtils.setMarginsDp(target, 16f, 8f, 16f, 8f));
        addBtn(root, "getWidth / getHeight",
                () -> show("w=" + ViewUtils.getWidth(target) + ", h=" + ViewUtils.getHeight(target)));
        addBtn(root, "设置不透明度(0.5f)",
                () -> ViewUtils.setAlpha(target, 0.5f));
        addBtn(root, "设置内边距(16,16,16,16)",
                () -> ViewUtils.setPaddingDp(target, 16f, 16f, 16f, 16f));
        addBtn(root, "将 View 绘制成 Bitmap", () -> {
            try {
                ViewUtils.captureViewBitmap(target);
                show("截图成功");
            } catch (Exception e) {
                show("截图失败: " + e.getMessage());
            }
        });
        addBtn(root, "digitToBold(\"时间 12:30\")",
                () -> show(ViewUtils.digitToBold("时间 12:30").toString()));
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