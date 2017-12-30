package com.zachkirlew.applications.waxwanderer.data.model.discogs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Urls : Serializable {

    @SerializedName("last")
    @Expose
    var last: String? = null

    @SerializedName("next")
    @Expose
    var next: String? = null

}