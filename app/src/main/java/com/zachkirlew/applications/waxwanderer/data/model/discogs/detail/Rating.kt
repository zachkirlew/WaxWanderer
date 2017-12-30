package com.zachkirlew.applications.waxwanderer.data.model.discogs.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Rating {

    @SerializedName("average")
    @Expose
    var average: Double? = null
    @SerializedName("count")
    @Expose
    var count: Int? = null

}