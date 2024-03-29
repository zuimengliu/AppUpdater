# AppUpdater

![Image](app/src/main/ic_launcher-web.png)

[![Download](https://img.shields.io/badge/download-App-blue.svg)](https://raw.githubusercontent.com/jenly1314/AppUpdater/master/app/release/app-release.apk)
[![JitPack](https://jitpack.io/v/jenly1314/AppUpdater.svg)](https://jitpack.io/#jenly1314/AppUpdater)
[![CI](https://travis-ci.org/jenly1314/AppUpdater.svg?branch=master)](https://travis-ci.org/jenly1314/AppUpdater)
[![CircleCI](https://circleci.com/gh/jenly1314/AppUpdater.svg?style=svg)](https://circleci.com/gh/jenly1314/AppUpdater)
[![API](https://img.shields.io/badge/API-15%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=15)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/mit-license.php)
[![Blog](https://img.shields.io/badge/blog-Jenly-9933CC.svg)](https://jenly1314.github.io/)
[![QQGroup](https://img.shields.io/badge/QQGroup-20867961-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=8fcc6a2f88552ea44b1411582c94fd124f7bb3ec227e2a400dbbfaad3dc2f5ad)

AppUpdater for Android 是一个专注于App更新，一键傻瓜式集成App版本升级的开源库。(无需担心通知栏适配；无需担心重复点击下载；无需担心App安装等问题；这些AppUpdater都已帮您处理好。)
 核心库主要包括app-updater和app-dialog。
> 下载更新和弹框提示分开，是因为这本来就是两个逻辑。完全独立开来能有效的解耦。
* app-updater 主要负责后台下载更新App，无需担心下载时各种配置相关的细节，一键傻瓜式升级。
* app-dialog 主要是提供常用的Dialog和DialogFragment，简化弹框提示，样式支持高度自定义。
> app-updater + app-dialog 配合使用，谁用谁知道。


## 功能介绍
- [x] 专注于App更新一键傻瓜式升级
- [x] 支持下载监听
- [x] 支持下载失败，重新下载
- [x] 支持下载优先取本地缓存
- [x] 支持通知栏提示内容和过程全部可配置
- [x] 支持Android Q(10)
- [x] 支持使用OkHttpClient下载

### [AndroidX version](https://github.com/jenly1314/AppUpdater/tree/androidx)

## Gif 展示
![Image](GIF.gif)

## 引入

### Maven：
```maven
    //app-updater
    <dependency>
      <groupId>com.king.app</groupId>
      <artifactId>app-updater</artifactId>
      <version>1.0.7</version>
      <type>pom</type>
    </dependency>
    
    //app-dialog
    <dependency>
      <groupId>com.king.app</groupId>
      <artifactId>app-dialog</artifactId>
      <version>1.0.7</version>
      <type>pom</type>
    </dependency>
```
### Gradle:
```gradle

    //----------AndroidX 版本
    //app-updater
    implementation 'com.king.app:app-updater:1.0.7-androidx'
    //app-dialog
    implementation 'com.king.app:app-dialog:1.0.7-androidx'
    
    //----------Android Support 版本
    //app-updater
    implementation 'com.king.app:app-updater:1.0.7'
    //app-dialog
    implementation 'com.king.app:app-dialog:1.0.7'
```
### Lvy:
```lvy
    //app-updater
    <dependency org='com.king.app' name='app-dialog' rev='1.0.7'>
      <artifact name='$AID' ext='pom'></artifact>
    </dependency>
    
    //app-dialog
    <dependency org='com.king.app' name='app-dialog' rev='1.0.7'>
      <artifact name='$AID' ext='pom'></artifact>
    </dependency>
```

###### 如果Gradle出现compile失败的情况，可以在Project的build.gradle里面添加如下：（也可以使用上面的GitPack来complie）
```gradle
    allprojects {
        repositories {
            //...
            maven { url 'https://dl.bintray.com/jenly/maven' }
        }
    }
```

## 示例

```Java
    //一句代码，傻瓜式更新
    new AppUpdater(getContext(),url).start();
```
```Java
    //简单弹框升级
    AppDialogConfig config = new AppDialogConfig();
    config.setTitle("简单弹框升级")
            .setOk("升级")
            .setContent("1、新增某某功能、\n2、修改某某问题、\n3、优化某某BUG、")
            .setOnClickOk(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AppUpdater.Builder()
                            .serUrl(mUrl)
                            .setFilename("AppUpdater.apk")
                            .build(getContext())
                            .start();
                    AppDialog.INSTANCE.dismissDialog();
                }
            });
    AppDialog.INSTANCE.showDialog(getContext(),config);
```
```Java
    //简单DialogFragment升级
    AppDialogConfig config = new AppDialogConfig();
    config.setTitle("简单DialogFragment升级")
            .setOk("升级")
            .setContent("1、新增某某功能、\n2、修改某某问题、\n3、优化某某BUG、")
            .setOnClickOk(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AppUpdater.Builder()
                            .serUrl(mUrl)
                            .setFilename("AppUpdater.apk")
                            .build(getContext())
                            .setHttpManager(OkHttpManager.getInstance())//使用OkHttpClient实现下载，需依赖okhttp库
                            .start();
                    AppDialog.INSTANCE.dismissDialogFragment(getSupportFragmentManager());
                }
            });
    AppDialog.INSTANCE.showDialogFragment(getSupportFragmentManager(),config);

```

更多使用详情，请查看[app](app)中的源码使用示例或直接查看[API帮助文档](https://jenly1314.github.io/projects/AppUpdater/doc/)

## 版本记录

#### v1.0.7：2019-12-18
*  优化细节

#### v1.0.6：2019-11-27
*  新增OkHttpManager        如果使用了OkHttpManager则必须依赖[okhttp](https://github.com/square/okhttp)
*  优化细节 (progress,total 变更 int -> long)

#### v1.0.5：2019-9-4
*  支持取消下载

#### v1.0.4：2019-6-4      [支持AndroidX版本](https://github.com/jenly1314/AppUpdater/tree/androidx)
*  支持添加请求头

#### v1.0.3：2019-5-9
*  新增支持下载APK优先取本地缓存，避免多次下载相同版本的APK文件
*  AppDialog支持隐藏Dialog的标题

#### v1.0.2：2019-3-18
*  新增通知栏是否震动和铃声提示配置
*  AppDialogConfig新增getView(Context context)方法

#### v1.0.1：2019-1-10
*  升级Gradle到4.6

#### v1.0  ：2018-6-29
*  AppUpdater初始版本

## 赞赏
如果您喜欢AppUpdater，或感觉AppUpdater帮助到了您，可以点右上角“Star”支持一下，您的支持就是我的动力，谢谢 :smiley:<p>
您也可以扫描下面的二维码，请作者喝杯咖啡 :coffee:
    <div>
        <img src="https://jenly1314.github.io/image/pay/wxpay.png" width="280" heght="350">
        <img src="https://jenly1314.github.io/image/pay/alipay.png" width="280" heght="350">
        <img src="https://jenly1314.github.io/image/pay/qqpay.png" width="280" heght="350">
        <img src="https://jenly1314.github.io/image/alipay_red_envelopes.jpg" width="233" heght="350">
    </div>

## 关于我
   Name: <a title="关于作者" href="https://about.me/jenly1314" target="_blank">Jenly</a>

   Email: <a title="欢迎邮件与我交流" href="mailto:jenly1314@gmail.com" target="_blank">jenly1314#gmail.com</a> / <a title="给我发邮件" href="mailto:jenly1314@vip.qq.com" target="_blank">jenly1314#vip.qq.com</a>

   CSDN: <a title="CSDN博客" href="http://blog.csdn.net/jenly121" target="_blank">jenly121</a>

   博客园: <a title="博客园" href="https://www.cnblogs.com/jenly" target="_blank">jenly</a>

   Github: <a title="Github开源项目" href="https://github.com/jenly1314" target="_blank">jenly1314</a>

   加入QQ群: <a title="点击加入QQ群" href="http://shang.qq.com/wpa/qunwpa?idkey=8fcc6a2f88552ea44b1411582c94fd124f7bb3ec227e2a400dbbfaad3dc2f5ad" target="_blank">20867961</a>
   <div>
       <img src="https://jenly1314.github.io/image/jenly666.png">
       <img src="https://jenly1314.github.io/image/qqgourp.png">
   </div>
