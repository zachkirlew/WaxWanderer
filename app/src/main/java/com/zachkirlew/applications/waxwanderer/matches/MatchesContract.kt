package com.zachkirlew.applications.waxwanderer.matches

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.VinylPreference
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


interface MatchesContract {

    interface View: BaseView<Presenter> {

        fun addMatch(match: User)
        fun showVinylReleaseDetailsUI()

        fun showNoMatchesView()
    }

    interface Presenter : BasePresenter {

        fun loadMatches()
    }
}