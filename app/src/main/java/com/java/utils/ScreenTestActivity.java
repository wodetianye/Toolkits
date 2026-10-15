package com.java.utils;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.utils.other.ScreenUtils;

public class ScreenTestActivity extends AppCompatActivity {

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

        addBtn(root, "屏幕 Width", () -> show("width=" + ScreenUtils.getScreenWidth()));
        addBtn(root, "屏幕 Height", () -> show("height=" + ScreenUtils.getScreenHeight()));
        addBtn(root, "屏幕 Density", () -> show("density=" + ScreenUtils.getScreenDensity()));
        addBtn(root, "dp2px(16f)", () -> show("px=" + ScreenUtils.dp2px(16f))); // dp 到 px
        addBtn(root, "px2dp(48f)", () -> show("dp=" + ScreenUtils.px2dp(48f))); // px 到 dp
        addBtn(root, "sp2px(16f)", () -> show("px=" + ScreenUtils.sp2px(16f))); // sp 到 px
        addBtn(root, "px2sp(48f)", () -> show("sp=" + ScreenUtils.px2sp(48f)));

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