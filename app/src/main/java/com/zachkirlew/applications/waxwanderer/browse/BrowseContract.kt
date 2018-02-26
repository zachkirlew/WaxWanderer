package com.zachkirlew.applications.waxwanderer.browse

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.Style

interface BrowseContract {

    interface View : BaseView<Presenter> {

        fun showStyles(styles : List<Style>)

        fun showAllGenres(genres : List<Style>)

        fun startVinylPreferenceActivity()
    }

    interface Presenter : BasePresenter {

        fun loadVinylPreferences()

        fun loadAllGenres()

        fun dispose()

    }

}