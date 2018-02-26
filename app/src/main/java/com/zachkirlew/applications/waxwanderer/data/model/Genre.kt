package com.zachkirlew.applications.waxwanderer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Genre : Serializable {

    @SerializedName("style")
    @Expose
    var style: String? = null

    @SerializedName("icon")
    @Expose
    var icon: String? = null

}