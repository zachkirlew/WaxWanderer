package com.zachkirlew.applications.waxwanderer.message

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.data.model.Message
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease

interface MessageContract {

    interface View {

        fun showMessage(message : Message)
        fun showChooseRecordDialog(favourites :List<VinylRelease>)

        fun updateMessage(message : Message)

    }

    interface Presenter : BasePresenter {

        fun loadMatch(matchedUserId : String?)

        fun loadMessages()
        fun sendMessage(messageText : String, authorId : String,attachedRelease : VinylRelease?)

        fun loadFavourites()

        fun addRating(vinylId : Int,rating : Double,messageId : String)
    }
}