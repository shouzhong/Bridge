package com.shouzhong.bridge;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.IntentFilter;

public class Bridge {

    private static Application app;

    static Application getApp() {
        if (app == null) {
            try {
                @SuppressLint("PrivateApi")
                Class<?> activityThread = Class.forName("android.app.ActivityThread");
                Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
                Object app = activityThread.getMethod("getApplication").invoke(thread);
                Bridge.app = (Application) app;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return app;
    }

    public static void init(Application app) {
        Bridge.app = app;
        String action = Bridge.getApp().getPackageName() + ".shouzhong.action.PROCESS_MESSAGE";
        String permission = Bridge.getApp().getPackageName() + ".shouzhong.permission.RECEIVER_PROCESS_MESSAGE";
        IntentFilter filter = new IntentFilter(action);
        Bridge.getApp().registerReceiver(new BridgeReceiver(), filter, permission, null);
        ActivityUtils.init();
        EventBusUtils.init();
    }
}
