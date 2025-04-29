package com.dev.lokabudaya

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.content.Intent

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1000)
    }
}