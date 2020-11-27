package com.kotlin.plugin2;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.Method;

/**
 * create by max at 2020/11/26 20:22
 */

public class ResourceUtil {

    private static String apkPath = "/sdcard/plugin2-debug.apk";

    public static Resources loadResource(Context context) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();

            Method addAssetPathMethod = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(assetManager, apkPath);

            Resources resources = context.getResources();
            return new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
