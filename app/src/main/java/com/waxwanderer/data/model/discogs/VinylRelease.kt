package com.waxwanderer.data.model.discogs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class VinylRelease : Serializable {

    @SerializedName("thumb")
    @Expose
    var thumb: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("style")
    @Expose
    var style: List<String>? = null

    @SerializedName("genre")
    @Expose
    var genre: List<String>? = null

    @SerializedName("year")
    @Expose
    var year: String? = null

    @SerializedName("catno")
    @Expose
    var catno: String? = null

    @SerializedName("label")
    @Expose
    var label: List<String>? = null
}