package com.aniruddha.aadpractice

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * - Room uses the DAO to issue queries to its database.
 * - Room provides compile-time checks of SQLite statements.
 * - Listing the entities class or classes creates corresponding tables in the database.
 * - if you modify the database schema, you need to update the version number
 *   and define how to handle migrations.
 */
@Database(entities = arrayOf(Word::class), version = 1, exportSchema = false)
abstract class WordRoomDatabase: RoomDatabase() {

    // Abstract getter method for DAO
    abstract fun wordDao() : WordDao

    /**
     * Populate the database in the background.
     */
    private class WordDatabaseCallback(
        private val scope : CoroutineScope
    ) : RoomDatabase.Callback() {
        // Populate words into DB only once when it first time created.
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE!!.let { wordRoomDatabase ->
                scope.launch {
                    val wordDao = wordRoomDatabase.wordDao()

                    wordDao.deleteAll()
                    val words = arrayOf("Dolphin", "Tiger", "Lion")
                    for (i in words) {
                        wordDao.insert(Word(i))
                    }
                }
            }
        }
    }

    companion object {

        // Writes to this field are immediately made visible to other threads using Volatile.
        @Volatile
        private var INSTANCE : WordRoomDatabase? = null

        // Usually, you only need one instance of the Room database for the whole app.
        fun getDatabase(context: Context, scope: CoroutineScope): WordRoomDatabase {
            if (INSTANCE != null) {
                return INSTANCE!!
            }
            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    "word_database")
                    // Wipes and rebuilds instead of migrating
                    // if no Migration object.
                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                return INSTANCE!!
            }
        }
    }
}
