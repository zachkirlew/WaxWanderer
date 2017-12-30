package com.zachkirlew.applications.waxwanderer.data.model.discogs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//Model class for deserialisation of discogs api response
class DiscogsResponse {

    @SerializedName("pagination")
    @Expose
    var pagination: Pagination? = null

    @SerializedName("results")
    @Expose
    var results: List<VinylRelease>? = null
}