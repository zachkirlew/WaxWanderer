package com.zachkirlew.applications.waxwanderer.match

import com.zachkirlew.applications.waxwanderer.data.model.User

interface OnSwipeListener {
    fun onSwipedLeft(user : User, position : Int)
    fun onSwipedRight(user : User, position : Int)
}