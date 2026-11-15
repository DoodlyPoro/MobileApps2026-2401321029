package com.example.booktracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.booktracker.data.entity.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM book_categories ORDER BY name COLLATE NOCASE")
    fun getAll(): LiveData<List<Category>>

    @Insert
    suspend fun insertAll(categories: List<Category>)
}