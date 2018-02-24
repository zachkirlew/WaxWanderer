package com.zachkirlew.applications.waxwanderer.data.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PushPayload {

    @SerializedName("to")
    @Expose
    var to: String? = null
    @SerializedName("message_id")
    @Expose
    var messageId: String? = null
    @SerializedName("notification")
    @Expose
    var notification: Notification? = null

}