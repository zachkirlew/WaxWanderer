package com.zachkirlew.applications.waxwanderer.matches

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.zachkirlew.applications.waxwanderer.data.model.RxChildEvent
import com.zachkirlew.applications.waxwanderer.data.model.User
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseChildEvent.EventType.*
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class MatchesPresenter(private @NonNull var matchesView: MatchesContract.View) : MatchesContract.Presenter {

    private val userId  = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance()

    init {
        matchesView.setPresenter(this)
    }

    override fun checkMatchCount() {

        val ref = database.reference.child("matches").child(userId)

        RxFirebaseDatabase.observeValueEvent(ref)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ if (!it.exists()) matchesView.showNoMatchesView(true) else matchesView.showNoMatchesView(false) }
    }


    override fun loadMatches() {

        val myRef = database.reference

        val userRef = myRef.child("users")

        val ref = database.reference.child("matches").child(userId)

        RxFirebaseDatabase.observeChildEvent(ref)
                .doOnNext { println("got ${it.key}") }
                .flatMap { it -> Flowable.zip(RxFirebaseDatabase.observeValueEvent(userRef.child(it.key)), Flowable.just(it), zipFunction) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { userEvent -> filterEvents(userEvent) }
    }


    private val zipFunction = BiFunction<DataSnapshot, RxFirebaseChildEvent<DataSnapshot>, RxChildEvent<User>> { dataSnapshot, rxDataSnapshot ->
        val matchedUser = dataSnapshot.getValue(User::class.java)
        RxChildEvent<User>(rxDataSnapshot, matchedUser)
    }

    private fun filterEvents(userEvent: RxChildEvent<User>) {
        when (userEvent.rxSnapShot.eventType) {

            ADDED -> matchesView.addMatch(userEvent.value)
            CHANGED -> TODO()
            REMOVED -> matchesView.removeMatch(userEvent.value?.id!!)
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


}