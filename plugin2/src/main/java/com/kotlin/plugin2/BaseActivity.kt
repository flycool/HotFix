package com.kotlin.plugin2

import android.content.ContextWrapper
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity

/**
 * create by max at 2020/11/26 20:09
 *
 */

open class BaseActivity : AppCompatActivity() {

    protected lateinit var contextWrapper: ContextWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resources = ResourceUtil.loadResource(application)

        contextWrapper = ContextThemeWrapper(baseContext, 0)

        val contextClass = contextWrapper.javaClass
        val resourceField = contextClass.getDeclaredField("mResources")
        resourceField.isAccessible = true
        resourceField.set(contextWrapper, resources)

    }

}