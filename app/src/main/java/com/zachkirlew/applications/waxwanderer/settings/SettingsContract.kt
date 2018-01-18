package com.zachkirlew.applications.waxwanderer.settings

import android.net.Uri
import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User


interface SettingsContract {

    interface View: BaseView<Presenter> {

        fun showUserDetails(user : User,minMatchAge : Int,maxMatchAge : Int,matchGender : String?)
        fun showDateFormatted(date : String)

        fun showMessage(message : String)

        fun startStylesActivity()

    }

    interface Presenter : BasePresenter {

        fun loadUserDetails()

        fun getFormattedDate(year : Int, month : Int, day : Int)

        fun submitDetails(name : String, userGender : String, matchGender : String,minMatchAge : Int,maxMatchAge : Int)

        fun saveProfileImage(imageHoldUri : Uri?)
    }
}