package com.zachkirlew.applications.waxwanderer.message

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zachkirlew.applications.waxwanderer.data.model.Message
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


class MessagePresenter(private @NonNull var messageView: MessageContract.View) : MessageContract.Presenter {

    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var chatId : String

    private val mTotalItemCount = 0
    private val mLastVisibleItemPosition: Int = 0
    private val mIsLoading = false
    private val mPostsPerPage = 20

    override fun loadMatch(matchedUserId: String?) {

        val myRef = database.reference
        val user = mFirebaseAuth.currentUser

        val ref = myRef.child("matches").child(user?.uid).child(matchedUserId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()) {
                    chatId = dataSnapshot.value as String

                    loadMessages()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun loadMessages() {

        val messageRef = database.reference.child("chat").child(chatId)

        val messageQuery = messageRef.limitToLast(10)

        messageQuery.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                val message = dataSnapshot.getValue<Message>(Message::class.java)
                message?.let { messageView.showMessage(it) }

//                currentPage++
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                println(dataSnapshot.key)
                val message = dataSnapshot.getValue<Message>(Message::class.java)!!
                messageView.updateMessage(message)
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

    override fun sendMessage(messageText: String, authorId: String,attachedRelease : VinylRelease?) {

        val key = database.reference.child("chat").child(chatId).push().key

        val message = Message(key, messageText, authorId, attachedRelease, System.currentTimeMillis().toString(),false,null)

        database.reference.child("chat").child(chatId).child(key).setValue(message)
    }

    override fun loadFavourites() {

        val myRef = database.reference
        val user = mFirebaseAuth.currentUser

        val ref = myRef.child("favourites").child(user?.uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val userFavourites = dataSnapshot.children.map { it.getValue<VinylRelease>(VinylRelease::class.java)!! }
                messageView.showChooseRecordDialog(userFavourites)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun addRating(vinylId: Int, rating: Double, messageId: String) {
        val myRef = database.reference.child("chat").child(chatId).child(messageId)

        myRef.child("rating").setValue(rating)
        myRef.child("rated").setValue(true)
    }

    override fun start() {}
}