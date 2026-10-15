package com.java.utils;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.utils.gson.GsonUtils;

import java.util.ArrayList;
import java.util.List;

public class GsonTestActivity extends AppCompatActivity {

    private TextView tvResult;

    public static class User {
        public String name;
        public int age;

        public User() {
        }

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public static class UserVo {
        public String name;
        public int age;
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

        final String userJson = "{\"name\":\"Tom\",\"age\":18}";
        final String userListJson = "[{\"name\":\"Tom\",\"age\":18},{\"name\":\"Jerry\",\"age\":20}]";

        // 序列化
        addBtn(container, "序列化（包含 null 字段）",
                () -> show(GsonUtils.toJson(new User("Tom", 18))));
        addBtn(container, "序列化（跳过 null 字段）",
                () -> {
                    User user = new User();
                    user.name = "Tom";
                    show(GsonUtils.toJsonNonNull(user));
                });
        addBtn(container, "序列化数组",
                () -> show(GsonUtils.toJson(new User[]{
                        new User("Tom", 18), new User("Jerry", 20)
                })));

        // 反序列化
        addBtn(container, "从 JSON 字符串反序列化为对象",
                () -> {
                    User u = GsonUtils.fromJson(userJson, User.class);
                    show(u == null ? "null" : u.name + ", " + u.age);
                });
        addBtn(container, "从 JSON 字符串反序列化为对象列表",
                () -> {
                    List<User> list = GsonUtils.fromJson2List(userListJson, User.class);
                    show("size=" + list.size() + ", first=" + list.get(0).name);
                });
        addBtn(container, "从 JSON 字符串反序列化为对象数组",
                () -> {
                    List<User> list = GsonUtils.fromJsonList(userListJson, User[].class);
                    show("size=" + list.size());
                });
        addBtn(container, "从 JSON 字符串反序列化为对象数组",
                () -> {
                    User[] arr = GsonUtils.fromJsonArray(userListJson, User[].class);
                    show(arr == null ? "null" : "length=" + arr.length);
                });

        // 基本类型数组
        addBtn(container, "从 JSON 字符串反序列化为 int 数组",
                () -> show(java.util.Arrays.toString(GsonUtils.fromJson2IntArray("[1,2,3]"))));
        addBtn(container, "从 JSON 字符串反序列化为 long 数组",
                () -> show(java.util.Arrays.toString(GsonUtils.fromJson2LongArray("[100,200]"))));
        addBtn(container, "从 JSON 字符串反序列化为 boolean 数组",
                () -> show(java.util.Arrays.toString(GsonUtils.fromJson2BooleanArray("[true,false]"))));
        addBtn(container, "从 JSON 字符串反序列化为 double 数组",
                () -> show(java.util.Arrays.toString(GsonUtils.fromJson2DoubleArray("[1.1,2.2]"))));

        // 对象转换
        addBtn(container, "对象转换为另一种对象",
                () -> {
                    UserVo vo = GsonUtils.convert(new User("Tom", 18), UserVo.class);
                    show(vo == null ? "null" : vo.name + ", " + vo.age);
                });
        addBtn(container, "对象列表转换为另一种对象列表",
                () -> {
                    List<User> users = new ArrayList<>();
                    users.add(new User("Tom", 18));
                    users.add(new User("Jerry", 20));
                    ArrayList<UserVo> vos = GsonUtils.convertList(users, UserVo.class);
                    show("size=" + vos.size() + ", first=" + vos.get(0).name);
                });

        // Bean 拷贝
        addBtn(container, "Bean 拷贝（不覆盖 null）",
                () -> {
                    User src = new User("Tom", 18);
                    UserVo dst = new UserVo();
                    dst.name = "old";
                    dst.age = 99;
                    GsonUtils.copyBean(src, dst);
                    show("name=" + dst.name + ", age=" + dst.age);
                });
        addBtn(container, "Bean 拷贝（覆盖 null）",
                () -> {
                    User src = new User(null, 18);
                    UserVo dst = new UserVo();
                    dst.name = "old";
                    GsonUtils.copyBean(src, dst, true);
                    show("name=" + dst.name + ", age=" + dst.age);
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