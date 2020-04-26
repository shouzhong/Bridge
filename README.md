# Bridge
## 说明
对eventbus，activity栈进行了跨进程适配
## 使用
### 依赖
```
implementation 'com.shouzhong:Bridge:1.0.0'
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
size | 当前进程打开activity数
allSize | 所有进程打开activity数
getLast | 获取当前进程最后一个打开的activity
get | 获取当前进程第n个打开的activity
getActivities | 获取当前进程activity栈
getLifecycle | 获取某个activity的生命周期
finish | finish所有匹配class的activity
exit | finish所有进程的所有activity
exit(带参数) | finish某个进程的所有activity