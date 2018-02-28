package com.zachkirlew.applications.waxwanderer.recommend

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.zachkirlew.applications.waxwanderer.data.model.Message
import com.zachkirlew.applications.waxwanderer.data.model.RxChildEvent
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.remote.notification.PushHelper
import com.zachkirlew.applications.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import durdinapps.rxfirebase2.RxFirebaseChildEvent.EventType.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecommendVinylDialogPresenter(@NonNull private var dialogView: RecommendVinylDialogContract.View,
                                    @NonNull private val pushHelper : PushHelper) : RecommendVinylDialogContract.Presenter {

    private val userId  = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance()
    private var compositeDisposable : CompositeDisposable? = null

    init {
        dialogView.setPresenter(this)
    }

    override fun loadFriends() {

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
            dialogView.showMessage(e.message)
        }
    }

    override fun sendRecommendation(user: User, vinyl: VinylRelease) {

        val ref = database.reference.child("matches").child(userId).child(user.id)

        RxFirebaseDatabase.observeSingleValueEvent(ref,{it.value as String}).toSingle()
                .doOnSubscribe { compositeDisposable?.add(it) }
                .map{sendMessage(user,"",userId!!,vinyl,it)}
                .subscribe({it ->dialogView.showMessage("Recommendation sent")
                                dialogView.dismiss()
                                dispose()},
                            {error-> dialogView.showMessage(error.message)})
    }

    private fun sendMessage(recipient : User,messageText: String, authorId: String,attachedRelease : VinylRelease?,chatId : String) {

        val key = database.reference.child("chat").child(chatId).push().key

        val message = Message(key, messageText, authorId, attachedRelease, System.currentTimeMillis().toString(),false,null)

        database.reference.child("chat").child(chatId).child(key).setValue(message)

        recipient.pushToken?.let {

            pushHelper.sendNotification(FirebaseAuth.getInstance().currentUser?.displayName,messageText,recipient.pushToken!!,attachedRelease)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ Log.i("",it.string())},
                            {error -> Log.e("","errro: " + error.message)})
        }
    }

    private val zipFunction = BiFunction<DataSnapshot, RxFirebaseChildEvent<DataSnapshot>, RxChildEvent<User>> { dataSnapshot, rxDataSnapshot ->
        val matchedUser = dataSnapshot.getValue(User::class.java)
        RxChildEvent(rxDataSnapshot, matchedUser)
    }

    private fun filterEvents(userEvent: RxChildEvent<User>) {
        when (userEvent.rxSnapShot.eventType) {

            ADDED -> dialogView.showFriend(userEvent.value)
            CHANGED -> TODO()
            REMOVED -> TODO()
            MOVED -> TODO()
        }
    }

    private fun checkMatchCount() {

        val ref = database.reference.child("matches").child(userId)

        RxFirebaseDatabase.observeValueEvent(ref).toObservable()
                .doOnSubscribe { compositeDisposable?.add(it)}
                .subscribe{ if (!it.exists()) dialogView.showNoFriendsView(true) else dialogView.showNoFriendsView(false) }
    }

    override fun dispose() {
        compositeDisposable?.dispose()
    }
}