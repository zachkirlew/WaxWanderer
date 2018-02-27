package com.zachkirlew.applications.waxwanderer.friends.requests

import com.zachkirlew.applications.waxwanderer.data.model.User

interface OnRequestInteractionListener {

    fun onRequestAccepted(user : User)
    fun onRequestDeleted(id : String?)
}