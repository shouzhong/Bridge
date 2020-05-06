package com.shouzhong.bridge;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.LinkedHashMap;

public class FragmentUtils {
    public static final String ON_ATTACH = "onAttach";
    public static final String ON_CREATE = "onCreate";
    public static final String ON_VIEW_CREATED = "onViewCreated";
    public static final String ON_ACTIVITY_CREATED = "onActivityCreated";
    public static final String ON_START = "onStart";
    public static final String ON_RESUME = "onResume";
    public static final String ON_PAUSE = "onPause";
    public static final String ON_STOP = "onStop";
    public static final String ON_DESTROY_VIEW = "onDestroyView";
    public static final String ON_DESTROY = "onDestroy";

    static final LinkedHashMap<Fragment, String> FRAGMENTS = new LinkedHashMap<>();

    static void sendBroadcast(int pid, String action, String data) {
        String permission = Bridge.getApp().getPackageName() + ".shouzhong.permission.RECEIVER_PROCESS_MESSAGE";
        Intent intent = new Intent(Bridge.getApp().getPackageName() + ".shouzhong.action.PROCESS_MESSAGE");
        intent.putExtra("type", "fragment");
        intent.putExtra("pid", pid);
        intent.putExtra("action", action);
        intent.putExtra("data", data);
        Bridge.getApp().sendBroadcast(intent, permission);
    }

    static void init() {
        if (!Utils.classExit("androidx.appcompat.app.AppCompatActivity")) return;
        if (!Utils.isMainProcess()) {
            sendBroadcast(Process.myPid(), "init", null);
        }
        Bridge.getApp().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                register(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    private static void register(final Activity act) {
        if (act == null || !Utils.classExit("androidx.appcompat.app.AppCompatActivity")) return;
        if (!(act instanceof AppCompatActivity)) return;
        AppCompatActivity temp = (AppCompatActivity) act;
        temp.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
                FRAGMENTS.put(f, ON_ATTACH + ":" + ActivityUtils.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                FRAGMENTS.put(f, ON_CREATE + ":" + ActivityUtils.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState) {
                FRAGMENTS.put(f, ON_VIEW_CREATED + ":" + ActivityUtils.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentActivityCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                FRAGMENTS.put(f, ON_ACTIVITY_CREATED + ":" + ActivityUtils.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.put(f, ON_START + ":" + ActivityUtils.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.put(f, ON_RESUME + ":" + ActivityUtils.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentPaused(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.put(f, ON_PAUSE + ":" + ActivityUtils.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentStopped(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.put(f, ON_STOP + ":" + ActivityUtils.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentViewDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.put(f, ON_DESTROY_VIEW + ":" + ActivityUtils.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.put(f, ON_DESTROY + ":" + ActivityUtils.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentDetached(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.remove(f);
                sendBroadcast(0, "remove", getUniqueId(f));
            }
        }, true);
    }

    /**
     * 获取fragment的标识
     *
     * @param fragment
     * @return
     */
    public static String getUniqueId(Fragment fragment) {
        if (fragment == null) return null;
        return fragment.getClass().getName() + ";" + Utils.hashCode(fragment) + ";" + Process.myPid();
    }
}
