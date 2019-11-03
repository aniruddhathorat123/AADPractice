package com.aniruddha.aadpractice

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


/**
 * DAO(Data Access Object) should be Interface or Abstract class.
 * DAO is a place where you specify SQL queries and associated them with method call.
 * By default, all queries (@Query) must be executed on a thread other than the main thread.
 */
@Dao
interface WordDao {

    // Suspend function that can be paused and resumed at a later time.
    // Used to execute long running operations without blocking.
    // Conflict strategy ignores a new word if it's exactly the same as one already in the list.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word)

    @Query(AADConstants.DELETE_ALL_QUERY)
    suspend fun deleteAll()

    @Query(AADConstants.SELECT_ALL_QUERY)
    fun getAllWords(): LiveData<List<Word>>
}