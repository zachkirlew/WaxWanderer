package com.zachkirlew.applications.waxwanderer.data.model.discogs.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Artist : Serializable {

    @SerializedName("anv")
    @Expose
    var anv: String? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("join")
    @Expose
    var join: String? = null
    @SerializedName("name")
    @Expose
    lateinit var name: String
    @SerializedName("resource_url")
    @Expose
    var resourceUrl: String? = null
    @SerializedName("role")
    @Expose
    var role: String? = null
    @SerializedName("tracks")
    @Expose
    var tracks: String? = null

}