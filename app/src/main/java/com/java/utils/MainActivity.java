package com.java.utils;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        addButton(root, "Toast工具类", ToastTestActivity.class);
        addButton(root, "Time工具类", TimeTestActivity.class);
        addButton(root, "Screen工具类", ScreenTestActivity.class);
        addButton(root, "View工具类", ViewTestActivity.class);
        addButton(root, "Bitmap工具类", BitmapTestActivity.class);
        addButton(root, "Convert工具类", ConvertTestActivity.class);
        addButton(root, "NetWork工具类", NetworkTestActivity.class);
        addButton(root, "Resource工具类", ResourceTestActivity.class);
        addButton(root, "File工具类", FileTestActivity.class);
        addButton(root, "Logger工具类", LoggerTestActivity.class);
        addButton(root, "Gson工具类", GsonTestActivity.class);
        addButton(root, "线程池工具类", ThreadTestActivity.class);
        setContentView(root);
    }

    private void addButton(LinearLayout parent, String text, Class<?> clazz) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextSize(25f);
        btn.setPadding(30, 30, 30, 30);
        btn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, clazz)));
        parent.addView(btn);
    }
}