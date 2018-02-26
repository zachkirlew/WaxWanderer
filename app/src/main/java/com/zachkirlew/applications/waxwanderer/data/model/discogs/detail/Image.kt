package com.zachkirlew.applications.waxwanderer.data.model.discogs.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Image : Serializable {

    @SerializedName("height")
    @Expose
    var height: Int? = null

    @SerializedName("resource_url")
    @Expose
    var resourceUrl: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("uri")
    @Expose
    var uri: String? = null

    @SerializedName("uri150")
    @Expose
    var uri150: String? = null

    @SerializedName("width")
    @Expose
    var width: Int? = null

}