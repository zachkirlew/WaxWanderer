package com.zachkirlew.applications.waxwanderer.leaderboard

import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User


interface LeaderBoardContract {

    interface View: BaseView<Presenter> {

        fun showUsers(users: List<User>)

    }

    interface Presenter{
        fun loadUsers()
    }
}