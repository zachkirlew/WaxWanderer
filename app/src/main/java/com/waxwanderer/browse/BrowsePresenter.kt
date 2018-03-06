package com.waxwanderer.browse

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.waxwanderer.data.model.Style
import com.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.lang.Exception

class BrowsePresenter(@NonNull private var browseView: BrowseContract.View): BrowseContract.Presenter {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val TAG = BrowsePresenter::class.java.simpleName

    private var compositeDisposable : CompositeDisposable? = null

    init {
        browseView.setPresenter(this)
    }

    override fun start() {
        compositeDisposable = CompositeDisposable()
        loadVinylPreferences()
        loadAllGenres()
    }

    override fun loadVinylPreferences(){

        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        val vinylRef = myRef.child("vinylPreferences").child(user?.uid)

        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeSingleValueEvent(vinylRef,{it.children.map { it.getValue<Style>(Style::class.java)!! }}).toObservable()   else Observable.error(Exception("No internet connection")) }
                .doOnSubscribe { compositeDisposable?.add(it) }
                .subscribe ({browseView.showStyles(it) },
                        {error ->
                            browseView.showMessage(error.message)
                            browseView.changeProgressBarVisibility(false)
                        })
    }

    override fun loadAllGenres(){

        val myRef = database.reference

        val vinylRef = myRef.child("genres")

        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeSingleValueEvent(vinylRef,{it.children.map { it.getValue<Style>(Style::class.java)!!}}).toObservable()   else Observable.error(Exception("No internet connection")) }
                .doOnSubscribe { compositeDisposable?.add(it) }
                .subscribe ({browseView.showAllGenres(it)  },
                        {error -> browseView.showMessage(error.message)
                            browseView.changeProgressBarVisibility(false)
                        })
    }

    override fun dispose() {
        compositeDisposable?.dispose()
    }
}