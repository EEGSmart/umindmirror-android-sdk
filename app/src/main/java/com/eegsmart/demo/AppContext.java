package com.eegsmart.demo;

import android.app.Application;

import com.eegsmart.blesdk.baseble.AndroidBle;

/**
 * Created by yunting on 2018/7/25.
 */

public class AppContext extends Application {

    private static AppContext app;

    public static AppContext getInstance() {
        if (app == null) {
            synchronized (AppContext.class) {
                if (app == null) {
                    app = new AppContext();
                }
            }
        }
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidBle.getInstance().initBleClient(getApplicationContext());
    }

}
