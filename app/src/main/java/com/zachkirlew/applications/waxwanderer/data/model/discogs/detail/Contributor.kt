package com.zachkirlew.applications.waxwanderer.data.model.discogs.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Contributor {

    @SerializedName("resource_url")
    @Expose
    var resourceUrl: String? = null
    @SerializedName("username")
    @Expose
    var username: String? = null

}