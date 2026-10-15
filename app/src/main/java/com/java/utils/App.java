package com.java.utils;

import android.app.Application;

import com.java.utils.log.Logger;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.initLogUtils(true, Logger.DEBUG, "LoggerTest");
        UtilsManagerKt.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        UtilsManagerKt.clearCache();
    }
}
