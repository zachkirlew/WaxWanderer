package com.zachkirlew.applications.waxwanderer.settings

import android.net.Uri
import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.zachkirlew.applications.waxwanderer.data.model.User
import java.text.DateFormat
import java.util.*


class SettingsPresenter(private @NonNull var matchView: SettingsContract.View) : SettingsContract.Presenter  {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private var dob: Date? = null

    init{
        matchView.setPresenter(this)
    }

    override fun start() {
        loadUserDetails()
    }

    override fun loadUserDetails() {

        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        val ref = myRef.child("users").child(user?.uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val userInfo = dataSnapshot.getValue(User::class.java)

                dob = userInfo?.dob

                matchView.showUserDetails(userInfo!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun getFormattedDate(year : Int, month : Int, day : Int){
        val cal = Calendar.getInstance()
        cal.timeInMillis = 0
        cal.set(year, month, day, 0, 0, 0)

        dob = cal.time

        val dfMediumUK = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
        val dateFormatted = dfMediumUK.format(dob)

        matchView.showDateFormatted(dateFormatted)
    }

    override fun submitDetails(name : String, userGender : String, matchGender : String, matchAge : String) {

        val user = mFirebaseAuth.currentUser
        val userRef = database.reference.child("users").child(user?.uid)

        userRef.child("name").setValue(name)
        userRef.child("dob").setValue(dob)
        userRef.child("gender").setValue(userGender)

        userRef.child("matchPreference").child("ageRange").setValue(matchAge)
        userRef.child("matchPreference").child("gender").setValue(matchGender)
    }

    override fun saveProfileImage(imageHoldUri: Uri?) {

        val storageRef = FirebaseStorage.getInstance().reference

        val userDatabse = FirebaseDatabase.getInstance().reference.child("users").child(mFirebaseAuth.currentUser?.uid)

        if (imageHoldUri != null) {

            val mChildStorage = storageRef.child("User_Profile").child(imageHoldUri.lastPathSegment)
            val profilePicUrl = imageHoldUri.lastPathSegment

            mChildStorage.putFile(imageHoldUri).addOnSuccessListener({ taskSnapshot ->
                val imageUrl = taskSnapshot.downloadUrl

                userDatabse.child("imageurl").setValue(imageUrl?.toString())
            })
        }
    }

}