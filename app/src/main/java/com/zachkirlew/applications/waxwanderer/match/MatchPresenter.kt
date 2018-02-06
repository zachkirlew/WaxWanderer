package com.zachkirlew.applications.waxwanderer.match

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.zachkirlew.applications.waxwanderer.data.local.UserPreferences
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType
import java.lang.Exception
import java.util.*


class MatchPresenter(private @NonNull var matchView: MatchContract.View,
                     private @NonNull val preferences: UserPreferences) : MatchContract.Presenter {

    private val TAG = MatchPresenter::class.java.simpleName

    private lateinit var compositeDisposable : CompositeDisposable
    private val database = FirebaseDatabase.getInstance()

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private var matchedUserIds : List<String>? = null

    init {
        matchView.setPresenter(this)
    }

    override fun start() {
        compositeDisposable= CompositeDisposable()
        getUsers()
    }

    private fun getUsers(){
        val lowerAgeLimit = preferences.minMatchAge
        val upperAgeLimit = preferences.maxMatchAge

        val myRef = database.reference

        val usersRef = myRef.child("users")
        val matchesRef = myRef.child("matches").child(userId)

        val query = getQuery(usersRef)

        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeValueEvent(matchesRef).toObservable() else Observable.error(Exception("No internet connection"))}
                .doOnNext { dataSnapshot -> matchedUserIds = dataSnapshot.children.map{it.key} }
                .flatMap { RxFirebaseDatabase.observeValueEvent(query,{dataSnapshot -> dataSnapshot.children.map { it.getValue<User>(User::class.java)!!}}).toObservable() }
                .map { list -> list.filter{dobToAge(it.dob) in lowerAgeLimit..upperAgeLimit} } //remove anyone not in user match age range
                .map {list -> list.filter{!matchedUserIds!!.contains(it.id)}} // filter any already matched users
                .map { list -> list.filter {userId != it.id} } //remove current user from list
                .subscribe(observer)
    }

    private val observer  = object : Observer<List<User>>{
        override fun onSubscribe(d: Disposable) {
            compositeDisposable.add(d)
        }

        override fun onError(e: Throwable) {
            matchView.showMessage(e.message)
        }

        override fun onComplete() {
            Log.i(TAG,"Potential matches retrieved")
        }

        override fun onNext(userList: List<User>) {
            matchView.showUsers(userList)
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

    override fun handleLike(likedUser: User) {

        val myRef = database.reference

        val likeRef = database.reference.child("likes").child(likedUser.id).child(userId)

        RxFirebaseDatabase.observeSingleValueEvent(likeRef)
                .doOnSubscribe{ compositeDisposable.add(it) }
                .subscribe { dataSnapshot ->
                    //liked user has current user in likes
                    if (dataSnapshot.exists()) {
                        //It's a match!
                        likedUser.name?.let { matchView.showMatchDialog(it) }

                        val chatKey = myRef.child("chat").push().key

                        //add to both accounts and set chat id
                        myRef.child("matches").child(userId)
                                .child(likedUser.id).setValue(chatKey)

                        myRef.child("matches").child(likedUser.id)
                                .child(userId).setValue(chatKey)

                        //remove old like from liked user's account
                        myRef.child("likes").child(likedUser.id)
                                .child(userId).setValue(null)
                    }
                    //user doesn't have current user in their likes
                    else {
                        myRef.child("likes").child(userId)
                                .child(likedUser.id).setValue(true)
                    }
                }
    }

    override fun loadUserFavourites(userId: String?,viewPosition : Int) {

        val myRef = database.reference

        val ref = myRef.child("favourites").child(userId)

        RxFirebaseDatabase.observeValueEvent(ref)
                .toObservable()
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        val vinyls = dataSnapshot.children.map { it.getValue<VinylRelease>(VinylRelease::class.java)!! }
                        matchView.showUserFavourites(vinyls, viewPosition)
                    } else
                        matchView.showNoUserFavourites()
                }
    }

    override fun loadVinylPreference(userId: String?, viewPosition: Int) {
        val myRef = database.reference

        val ref = myRef.child("vinylPreferences").child(userId)

        RxFirebaseDatabase.observeValueEvent(ref, { it.children.map { it.value as String } })
                .toObservable()
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe(
                        { styles ->
                            val commaSeparatedStyles = android.text.TextUtils.join(", ", styles)
                            matchView.showVinylPreference(commaSeparatedStyles, viewPosition)
                        },
                        { throwable -> matchView.showMessage(throwable.message) })
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