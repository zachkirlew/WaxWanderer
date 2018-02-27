package com.zachkirlew.applications.waxwanderer.friends.requests

import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User

interface RequestsContract {

    interface View: BaseView<Presenter> {

        fun showRequest(user: User?)

        fun removeRequestFromList(userId : String)

        fun showNoRequestsView(show : Boolean)

        fun showFriendDialog(userName : String)
    }

    interface Presenter{

        fun loadRequests()

        fun acceptRequest(befriendedUser : User)

        fun deleteRequest(matchId : String)

        fun dispose()

    }
}