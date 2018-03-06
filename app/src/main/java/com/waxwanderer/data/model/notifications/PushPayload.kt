package com.waxwanderer.data.model.notifications

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PushPayload {

    @SerializedName("to")
    @Expose
    var to: String? = null

    @SerializedName("data")
    @Expose
    var data :HashMap<String,Any>? = null
}