package com.zachkirlew.applications.waxwanderer.recommendations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.recommendation.RecommenderImp
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import io.reactivex.FlowableSubscriber
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
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val compositeDisposable = CompositeDisposable()

    override fun start() {
        loadRecommendedUsers()
    }

    override fun loadRecommendedUsers() {

        val currentUserId = mFirebaseAuth.currentUser?.uid

        val userRef = database.reference.child("users")

        recommender.recommendUserToUser(currentUserId!!, 5)
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

    private  fun checkIdList(userIds: List<String>){
        if (userIds.isEmpty()) {
            recommendationsView.showNoRecommendationsView()
        }
    }

    override fun likeUser(userId: String, position: Int) {
        val myRef = database.reference

        val currentUserId = mFirebaseAuth.currentUser?.uid
        val likeRef = myRef.child("likes").child(userId).child(currentUserId)

        RxFirebaseDatabase.observeSingleValueEvent(likeRef)
                .doOnSubscribe{compositeDisposable.add(it)}
                .subscribe { dataSnapshot ->
                    //liked user has current user in likes
                    if (dataSnapshot.exists()) {

                        val chatKey = myRef.child("chat").push().key

                        //add to both accounts and set chat id
                        myRef.child("matches").child(currentUserId)
                                .child(userId).setValue(chatKey)

                        myRef.child("matches").child(userId)
                                .child(currentUserId).setValue(chatKey)

                        //remove old like from liked user's account
                        likeRef.setValue(null)
                    }
                    //user doesn't have current user in their likes
                    else {
                        myRef.child("likes").child(currentUserId)
                                .child(userId).setValue(true)
                    }
                    recommendationsView.removeUser(position)
                }
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }

}