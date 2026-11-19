package com.example.booktracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.booktracker.data.dao.BookDao
import com.example.booktracker.data.dao.CategoryDao
import com.example.booktracker.data.entity.Book
import com.example.booktracker.data.entity.Category

@Database(
    entities = [Book::class, Category::class],
    version = 1,
    exportSchema = false
)

abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun categoryDao(): CategoryDao

    private class PrepopulateCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            PRESET_CATEGORIES.forEach { name ->
                db.execSQL("INSERT INTO book_categories (name) VALUES ('$name')")
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: BookDatabase? = null
        val PRESET_CATEGORIES = listOf(
            "Fantasy", "Romance", "Sci-fi", "Thriller", "Classical",
            "History", "Biography", "Motivational", "Psychology",
            "Horror", "Adventure", "Art", "Technology",
            "Philosophy", "Detective fiction", "Economy", "Children", "Other"
        )

        fun getInstance(context: Context): BookDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder (
                    context.applicationContext,
                    BookDatabase::class.java,
                    "books.db"
                )
                    .addCallback(PrepopulateCallback())
                    .build()
                    .also { INSTANCE = it }
            }
    }
}