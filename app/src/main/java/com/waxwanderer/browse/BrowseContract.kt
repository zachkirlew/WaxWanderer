package com.waxwanderer.browse

import com.waxwanderer.base.BasePresenter
import com.waxwanderer.base.BaseView
import com.waxwanderer.data.model.Style

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