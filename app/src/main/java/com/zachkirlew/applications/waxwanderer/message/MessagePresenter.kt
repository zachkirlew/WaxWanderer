package com.zachkirlew.applications.waxwanderer.message

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zachkirlew.applications.waxwanderer.data.model.Message
import com.zachkirlew.applications.waxwanderer.data.model.RxChildEvent
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.recommendation.RecommenderImp
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MessagePresenter(private @NonNull var messageView: MessageContract.View,
                       private @NonNull val recommender: RecommenderImp) : MessageContract.Presenter {

    private val TAG = MessagePresenter::class.java.simpleName

    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var chatId : String

    private lateinit var recipientUid : String

    override fun loadMessages(matchedUserId: String) {

        recipientUid = matchedUserId

        val user = mFirebaseAuth.currentUser

        val myRef = database.reference

        val matchesRef = myRef.child("matches").child(user?.uid).child(matchedUserId)

        RxFirebaseDatabase.observeSingleValueEvent(matchesRef,{it.value as String})
                .doOnSubscribe { compositeDisposable.add(it) }
                .doOnSuccess { chatId = it }
                .toFlowable()
                .flatMap {chatId -> RxFirebaseDatabase.observeChildEvent(myRef.child("chat").child(chatId)) }
                .toObservable()
                .subscribe(messageObserver)
    }

    private val messageObserver = object : Observer<RxFirebaseChildEvent<DataSnapshot>>{
        override fun onNext(message: RxFirebaseChildEvent<DataSnapshot>) {
            messageView.addMessage(message)
        }

        override fun onSubscribe(d: Disposable) {
           compositeDisposable.add(d)
        }

        override fun onError(e: Throwable) {
            messageView.showError(e.message)
        }

        override fun onComplete() {
        }
    }


    override fun sendMessage(messageText: String, authorId: String,attachedRelease : VinylRelease?) {

        val key = database.reference.child("chat").child(chatId).push().key

        val message = Message(key, messageText, authorId, attachedRelease, System.currentTimeMillis().toString(),false,null)

        database.reference.child("chat").child(chatId).child(key).setValue(message)
    }

    override fun loadFavourites() {

        val myRef = database.reference
        val user = mFirebaseAuth.currentUser

        val ref = myRef.child("favourites").child(user?.uid)

        RxFirebaseDatabase.observeSingleValueEvent(ref,{it.children.map { it.getValue<VinylRelease>(VinylRelease::class.java)!! }})
                .doOnSubscribe { compositeDisposable.add(it)}
                .subscribe{userFavourites->messageView.showChooseRecordDialog(userFavourites)}
    }

    override fun addRating(vinylId: Int, rating: Double, messageId: String) {
        val user = mFirebaseAuth.currentUser
        val myRef = database.reference.child("chat").child(chatId).child(messageId)

        myRef.child("rating").setValue(rating)
        myRef.child("rated").setValue(true)

        addRatingToRecommender(user?.uid!!,vinylId,rating)

        println("rating is $rating")

        when(rating){
            3.00 -> awardPointsToUser(5)
            4.00 -> awardPointsToUser(7)
            5.00 -> awardPointsToUser(10)
        }
    }

    private fun addRatingToRecommender(uid: String, vinylId: Int, rating: Double) {

        //Rating rescaled to interval [-1.0,1.0], where -1.0 means the worst rating possible,
        // 0.0 means neutral, and 1.0 means absolutely positive rating.

        val scaledRating = (rating - 3) / 2
        recommender.addRating(uid, vinylId.toString(),scaledRating)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({it ->Log.i("MessagePresenter",it)},
                            {error -> Log.e(TAG,error.message)})
    }

    private fun awardPointsToUser(points : Int) {
        val userRef = database.reference.child("users").child(recipientUid)

        RxFirebaseDatabase.observeSingleValueEvent(userRef, User::class.java)
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe({ recipient -> val newScore = recipient.score + points
                                            userRef.child("score").setValue(newScore)
                            },
                        { throwable -> messageView.showError(throwable.message) })
    }


    override fun start() {}

    override fun dispose() {
        compositeDisposable.clear()
    }
}