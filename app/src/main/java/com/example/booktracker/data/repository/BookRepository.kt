package com.example.booktracker.data.repository

import androidx.lifecycle.LiveData
import com.example.booktracker.data.dao.BookDao
import com.example.booktracker.data.dao.CategoryDao
import com.example.booktracker.data.entity.Book
import com.example.booktracker.data.entity.BookWithCategory
import com.example.booktracker.data.entity.Category

class BookRepository (
    private val bookDao: BookDao,
    private val categoryDao: CategoryDao
){
    val categories: LiveData<List<Category>> = categoryDao.getAll()

    val bookCount: LiveData<Int> = bookDao.count()

    fun search(filter: BookFilter): LiveData<List<BookWithCategory>> {
        val ids = filter.categoryIds.ifEmpty { listOf(NO_CATEGORY_ID) }
        return bookDao.filter(
            title = filter.title.blankToNull(),
            author = filter.author.blankToNull(),
            status = filter.status.blankToNull(),
            categoryIds = ids,
            catCount = filter.categoryIds.size
        )
    }

    suspend fun insert(book: Book): Long = bookDao.insert(book)

    private fun String?.blankToNull(): String? = this?.trim()?.ifBlank { null }
    private companion object {
        const val NO_CATEGORY_ID = -1L
    }
}