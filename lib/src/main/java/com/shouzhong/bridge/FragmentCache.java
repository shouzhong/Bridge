package com.shouzhong.bridge;

import android.text.TextUtils;

import java.util.LinkedHashMap;

public class FragmentCache {
    static final LinkedHashMap<String, String> CACHE = new LinkedHashMap<>();

    static void put(String key, String value) {
        if (TextUtils.isEmpty(key)) return;
        CACHE.put(key, value);
    }

    static void remove(String key) {
        if (TextUtils.isEmpty(key)) return;
        CACHE.remove(key);
    }
}
