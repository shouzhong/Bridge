# Bridge
## 说明
对eventbus，activity栈进行了跨进程适配
## 使用
### 依赖
```
implementation 'com.shouzhong:Bridge:1.0.2'
implementation 'org.greenrobot:eventbus:3.2.0'
implementation 'com.google.code.gson:gson:2.8.6'
```
### 代码
在Applicaition的onCreate方法中调用
```
Bridge.init(this);
```

## 方法说明
EventBusUtils：参考EventBus

ActivityUtils

方法 | 说明
------------ | -------------
getActivities | 获取当前进程activity栈
geTopActivity | 获取当前进程顶部activity
getActivity | 获取当前进程的某个activity
getLifecycle | 获取某个activity的当前生命周期
size | 所有进程activity数
size(带参数) | 某个进程activity数
finish | finish某个或者某类activity
exit | finish所有进程的所有activity
exit(带参数) | finish某个进程的所有activity
getUniqueId | 获取activity的标识