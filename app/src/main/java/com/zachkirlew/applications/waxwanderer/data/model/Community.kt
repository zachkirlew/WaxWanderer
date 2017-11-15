package com.zachkirlew.applications.waxwanderer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Community {

        @SerializedName("want")
        @Expose
        var want: Int? = null

        @SerializedName("have")
        @Expose
        var have: Int? = null
}