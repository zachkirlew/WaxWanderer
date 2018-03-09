package com.waxwanderer.friends.all

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.waxwanderer.data.model.RxChildEvent
import com.waxwanderer.data.model.User
import com.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseChildEvent.EventType.*
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction

class FriendsPresenter(@NonNull private var friendsView: FriendsContract.View) : FriendsContract.Presenter {

    private val userId  = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance()
    private var compositeDisposable : CompositeDisposable? = null

    init {
        friendsView.setPresenter(this)
    }

    override fun loadMatches() {

        compositeDisposable = CompositeDisposable()

        checkMatchCount()

        val myRef = database.reference

        val userRef = myRef.child("users")

        val ref = database.reference.child("matches").child(userId)

        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeChildEvent(ref).toObservable() else Observable.error(Exception("No internet connection")) }
                .doOnSubscribe { compositeDisposable?.add(it) }
                .flatMap { it -> Observable.zip(RxFirebaseDatabase.observeValueEvent(userRef.child(it.key)).toObservable(), Observable.just(it), zipFunction) }
                .subscribe(observer)
    }

    private val observer = object  : Observer<RxChildEvent<User>>{
        override fun onSubscribe(d: Disposable) {
            compositeDisposable?.add(d)
        }

        override fun onComplete() {
        }

        override fun onNext(userEvent: RxChildEvent<User>) {
            filterEvents(userEvent)
        }

        override fun onError(e: Throwable) {
            friendsView.showMessage(e.message)
        }
    }

    private val zipFunction = BiFunction<DataSnapshot, RxFirebaseChildEvent<DataSnapshot>, RxChildEvent<User>> { dataSnapshot, rxDataSnapshot ->
        val matchedUser = dataSnapshot.getValue(User::class.java)
        RxChildEvent(rxDataSnapshot, matchedUser)
    }

    private fun filterEvents(userEvent: RxChildEvent<User>) {
        when (userEvent.rxSnapShot.eventType) {

            ADDED -> friendsView.showFriend(userEvent.value)
            CHANGED -> TODO()
            REMOVED -> friendsView.removeFriendFromList(userEvent.value?.id!!)
            MOVED -> TODO()
        }
    }

    override fun deleteMatch(matchId: String) {

        val myRef = database.reference

        //remove match from users account first
        myRef.child("matches").child(userId)
                .child(matchId).setValue(null)

        //then remove from connections account
        myRef.child("matches").child(matchId)
                .child(userId).setValue(null)
    }

    private fun checkMatchCount() {

        val ref = database.reference.child("matches").child(userId)

        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeValueEvent(ref).toObservable() else Observable.error(Exception("No internet connection")) }
                .doOnSubscribe { compositeDisposable?.add(it)}
                .subscribe({ if (!it.exists()) friendsView.showNoFriendsView(true) else friendsView.showNoFriendsView(false) },
                        {friendsView.showMessage(it.message)})
    }

    override fun dispose() {
        compositeDisposable?.dispose()
    }
}