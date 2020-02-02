package com.aniruddha.aadpractice

import android.net.Uri
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


private const val TAG = "NetworkUtils : "
// Base URL for Books API.
private const val BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?"
// Parameter for the search string.
private const val QUERY_PARAM = "q"
// Parameter that limits search results.
private const val MAX_RESULTS = "maxResults"
// Parameter to filter by print type.
private const val PRINT_TYPE = "printType"

/**
 * Class that performs the network utility operations.
 */
class NetworkUtils {

    companion object {
        private lateinit var urlConnection: HttpURLConnection
        private lateinit var reader: BufferedReader
        private lateinit var bookJSONString: String

        fun getBookInfo(queryString: String): String {
            try {
                val uri : Uri? = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .appendQueryParameter(MAX_RESULTS, "10")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build()

                val request : URL = URL(uri.toString())

                urlConnection = request.openConnection() as HttpURLConnection
                urlConnection.let {
                    it.requestMethod = "GET"
                    it.connect()
                }

                // Get the InputStream.
                val inputStream: InputStream = urlConnection.inputStream

                // Create a buffered reader from that input stream.
                reader = BufferedReader(InputStreamReader(inputStream))

                // Use a StringBuilder to hold the incoming response.
                val builder = StringBuilder()

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                    // Since it's JSON, adding a newline isn't necessary (it won't
                    // affect parsing) but it does make debugging a *lot* easier
                    // if you print out the completed buffer for debugging.
                    builder.append("\n")
                }

                // replacement code for above.
                /*builder.apply {
                    // Since it's JSON, adding a newline isn't necessary (it won't
                    // affect parsing) but it does make debugging a *lot* easier
                    // if you print out the completed buffer for debugging.
                    while (reader.readLine().also { line = it } != null) {
                        this.append(line).append("\n")
                    }
                }*/

                if (builder.isEmpty()) {
                    // No point of parsing.
                    return ""
                }
                bookJSONString = builder.toString()


            } catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                if (::urlConnection.isInitialized) {
                    urlConnection.disconnect()
                }
                if (::reader.isInitialized) {
                    try {
                        reader.close();
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            Log.d(TAG, ": $bookJSONString")
            return bookJSONString
        }
    }
}