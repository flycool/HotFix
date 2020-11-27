package com.kotlin.hotfix

import android.app.Application
import android.content.Context
import com.kotlin.hotfix.plugin.HookUtil
import java.io.File

/**
 * create by max at 2020/11/24 11:25
 *
 */

class MyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            HotFix2.installPatch(this, "/sdcard/patch.dex")

            HotFix.installPatch(this,
                File("/sdcard/patch.dex"),
                File("/sdcard/plugin2-debug.apk")

            )

            HookUtil.hookAMS()
            HookUtil.hookHandler()

        }

    }
}