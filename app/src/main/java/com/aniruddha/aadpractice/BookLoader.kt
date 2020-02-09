package com.aniruddha.aadpractice

import android.content.Context
import androidx.loader.content.AsyncTaskLoader

class BookLoader(context: Context,val queryData: String): AsyncTaskLoader<String>(context) {

    override fun onStartLoading() {
        super.onStartLoading()
        forceLoad()
    }

    override fun loadInBackground(): String? {
        return NetworkUtils.getBookInfo(queryData)
    }
}