package com.waxwanderer.match

import com.waxwanderer.base.BasePresenter
import com.waxwanderer.base.BaseView
import com.waxwanderer.data.model.User
import com.waxwanderer.data.model.UserCard


interface MatchContract {

    interface View: BaseView<Presenter> {

        fun startRecommendationsActivity()

        fun addUserCard(userCard : UserCard)

        fun showMatchDialog(likedUserName : String)

        fun getCardCount() : Int

        fun hideProgressBar()

        fun showProgressBar()

        fun showNoUsersView()

        override fun showMessage(message : String?)
    }

    interface Presenter : BasePresenter {

        fun getUsers()

        fun interactWithUser(user : User, isLike : Boolean)

        fun dispose()
    }
}