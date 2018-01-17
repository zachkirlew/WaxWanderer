package com.zachkirlew.applications.waxwanderer.message

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zachkirlew.applications.waxwanderer.data.model.Message


class MessagePresenter(private @NonNull var messageView: MessageContract.View) : MessageContract.Presenter {

    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var chatId : String

    override fun loadMatch(matchedUserId: String?) {

        val myRef = database.reference
        val user = mFirebaseAuth.currentUser

        val ref = myRef.child("matches").child(user?.uid).child(matchedUserId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()) {
                    chatId = dataSnapshot.value as String

                    loadMessages(chatId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun loadMessages(chatId : String ) {

        database.reference.child("chat").child(chatId).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                val message = dataSnapshot.getValue<Message>(Message::class.java)

                message?.let { messageView.showMessage(it) }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

//                Log.d("REMOVED", dataSnapshot.getValue<Message>(Message::class.java)!!.toString())
//
//                messageView.removeMessage(dataSnapshot.getValue<Message>(Message::class.java)!!)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    override fun sendMessage(message: Message) {
        val key = database.reference.child("chat").child(chatId).push().key
        database.reference.child("chat").child(chatId).child(key).setValue(message)
    }



    override fun start() {}


}