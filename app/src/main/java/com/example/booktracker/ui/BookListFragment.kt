package com.example.booktracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.booktracker.R
import com.example.booktracker.data.BookDatabase
import com.example.booktracker.data.repository.BookRepository
import com.example.booktracker.viewmodel.BookViewModel

class BookListFragment : Fragment() {
    private lateinit var viewModel: BookViewModel
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onViewCreated(
        view:View,
        savedInstanceState:  Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewBooks)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        bookAdapter = BookAdapter(emptyList()) { book ->
            //to be added
        }
        recyclerView.adapter = bookAdapter

        val database = BookDatabase.getInstance(requireContext())
        val repository = BookRepository(database.bookDao(), database.categoryDao())
        val factory = BookViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[BookViewModel::class.java]
        viewModel = ViewModelProvider(this)[BookViewModel::class.java]

        viewModel.searchResults.observe(viewLifecycleOwner) { books ->
            books?.let {
                bookAdapter.updateBooks(it)
            }
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddBook)
        fab.setOnClickListener {
            //to be added
        }
    }
}