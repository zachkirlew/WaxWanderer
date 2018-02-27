package com.zachkirlew.applications.waxwanderer.friends

import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User


interface FriendsContract {

    interface View: BaseView<Presenter> {

        fun showFriend(user: User?)

        fun removeFriendFromList(userId : String)

        fun showNoFriendsView(show : Boolean)

        fun startMessagesActivity()
    }

    interface Presenter{

        fun loadMatches()

        fun deleteMatch(matchId : String)

        fun dispose()

    }
}