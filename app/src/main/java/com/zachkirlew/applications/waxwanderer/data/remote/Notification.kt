package com.zachkirlew.applications.waxwanderer.data.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Notification {

    @SerializedName("body")
    @Expose
    var body: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("sound")
    @Expose
    var sound: String? = null
    @SerializedName("priority")
    @Expose
    var priority: String? = null
}