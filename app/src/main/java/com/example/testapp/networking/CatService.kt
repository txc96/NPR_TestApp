package com.example.testapp.networking

import com.example.testapp.models.Cat
import okhttp3.Call
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CatService {
    //Fetch a random cat image
    @GET("/cat")
    suspend fun getCat(): ResponseBody

    //Fetch a number of random cat objects
    @GET("/api/cats")
    suspend fun getCats(@Query("limit") catCount: Int = 10): List<Cat>

    //Fetch a cat image by tag
    @GET("/cat/{tag}")
    suspend fun getCatByTag(@Path("tag") catTag: String): ResponseBody

    //Fetch a cat image by id
    @GET("/cat/{id}")
    suspend fun getCatByID(@Path("id") catID: String = ""): Cat

    //Fetch all listed api tags
    @GET("/api/tags")
    suspend fun  getCatTags(): List<String>
}