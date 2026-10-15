package com.java.utils;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.utils.other.NetWorkUtils;

public class NetworkTestActivity extends AppCompatActivity {

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

        NetWorkUtils.SimpleNetworkObserver observer = new NetWorkUtils.SimpleNetworkObserver() {
            @Override
            public void onNetConnectSuccess() {
                show("网络已连接");
            }

            @Override
            public void onNetDisConnect() {
                show("网络已断开");
            }

            @Override
            public void onNetValidated() {
                show("网络已验证");
            }
        };
        NetWorkUtils.registerNetworkObserver(observer);

        addBtn(root, "检测网络",
                () -> show(String.valueOf(NetWorkUtils.checkNetwork())));
        addBtn(root, "获取网络类型值",
                () -> show(String.valueOf(NetWorkUtils.getNetworkTypeValue())));
        addBtn(root, "获取网络类型字符串",
                () -> show(NetWorkUtils.getNetworkTypeStr()));
        addBtn(root, "是否连接到Wi-Fi",
                () -> show(String.valueOf(NetWorkUtils.isWifiConnected())));
        addBtn(root, "是否连接到移动网络",
                () -> show(String.valueOf(NetWorkUtils.isMobileConnected())));

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