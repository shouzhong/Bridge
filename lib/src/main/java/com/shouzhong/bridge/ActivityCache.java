package com.shouzhong.bridge;

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

    static Map.Entry<String, String> getLast() {
        if (CACHE.isEmpty()) return null;
        try {
            Field tail = CACHE.getClass().getDeclaredField("tail");
            tail.setAccessible(true);
            return (Map.Entry<String, String>) tail.get(CACHE);
        } catch (Exception e) {}
        return get(size() - 1);
    }

    static Map.Entry<String, String> get(int position) {
        if (position > size() - 1) return null;
        int i = 0;
        for (Map.Entry<String, String> entry : CACHE.entrySet()) {
            if (i++ == position) return entry;
        }
        return null;
    }

    static int size() {
        return CACHE.size();
    }
}
