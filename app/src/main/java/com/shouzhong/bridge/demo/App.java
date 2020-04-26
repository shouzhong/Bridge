package com.shouzhong.bridge.demo;

import android.app.Application;

import com.shouzhong.bridge.Bridge;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bridge.init(this);
    }
}
