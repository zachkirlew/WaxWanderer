package com.zachkirlew.applications.waxwanderer.friends.search

import com.zachkirlew.applications.waxwanderer.data.model.User

interface OnRequestSentListener {
    fun onRequestSent(user : User)
}