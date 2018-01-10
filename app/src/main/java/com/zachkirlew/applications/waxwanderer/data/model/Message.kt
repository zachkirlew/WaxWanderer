package com.zachkirlew.applications.waxwanderer.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Message {

    var userID: String? = null
    var username: String? = null
    var message: String? = null
    var timestamp: Long? = null
    var isNotification: Boolean? = null


}