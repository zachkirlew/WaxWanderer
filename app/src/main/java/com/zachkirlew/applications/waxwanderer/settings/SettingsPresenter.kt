package com.zachkirlew.applications.waxwanderer.settings

import android.net.Uri
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.zachkirlew.applications.waxwanderer.data.local.UserPreferences
import com.zachkirlew.applications.waxwanderer.data.model.User
import java.text.DateFormat
import java.util.*


class SettingsPresenter(private @NonNull var settingsView: SettingsContract.View, private @NonNull val preferences: UserPreferences) : SettingsContract.Presenter {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private var dob: Date? = null

    init {
        settingsView.setPresenter(this)
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

                settingsView.showUserDetails(userInfo!!, preferences.minMatchAge, preferences.maxMatchAge, preferences.matchGender)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun submitDetails(name: String, userGender: String, matchGender: String, minMatchAge: Int, maxMatchAge: Int) {

        val user = mFirebaseAuth.currentUser
        val userRef = database.reference.child("users").child(user?.uid)

        userRef.child("name").setValue(name)
        userRef.child("dob").setValue(dob)
        userRef.child("gender").setValue(userGender)

        preferences.minMatchAge = minMatchAge
        preferences.maxMatchAge = maxMatchAge

        preferences.matchGender = matchGender

        settingsView.showMessage("Profile updated")
    }


    override fun getFormattedDate(year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = 0
        cal.set(year, month, day, 0, 0, 0)

        dob = cal.time

        val dfMediumUK = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
        val dateFormatted = dfMediumUK.format(dob)

        settingsView.showDateFormatted(dateFormatted)
    }


    override fun saveProfileImage(imageHoldUri: Uri?) {

        val storageRef = FirebaseStorage.getInstance().reference

        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(mFirebaseAuth.currentUser?.uid)

        if (imageHoldUri != null) {

            val mChildStorage = storageRef.child("User_Profile").child(imageHoldUri.lastPathSegment)

            mChildStorage.putFile(imageHoldUri)
                    .addOnSuccessListener({ taskSnapshot ->
                        val imageUrl = taskSnapshot.downloadUrl

                        userRef.child("imageurl").setValue(imageUrl?.toString())

                        settingsView.showMessage("Profile picture changed")
                    }).addOnFailureListener({ exception -> settingsView.showMessage(exception.message)})
        }
    }
}