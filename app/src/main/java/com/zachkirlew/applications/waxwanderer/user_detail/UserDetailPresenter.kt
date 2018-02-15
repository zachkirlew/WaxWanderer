package com.zachkirlew.applications.waxwanderer.user_detail

import android.support.annotation.NonNull
import com.google.firebase.database.FirebaseDatabase
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.disposables.CompositeDisposable


class UserDetailPresenter(@NonNull private var userDetailView: UserDetailContract.View) : UserDetailContract.Presenter  {

    private val database = FirebaseDatabase.getInstance()

    private val compositeDisposable : CompositeDisposable = CompositeDisposable()


    override fun loadUserFavourites(userId: String) {

        val myRef = database.reference

        val ref = myRef.child("favourites").child(userId).limitToLast(5)

        RxFirebaseDatabase.observeValueEvent(ref).toObservable()
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe{dataSnapshot->
                    if(dataSnapshot.exists()){
                        val vinyls = dataSnapshot.children.map { it.getValue<VinylRelease>(VinylRelease::class.java)!! }
                        userDetailView.showUserFavourites(vinyls)
                    }
                    else{
                        userDetailView.showNoFavouritesView()
                    }
                }
    }

    override fun loadUserStyles(userId: String) {
        val myRef = database.reference

        val ref = myRef.child("vinylPreferences").child(userId)

        RxFirebaseDatabase.observeValueEvent(ref).toObservable()
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe{dataSnapshot->
                    val preferredStyles = dataSnapshot.children.map { it.value as String }
                    val commaSeparatedStyles = android.text.TextUtils.join(", ", preferredStyles)

                    userDetailView.showUserStyles(commaSeparatedStyles)
                }
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }

    override fun start() {}


}