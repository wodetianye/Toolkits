# ToolKit
为Android开发常用工具类库,避免平时新建项目时每次都要重写大量代码,该项目中为您提供了基本常用的类库及基类封装,
项目中加入依赖后即可直接开发业务,无须关心项目架构,因为基类已经帮你实现

# 仓库迁移 已废弃
~~implementation 'com.gitlab.lvww.toolkit:toolkit:5.1.0'~~ 

```
cd existing_repo
git remote add origin https://gitlab.com/lvweiwei/toolkit.git
git branch -M main
git push -uf origin main
```

***

## 主线版本分支定义说明
主线版本分支只作发版和紧急bug修复,不做开发迭代

## 单线分支定义说明
单线分支只做单功能开发,一个单线分支代码中一般只包含一个小功能,例如thread单线分支,里边只有线程相关的功能

## 版本号定义说明
主线版本号1.0.0：1.0.0代表主线版本，4代表主线版本号，第2位代表功能版本，第3位代表功能版本补丁

主分支版本迭代初始版本为1.0.0，每合并一个小功能模块第二位版本号+1，新增一个module模块功能时第一位版本号+1

单线版本必须基于1.0.0拉出进行单功能开发，开发完毕后合入主线分支才能进行发版

## 分支说明

默认分支为main分支，分支初始版本为1.0.0,增加一个大的功能模块时主线分支变为2.0.0

- [ ] 基础Thread线程池工具类
- [ ] Time时间转换工具类
- [ ] File文件读写工具类
- [ ] Network网络检测工具
- [ ] Log网络检测工具
- [ ] Gson解析工具类
- [ ] 类型转换工具类

***

## 5.1.0版本说明和

## UtilsManager功能说明
````
在您的Application中加入
void init(Context context) 所有工具类的初始化
void clearCache() 所有工具类的反初始化
````

## Logger功能说明
````
void switchLog(boolean logSwitch) log开关控制 默认是开启
void v(String args) V级别的log打印
void d(String args) D级别的log打印
void i(String args) I级别的log打印
void w(String args) W级别的log打印
void e(String args) E级别的log打印
void setLogLevel(int level) log输出级别 控制
setDefaultTag(String defaultTag) log默认Tag设置,如果没有设置此值,输出log时会自动获取执行该方法的方法名字
...
````

## ConvertUtils功能简介
````
该类是对基本数据类型之间的转换,包括数据对象、集合判空、方法参数校验、基本类型的大小比较等
void checkParam(String method, Object... args)                                                      某些方法执行的前置参数校验,可以避免很多空指针异常,后续考虑采用切面编程进行改进
String convertString(Object object)                                                                 转换为String类型
Integer str2Int(@Nullable String src)
Long str2Long(@Nullable String src, long defaultValue) 
Float str2Float(@Nullable String src)
Double str2Double(@Nullable String src)
Long double2ln(Double arg)
Integer ln2int(Long arg)
boolean equals(String args, String args1)
boolean isEmpty(String str)
<T> boolean isEmpty(T[] objects)
boolean isEmpty(List<?> objects)
boolean compareTo(Long arg, Long args)                                                              封装后的基本数据类型是无法通过==直接进行比较的,该类通过改进原生compareTo使代码更加健壮
...
````

## ThreadManager功能说明
````
String getThreadInfo()                                                                              获取线程的基本信息
postUi(Runnable runnable)                                                                           发送到主线程,一般用作更新UI
postDelay(Runnable runnable, long time)                                                             延迟线程
void execute(Runnable runnable)                                                                     开启子线程
<T> T execute(RunTask<T> runnable)                                                                  开启携带返回值的子线程
void asyncDelay(Runnable run, long start, TimeUnit unit)                                            延迟线程
ScheduledFuture asyncWithFixDelay(Runnable run, long start, long interval, TimeUnit unit)           周期线程
ScheduledFuture asyncAtFixDelay(Runnable run, long start, long interval, TimeUnit unit)             周期线程
void uncaughtException(Thread t, Throwable e)                                                       捕获线程执行的异常
...
````

## TimeUtils关键方法说明
```
     
```

## FileUtils关键方法说明
````

...
````

## NetWorkUtils功能说明
````

...
````

## GsonUtols功能说明
````

...
````

## ToastUtils功能说明
````

````

## ResourceUtils功能说明
````

...
````

## ScreenUtils功能说明
````

...
````
