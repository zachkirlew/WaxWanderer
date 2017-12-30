package com.zachkirlew.applications.waxwanderer.data.model.discogs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Pagination {

    @SerializedName("per_page")
    @Expose
    var perPage: Int? = null

    @SerializedName("items")
    @Expose
    var items: Int? = null

    @SerializedName("page")
    @Expose
    var page: Int? = null

    @SerializedName("urls")
    @Expose
    var urls: Urls? = null

    @SerializedName("pages")
    @Expose
    var pages: Int? = null

}