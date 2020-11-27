package com.kotlin.plugin2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater

class PluginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Plugin", "pluginActivity start ok====: ");

//        setContentView(R.layout.activity_main)
        val view = LayoutInflater.from(contextWrapper).inflate(R.layout.activity_main, null)
        setContentView(view)
    }
}