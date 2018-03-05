package com.waxwanderer.data.model.discogs.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Label : Serializable {

    @SerializedName("catno")
    @Expose
    var catno: String? = null
    @SerializedName("entity_type")
    @Expose
    var entityType: String? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("resource_url")
    @Expose
    var resourceUrl: String? = null

}