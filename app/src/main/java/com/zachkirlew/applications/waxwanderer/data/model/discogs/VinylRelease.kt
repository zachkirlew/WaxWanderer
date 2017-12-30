package com.zachkirlew.applications.waxwanderer.data.model.discogs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.zachkirlew.applications.waxwanderer.data.model.discogs.Community


class VinylRelease {

    @SerializedName("thumb")
    @Expose
    var thumb: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("uri")
    @Expose
    var uri: String? = null

    @SerializedName("community")
    @Expose
    var community: Community? = null

    @SerializedName("resource_url")
    @Expose
    var resourceUrl: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("style")
    @Expose
    var style: List<String>? = null

    @SerializedName("format")
    @Expose
    var format: List<String>? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("barcode")
    @Expose
    var barcode: List<String>? = null


    @SerializedName("label")
    @Expose
    var label: List<String>? = null

    @SerializedName("catno")
    @Expose
    var catno: String? = null

    @SerializedName("year")
    @Expose
    var year: String? = null

    @SerializedName("genre")
    @Expose
    var genre: List<String>? = null
}