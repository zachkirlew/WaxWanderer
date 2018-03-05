package com.waxwanderer.data.model.discogs.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Video : Serializable {

    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("duration")
    @Expose
    var duration: Int? = null
    @SerializedName("embed")
    @Expose
    var embed: Boolean? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("uri")
    @Expose
    var uri: String? = null

}