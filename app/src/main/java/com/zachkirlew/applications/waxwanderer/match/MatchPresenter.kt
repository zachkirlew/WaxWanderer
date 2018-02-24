package com.zachkirlew.applications.waxwanderer.match

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.zachkirlew.applications.waxwanderer.data.local.UserPreferences
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.UserCard
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType
import java.lang.Exception
import java.util.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage




class MatchPresenter(private @NonNull var matchView: MatchContract.View,
                     private @NonNull val preferences: UserPreferences) : MatchContract.Presenter {

    private val FCM_SERVER_CONNECTION = "@gcm.googleapis.com"

    private val TAG = MatchPresenter::class.java.simpleName

    private lateinit var compositeDisposable: CompositeDisposable
    private val database = FirebaseDatabase.getInstance()

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private var matchedUserIds: List<String>? = null

    init {
        matchView.setPresenter(this)
    }

    override fun start() {
        compositeDisposable = CompositeDisposable()
        getUsers()
    }

    private fun getUsers() {
        val lowerAgeLimit = preferences.minMatchAge
        val upperAgeLimit = preferences.maxMatchAge

        val myRef = database.reference

        val usersRef = myRef.child("users")
        val matchesRef = myRef.child("matches").child(userId)

        val userQuery = getQuery(usersRef)

        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeValueEvent(matchesRef).toObservable() else Observable.error(Exception("No internet connection")) }
                .doOnNext { dataSnapshot -> matchedUserIds = dataSnapshot.children.map { it.key } }
                .flatMap { RxFirebaseDatabase.observeValueEvent(userQuery, { dataSnapshot -> dataSnapshot.children.map { it.getValue<User>(User::class.java)!! } }).toObservable() }
                .map { list -> list.filter { dobToAge(it.dob) in lowerAgeLimit..upperAgeLimit } } //remove anyone not in current user's match age range preference
                .map { list -> list.filter { !matchedUserIds!!.contains(it.id) } } // filter out any already matched users
                .map { list -> list.filter { userId != it.id } } //remove current user from list
                .flatMap { list -> Observable.fromIterable(list) }
                .flatMap { user ->
                    Observable.zip(
                            Observable.just(user),
                            RxFirebaseDatabase.observeValueEvent(myRef.child("favourites").child(user.id).limitToLast(4)).toObservable(),
                            RxFirebaseDatabase.observeValueEvent(myRef.child("vinylPreferences").child(user.id)).toObservable()
                                    .doOnSubscribe { compositeDisposable.add(it) },
                            zipFunction)
                }
                .subscribe(observer)
    }

    private val zipFunction = Function3<User, DataSnapshot, DataSnapshot, UserCard> { user, favouritesSnapshot, vinylPrefSnapshot ->
        var vinyls: List<VinylRelease>? = null

        if (favouritesSnapshot.exists()) {
            vinyls = favouritesSnapshot.children.map { it.getValue<VinylRelease>(VinylRelease::class.java)!! }
        }

        val styles = vinylPrefSnapshot.children.map { it.value as String }

        UserCard(user, styles, vinyls)
    }

    private val observer = object : Observer<UserCard> {
        override fun onSubscribe(d: Disposable) {
            compositeDisposable.add(d)
        }

        override fun onError(e: Throwable) {
            matchView.showMessage(e.message)
        }

        override fun onComplete() {
            Log.i(TAG, "Potential matches retrieved")
        }

        override fun onNext(userCard: UserCard) {
            matchView.addUserCard(userCard)
        }
    }

    private fun getQuery(myRef: DatabaseReference): Query {
        val filterGender = preferences.matchGender

        return when (filterGender) {
            "Males" -> {
                myRef.orderByChild("gender").startAt("Male").endAt("Male")
            }
            "Females" -> {
                myRef.orderByChild("gender").startAt("Female").endAt("Female")
            }
            else -> {
                myRef
            }
        }
    }


    override fun likeUser(likedUser: User) {

        val likeRef = database.reference.child("likes").child(likedUser.id).child(userId)

        RxFirebaseDatabase.observeSingleValueEvent(likeRef)
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe({ handleLikeLogic(it, likedUser) })
    }

    private fun handleLikeLogic(dataSnapshot: DataSnapshot, likedUser: User) {
        val myRef = database.reference

        if (dataSnapshot.exists()) {
            //It's a match!
            likedUser.name?.let { matchView.showMatchDialog(it) }
            likedUser.pushToken?.let { sendNotification(likedUser.pushToken) }

            recordMatch(myRef, likedUser.id)
            removeOldLike(myRef, likedUser.id)
        }
        //user doesn't have current user in their likes
        else {
            recordLike(myRef, likedUser.id)
        }
    }

    private fun recordMatch(myRef: DatabaseReference, likedUserId: String?) {

        val chatKey = myRef.child("chat").push().key

        //add to both accounts and set chat id
        myRef.child("matches").child(userId)
                .child(likedUserId).setValue(chatKey)

        myRef.child("matches").child(likedUserId)
                .child(userId).setValue(chatKey)
    }

    private fun removeOldLike(myRef: DatabaseReference, likedUserId: String?) {
        myRef.child("likes").child(likedUserId)
                .child(userId).setValue(null)
    }

    private fun recordLike(myRef: DatabaseReference, likedUserId: String?) {
        myRef.child("likes").child(userId)
                .child(likedUserId).setValue(true)
    }


    private fun sendNotification(token: String?) {
        // This registration token comes from the client FCM SDKs.

        val fm = FirebaseMessaging.getInstance()
        fm.send(RemoteMessage.Builder("1087890678356@gcm.googleapis.com")
                .setMessageId(Integer.toString(Random().nextInt()))
                .addData("my_message", "Hello World")
                .addData("my_action", "SAY_HELLO")
                .build())
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }

    private fun dobToAge(date: Date?): Int {

        val birthDate = LocalDate(date)

        val todaysDate = LocalDate()

        return Period(birthDate, todaysDate, PeriodType.yearMonthDay()).years
    }
}