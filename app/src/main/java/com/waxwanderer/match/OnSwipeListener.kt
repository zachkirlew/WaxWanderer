package com.waxwanderer.match

import com.waxwanderer.data.model.User

interface OnSwipeListener {
    fun onSwipedLeft(user : User, position : Int)
    fun onSwipedRight(user : User, position : Int)
}