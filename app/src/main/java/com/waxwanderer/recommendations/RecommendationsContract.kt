package com.waxwanderer.recommendations

import com.waxwanderer.base.BasePresenter
import com.waxwanderer.data.model.User


interface RecommendationsContract {

    interface View {

        fun showMessage(message : String?)

        fun showNoRecommendationsView()

        fun showRecommendedUser(user : User)

        fun removeUser(position : Int)

    }

    interface Presenter : BasePresenter {

        fun loadRecommendedUsers()

        fun likeUser(likedUserId : String, position : Int)

        fun dispose()
    }
}