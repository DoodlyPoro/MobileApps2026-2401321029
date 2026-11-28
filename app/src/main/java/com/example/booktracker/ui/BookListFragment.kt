package com.example.booktracker.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booktracker.R
import com.example.booktracker.data.BookDatabase
import com.example.booktracker.data.entity.Category
import com.example.booktracker.data.repository.BookRepository
import com.example.booktracker.viewmodel.BookViewModel
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AlertDialog
import android.widget.Toast

class BookListFragment : Fragment() {
    private lateinit var viewModel: BookViewModel
    private lateinit var bookAdapter: BookAdapter
    private var categoriesList: List<Category> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val etSearch = view.findViewById<TextInputEditText>(R.id.etSearch)
        val spinnerFilterCategory = view.findViewById<Spinner>(R.id.spinnerFilterCategory)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewBooks)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        bookAdapter = BookAdapter(
            bookItems = emptyList(),
            onBookClick = { bookWithCategory ->
                viewModel.selectBook(bookWithCategory.book)
            },
            onDeleteClick = { bookWithCategory ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Book")
                    .setMessage("Are you sure you want to delete \"${bookWithCategory.book.title}\"?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.delete(bookWithCategory.book)
                        Toast.makeText(requireContext(), "Book deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onShareClick = { bookWithCategory ->
                val book = bookWithCategory.book
                val categoryName = bookWithCategory.category?.name ?: "Uncategorized"
                val shareText = buildString {
                    appendLine("Book: ${book.title}")
                    appendLine("Author: ${book.author}")
                    appendLine("Category: $categoryName")
                    appendLine("Status: ${book.status}")
                    appendLine("Rating: ${book.rating}/10")
                    if (book.notes.isNotBlank()) {
                        appendLine("Notes: ${book.notes}")
                    }
                }.trim()

                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Book: ${book.title}")
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                startActivity(Intent.createChooser(sendIntent, "Share book via"))
            }
        )
        recyclerView.adapter = bookAdapter

        val database = BookDatabase.getInstance(requireContext())
        val repository = BookRepository(database.bookDao(), database.categoryDao())
        val factory = BookViewModel.Factory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory)[BookViewModel::class.java]

        // Зареждане на категориите в спинъра за филтриране ("All Categories" като първа опция)
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoriesList = categories
            val options = listOf("All Categories") + categories.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                options
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            spinnerFilterCategory.adapter = adapter
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim().orEmpty()
                val current = viewModel.currentFilter()
                viewModel.setFilter(
                    current.copy(title = query.ifEmpty { null })
                )
            }
        })

        spinnerFilterCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemView: View?,
                position: Int,
                id: Long
            ) {
                val current = viewModel.currentFilter()
                val selectedCategoryIds = if (position <= 0) {
                    emptyList()
                } else {
                    val category = categoriesList.getOrNull(position - 1)
                    category?.let { listOf(it.id) } ?: emptyList()
                }
                viewModel.setFilter(
                    current.copy(categoryIds = selectedCategoryIds)
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { books ->
            books?.let {
                bookAdapter.updateBooks(it)
            }
        }
    }
}