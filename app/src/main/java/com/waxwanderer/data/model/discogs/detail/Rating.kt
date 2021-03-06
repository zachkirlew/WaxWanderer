package com.waxwanderer.data.model.discogs.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Rating : Serializable {

    @SerializedName("average")
    @Expose
    var average: Double? = null
    @SerializedName("count")
    @Expose
    var count: Int? = null

}