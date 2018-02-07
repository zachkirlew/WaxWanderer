package com.zachkirlew.applications.waxwanderer.recommendations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.recommendation.RecommenderImp
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class RecommendationsPresenter(private @NonNull var recommendationsView: RecommendationsContract.View,
                               private @NonNull val recommender: RecommenderImp) : RecommendationsContract.Presenter {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance().uid

    private val compositeDisposable = CompositeDisposable()

    private val RECOMMENDATION_COUNT : Long = 10

    override fun start() {
        loadRecommendedUsers()
    }

    override fun loadRecommendedUsers() {

        val userRef = database.reference.child("users")

        recommender.recommendUserToUser(userId!!, RECOMMENDATION_COUNT)
                .flatMap { userIds ->
                    Observable.fromIterable(userIds).flatMap { RxFirebaseDatabase.observeValueEvent(userRef.child(it),
                            { dataSnapshot -> dataSnapshot.getValue<User>(User::class.java)!! }).toObservable() } }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommendedObserver)
    }

    private val recommendedObserver = object : Observer<User>{
        override fun onSubscribe(d: Disposable) {
            compositeDisposable.add(d)
        }

        override fun onError(error: Throwable) {
            recommendationsView.showMessage(error.message)
        }

        override fun onComplete() {
        }

        override fun onNext(user: User) {
            recommendationsView.showRecommendedUser(user)
        }
    }

    override fun likeUser(likedUserId: String, position: Int) {

        val likeRef = database.reference.child("likes").child(likedUserId).child(userId)

        RxFirebaseDatabase.observeSingleValueEvent(likeRef)
                .doOnSubscribe{ compositeDisposable.add(it) }
                .subscribe ({ handleLikeLogic(it,likedUserId,position)})
    }

    private fun handleLikeLogic(dataSnapshot : DataSnapshot,likedUser: String,position: Int){
        val myRef = database.reference

        if (dataSnapshot.exists()) {
            //It's a match!
            recordMatch(myRef,likedUser)
            removeOldLike(myRef,likedUser)
        }
        //user doesn't have current user in their likes
        else {
            recordLike(myRef,likedUser)
        }
        recommendationsView.removeUser(position)
    }

    private fun recordMatch(myRef: DatabaseReference, likedUserId: String?){

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


    override fun dispose() {
        compositeDisposable.dispose()
    }

}