package com.zachkirlew.applications.waxwanderer.similar_users

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User


interface SimilarUsersContract {

    interface View: BaseView<Presenter> {

        fun showSimilarUsers(users: List<User>)
    }

    interface Presenter : BasePresenter {

        fun loadSimilarUsers(userInfo : User)
    }
}