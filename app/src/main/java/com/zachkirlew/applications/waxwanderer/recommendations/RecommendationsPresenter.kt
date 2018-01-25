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
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class RecommendationsPresenter(private @NonNull var recommendationsView: RecommendationsContract.View,
                               private @NonNull val recommender: RecommenderImp) : RecommendationsContract.Presenter {

    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var likes : List<String>

    override fun start() {
        loadLikes()
    }

    override fun loadLikes() {
        val currentUserId = mFirebaseAuth.currentUser?.uid
        val likesRef = database.reference.child("likes").child(currentUserId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    likes = dataSnapshot.children.map { it.key }
                    loadRecommendedUsers()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun loadRecommendedUsers() {
        val currentUserId = mFirebaseAuth.currentUser?.uid

        recommender.recommendUserToUser(currentUserId!!,5)
                .doOnError { error -> recommendationsView.showMessage(error.message)}
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{userIds ->

                        val userIdsFiltered = removeLikedUsers(userIds)

                        if (userIdsFiltered.isNotEmpty()) {
                            loadUsersDetails(userIdsFiltered)
                        } else {
                            recommendationsView.showNoRecommendationsView()
                        }
                }
    }

    override fun likeUser(userId: String, position: Int) {
        val myRef = database.reference
        val currentUserId = mFirebaseAuth.currentUser?.uid

        myRef.child("likes").child(userId).child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //liked user has current user in likes
                if(dataSnapshot.exists()){

                    val chatKey = myRef.child("chat").push().key

                    //add to both accounts and set chat id
                    myRef.child("matches").child(currentUserId)
                            .child(userId).setValue(chatKey)

                    myRef.child("matches").child(userId)
                            .child(currentUserId).setValue(chatKey)

                    //remove old like from liked user's account
                    myRef.child("likes").child(userId)
                            .child(currentUserId).setValue(null)
                }
                //user doesn't have current user in their likes
                else{
                    myRef.child("likes").child(currentUserId)
                            .child(userId).setValue(true)
                }
                recommendationsView.removeUser(position)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
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

    private fun removeLikedUsers(userIds : List<String>): List<String> {
        return userIds.filter { !likes.contains(it) }
    }
}