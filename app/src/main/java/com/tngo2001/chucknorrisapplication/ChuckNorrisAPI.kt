package com.tngo2001.chucknorrisapplication

import com.google.gson.Gson
import com.tngo2001.chucknorrisapplication.model.CategoriesResponseModel
import com.tngo2001.chucknorrisapplication.model.JokeResponseModel
import com.tngo2001.chucknorrisapplication.model.JokesResponseModel
import com.tngo2001.chucknorrisapplication.model.NumberResponseModel
import okhttp3.*
import okio.IOException


class ChuckNorrisAPI {
    inline fun <reified T> getResponse(url: String, crossinline callback: (response: T) -> Unit) {
        val httpClient = OkHttpClient()
        val gson = Gson()

        val numberRequest = Request.Builder()
            .url(url)
            .build()
        httpClient.newCall(numberRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val responseString = response.body?.string()
                    val responseModel = gson.fromJson<T>(
                        responseString,
                        T::class.java
                    )
                    callback(responseModel)
                }
            }
        })
    }
}