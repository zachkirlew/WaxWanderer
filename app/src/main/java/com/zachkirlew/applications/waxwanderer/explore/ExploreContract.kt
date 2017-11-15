package com.zachkirlew.applications.waxwanderer.explore

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.VinylRelease


interface ExploreContract {

    interface View: BaseView<Presenter> {


        fun showVinylReleases(vinyls: List<VinylRelease>)

        fun showVinylReleaseDetailsUI()

    }

    interface Presenter : BasePresenter {

        fun loadVinylReleases()

        fun openTaskDetails()

    }
}