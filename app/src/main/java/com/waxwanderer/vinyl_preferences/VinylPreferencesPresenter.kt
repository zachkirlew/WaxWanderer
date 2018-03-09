package com.waxwanderer.vinyl_preferences

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.waxwanderer.data.model.Style
import com.waxwanderer.data.model.User
import com.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable


class VinylPreferencesPresenter(private @NonNull var stylesView: VinylPreferencesContract.View) : VinylPreferencesContract.Presenter {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private var compositeDisposable : CompositeDisposable = CompositeDisposable()

    override fun loadGenres() {

        val database = database.reference
        val genresRef = database.child("styles")

        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) Observable.just(true) else Observable.error(Exception("No internet connection")) }
                .flatMap{RxFirebaseDatabase.observeValueEvent(genresRef,
                        {dataSnapshot->
                            val genreMap = dataSnapshot.children.asIterable()
                            genreMap.map { it.key }}).toObservable()}
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe({stylesView.showGenres(it)},
                        {error -> stylesView.showMessage(error.message)})
    }

    override fun loadStyles(genre: String) {

        val stylesRef = database.reference.child("styles").child(genre)
        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) Observable.just(true) else Observable.error(Exception("No internet connection")) }
                .flatMap{RxFirebaseDatabase.observeValueEvent(stylesRef,
                        {dataSnapshot->
                            val stylesMap = dataSnapshot.children.asIterable()
                            stylesMap.map { it.getValue<Style>(Style::class.java)!! } }).toObservable()}
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe({stylesView.showStyles(it)},
                        {error -> stylesView.showMessage(error.message)})
    }

    override fun savePreferences(selectedStyles: List<Style>) {

        val myRef = database.reference
        val user = mFirebaseAuth.currentUser

        myRef.child("vinylPreferences").child(user?.uid).setValue(selectedStyles)

        stylesView.startNextActivity()
    }

    override fun loadVinylPrefs() {
        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        val vinylRef = myRef.child("vinylPreferences").child(user?.uid)

        RxFirebaseDatabase.observeValueEvent(vinylRef,{it.children.map { it.getValue<Style>(Style::class.java)!!}}).toObservable()
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe({stylesView.showUsersPreferredStyles(it)})
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }

}