package com.waxwanderer.message

import com.google.firebase.database.DataSnapshot
import com.waxwanderer.base.BasePresenter
import com.waxwanderer.data.model.User
import com.waxwanderer.data.model.discogs.VinylRelease
import durdinapps.rxfirebase2.RxFirebaseChildEvent

interface MessageContract {

    interface View {

        fun showError(message : String?)

        fun addMessage(message: RxFirebaseChildEvent<DataSnapshot>)

        fun showChooseRecordDialog(favourites :List<VinylRelease>)
    }

    interface Presenter : BasePresenter {

        fun loadMessages(matchedUser: User)

        fun sendMessage(messageText : String, authorId : String,attachedRelease : VinylRelease?)

        fun loadFavourites()

        fun addRating(vinylId : Int,rating : Double,messageId : String)

        fun dispose()
    }
}