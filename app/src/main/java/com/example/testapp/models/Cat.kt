package com.example.testapp.models

import com.google.gson.annotations.SerializedName

data class Cat (
    /***
     * This model never ended up being used due to the how the cataas API returns random cats. (explained in CatViewModel)
     * The intention would have been to load 10 cats at a time, then fetch the image by id. A user could
     * have selected to favorite a cat, saving the cat object in a local DB. Which would have then been fetched
     * by their idea when browsing favorites
     */
    @SerializedName("_id")
    val catID: String = "",
    @SerializedName("tags")
    val catTags: List<String>,
    @SerializedName("owner")
    val catOwner: String = "",
    @SerializedName("createdAt")
    val catCreatedAt: String = "",
    @SerializedName("updatedAt")
    val catUpdatedAt: String = ""
)