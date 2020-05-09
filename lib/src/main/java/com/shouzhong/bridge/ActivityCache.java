package com.shouzhong.bridge;

import android.app.Activity;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

class ActivityCache {
    static final LinkedHashMap<String, String> CACHE = new LinkedHashMap<>();

    static void put(String s, String life) {
        if (TextUtils.isEmpty(s)) return;
        CACHE.put(s, life);
    }

    static void remove(String s) {
        if (TextUtils.isEmpty(s)) return;
        CACHE.remove(s);
    }

    static boolean contains(Class<? extends Activity> cls) {
        if (cls == null) return false;
        for (String s : CACHE.keySet()) {
            if (s.startsWith(cls.getName() + ";")) return true;
        }
        return false;
    }

    static Map.Entry<String, String> getLast() {
        if (CACHE.isEmpty()) return null;
        try {
            Field tail = CACHE.getClass().getDeclaredField("tail");
            tail.setAccessible(true);
            return (Map.Entry<String, String>) tail.get(CACHE);
        } catch (Exception e) {}
        return get(CACHE.size() - 1);
    }

    static Map.Entry<String, String> get(int position) {
        if (position > CACHE.size() - 1) return null;
        int i = 0;
        for (Map.Entry<String, String> entry : CACHE.entrySet()) {
            if (i++ == position) return entry;
        }
        return null;
    }

    static String getLifecycle(String key) {
        return CACHE.get(key);
    }

    static int size() {
        return CACHE.size();
    }

    static int size(int pid) {
        if (pid == 0) return 0;
        int i = 0;
        for (String s : CACHE.keySet()) {
            if (s.endsWith(";" + pid)) i++;
        }
        return i;
    }
}
