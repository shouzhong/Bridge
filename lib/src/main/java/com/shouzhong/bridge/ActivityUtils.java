package com.shouzhong.bridge;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActivityUtils {
    public static final String ON_CREATE = "onCreate";
    public static final String ON_START = "onStart";
    public static final String ON_RESUME = "onResume";
    public static final String ON_PAUSE = "onPause";
    public static final String ON_STOP = "onStop";

    static final LinkedHashMap<Activity, String> ACTIVITIES = new LinkedHashMap<>();

    static void sendBroadcast(int pid, String action, String data) {
        String permission = Bridge.getApp().getPackageName() + ".permission.RECEIVER_PROCESS_MESSAGE";
        Intent intent = new Intent(Bridge.getApp().getPackageName() + ".action.PROCESS_MESSAGE");
        intent.putExtra("type", "activity");
        intent.putExtra("pid", pid);
        intent.putExtra("action", action);
        intent.putExtra("data", data);
        Bridge.getApp().sendBroadcast(intent, permission);
    }

    static void init() {
        if (!Utils.isMainProcess()) {
            sendBroadcast(Process.myPid(), "init", null);
        }
        Bridge.getApp().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ACTIVITIES.put(activity, ON_CREATE);
                sendBroadcast(0, "put", activity.getClass().getName() + ";" + Utils.hashCode(activity) + "->" + ON_CREATE);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                ACTIVITIES.put(activity, ON_START);
                sendBroadcast(0, "put", activity.getClass().getName() + ";" + Utils.hashCode(activity) + "->" + ON_START);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                ACTIVITIES.put(activity, ON_RESUME);
                sendBroadcast(0, "put", activity.getClass().getName() + ";" + Utils.hashCode(activity) + "->" + ON_RESUME);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                ACTIVITIES.put(activity, ON_PAUSE);
                sendBroadcast(0, "put", activity.getClass().getName() + ";" + Utils.hashCode(activity) + "->" + ON_PAUSE);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                ACTIVITIES.put(activity, ON_STOP);
                sendBroadcast(0, "put", activity.getClass().getName() + ";" + Utils.hashCode(activity) + "->" + ON_STOP);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                ACTIVITIES.remove(activity);
                sendBroadcast(0, "remove", activity.getClass().getName() + ";" + Utils.hashCode(activity));
            }
        });
    }

    public static int size() {
        return ACTIVITIES.size();
    }

    public static int allSize() {
        return ActivityCache.size();
    }

    public static Activity getLast() {
        if (ACTIVITIES.isEmpty()) return null;
        try {
            Field tail = ACTIVITIES.getClass().getDeclaredField("tail");
            tail.setAccessible(true);
            return ((Map.Entry<Activity, String>) tail.get(ACTIVITIES)).getKey();
        } catch (Exception e) {}
        return get(size() - 1);
    }

    public static Activity get(int position) {
        if (position > size() - 1) return null;
        int i = 0;
        for (Activity act : ACTIVITIES.keySet()) {
            if (i++ == position) return act;
        }
        return null;
    }

    public static String getLifecycle(Activity act) {
        return ACTIVITIES.get(act);
    }

    public static void finish(Class<? extends Activity> cls) {
        if (cls == null) return;
        sendBroadcast(0, "finish", cls.getName());
    }

    public static void exit() {
        sendBroadcast(0, "exit", null);
    }

    public static void exit(int pid) {
        sendBroadcast(pid, "exit", null);
    }

    public static void exit(String processName) {
        sendBroadcast(0, "exit", processName);
    }
}
