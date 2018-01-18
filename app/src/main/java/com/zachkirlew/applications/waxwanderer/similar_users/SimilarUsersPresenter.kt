package com.zachkirlew.applications.waxwanderer.similar_users

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.local.UserPreferences
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.recommendation.FallbackRecommender
import com.zachkirlew.applications.waxwanderer.data.recommendation.RecommenderImp
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType
import java.util.*


class SimilarUsersPresenter(private @NonNull var similarUsersView: SimilarUsersContract.View,
                            private @NonNull val preferences: UserPreferences,
                            private @NonNull val recommender: RecommenderImp) : SimilarUsersContract.Presenter {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    lateinit var userInfo: User

    lateinit var user: FirebaseUser

    lateinit var matchedUserIds : List<String>

    init {
        similarUsersView.setPresenter(this)
    }

    override fun start() {

        val myRef = database.reference

        user = mFirebaseAuth.currentUser!!

        val userRef = myRef.child("users").child(user.uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                userInfo = dataSnapshot.getValue(User::class.java)!!
//                getFavouriteCount(userInfo.id!!)

                loadAlreadyMatched()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override  fun loadAlreadyMatched() {
        val myRef = database.reference
        val userRef = myRef.child("matches").child(user.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    matchedUserIds = dataSnapshot.children.map { it.key }
                    loadUsers()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

//    override fun getFavouriteCount(userId: String) {
//        val myRef = database.reference
//        val userRef = myRef.child("favourites").child(user.uid)
//
//        userRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if(dataSnapshot.exists()){
//                    val favouriteCount = dataSnapshot.children.count()
//
//                    loadUsers()
//
//                    //use recombee
//                    if(favouriteCount > 10)
//                        loadSimilarUsers()
//                    else
//                        loadUsers()
//                }
//                else
//                    loadUsers()
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//            }
//        })
//    }

    override fun loadSimilarUsers() {
        recommender.recommendUserToUser(user.uid,50)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<String>>{
                    override fun onNext(ids: List<String>) {

                    }
                    override fun onComplete() {
                    }
                    override fun onSubscribe(d: Disposable) {
                    }
                    override fun onError(e: Throwable) {
                    }
                })
    }

    override fun loadUsers() {

        val usersRef = FirebaseDatabase.getInstance().reference.child("users")

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val users = ArrayList<User>()

                for (child in dataSnapshot.children) {
                    child.getValue<User>(User::class.java)?.let { users.add(it) }
                }


                val fallbackRecommender = FallbackRecommender()

//                users.sortedWith(compareBy { fallbackRecommender.calculateJaccardSimilarity(it.) })

//                Collections.shuffle(users)
                filterSimilarUsers(users)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

//    private fun loadVinylPreferences(userId : String){
//
//        val usersRef = FirebaseDatabase.getInstance().reference.child("vinylPreference").child(userId)
//
//        usersRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//
//                val stylesList = dataSnapshot.children.map { it.value }
//
//                val fallbackRecommender = FallbackRecommender()
//
//                users.sortedWith(compareBy { fallbackRecommender.calculateJaccardSimilarity(it.) })
//
//                filterSimilarUsers(users)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//            }
//        })
//    }

    override fun filterSimilarUsers(similarUserList: List<User>) {

        val filterGender = preferences.matchGender

        val lowerAgeLimit = preferences.minMatchAge
        val upperAgeLimit = preferences.maxMatchAge

        //filter users by age and gender
        var filteredUsers = similarUserList.filter { user.uid != it.id }
                .filter { dobToAge(it.dob) in lowerAgeLimit..upperAgeLimit }

        //remove any users that that the current user has already matched with
                if(matchedUserIds.isNotEmpty()){
                    filteredUsers = filteredUsers.filter { !matchedUserIds.contains(it.id)}
                }
        filteredUsers.forEach { println(it.name) }

        when (filterGender) {
            "Males" -> {
                similarUsersView.showSimilarUsers(filteredUsers.filter { it.gender == "Male" })
            }
            "Females" -> {
                similarUsersView.showSimilarUsers(filteredUsers.filter { it.gender == "Female" })
            }
            else -> {
                similarUsersView.showSimilarUsers(filteredUsers)
            }
        }

    }

    override fun handleLike(likedUser: User) {

        val myRef = database.reference

        val userUid = mFirebaseAuth.currentUser?.uid

        myRef.child("likes").child(likedUser.id).child(userUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //liked user has current user in likes
                if(dataSnapshot.exists()){
                    //It's a match!
                    likedUser.name?.let { similarUsersView.showMatchDialog(it) }

                    val chatKey = myRef.child("chat").push().key

                    //add to both accounts and set chat id
                    myRef.child("matches").child(userUid)
                            .child(likedUser.id).setValue(chatKey)

                    myRef.child("matches").child(likedUser.id)
                            .child(userUid).setValue(chatKey)

                    //remove old like from liked user's account
                    myRef.child("likes").child(likedUser.id)
                            .child(userUid).setValue(null)
                }
                //user doesn't have current user in their likes
                else{
                    myRef.child("likes").child(userUid)
                            .child(likedUser.id).setValue(true)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun loadUserFavourites(userId: String?,viewPosition : Int) {

        val myRef = database.reference

        val ref = myRef.child("favourites").child(userId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val vinyls = ArrayList<VinylRelease>()

                for (child in dataSnapshot.children) {
                    child.getValue<VinylRelease>(VinylRelease::class.java)?.let { vinyls.add(it) }
                }

                if(vinyls.isEmpty())
                    similarUsersView.showNoUserFavourites()
                else
                    similarUsersView.showUserFavourites(vinyls,viewPosition)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }


    private fun dobToAge(date: Date?): Int {

        val birthDate = LocalDate(date)

        val todaysDate = LocalDate()

        return Period(birthDate, todaysDate, PeriodType.yearMonthDay()).years
    }


}