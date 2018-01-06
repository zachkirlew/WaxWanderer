package com.zachkirlew.applications.waxwanderer.settings

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.VinylPreference
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


interface SettingsContract {

    interface View: BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {

        fun loadUserDetails()

    }
}