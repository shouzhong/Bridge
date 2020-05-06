package com.shouzhong.bridge;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ActivityUtils {
    public static final String ON_CREATE = "onCreate";
    public static final String ON_START = "onStart";
    public static final String ON_RESUME = "onResume";
    public static final String ON_PAUSE = "onPause";
    public static final String ON_STOP = "onStop";

    static final LinkedHashMap<Activity, String> ACTIVITIES = new LinkedHashMap<>();

    static void sendBroadcast(int pid, String action, String data) {
        String permission = Bridge.getApp().getPackageName() + ".shouzhong.permission.RECEIVER_PROCESS_MESSAGE";
        Intent intent = new Intent(Bridge.getApp().getPackageName() + ".shouzhong.action.PROCESS_MESSAGE");
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
                sendBroadcast(0, "put", getUniqueId(activity) + "->" + ACTIVITIES.get(activity));
            }

            @Override
            public void onActivityStarted(Activity activity) {
                ACTIVITIES.put(activity, ON_START);
                sendBroadcast(0, "put", getUniqueId(activity) + "->" + ACTIVITIES.get(activity));
            }

            @Override
            public void onActivityResumed(Activity activity) {
                ACTIVITIES.put(activity, ON_RESUME);
                sendBroadcast(0, "put", getUniqueId(activity) + "->" + ACTIVITIES.get(activity));
            }

            @Override
            public void onActivityPaused(Activity activity) {
                ACTIVITIES.put(activity, ON_PAUSE);
                sendBroadcast(0, "put", getUniqueId(activity) + "->" + ACTIVITIES.get(activity));
            }

            @Override
            public void onActivityStopped(Activity activity) {
                ACTIVITIES.put(activity, ON_STOP);
                sendBroadcast(0, "put", getUniqueId(activity) + "->" + ACTIVITIES.get(activity));
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                ACTIVITIES.remove(activity);
                sendBroadcast(0, "remove", getUniqueId(activity));
            }
        });
    }

    /**
     * 获取当前进程activity栈
     *
     * @return
     */
    public static List<Activity> getActivities() {
        List<Activity> list = new ArrayList<>();
        for (Activity act : ACTIVITIES.keySet()) {
            list.add(act);
        }
        return list;
    }

    /**
     * 获取当前进程顶部activity
     *
     * @return
     */
    public static Activity geTopActivity() {
        if (ACTIVITIES.isEmpty()) return null;
        try {
            Field tail = ACTIVITIES.getClass().getDeclaredField("tail");
            tail.setAccessible(true);
            return ((Map.Entry<Activity, String>) tail.get(ACTIVITIES)).getKey();
        } catch (Exception e) {}
        List<Activity> list = getActivities();
        return list.get(list.size() - 1);
    }

    /**
     * 获取当前进程的某个activity
     *
     * @param uniqueId activity标识
     * @return
     */
    public static Activity getActivity(String uniqueId) {
        if (ACTIVITIES.isEmpty() || TextUtils.isEmpty(uniqueId)) return null;
        List<Activity> list = getActivities();
        for (Activity act : list) {
            if (TextUtils.equals(uniqueId, getUniqueId(act))) return act;
        }
        return null;
    }

    /**
     * 获取某个activity的生命周期
     *
     * @param act
     * @return
     */
    public static String getLifecycle(Activity act) {
        if (act == null) return null;
        return ACTIVITIES.get(act);
    }

    /**
     * 获取某个activity的生命周期
     *
     * @param uniqueId
     * @return
     */
    public static String getLifecycle(String uniqueId) {
        if (TextUtils.isEmpty(uniqueId)) return null;
        return ActivityCache.getLifecycle(uniqueId);
    }

    /**
     * 获取全部进程的activity数
     *
     * @return
     */
    public static int size() {
        return ActivityCache.size();
    }

    /**
     * 获取某个进程的activity数
     *
     * @param pid
     * @return
     */
    public static int size(int pid) {
        if (pid == Process.myPid()) return ACTIVITIES.size();
        return ActivityCache.size(pid);
    }

    /**
     * 获取某个进程的activity数
     *
     * @param processName
     * @return
     */
    public static int size(String processName) {
        if (TextUtils.isEmpty(processName)) return 0;
        return ActivityCache.size(Utils.getPid(processName));
    }

    /**
     * finish所有进程的某个类型的activity（实际是某个进程）
     *
     * @param cls
     */
    public static void finish(Class<? extends Activity> cls) {
        if (cls == null) return;
        sendBroadcast(0, "finish", cls.getName());
    }

    /**
     * finish某个activity
     *
     * @param uniqueId
     */
    public static void finish(String uniqueId) {
        if (TextUtils.isEmpty(uniqueId)) return;
        sendBroadcast(0, "finish", uniqueId);
    }

    /**
     * finish所有进程的所有activity
     *
     */
    public static void exit() {
        sendBroadcast(0, "exit", null);
    }

    /**
     * finish某个进程的所有activity
     *
     * @param pid
     */
    public static void exit(int pid) {
        sendBroadcast(pid, "exit", null);
    }

    /**
     * finish某个进程的所有activity
     *
     * @param processName
     */
    public static void exit(String processName) {
        if (TextUtils.isEmpty(processName)) return;
        sendBroadcast(0, "exit", processName);
    }

    /**
     * 获取acitivity的标识
     *
     * @param activity
     * @return
     */
    public static String getUniqueId(Activity activity) {
        if (activity == null) return null;
        return activity.getClass().getName() + ";" + Utils.hashCode(activity) + ";" + Process.myPid();
    }
}
