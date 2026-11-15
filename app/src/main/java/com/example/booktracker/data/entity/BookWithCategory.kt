package com.example.booktracker.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class BookWithCategory (
    @Embedded val book: Book,
    @Relation(parentColumn = "categoryId", entityColumn = "id")
    val category: Category?
)