package com.kotlin.hotfix;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * create by max at 2020/11/23 22:30
 */

public class ReflectUtil {

    public static Field findFileld(Object instance, String name) {
        Class<?> cls = instance.getClass();

        while (cls != Object.class) {
            try {
                Field f = cls.getDeclaredField(name);
                if (f != null) {
                    f.setAccessible(true);
                    return f;
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            cls = cls.getSuperclass();
        }

        throw new RuntimeException(name + "filed not found");
    }

    public static Method findMethod(Object instance, String name, Class<?>... parameterTypes) {
        Class<?> cls = instance.getClass();

        while (cls != Object.class) {
            try {
                Method m = cls.getDeclaredMethod(name, parameterTypes);
                if (m != null) {
                    m.setAccessible(true);
                    return m;
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            cls = cls.getSuperclass();
        }

        throw new RuntimeException(name + "method not found");
    }
}
