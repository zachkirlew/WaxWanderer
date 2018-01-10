package com.zachkirlew.applications.waxwanderer.message

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.zachkirlew.applications.waxwanderer.data.model.Message


class MessagePresenter(private @NonNull var messageView: MessageContract.View) : MessageContract.Presenter {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()


    override fun loadMessages() {

        database.reference.child("messages").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                val message = dataSnapshot.getValue<Message>(Message::class.java)

                message?.let { messageView.showMessage(it) }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                Log.d("REMOVED", dataSnapshot.getValue<Message>(Message::class.java)!!.toString())

                messageView.removeMessage(dataSnapshot.getValue<Message>(Message::class.java)!!)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    override fun sendNewMessage(messageText: String, isNotification: Boolean) {
        if (!messageText.isEmpty()) {



            val message = Message()
            message.userID = mFirebaseAuth.currentUser?.uid!!
            message.username = mFirebaseAuth.currentUser?.displayName!!
            message.message = messageText
            message.timestamp = System.currentTimeMillis() / 1000L
            message.isNotification = isNotification
            val key = database.reference.child("messages").push().key
            database.reference.child("messages").child(key).setValue(message)
        }

    }


    override fun start() {}


}