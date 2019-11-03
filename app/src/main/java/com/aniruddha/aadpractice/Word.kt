package com.aniruddha.aadpractice

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Annotation helps to identify how each part of Word class relates to entry in database.
 * @ColumnInfo(name = "<name-of-column-in-table>") : Specify the name of a column in the table,
 * if you want the column name to be different from the name of the member variable.
 */
@Entity(tableName = "word_table")
data class Word(@PrimaryKey
                @NonNull
                @ColumnInfo(name = "word")
                val mWord : String)