package com.example.testapp.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitBuilder {
    /**
     * Helper class for setting up retrofit service
     */

    //URL hardcoded, should be fetched from a Resources folder
    private const val URL = "https://cataas.com"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val catService: CatService = getRetrofit().create(CatService::class.java)
}