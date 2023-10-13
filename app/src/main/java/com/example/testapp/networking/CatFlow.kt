package com.example.testapp.networking

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.http.Query

/***
 * Helper class for Flow to pass ResponseBody, Cat, and List<String> objects
 */

class CatFlow(private val catService: CatService) : CatHelper {
    override fun getCat() = flow{
        emit(catService.getCat())
    }

    override fun getCats()= flow{
        emit(catService.getCats())
    }

    override fun getCatByID()= flow{
        emit(catService.getCatByID())
    }

    override fun getCatByTag(@Query(value = "tag") catTag: String) = flow{
        emit(catService.getCatByTag(catTag))
    }

    override fun getCatTags()= flow{
        emit(catService.getCatTags())
    }
}