package com.aniruddha.aadpractice

import androidx.lifecycle.LiveData


/**
 * Repository abstracts access to multiple data sources.
 * Acts like single point of contact from front-end and back-end.
 * Logic which decides from where data to be fetch should define in repository.
 */
class WordRepository(private val wordDao: WordDao) {
    // Wrapper method that return cached words as LiveData.
    val mAllWordDao: LiveData<List<Word>> = wordDao.getAllWords()

    // Wrapper for insert, should run on worker thread, otherwise app will crash.
    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }
}