package com.java.utils;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.java.utils.other.ToastUtils;

public class ToastTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        addBtn(root, "showShort(String)",
                () -> ToastUtils.showShort("短 Toast"));
        addBtn(root, "showLong(String)",
                () -> ToastUtils.showLong("长 Toast"));
        addBtn(root, "showShort(resId)",
                () -> ToastUtils.showShort(android.R.string.ok));
        addBtn(root, "自定义时长 3000ms",
                () -> ToastUtils.showWithDuration("3 秒 Toast", 3000L));
        addBtn(root, "底部位置",
                () -> ToastUtils.show("底部 Toast", Toast.LENGTH_SHORT, Gravity.BOTTOM, 0, 200));
        addBtn(root, "链式调用",
                () -> ToastUtils.with(this)
                        .message("链式 Toast")
                        .gravity(Gravity.CENTER)
                        .show());
        addBtn(root, "取消当前 Toast", ToastUtils::cancel);

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
}