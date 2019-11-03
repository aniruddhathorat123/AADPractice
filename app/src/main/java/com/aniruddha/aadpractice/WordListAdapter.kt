package com.aniruddha.aadpractice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordListAdapter(context: Context): RecyclerView.Adapter<WordListAdapter.WordViewHolder>() {
    private val layoutInflater : LayoutInflater = LayoutInflater.from(context)
    private var mWords = emptyList<Word>()

    // It inflates the item layout, and returns a ViewHolder with the layout and the adapter.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView : View = layoutInflater.inflate(R.layout.recyclerview_item,
            parent,
            false)
        return WordViewHolder(itemView)
    }

    // This method connects your data to the view holder.
    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.wordItemView.text = mWords[position].mWord
    }

    override fun getItemCount() = mWords.size

    fun setWords(words : List<Word>) {
        mWords = words
        notifyDataSetChanged()
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     * The return result from ViewHolder is used by onBindView.
     */
    inner class WordViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val wordItemView = itemView.findViewById<TextView>(R.id.wordTextView)!!
    }
}