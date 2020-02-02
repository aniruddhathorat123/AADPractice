package com.aniruddha.aadpractice

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_who_wrote_it.*
import org.json.JSONObject
import java.lang.ref.WeakReference

private const val TAG = "WhoWroteIt: "

class WhoWroteIt : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_who_wrote_it)
    }

    fun searchBooks(view: View) {
        val queryBook = bookInput.text.toString()
    }
}

class FetchBooks(title: TextView, author: TextView) : AsyncTask<String, Void, String>() {
    private val titleText: WeakReference<TextView> = WeakReference(title)
    private val authorText: WeakReference<TextView> = WeakReference(author)

    override fun doInBackground(vararg params: String?): String {
        return NetworkUtils.getBookInfo(params[0] ?: "")
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        titleText.get()?.text = ""
        authorText.get()?.text = ""
        // convert the response into jJSONObject
        try {
            val itemsArray = JSONObject(result).getJSONArray("items")

            var i = 0
            var title = ""
            var authors = ""

            // Look for results in the items array, exiting
            // when both the title and author
            // are found or when all items have been checked.\
            while (i < itemsArray.length() && authors.isNullOrBlank() && title.isNullOrBlank()) {
                // Get the current item information
                val book = itemsArray.getJSONObject(i)
                val volumeInfo = book.getJSONObject("volumeInfo")

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                title = volumeInfo.getString("title")
                authors = volumeInfo.getString("authors")
                i++
            }

        } catch (e: Exception) {
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results.
            e.printStackTrace()
        }
    }

}

