package com.java.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.java.utils.other.BitmapUtils;

public class BitmapTestActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap src;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        imageView = new ImageView(this);
        imageView.setPadding(30, 30, 30, 30);
        root.addView(imageView);

        src = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        src.eraseColor(Color.BLUE);
        imageView.setImageBitmap(src);

        addBtn(root, "按尺寸压缩(100, 100)",
                () -> imageView.setImageBitmap(BitmapUtils.compressBySize(src, 100, 100)));
        addBtn(root, "裁剪 Bitmap(50, 50, 100, 100)",
                () -> imageView.setImageBitmap(BitmapUtils.crop(src, 50, 50, 100, 100)));
        addBtn(root, "裁剪中心正方形",
                () -> imageView.setImageBitmap(BitmapUtils.cropCenterSquare(src)));
        addBtn(root, "缩放(100, 100)",
                () -> imageView.setImageBitmap(BitmapUtils.scale(src, 100, 100)));
        addBtn(root, "按比例缩放(0.5)",
                () -> imageView.setImageBitmap(BitmapUtils.scaleByRatio(src, 0.5f)));
        addBtn(root, "旋转(90)",
                () -> imageView.setImageBitmap(BitmapUtils.rotate(src, 90f)));
        addBtn(root, "水平翻转",
                () -> imageView.setImageBitmap(BitmapUtils.flipHorizontal(src)));
        addBtn(root, "设置透明度(0.5)",
                () -> imageView.setImageBitmap(BitmapUtils.setAlpha(src, 0.5f)));
        addBtn(root, "模糊处理(15f)",
                () -> imageView.setImageBitmap(BitmapUtils.blur(src, 15f)));
        addBtn(root, "快速模糊处理(20f)",
                () -> imageView.setImageBitmap(BitmapUtils.blurFast(src, 20f)));
        addBtn(root, "转换为圆形",
                () -> imageView.setImageBitmap(BitmapUtils.toCircle(src)));
        addBtn(root, "转换为圆角(20f)",
                () -> imageView.setImageBitmap(BitmapUtils.toRoundCorner(src, 20f)));
        addBtn(root, "转换为带边框的圆形",
                () -> imageView.setImageBitmap(BitmapUtils.toCircleWithBorder(src, 8f, Color.WHITE)));
        addBtn(root, "还原", () -> imageView.setImageBitmap(src));

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