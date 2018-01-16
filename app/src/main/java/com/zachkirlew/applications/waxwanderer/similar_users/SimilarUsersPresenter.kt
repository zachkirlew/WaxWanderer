package com.zachkirlew.applications.waxwanderer.similar_users

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType
import java.util.*


class SimilarUsersPresenter(private @NonNull var similarUsersView: SimilarUsersContract.View) : SimilarUsersContract.Presenter {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    lateinit var userInfo: User

    lateinit var user: FirebaseUser

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

                loadSimilarUsers(userInfo)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun loadSimilarUsers(userInfo: User) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("users")

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val users = ArrayList<User>()

                for (child in dataSnapshot.children) {
                    child.getValue<User>(User::class.java)?.let { users.add(it) }
                }

                Collections.shuffle(users)

                val filterGender = userInfo.matchPreference?.gender
                val filterAge = userInfo.matchPreference?.ageRange

                val lowerAgeLimit: Int = filterAge?.split(" - ")?.get(0)?.toInt()!!
                val upperAgeLimit: Int = filterAge.split(" - ")[1].toInt()

                //filter users by age and gender
                var filteredUsers = users.filter { user.uid != it.id }
                        .filter { dobToAge(it.dob) in lowerAgeLimit..upperAgeLimit }

                //remove any users that that the current user has already matched with
                if(userInfo.connections?.matches!=null){
                    filteredUsers = filteredUsers.filter { !userInfo.connections?.matches?.containsKey(it.id)!! }
                }

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

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun handleLike(likedUser: User) {

        val myRef = database.reference

        val userUid = mFirebaseAuth.currentUser?.uid

        //if liked user doesn't have any likes
        if (likedUser.connections?.likes !== null) {

            //if liked user has current user in their likes
            if (likedUser.connections?.likes?.containsKey(userUid)!!) {
                //It's a match!

                likedUser.name?.let { similarUsersView.showMatchDialog(it) }

                val chatKey = myRef.child("chat").push().key

                //add to both accounts and set chat id
                myRef.child("users").child(userUid)
                        .child("connections").child("matches")
                        .child(likedUser.id).setValue(chatKey)

                myRef.child("users").child(likedUser.id)
                        .child("connections").child("matches")
                        .child(userUid).setValue(chatKey)

                //remove old like from liked user's account
                myRef.child("users").child(likedUser.id)
                        .child("connections").child("likes")
                        .child(userUid).setValue(null)

            } else {
                likeUser(userUid, likedUser.id)
            }
        } else {
            likeUser(userUid, likedUser.id)
        }
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

    private fun likeUser(currentUserId: String?, likedUserId: String?) {

        val myRef = database.reference

        myRef.child("users").child(currentUserId)
                .child("connections").child("likes")
                .child(likedUserId).setValue(true)
    }


    private fun dobToAge(date: Date?): Int {

        val birthDate = LocalDate(date)

        val todaysDate = LocalDate()

        return Period(birthDate, todaysDate, PeriodType.yearMonthDay()).years
    }


}