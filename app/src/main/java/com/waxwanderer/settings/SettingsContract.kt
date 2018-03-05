package com.waxwanderer.settings

import android.net.Uri
import com.waxwanderer.base.BasePresenter
import com.waxwanderer.base.BaseView
import com.waxwanderer.data.model.User


interface SettingsContract {

    interface View: BaseView<Presenter> {

        fun showUserDetails(user : User, minMatchAge : Int, maxMatchAge : Int, matchGender : String?)
        fun showDateFormatted(date : String)
        fun startStylesActivity()
    }

    interface Presenter : BasePresenter {

        fun loadUserDetails()
        fun getFormattedDate(year : Int, month : Int, day : Int)
        fun submitDetails(name : String, userGender : String, matchGender : String,minMatchAge : Int,maxMatchAge : Int)
        fun saveProfileImage(imageHoldUri : Uri?)
        fun dispose()
    }
}