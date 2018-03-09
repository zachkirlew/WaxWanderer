package com.waxwanderer.leaderboard

import android.support.annotation.NonNull
import com.google.firebase.database.FirebaseDatabase
import com.waxwanderer.data.model.User
import com.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observable
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

        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeValueEvent(ref,{it.children.map { it.getValue(User::class.java)!! }})
                        .toObservable() else Observable.error(Exception("No internet connection")) }
                .doOnSubscribe {disposable = it}
                .subscribe({usersSortedByScore->leaderboardView.showUsers(usersSortedByScore)},
                        {error -> leaderboardView.showMessage(error.message)})
    }

    override fun dispose() {
        disposable?.dispose()
    }
}