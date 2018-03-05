package com.waxwanderer.friends.search

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.waxwanderer.data.model.User
import com.waxwanderer.data.remote.notification.PushHelper
import com.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

class FriendsSearchPresenter(@NonNull private var friendsView: FriendsSearchContract.View,
                             @NonNull private val pushHelper : PushHelper) : FriendsSearchContract.Presenter {

    private val userId  = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance()
    private var compositeDisposable : CompositeDisposable? = null

    init {
        friendsView.setPresenter(this)
    }

    private lateinit var friends: List<String>

    override fun loadUsers(nameQuery: String?) {

        val query = capitalize(nameQuery)

        compositeDisposable = CompositeDisposable()

        val friendsRef = database.reference.child("matches").child(userId)

        val ref = database.reference.child("users").orderByChild("name").startAt(query).endAt(query + "\uf8ff")

        InternetConnectionUtil.isInternetOn()
                .flatMapSingle { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeSingleValueEvent(friendsRef).toSingle() else Single.error(Exception("No internet connection"))}
                .doOnNext { friends = it.children.map { it.key } }
                .flatMapSingle { RxFirebaseDatabase.observeSingleValueEvent(ref).toSingle() }
                .map { dataSnapshot->  dataSnapshot.children.asIterable().map { it.getValue<User>(User::class.java)!! }}
                .map { it.filter { it.id!=userId } }
                .map{it.filter{!friends.contains(it.id)}}
                .singleElement()
                .toSingle()
                .subscribe(observer)
    }

    override fun sendFriendRequest(user: User) {
        database.reference.child("friendRequests").child(user.id).child(userId).setValue(true)

        user.pushToken?.let { sendNotification(user.pushToken!!,
                "",
                "You received a friend request ${FirebaseAuth.getInstance().currentUser?.displayName} ") }
    }

    private fun sendNotification(token: String, title: String?, message: String?) {

        pushHelper.sendNotification(title,message,token,null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it -> Log.i("", it.string()) },
                        { error -> Log.e("", error.message) })
    }


    private val observer = object  : SingleObserver<List<User>> {
        override fun onSubscribe(d: Disposable) {
            compositeDisposable?.add(d)
        }

        override fun onSuccess(users: List<User>) {
            friendsView.setIsSearching(false)
            if(users.isEmpty())
                friendsView.showNoUsersView(true)
            else{
                friendsView.showNoUsersView(false)
                users.forEach { friendsView.showUser(it) }
            }
        }

        override fun onError(e: Throwable) {
            friendsView.setIsSearching(false)
            friendsView.showMessage(e.message)
        }
    }


    override fun dispose() {
        compositeDisposable?.dispose()
    }

    private fun capitalize(capString: String?): String {
        val capBuffer = StringBuffer()
        val capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString)
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase())
        }

        return capMatcher.appendTail(capBuffer).toString()
    }
}