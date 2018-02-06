package com.zachkirlew.applications.waxwanderer.main

import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.util.Log
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.base.OnSignOutListener
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


class MainPresenter(private @NonNull val mainView: MainContract.View): MainContract.Presenter {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = MainActivity::class.java.simpleName

    private var compositeDisposable : CompositeDisposable = CompositeDisposable()

    private val mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        // Check if user is signed in (non-null) and update UI accordingly.
        val user = firebaseAuth.currentUser

        if(user==null){
            Log.d(TAG, "User is Signed Out")
            mainView.startLoginActivity()
        }
        else{
            mainView.startExploreFragment()
            loadUserDetails()
        }
    }

    override fun loadUserDetails() {
        val displayName = mFirebaseAuth.currentUser?.displayName

        mainView.showDisplayName(displayName!!)
        loadUserProfilePic()
    }

    override fun removeDisposables(fragment: Fragment?) {
        if(fragment is OnSignOutListener)
            fragment.onSignOut()
    }

    override fun signOut() {
        dispose()
        LoginManager.getInstance().logOut()
        FirebaseAuth.getInstance().signOut()

        mainView.startLoginActivity()
    }

    private fun loadUserProfilePic() {
        val myRef = FirebaseDatabase.getInstance().reference

        val user = mFirebaseAuth.currentUser

        val ref = myRef.child("users").child(user?.uid).child("imageurl")

        RxFirebaseDatabase.observeSingleValueEvent(ref)
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe{dataSnapshot ->
                    if(dataSnapshot.exists()){
                        val imageUrl = dataSnapshot.value as String
                        mainView.showProfilePicture(imageUrl)
                    }
                }
    }

    override fun setAuthListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener)
    }

    override fun removeAuthListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener)
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }
}