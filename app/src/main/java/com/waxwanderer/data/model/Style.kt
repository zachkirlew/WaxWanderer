package com.waxwanderer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Style : Serializable {

    @SerializedName("style")
    @Expose
    var style: String? = null

    @SerializedName("icon")
    @Expose
    var icon: String? = null

    @SerializedName("backgroundImage")
    @Expose
    var backgroundImage: String? = null

    var isSelected : Boolean = false
}