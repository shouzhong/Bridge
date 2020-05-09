package com.shouzhong.bridge;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FragmentStack {
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
        if (!Utils.classExists("androidx.appcompat.app.AppCompatActivity")) return;
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
        if (act == null || !Utils.classExists("androidx.appcompat.app.AppCompatActivity")) return;
        if (!(act instanceof AppCompatActivity)) return;
        AppCompatActivity temp = (AppCompatActivity) act;
        temp.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
                FRAGMENTS.put(f, ON_ATTACH + ":" + ActivityStack.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                FRAGMENTS.put(f, ON_CREATE + ":" + ActivityStack.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState) {
                FRAGMENTS.put(f, ON_VIEW_CREATED + ":" + ActivityStack.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentActivityCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                FRAGMENTS.put(f, ON_ACTIVITY_CREATED + ":" + ActivityStack.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.put(f, ON_START + ":" + ActivityStack.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.put(f, ON_RESUME + ":" + ActivityStack.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentPaused(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.put(f, ON_PAUSE + ":" + ActivityStack.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentStopped(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.put(f, ON_STOP + ":" + ActivityStack.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentViewDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.put(f, ON_DESTROY_VIEW + ":" + ActivityStack.getUniqueId(act));
                sendBroadcast(0, "put", getUniqueId(f) + "->" + FRAGMENTS.get(f));
            }

            @Override
            public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                FRAGMENTS.put(f, ON_DESTROY + ":" + ActivityStack.getUniqueId(act));
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
     * 获取当前进程的fragment栈
     *
     * @return
     */
    public static List<Fragment> getFragments() {
        List<Fragment> list = new ArrayList<>();
        for (Fragment f : FRAGMENTS.keySet()) {
            list.add(f);
        }
        return list;
    }

    /**
     * 获取当前进程的某个fragment
     *
     * @param uniqueId
     * @return
     */
    public static Fragment getFragment(String uniqueId) {
        if (FRAGMENTS.isEmpty() || TextUtils.isEmpty(uniqueId)) return null;
        for (Fragment f : FRAGMENTS.keySet()) {
            if (TextUtils.equals(uniqueId, getUniqueId(f))) return f;
        }
        return null;
    }

    /**
     * 获取某个fragment的生命周期
     *
     * @param fragment
     * @return
     */
    public static String getLifecycle(Fragment fragment) {
        if (fragment == null) return null;
        String s = FRAGMENTS.get(fragment);
        if (TextUtils.isEmpty(s)) return null;
        return s.split(":")[0];
    }

    /**
     * 获取某个fragment的生命周期
     *
     * @param uniqueId
     * @return
     */
    public static String getLifecycle(String uniqueId) {
        return FragmentCache.getLifecycle(uniqueId);
    }

    /**
     * 是否包含某个类型的fragment
     *
     * @param cls
     * @return
     */
    public static boolean contains(Class<? extends Fragment> cls) {
        return FragmentCache.contains(cls);
    }

    /**
     * 获取全部进程的fragment数
     *
     * @return
     */
    public static int size() {
        return FragmentCache.size();
    }

    /**
     * 获取某个进程的fragment数
     *
     * @param pid
     * @return
     */
    public static int size(int pid) {
        if (pid == Process.myPid()) return FRAGMENTS.size();
        return FragmentCache.size(pid);
    }

    /**
     * 获取某个进程的fragment数
     *
     * @param processName
     * @return
     */
    public static int size(String processName) {
        if (TextUtils.isEmpty(processName)) return 0;
        return FragmentCache.size(Utils.getPid(processName));
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
