package com.example.booktracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.booktracker.R
import com.example.booktracker.data.entity.BookWithCategory

class BookAdapter(
    private var bookItems: List<BookWithCategory>,
    private val onBookClick: (BookWithCategory) -> Unit // Трябва да е BookWithCategory тук
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textTitle)
        val authorTextView: TextView = itemView.findViewById(R.id.textAuthor)
        val statusTextView: TextView = itemView.findViewById(R.id.textStatus)
        val ratingTextView: TextView = itemView.findViewById(R.id.textRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val item = bookItems[position] // item е от тип BookWithCategory

        holder.titleTextView.text = item.book.title
        holder.authorTextView.text = item.book.author
        holder.statusTextView.text = "Status: ${item.book.status}"
        holder.ratingTextView.text = "★ ${item.book.rating}/10"

        // При клик подаваме целият item (BookWithCategory), а НЕ item.book
        holder.itemView.setOnClickListener {
            onBookClick(item)
        }
    }

    override fun getItemCount(): Int = bookItems.size

    fun updateBooks(newItems: List<BookWithCategory>) {
        bookItems = newItems
        notifyDataSetChanged()
    }
}