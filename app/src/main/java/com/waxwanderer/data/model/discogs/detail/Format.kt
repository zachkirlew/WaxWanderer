package com.waxwanderer.data.model.discogs.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Format : Serializable {

    @SerializedName("descriptions")
    @Expose
    var descriptions: List<String>? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("qty")
    @Expose
    var qty: String? = null

}