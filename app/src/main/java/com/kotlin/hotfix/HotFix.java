package com.kotlin.hotfix;

import android.app.Application;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dalvik.system.PathClassLoader;

/**
 * create by max at 2020/11/23 22:28
 */

/**
 * readme:
 */

public class HotFix {


    /**
     * 1 获取当前应用的PathClassloader
     * <p>
     * 2 反射获取到DexPathList属性对象pathList
     * <p>
     * 3 反射修改pathList的dexElements
     * 3.1 把补丁包patch.dex转化为Element[]
     * 3.2 获得pathList的 dexElements 属性(old)
     * 3.3 path+old合并，并反射赋值给pathList的dexElements
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void installPatch(Application application, File... patchFile) {
        if (patchFile.length == 0) {
            return;
        }

        ClassLoader classLoader = application.getClassLoader();

        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(patchFile));

        File codeCacheDir = application.getCodeCacheDir();

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                ApplicationInfo applicationInfo = application.getPackageManager().getApplicationInfo("com.kotlin.hotfix", 0);
                String apkPath = applicationInfo.sourceDir;
                files.add(new File(apkPath));

                classLoader = NewClassLoaderInjector.inject(application,  classLoader, codeCacheDir, true, files);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return;
        }

        try {
            //2
            Field pathListField = ReflectUtil.findFileld(classLoader, "pathList");
            Object pathList = pathListField.get(classLoader);

            //3.1


            ArrayList<IOException> suppressedException = new ArrayList<>();

            Method makePathElements = ReflectUtil.findMethod(pathList, "makePathElements", List.class, File.class, List.class);
            //静态方法可以传null
            Object[] patchElements = (Object[]) makePathElements.invoke(null, files, codeCacheDir, suppressedException);

            ReflectUtil.findFileld(pathList, "dexElements");

            System.out.println("patchElements======== " + patchElements.length);

            //3.2
            Field dexElementsField = ReflectUtil.findFileld(pathList, "dexElements");
            Object[] dexElements = (Object[]) dexElementsField.get(pathList);

            System.out.println("dexElements======== " + dexElements.length);
            //3.3
            Class<?> type = dexElements.getClass().getComponentType();
            Object[] newElements = (Object[]) Array.newInstance(type, patchElements.length + dexElements.length);


            System.arraycopy(patchElements, 0, newElements, 0, patchElements.length);
            System.arraycopy(dexElements, 0, newElements, patchElements.length, dexElements.length);

            dexElementsField.set(pathList, newElements);
            System.out.println("done========newElements======== " + newElements.length);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
