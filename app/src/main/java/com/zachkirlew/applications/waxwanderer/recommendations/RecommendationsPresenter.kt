package com.zachkirlew.applications.waxwanderer.recommendations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.recommendation.RecommenderImp
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import io.reactivex.FlowableSubscriber
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class RecommendationsPresenter(private @NonNull var recommendationsView: RecommendationsContract.View,
                               private @NonNull val recommender: RecommenderImp) : RecommendationsContract.Presenter {

    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun start() {
        loadRecommendedUsers()
    }

    override fun loadRecommendedUsers() {
        val currentUserId = mFirebaseAuth.currentUser?.uid

        recommender.recommendUserToUser(currentUserId!!,10)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{userIds ->

                    if(userIds.isNotEmpty()){
                        loadUsersDetails(userIds)
                    }
                    else{
                        recommendationsView.showNoRecommendationsView()
                    }
                }
    }

    private fun loadUsersDetails(userIds: List<String>) {

        val userRef = database.reference.child("users")

        Flowable.fromIterable(userIds)
                .flatMap { RxFirebaseDatabase.observeValueEvent(userRef.child(it),{ dataSnapshot -> dataSnapshot.getValue<User>(User::class.java)!! })}
                .toObservable()
                .subscribe(object : Observer<User>{
                    override fun onNext(user: User) {
                        recommendationsView.showRecommendedUser(user)
                    }
                    override fun onSubscribe(d: Disposable) {
                    }
                    override fun onError(e: Throwable) {
                        recommendationsView.showMessage(e.message)
                    }
                    override fun onComplete() {
                    }
                })
    }
}