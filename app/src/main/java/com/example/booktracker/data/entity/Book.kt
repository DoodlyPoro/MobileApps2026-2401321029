package com.example.booktracker.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.booktracker.utils.Validatable
import com.example.booktracker.utils.ValidationRule

@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("categoryId")]
)
data class Book (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val status: String,
    val rating: Int,
    val notes: String,
    val categoryId: Long?
) : Validatable {
    override val validationErrors: MutableList<String> = mutableListOf()

    override fun validate(): Boolean {
        validationErrors.clear()

        val titleRule = ValidationRule(
            regex = Regex("^[a-zA-Z0-9\\s.,!?:'-]{2,100}$"),
            errorMessage = "The title must contain only valid characters (2-100)."
        )
        val authorRule = ValidationRule(
            regex = Regex("^[a-zA-Z\\s.-]{2,60}$"),
            errorMessage = "Author name must contain only letters (2-60)."
        )

        checkRule(title, titleRule)
        checkRule(author, authorRule)

        if (rating !in 1..10) {
            validationErrors.add("The rating must be between 1 and 10.")
        }

        return validationErrors.isEmpty()
    }
}