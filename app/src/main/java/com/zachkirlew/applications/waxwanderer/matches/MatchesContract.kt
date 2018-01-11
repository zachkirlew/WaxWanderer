package com.zachkirlew.applications.waxwanderer.matches

import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User


interface MatchesContract {

    interface View: BaseView<Presenter> {

        fun addMatch(match: User)

        fun showNoMatchesView()

        fun startMessagesActivity()

    }

    interface Presenter{

        fun loadMatches()

        fun deleteMatch(match : User)
    }
}