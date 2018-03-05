package com.waxwanderer.friends.search

import com.waxwanderer.data.model.User

interface OnRequestSentListener {
    fun onRequestSent(user : User)
}