package com.example.booktracker.utils

data class ValidationRule(
    val regex: Regex,
    val errorMessage: String
)

interface Validatable {
    val validationErrors: MutableList<String>

    fun checkRule(value: String, rule: ValidationRule) {
        if (!rule.regex.matches((value.trim()))) {
            validationErrors.add(rule.errorMessage)
        }
    }

    fun validate(): Boolean
}