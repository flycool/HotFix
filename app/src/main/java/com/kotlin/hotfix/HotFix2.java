package com.kotlin.hotfix;

import android.app.Application;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * create by max at 2020/11/24 19:43
 */

public class HotFix2 {

    public static void installPatch(Application application, String patchPath) {
        if (patchPath == null) {
            return;
        }
        File cacheDir = application.getCacheDir();

        doInject(application, patchPath, cacheDir);

    }

    private static void doInject(Application application, String patchPath, File cacheDir) {

        PathClassLoader pathClassLoader = (PathClassLoader) application.getClassLoader();
        //实例化dexClassLoader用于加载补丁dex
        DexClassLoader dexClassLoader = new DexClassLoader(patchPath, cacheDir.getAbsolutePath(), null, pathClassLoader);

        try {
            //获取dexclassloader和pathclassloader的dexpathlist
            Object dexPathList = getPathList(dexClassLoader);
            Object pathPathList = getPathList(pathClassLoader);

            //获取补丁的elements数组和程序原来的elements
            Object dexElements = getDexElements(dexPathList);
            Object pathElements = getDexElements(pathPathList);

            //合并两个数组
            Object resultElements = combineArray(dexElements, pathElements);

            //将合并后的数组设置给PathClassLoader
            setField(pathPathList, pathPathList.getClass(), "dexElements", resultElements);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static Object combineArray(Object dexElements, Object pathElements) {
        int dexlength = Array.getLength(dexElements);
        int pathlength = Array.getLength(pathElements);
        Object newElements = Array.newInstance(dexElements.getClass().getComponentType(), dexlength + pathlength);

        System.arraycopy(dexElements, 0, newElements, 0, dexlength);
        System.arraycopy(pathElements, 0, newElements, dexlength, pathlength);

        return newElements;
    }

    private static void setField(Object pathPathList, Class<?> clazz, String fieldName, Object resultElements) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(pathPathList, resultElements);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //获得dexElements
    private static Object getDexElements(Object dexPathList) {
        return getField(dexPathList, dexPathList.getClass(), "dexElements");
    }

    private static Object getPathList(Object classLoader) throws ClassNotFoundException {
        return getField(classLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }


    private static Object getField(Object obj, Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
