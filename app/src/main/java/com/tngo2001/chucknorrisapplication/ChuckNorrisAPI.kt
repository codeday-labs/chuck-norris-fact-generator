package com.tngo2001.chucknorrisapplication

import com.google.gson.Gson
import com.tngo2001.chucknorrisapplication.model.CategoriesResponseModel
import com.tngo2001.chucknorrisapplication.model.JokeResponseModel
import com.tngo2001.chucknorrisapplication.model.JokesResponseModel
import com.tngo2001.chucknorrisapplication.model.NumberResponseModel
import okhttp3.*
import okio.IOException


class ChuckNorrisAPI {
    private val httpClient = OkHttpClient()
    val gson = Gson()

    fun getNumber(callback: (response: NumberResponseModel) -> Unit) {
        val numberRequest = Request.Builder()
            .url("http://api.icndb.com/jokes/count")
            .build()
        httpClient.newCall(numberRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val responseString = response.body?.string()
                    val numberResponseModel = gson.fromJson<NumberResponseModel>(
                        responseString,
                        NumberResponseModel::class.java
                    )
                    callback(numberResponseModel)
                }
            }
        })
    }

    fun getCategories(callback: (response: CategoriesResponseModel) -> Unit) {
        val categoryRequest = Request.Builder()
            .url("http://api.icndb.com/categories")
            .build()
        httpClient.newCall(categoryRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val responseString = response.body?.string()
                    val categoriesResponseModel = gson.fromJson<CategoriesResponseModel>(
                        responseString,
                        CategoriesResponseModel::class.java
                    )
                    callback(categoriesResponseModel)
                }
            }
        })
    }

    fun getJoke(url: String, callback: (response: JokeResponseModel) -> Unit) {
        val jokeRequest = Request.Builder()
            .url(url)
            .build()

        httpClient.newCall(jokeRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val responseString = response.body?.string()
                    val jokeResponseModel = gson.fromJson<JokeResponseModel>(
                        responseString,
                        JokeResponseModel::class.java
                    )
                    callback(jokeResponseModel)
                }
            }
        })
    }

    fun getJokes(url: String, callback: (response: JokesResponseModel) -> Unit) {
        val jokesRequest = Request.Builder()
            .url(url)
            .build()

        httpClient.newCall(jokesRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val responseString = response.body?.string()
                    val jokesResponseModel = gson.fromJson<JokesResponseModel>(
                        responseString,
                        JokesResponseModel::class.java
                    )
                    callback(jokesResponseModel)
                }
            }
        })
    }
}