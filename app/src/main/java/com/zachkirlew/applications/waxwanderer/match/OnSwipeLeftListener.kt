package com.zachkirlew.applications.waxwanderer.match

import com.zachkirlew.applications.waxwanderer.data.model.User

interface OnSwipeLeftListener {
    fun onSwipedLeft(user : User)
}