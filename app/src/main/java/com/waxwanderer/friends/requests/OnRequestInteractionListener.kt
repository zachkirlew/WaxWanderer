package com.waxwanderer.friends.requests

import com.waxwanderer.data.model.User

interface OnRequestInteractionListener {

    fun onRequestAccepted(user : User)
    fun onRequestDeleted(id : String?)
}