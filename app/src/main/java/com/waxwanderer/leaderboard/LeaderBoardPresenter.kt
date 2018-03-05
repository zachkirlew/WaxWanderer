package com.waxwanderer.leaderboard

import android.support.annotation.NonNull
import com.google.firebase.database.FirebaseDatabase
import com.waxwanderer.data.model.User
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.disposables.Disposable

class LeaderBoardPresenter(private @NonNull var leaderboardView: LeaderBoardContract.View) : LeaderBoardContract.Presenter  {

    private val database = FirebaseDatabase.getInstance()
    private var disposable : Disposable? = null

    init{
        leaderboardView.setPresenter(this)
    }

    override fun loadUsers() {

        val myRef = database.reference

        val ref = myRef.child("users").orderByChild("score").limitToLast(10)

        RxFirebaseDatabase.observeValueEvent(ref,{it.children.map { it.getValue(User::class.java)!! }})
                .toObservable()
                .doOnSubscribe {disposable = it}
                .subscribe{usersSortedByScore->leaderboardView.showUsers(usersSortedByScore)}
    }

    override fun dispose() {
        disposable?.dispose()
    }
}