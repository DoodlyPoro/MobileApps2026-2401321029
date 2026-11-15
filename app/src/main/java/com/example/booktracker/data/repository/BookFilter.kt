package com.example.booktracker.data.repository

data class BookFilter (
    val title: String? = null,
    val author: String? = null,
    val status: String? = null,
    val categoryIds: List<Long> = emptyList()
)