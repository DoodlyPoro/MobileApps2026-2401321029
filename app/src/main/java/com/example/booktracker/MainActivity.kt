package com.example.booktracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.booktracker.ui.BookListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Зареждаме началния фрагмент в контейнера при стартиране
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, BookListFragment())
                .commit()
        }
    }
}