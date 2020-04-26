package com.shouzhong.bridge;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
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
        String name = getCurrentProcessNameByFile();
        if (!TextUtils.isEmpty(name)) return name;
        name = getCurrentProcessNameByAms();
        if (!TextUtils.isEmpty(name)) return name;
        name = getCurrentProcessNameByReflect();
        return name;
    }

    private static String getCurrentProcessNameByFile() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getCurrentProcessNameByAms() {
        try {
            ActivityManager am = (ActivityManager) Bridge.getApp().getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) return "";
            List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
            if (info == null || info.size() == 0) return "";
            int pid = android.os.Process.myPid();
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
