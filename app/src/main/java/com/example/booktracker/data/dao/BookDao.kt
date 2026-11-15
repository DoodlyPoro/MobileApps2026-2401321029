package com.example.booktracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.booktracker.data.entity.Book
import com.example.booktracker.data.entity.BookWithCategory

@Dao
interface BookDao {
    @Insert
    suspend fun insert(book: Book): Long

    @Transaction
    @Query(
        """
        SELECT * FROM books
        WHERE (:title IS NULL OR title LIKE '%' || :title || '%')
          AND (:author IS NULL OR author LIKE '%' || :author || '%')
          AND (:status IS NULL OR status = :status)
          AND (:catCount = 0 OR categoryId IN (:categoryIds))
        ORDER BY title COLLATE NOCASE
        """
    )
    fun filter(
        title: String?,
        author: String?,
        status: String?,
        categoryIds: List<Long>,
        catCount: Int
    ): LiveData<List<BookWithCategory>>

    @Query("SELECT COUNT(*) FROM books")
    fun count(): LiveData<Int>
}