package com.aniruddha.aadpractice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


/**
 * Acts as communication point between Repository and UI.
 * Survives configuration change.
 */
class WordViewModel(application: Application): AndroidViewModel(application) {
    private val mWordRepository: WordRepository
    // Getter method to gets all words, hides the implementation form UI.
    val mAllWords: LiveData<List<Word>>

    init {
        val wordDao = WordRoomDatabase.getDatabase(application, viewModelScope).wordDao()
        mWordRepository = WordRepository(wordDao)
        mAllWords = mWordRepository.mAllWordDao
    }

    // Not executing on UI thread.
    // ViewModels have a coroutine scope based on their lifecycle called.
    // viewModelScope used here, part of androidx library.
    fun insert(word: Word) = viewModelScope.launch {
        mWordRepository.insert(word)
    }
}