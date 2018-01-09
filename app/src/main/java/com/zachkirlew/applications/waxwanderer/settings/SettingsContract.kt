package com.zachkirlew.applications.waxwanderer.settings

import android.net.Uri
import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.VinylPreference
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


interface SettingsContract {

    interface View: BaseView<Presenter> {

        fun showUserDetails(user : User)

        fun showDateFormatted(date : String)

    }

    interface Presenter : BasePresenter {

        fun loadUserDetails()

        fun getFormattedDate(year : Int, month : Int, day : Int)

        fun submitDetails(name : String, userGender : String, matchGender : String, matchAge : String)

        fun saveProfileImage(imageHoldUri : Uri?)
    }
}