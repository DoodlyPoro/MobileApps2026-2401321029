package com.example.booktracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.booktracker.data.entity.Book
import com.example.booktracker.data.entity.BookWithCategory
import com.example.booktracker.data.entity.Category
import com.example.booktracker.data.repository.BookFilter
import com.example.booktracker.data.repository.BookRepository
import kotlinx.coroutines.launch

class BookViewModel (private  val repository: BookRepository) : ViewModel() {
    val categories: LiveData<List<Category>> = repository.categories

    private val filter = MutableLiveData(BookFilter())

    val searchResults: LiveData<List<BookWithCategory>> =
        filter.switchMap { repository.search(it) }

    private val _selectedBook = MutableLiveData<Book?>(null)
    val selectedBook: LiveData<Book?> = _selectedBook

    fun selectBook(book: Book?) {
        _selectedBook.value = book
    }

    fun clearSelection() {
        _selectedBook.value = null
    }

    fun update(book: Book) {
        viewModelScope.launch {
            repository.update(book)
            _selectedBook.value = null
        }
    }

    fun setFilter(newFilter: BookFilter) {
        filter.value = newFilter
    }

    fun currentFilter(): BookFilter = filter.value ?: BookFilter()

    fun insert(book: Book) {
        viewModelScope.launch { repository.insert(book) }
    }

    class Factory(private val repository: BookRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) : T {
            if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BookViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}