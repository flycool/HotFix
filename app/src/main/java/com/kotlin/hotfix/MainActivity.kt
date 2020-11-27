package com.kotlin.hotfix

import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Util.test()

        findViewById<TextView>(R.id.hello).setOnClickListener {

            val intent = Intent()
            intent.setComponent(
                ComponentName(
                    "com.kotlin.plugin2",
                    "com.kotlin.plugin2.PluginActivity"
                )
            )
            startActivity(intent)
        }
    }
}