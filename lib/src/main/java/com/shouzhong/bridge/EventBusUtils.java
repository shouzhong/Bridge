package com.shouzhong.bridge;

import android.content.Intent;
import android.os.Process;

import org.greenrobot.eventbus.EventBus;

public class EventBusUtils {

    static EventBus get() {
        return EventBus.getDefault();
    }

    static void sendBroadcast(int pid, String action, String data) {
        String permission = Bridge.getApp().getPackageName() + ".shouzhong.permission.RECEIVER_PROCESS_MESSAGE";
        Intent intent = new Intent(Bridge.getApp().getPackageName() + ".shouzhong.action.PROCESS_MESSAGE");
        intent.putExtra("type", "eventbus");
        intent.putExtra("pid", pid);
        intent.putExtra("action", action);
        intent.putExtra("data", data);
        Bridge.getApp().sendBroadcast(intent, permission);
    }

    static void init() {
        if (!Utils.isMainProcess()) {
            sendBroadcast(Process.myPid(), "init", null);
        }
    }

    public static void register(Object subscriber) {
        if (subscriber == null) return;
        get().register(subscriber);
        int hashCode = System.identityHashCode(subscriber);
        sendBroadcast(0, "register", subscriber.getClass().getName() + ";" + hashCode);
    }

    public static void unregister(Object obj) {
        if (obj == null) return;
        if (!get().isRegistered(obj)) return;
        get().unregister(obj);
        int hashCode = System.identityHashCode(obj);
        sendBroadcast(0, "unregister", obj.getClass().getName() + ";" + hashCode);
    }

    public static boolean isRegistered(Object subscriber) {
        if (subscriber == null) return false;
        return get().isRegistered(subscriber);
    }

    public static void post(Object obj) {
        if (obj == null) return;
        sendBroadcast(0, "post",  obj.getClass().getName() + ";" + Utils.getGson().toJson(obj));
    }

    public static void postSticky(Object obj) {
        if (obj == null) return;
        sendBroadcast(0, "postSticky",  obj.getClass().getName() + ";" + Utils.getGson().toJson(obj));
    }

    public static <T> T getStickyEvent(Class<T> eventType) {
        if (eventType == null) return null;
        return get().getStickyEvent(eventType);
    }

    public static <T> T removeStickyEvent(Class<T> eventType) {
        if (eventType == null) return null;
        T t = get().removeStickyEvent(eventType);
        sendBroadcast(0, "removeStickyEvent",  eventType.getName());
        return t;
    }

    public static boolean removeStickyEvent(Object event) {
        if (event == null) return false;
        boolean b = get().removeStickyEvent(event);
        sendBroadcast(0, "removeStickyEvent",  event.getClass().getName());
        return b;
    }

    public static void removeAllStickyEvents() {
        sendBroadcast(0, "removeAllStickyEvents",  null);
    }

    public static boolean hasSubscriberForEvent(Class<?> eventClass) {
        if (eventClass == null) return false;
        return get().hasSubscriberForEvent(eventClass) || EventBusCache.hasSubscriberForEvent(eventClass.getName());
    }
}
