package com.example.testapp.networking

import com.example.testapp.models.Cat
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.http.Query

interface CatHelper {

    /***
     * Helper class for endpoints
     */

    fun getCat(): Flow<ResponseBody>

    //Not used
    fun getCats(/*@Query("limit") int catCount*/): Flow<List<Cat>>

    //Not used
    fun getCatByID(/*@Query("id") String catID*/): Flow<Cat>

    fun getCatByTag(@Query("tag") catTag: String): Flow<ResponseBody>

    fun  getCatTags(): Flow<List<String>>
}