package com.java.utils;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.utils.other.ResourceUtils;

public class ResourceTestActivity extends AppCompatActivity {

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

        addBtn(root, "获取字符串资源 (android.R.string.ok)",
                () -> show(ResourceUtils.getString(android.R.string.ok)));
        addBtn(root, "获取颜色资源 (android.R.color.holo_red_dark)",
                () -> show(String.valueOf(ResourceUtils.getColor(android.R.color.holo_red_dark))));
        addBtn(root, "获取Drawable资源 (android.R.drawable.ic_menu_add)",
                () -> {
                    Drawable d = ResourceUtils.getDrawable(android.R.drawable.ic_menu_add);
                    show(d == null ? "null" : d.getClass().getSimpleName());
                });
        addBtn(root, "获取资源ID (ok, string)",
                () -> show(String.valueOf(ResourceUtils.getResId("ok", ResourceUtils.TYPE_STRING))));
        addBtn(root, "获取显示度量 (displayMetrics)",
                () -> show("density=" + ResourceUtils.getDisplayMetrics().density));
        addBtn(root, "获取颜色状态列表 (android.R.color.holo_blue_dark)",
                () -> show(String.valueOf(ResourceUtils.getColorStateList(android.R.color.holo_blue_dark))));

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