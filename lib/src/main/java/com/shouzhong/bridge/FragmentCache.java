package com.shouzhong.bridge;

import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import java.util.LinkedHashMap;

class FragmentCache {
    static final LinkedHashMap<String, String> CACHE = new LinkedHashMap<>();

    static void put(String key, String value) {
        if (TextUtils.isEmpty(key)) return;
        CACHE.put(key, value);
    }

    static void remove(String key) {
        if (TextUtils.isEmpty(key)) return;
        CACHE.remove(key);
    }

    static boolean contains(Class<? extends Fragment> cls) {
        if (cls == null) return false;
        for (String s : CACHE.keySet()) {
            if (s.startsWith(cls.getName() + ";")) return true;
        }
        return false;
    }

    static String getLifecycle(String key) {
        if (TextUtils.isEmpty(key)) return null;
        String s = CACHE.get(key);
        if (TextUtils.isEmpty(s)) return null;
        return s.split(":")[0];
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
