package com.aniruddha.aadpractice

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import kotlinx.android.synthetic.main.activity_who_wrote_it.*
import org.json.JSONObject
import java.lang.ref.WeakReference

private const val TAG = "WhoWroteIt: "
private const val BOOK_LOADER_ID = 0
private const val BOOK_QURTY_KEY = "book_key"

class WhoWroteIt : AppCompatActivity(), LoaderManager.LoaderCallbacks<String> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_who_wrote_it)

        if (supportLoaderManager.getLoader<String>(BOOK_LOADER_ID) != null) {
            supportLoaderManager.initLoader(BOOK_LOADER_ID, null, this)
        }
    }

    /**
     * Search for the list of books
     */
    fun searchBooks(view: View) {
        hideInputFiled(view)
        val queryBook = bookInput.text.toString()

        if (isNetworkAvailable()) {
            if (queryBook.isNotBlank()) {
                val bundleData = Bundle()
                bundleData.putString(BOOK_QURTY_KEY, queryBook)
                //FetchBooks(titleText, authorText).execute(queryBook)
                supportLoaderManager.restartLoader(
                    BOOK_LOADER_ID,
                    bundleData,
                    this
                )
                authorText.text = ""
                titleText.text = getString(R.string.loading)
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.empty_input_submit_error,
                    Toast.LENGTH_LONG
                )
                    .show()
                clearFields()
            }
        }
    }

    /**
     * Hide the keyboard when user presses the search button.
     */
    private fun hideInputFiled(view: View) {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    /**
     * Check the network connectivity and returns true if network available otherwise return false.
     */
    private fun isNetworkAvailable(): Boolean {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            return true
        }
        Toast.makeText(applicationContext, R.string.network_connection_error, Toast.LENGTH_LONG)
            .show()
        clearFields()
        return false
    }

    /**
     * Clear the UI fields.
     */
    private fun clearFields() {
        titleText.text = ""
        authorText.text = ""
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<String> {
        var queryString = ""
        if (args != null && !args.getString(BOOK_QURTY_KEY).isNullOrBlank()) {
            queryString = args.getString(BOOK_QURTY_KEY)
        }
        return BookLoader(this, queryString)
    }

    override fun onLoadFinished(loader: Loader<String>, data: String?) {
        titleText.text = ""
        authorText.text = ""
        // convert the response into jJSONObject
        try {
            val itemsArray = JSONObject(data).getJSONArray("items")

            var i = 0
            var title = ""
            var authors = ""

            // Look for results in the items array, exiting
            // when both the title and author
            // are found or when all items have been checked.\
            while (i < itemsArray.length() && authors.isBlank() && title.isBlank()) {
                // Get the current item information
                val book = itemsArray.getJSONObject(i)
                val volumeInfo = book.getJSONObject("volumeInfo")

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                title = volumeInfo.getString("title")
                authors = volumeInfo.getString("authors")
                titleText.text = title
                authorText.text = authors
                i++
            }

        } catch (e: Exception) {
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results.
            e.printStackTrace()
        }
    }

    override fun onLoaderReset(loader: Loader<String>) {
        // no-op
    }
}

// removed AsyncTask class
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
            while (i < itemsArray.length() && authors.isBlank() && title.isBlank()) {
                // Get the current item information
                val book = itemsArray.getJSONObject(i)
                val volumeInfo = book.getJSONObject("volumeInfo")

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                title = volumeInfo.getString("title")
                authors = volumeInfo.getString("authors")
                titleText.get()?.text = title
                authorText.get()?.text = authors
                i++
            }

        } catch (e: Exception) {
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results.
            e.printStackTrace()
        }
    }
}

