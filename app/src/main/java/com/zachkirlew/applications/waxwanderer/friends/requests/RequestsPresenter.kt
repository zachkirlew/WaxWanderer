package com.zachkirlew.applications.waxwanderer.friends.requests

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.zachkirlew.applications.waxwanderer.data.model.RxChildEvent
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.notifications.Notification
import com.zachkirlew.applications.waxwanderer.data.model.notifications.PushPayload
import com.zachkirlew.applications.waxwanderer.data.remote.notification.PushHelper
import com.zachkirlew.applications.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class RequestsPresenter(@NonNull private var requestsView: RequestsContract.View,
                        @NonNull private val pushHelper : PushHelper) : RequestsContract.Presenter {

    private val userId  = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance()
    private var compositeDisposable : CompositeDisposable? = null

    init {
        requestsView.setPresenter(this)
    }

    override fun loadRequests() {

        compositeDisposable = CompositeDisposable()

        checkMatchCount()

        val myRef = database.reference

        val userRef = myRef.child("users")

        val ref = database.reference.child("friendRequests").child(userId)

        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeChildEvent(ref).toObservable() else Observable.error(Exception("No internet connection")) }
                .doOnSubscribe { compositeDisposable?.add(it) }
                .flatMap { it -> Observable.zip(RxFirebaseDatabase.observeValueEvent(userRef.child(it.key)).toObservable(), Observable.just(it), zipFunction) }
                .subscribe(observer)
    }

    private val observer = object  : Observer<RxChildEvent<User>> {
        override fun onSubscribe(d: Disposable) {
            compositeDisposable?.add(d)
        }

        override fun onComplete() {
        }

        override fun onNext(userEvent: RxChildEvent<User>) {
            filterEvents(userEvent)
        }

        override fun onError(e: Throwable) {
            requestsView.showMessage(e.message)
        }
    }

    private val zipFunction = BiFunction<DataSnapshot, RxFirebaseChildEvent<DataSnapshot>, RxChildEvent<User>> { dataSnapshot, rxDataSnapshot ->
        val matchedUser = dataSnapshot.getValue(User::class.java)
        RxChildEvent(rxDataSnapshot, matchedUser)
    }

    private fun filterEvents(userEvent: RxChildEvent<User>) {
        when (userEvent.rxSnapShot.eventType) {

            RxFirebaseChildEvent.EventType.ADDED -> requestsView.showRequest(userEvent.value)
            RxFirebaseChildEvent.EventType.CHANGED -> TODO()
            RxFirebaseChildEvent.EventType.REMOVED -> requestsView.removeRequestFromList(userEvent.value?.id!!)
            RxFirebaseChildEvent.EventType.MOVED -> TODO()
        }
    }

    override fun acceptRequest(befriendedUser: User) {
        val myRef = database.reference

        befriendedUser.name?.let { requestsView.showFriendDialog(it) }
        befriendedUser.pushToken?.let { sendNotification(befriendedUser.pushToken!!,
                "Congratulations",
                "You became friends with ${FirebaseAuth.getInstance().currentUser?.displayName} ") }

        val chatKey = myRef.child("chat").push().key

        //add to both accounts and set chat id
        myRef.child("matches").child(userId)
                .child(befriendedUser.id).setValue(chatKey)

        myRef.child("matches").child(befriendedUser.id)
                .child(userId).setValue(chatKey)

        myRef.child("friendRequests").child(userId)
                .child(befriendedUser.id).setValue(null)
    }


    override fun deleteRequest(matchId: String) {

        val myRef = database.reference

        //remove match from users account first
        myRef.child("friendRequests").child(userId)
                .child(matchId).setValue(null)
    }

    private fun sendNotification(token: String, title: String?, message: String?) {

        pushHelper.sendNotification(title,message,token,null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it -> Log.i("RequestPres", it.string()) },
                        { error -> Log.e("RequestPres", error.message) })
    }

    private fun checkMatchCount() {

        val ref = database.reference.child("friendRequests").child(userId)

        RxFirebaseDatabase.observeValueEvent(ref).toObservable()
                .doOnSubscribe { compositeDisposable?.add(it)}
                .subscribe{ if (!it.exists()) requestsView.showNoRequestsView(true) else requestsView.showNoRequestsView(false) }
    }

    override fun dispose() {
        compositeDisposable?.dispose()
    }
}