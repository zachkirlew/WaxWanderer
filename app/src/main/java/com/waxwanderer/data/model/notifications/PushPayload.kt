package com.waxwanderer.data.model.notifications

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.waxwanderer.data.model.notifications.Notification


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

    @SerializedName("data")
    @Expose
    var data :HashMap<String,Any>? = null
}