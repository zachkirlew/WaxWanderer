package com.zachkirlew.applications.waxwanderer.data.model.discogs.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Identifier : Serializable {

    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("value")
    @Expose
    var value: String? = null

}