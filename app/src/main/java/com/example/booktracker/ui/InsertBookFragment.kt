package com.example.booktracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.booktracker.R
import com.example.booktracker.data.BookDatabase
import com.example.booktracker.data.entity.Book
import com.example.booktracker.data.entity.Category
import com.example.booktracker.data.repository.BookRepository
import com.example.booktracker.viewmodel.BookViewModel
import com.google.android.material.textfield.TextInputEditText

class InsertBookFragment : Fragment() {

    private lateinit var viewModel: BookViewModel
    private var categoriesList: List<Category> = emptyList()
    private val statuses = listOf("To Read", "Reading", "Completed")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_insert_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<TextInputEditText>(R.id.etTitle)
        val etAuthor = view.findViewById<TextInputEditText>(R.id.etAuthor)
        val spinnerStatus = view.findViewById<Spinner>(R.id.spinnerStatus)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val etRating = view.findViewById<TextInputEditText>(R.id.etRating)
        val etNotes = view.findViewById<TextInputEditText>(R.id.etNotes)
        val tvValidationErrors = view.findViewById<TextView>(R.id.tvValidationErrors)
        val btnSaveBook = view.findViewById<Button>(R.id.btnSaveBook)

        // Споделен ViewModel на ниво Activity, за да вижда BookListFragment промените веднага
        val database = BookDatabase.getInstance(requireContext())
        val repository = BookRepository(database.bookDao(), database.categoryDao())
        val factory = BookViewModel.Factory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory)[BookViewModel::class.java]

        // Spinner за статус
        val statusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statuses
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerStatus.adapter = statusAdapter

        // Spinner за категории от базата
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoriesList = categories
            val names = categories.map { it.name }
            val categoryAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            spinnerCategory.adapter = categoryAdapter
        }

        btnSaveBook.setOnClickListener {
            tvValidationErrors.visibility = View.GONE
            tvValidationErrors.text = ""

            val title = etTitle.text?.toString()?.trim().orEmpty()
            val author = etAuthor.text?.toString()?.trim().orEmpty()
            val status = spinnerStatus.selectedItem?.toString() ?: statuses.first()
            val rating = etRating.text?.toString()?.trim()?.toIntOrNull() ?: 0
            val notes = etNotes.text?.toString()?.trim().orEmpty()
            val selectedCategoryId = categoriesList.getOrNull(spinnerCategory.selectedItemPosition)?.id

            val newBook = Book(
                title = title,
                author = author,
                status = status,
                rating = rating,
                notes = notes,
                categoryId = selectedCategoryId
            )

            // Използваме вградената Validatable валидация на Book
            if (!newBook.validate()) {
                tvValidationErrors.text = newBook.validationErrors.joinToString("\n")
                tvValidationErrors.visibility = View.VISIBLE
                return@setOnClickListener
            }

            viewModel.insert(newBook)

            // Изчистване на формата след успешен запис
            etTitle.text?.clear()
            etAuthor.text?.clear()
            etRating.text?.clear()
            etNotes.text?.clear()
            spinnerStatus.setSelection(0)
            if (spinnerCategory.adapter != null && spinnerCategory.adapter.count > 0) {
                spinnerCategory.setSelection(0)
            }
            Toast.makeText(requireContext(), "Book saved!", Toast.LENGTH_SHORT).show()
        }
    }
}