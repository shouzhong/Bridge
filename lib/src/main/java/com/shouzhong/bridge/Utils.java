package com.shouzhong.bridge;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

class Utils {

    static Gson gson;

    static Gson getGson() {
        if (gson == null) gson = new Gson();
        return gson;
    }

    static boolean isMainProcess() {
        return Bridge.getApp().getPackageName().equals(getCurrentProcessName());
    }

    static String getCurrentProcessName() {
        int pid = Process.myPid();
        String name = getProcessNameByFile(pid);
        if (!TextUtils.isEmpty(name)) return name;
        name = getProcessNameByAms(pid);
        if (!TextUtils.isEmpty(name)) return name;
        name = getCurrentProcessNameByReflect();
        return name;
    }

    static String getProcessName(int pid) {
        String name = getProcessNameByFile(pid);
        if (!TextUtils.isEmpty(name)) return name;
        name = getProcessNameByAms(pid);
        return name;
    }

    static int getPid(String processName) {
        try {
            ActivityManager am = (ActivityManager) Bridge.getApp().getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) return 0;
            List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
            if (info == null || info.size() == 0) return 0;
            for (ActivityManager.RunningAppProcessInfo aInfo : info) {
                if (TextUtils.equals(aInfo.processName, processName)) return aInfo.pid;
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    private static String getProcessNameByFile(int pid) {
        try {
            File file = new File("/proc/" + pid + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getProcessNameByAms(int pid) {
        try {
            ActivityManager am = (ActivityManager) Bridge.getApp().getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) return "";
            List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
            if (info == null || info.size() == 0) return "";
            for (ActivityManager.RunningAppProcessInfo aInfo : info) {
                if (aInfo.pid == pid) {
                    if (aInfo.processName != null) {
                        return aInfo.processName;
                    }
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    private static String getCurrentProcessNameByReflect() {
        String processName = "";
        try {
            Application app = Bridge.getApp();
            Field loadedApkField = app.getClass().getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(app);

            Field activityThreadField = loadedApk.getClass().getDeclaredField("mActivityThread");
            activityThreadField.setAccessible(true);
            Object activityThread = activityThreadField.get(loadedApk);

            Method getProcessName = activityThread.getClass().getDeclaredMethod("getProcessName");
            processName = (String) getProcessName.invoke(activityThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processName;
    }

    static Method getMethodByReflect(Class cls, String methodName, Class<?>... parameterTypes) {
        try {
            return cls.getDeclaredMethod(methodName, parameterTypes);
        } catch (Exception e) {}
        return null;
    }

    static int hashCode(Object obj) {
        int hashCode;
        try {
            Method method = Utils.getMethodByReflect(Object.class, "identityHashCode", Object.class);
            hashCode = (int) method.invoke(null, obj);
        } catch (Exception e) {
            hashCode = obj.hashCode();
        }
        return hashCode;
    }
}
