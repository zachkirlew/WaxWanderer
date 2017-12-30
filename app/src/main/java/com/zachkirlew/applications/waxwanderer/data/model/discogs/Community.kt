package com.zachkirlew.applications.waxwanderer.data.model.discogs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Community : Serializable {

        @SerializedName("want")
        @Expose
        var want: Int? = null

        @SerializedName("have")
        @Expose
        var have: Int? = null
}