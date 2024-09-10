package com.example.navigationtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.example.navigationtest.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val moveAnimation = TranslateAnimation(0f,2000f,0f,0f)
        moveAnimation.duration = 6000
        moveAnimation.interpolator = LinearInterpolator()

        Handler().postDelayed({
            binding.loadProgBtn.animation = moveAnimation
            Handler().postDelayed({
                val intent = Intent(this, LogInActivity::class.java)
                startActivity(intent)
                this.finish()
            },4500)
        }, 0)
    }
}