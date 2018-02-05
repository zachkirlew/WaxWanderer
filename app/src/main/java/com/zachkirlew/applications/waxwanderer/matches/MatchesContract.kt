package com.zachkirlew.applications.waxwanderer.matches

import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User


interface MatchesContract {

    interface View: BaseView<Presenter> {

        fun addMatch(user: User?)

        fun removeMatch(userId : String)

        fun showNoMatchesView(show : Boolean)

        fun startMessagesActivity()
    }

    interface Presenter{

        fun checkMatchCount()

        fun loadMatches()

        fun deleteMatch(matchId : String)

    }
}