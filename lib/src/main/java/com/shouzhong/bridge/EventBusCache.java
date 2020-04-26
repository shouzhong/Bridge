package com.shouzhong.bridge;

import android.text.TextUtils;

import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

class EventBusCache {
    private static final int BRIDGE = 0x40;
    private static final int SYNTHETIC = 0x1000;
    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT | Modifier.STATIC | BRIDGE | SYNTHETIC;

    static final LinkedHashMap<String, List<String>> TYPE_BY_SUBSCRIBER = new LinkedHashMap<>();
    static final LinkedHashMap<String, String> STICKY_EVENTS = new LinkedHashMap<>();

    static boolean hasSubscriberForEvent(String s) {
        if (TextUtils.isEmpty(s)) return false;
        for (List<String> list : TYPE_BY_SUBSCRIBER.values()) {
            if (list == null) continue;
            if (list.contains(s)) return true;
        }
        return false;
    }

     static void register(String s) {
        if (TextUtils.isEmpty(s)) return;
        List<String> types = findMethods(s);
        if (types == null) return;
         TYPE_BY_SUBSCRIBER.put(s, types);
    }

    static void unregister(String s) {
        if (TextUtils.isEmpty(s)) return;
        TYPE_BY_SUBSCRIBER.remove(s);
    }

    static void postSticky(String cls, String data) {
        if (TextUtils.isEmpty(cls)) return;
        STICKY_EVENTS.put(cls, data);
    }

    static void removeStickyEvent(String cls) {
        if (TextUtils.isEmpty(cls)) return;
        STICKY_EVENTS.remove(cls);
    }

    static void removeAllStickyEvents() {
        STICKY_EVENTS.clear();
    }

    private static List<String> findMethods(String s) {
        try {
            Class cls = Class.forName(s.substring(0, s.indexOf(';')));
            Method[] methods;
            try {
                methods = cls.getDeclaredMethods();
            } catch (Exception e) {
                methods = cls.getMethods();
            }
            if (methods == null || methods.length == 0) return null;
            List<String> list = null;
            for (Method method : methods) {
                int modifiers = method.getModifiers();
                if ((modifiers & Modifier.PUBLIC) == 0 || (modifiers & MODIFIERS_IGNORE) != 0) continue;
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes == null || parameterTypes.length != 1) continue;
                Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
                if (subscribeAnnotation == null) continue;
                if (list == null) list = new ArrayList<>();
                list.add(parameterTypes[0].getName());
            }
            return list;
        } catch (Exception e) { }
        return null;
    }
}
