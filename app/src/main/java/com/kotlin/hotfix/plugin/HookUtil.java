package com.kotlin.hotfix.plugin;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * create by max at 2020/11/26 10:23
 */

public class HookUtil {

    private static String TARGET_INTENT = "target_intent";

    public static void hookAMS() {

        //代理 ActivityManagerService 的startActivity 方法
        try {
            Field iActivityManagerSingletonField = null;
            // 大于 8.0 小于 10.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
                iActivityManagerSingletonField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Class<?> activityManagerClass = Class.forName("android.app.ActivityManagerNative");
                iActivityManagerSingletonField = activityManagerClass.getDeclaredField("gDefault");
            }

            iActivityManagerSingletonField.setAccessible(true);
            Object iActivityManagerSingleton = iActivityManagerSingletonField.get(null);

            Class<?> singleTonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singleTonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object mInstance = mInstanceField.get(iActivityManagerSingleton);


            Class<?> iAtivityManagerClass = Class.forName("android.app.IActivityManager");
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Object proxyInstance = Proxy.newProxyInstance(contextClassLoader, new Class[]{iAtivityManagerClass}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    String methodName = method.getName();
                    if (methodName.startsWith("startActivity")) {
                        int index = 0;
                        for (int i = 0; i < args.length; i++) {
                            if (args[i] instanceof Intent) {
                                index = i;
                                break;
                            }
                        }
                        //启动插件的Intent
                        Intent intent = (Intent) args[index];

                        Intent proxyIntent = new Intent();
                        proxyIntent.setClassName("com.kotlin.hotfix", "com.kotlin.hotfix.ProxyActivity");

                        proxyIntent.putExtra(TARGET_INTENT, intent);

                        args[index] = proxyIntent;

                    }

                    return method.invoke(mInstance, args);
                }
            });

            //用代理对象替换IActivityManager对象
            mInstanceField.set(iActivityManagerSingleton, proxyInstance);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hookHandler()  {

        Handler.Callback callback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                //android 8.0 version
                if (msg.what == 100) {
                    //ActivityClientRecord的对象--msg.obj
                    try {
                        Field intentField = msg.obj.getClass().getDeclaredField("intent");
                        intentField.setAccessible(true);
                        //启动代理的intent
                        Intent intentProxy = (Intent) intentField.get(msg.obj);

                        Intent pluginIntent = intentProxy.getParcelableExtra(TARGET_INTENT);
                        if (pluginIntent != null) {
                            //替换plugin intent
                            intentField.set(msg.obj, pluginIntent);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //android 9.0 version
                else if (msg.what == 159) {

                }
                return false;
            }
        };

        //替换系统的callback对象
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field currentActivityThreadFiled = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            currentActivityThreadFiled.setAccessible(true);
            Object currentActivityThread = currentActivityThreadFiled.get(null);

            //mH对象
            Field mHField = activityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            Handler mHander = (Handler) mHField.get(currentActivityThread);

            Class<?> handlerClass = Class.forName("android.os.Handler");
            Field mCallbackField = handlerClass.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);

            mCallbackField.set(mHander, callback);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
