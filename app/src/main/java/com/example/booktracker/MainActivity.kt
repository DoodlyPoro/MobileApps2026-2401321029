package com.example.booktracker

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val savedMode = prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedMode)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnToggleTheme = findViewById<MaterialButton>(R.id.btnToggleTheme)

        fun updateButtonLabel() {
            val isNight = when (AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_YES -> true
                AppCompatDelegate.MODE_NIGHT_NO -> false
                else -> {
                    val uiNight = resources.configuration.uiMode and
                            Configuration.UI_MODE_NIGHT_MASK
                    uiNight == Configuration.UI_MODE_NIGHT_YES
                }
            }
            btnToggleTheme.text = if (isNight) "Light Mode" else "Dark Mode"
        }

        updateButtonLabel()

        btnToggleTheme.setOnClickListener {
            val currentlyNight = btnToggleTheme.text == "Light Mode"
            val nextMode = if (currentlyNight) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            prefs.edit().putInt("night_mode", nextMode).apply()
            AppCompatDelegate.setDefaultNightMode(nextMode)
            updateButtonLabel()
        }
    }
}