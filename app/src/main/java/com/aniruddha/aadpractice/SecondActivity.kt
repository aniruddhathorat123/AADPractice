package com.aniruddha.aadpractice

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_second.*


class SecondActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var wordViewModel: WordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val wordListAdapter = WordListAdapter(this)
        recyclerView = findViewById(R.id.recyclerView)
        addWord.setOnClickListener(this)
        recyclerView.apply {
            adapter = wordListAdapter
            layoutManager = LinearLayoutManager(this@SecondActivity)
        }

        wordViewModel = ViewModelProviders.of(this).get(WordViewModel::class.java)
        wordViewModel.mAllWords.observe(this, Observer { words ->
            words!!.let {
                wordListAdapter.setWords(it)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.addWord -> {
                val wordEditText = EditText(this)
                wordEditText.apply {
                    inputType = InputType.TYPE_CLASS_TEXT
                    hint = getString(R.string.hint_word)
                }

                // Dialog is used to add the word.
                AlertDialog.Builder(this).apply {
                    title = "New Word Dialog"
                    setMessage("Enter word to add")
                    setView(wordEditText)
                    setPositiveButton(android.R.string.yes) { dialog, which ->
                        val newWord = wordEditText.text.toString()
                        if (newWord.isNotBlank()) {
                            wordViewModel.insert(Word(newWord))
                        } else {
                            Toast.makeText(context, "Please Enter valid Word", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    setNegativeButton(android.R.string.cancel) { dialog, which ->
                    }
                }.create().show()
            }
        }
    }
}
