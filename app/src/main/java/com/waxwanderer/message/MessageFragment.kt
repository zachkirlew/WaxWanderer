package com.waxwanderer.message


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.waxwanderer.data.model.Message
import com.waxwanderer.data.model.User
import com.waxwanderer.data.model.discogs.VinylRelease
import com.waxwanderer.data.recommendation.RecommenderImp
import com.waxwanderer.data.remote.notification.PushHelper
import com.waxwanderer.R
import com.waxwanderer.login.LoginActivity
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import java.io.Serializable
import java.util.*


class MessageFragment : Fragment(), MessageContract.View, ShareVinylDialogFragment.FavouriteAdapter.OnShareClickedListener, RatingBarFragment.RatingSubmittedListener, DialogInterface.OnDismissListener {

    private var messages: ArrayList<Message>? = null
    private lateinit var adapter: MessageAdapter

    private lateinit var uid: String

    private lateinit var chatList: RecyclerView

    private lateinit var messageInput: EditText

    private lateinit var shareVinylButton: ImageView

    private var presenter: MessageContract.Presenter? = null

    private var shareVinylDialogFragment: ShareVinylDialogFragment? = null

    private lateinit var sendButton: ImageButton


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_message, container, false)

        if(FirebaseAuth.getInstance().currentUser==null){
            showLoginActivity()
        }
        else{
            uid = FirebaseAuth.getInstance().currentUser?.uid!!

            view = init(view)

            initializePresenter()

            setupAdapter()
            setupList()

            val matchedUser = activity?.intent?.extras?.get("matchedUserId") as String

            presenter?.loadRecipient(matchedUser)
        }

        return view
    }

    private fun init(rootView : View) : View{
        chatList = rootView.findViewById(R.id.list_chat)
        messageInput = rootView.findViewById(R.id.input_message)
        sendButton = rootView.findViewById(R.id.button_sent)
        shareVinylButton = rootView.findViewById(R.id.button_vinyl_share)

        sendButton.setOnClickListener { sendMessage() }

        shareVinylButton.setOnClickListener {
            it.isClickable = false
            presenter?.loadFavourites()
        }

        return rootView
    }

    override fun showUserDetails(user: User) {
        activity?.title = user.name
    }

    override fun addMessage(message: RxFirebaseChildEvent<DataSnapshot>) {
        adapter.manageChildItem(message)

        if(message.eventType==RxFirebaseChildEvent.EventType.ADDED)
            chatList.scrollToPosition(adapter.itemCount - 1)
    }

    private fun setupAdapter() {
        messages = ArrayList()
        adapter = MessageAdapter(messages!!, uid, this)
    }

    private fun setupList() {
        chatList.layoutManager = LinearLayoutManager(chatList.context)
        chatList.adapter = adapter
    }

    override fun showError(message: String?) {
        Toast.makeText(activity, message,
                Toast.LENGTH_SHORT).show()
    }

    private fun showLoginActivity(){
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()

    }

    fun showRatingDialog(chatId: String, vinylId: Int?) {

        val ratingBarFragment = RatingBarFragment()

        val bundle = Bundle()
        bundle.putSerializable("message_id", chatId)
        bundle.putSerializable("vinyl_id", vinylId)

        ratingBarFragment.arguments = bundle

        ratingBarFragment.setOnRatingSubmittedListener(this)

        ratingBarFragment.show(fragmentManager, "dialog")
    }

    override fun onRatingSubmitted(vinylId: Int, rating: Double, messageId: String) {
        presenter?.addRating(vinylId, rating, messageId)
    }

    private fun initializePresenter() {
        if (presenter == null)
            presenter = MessagePresenter(this, RecommenderImp(activity!!), PushHelper.getInstance(activity!!))
    }

    override fun showChooseRecordDialog(favourites: List<VinylRelease>) {

        shareVinylDialogFragment = ShareVinylDialogFragment()

        val bundle = Bundle()
        bundle.putSerializable("favouriteList", favourites as Serializable)

        shareVinylDialogFragment?.arguments = bundle
        shareVinylDialogFragment?.setOnShareClickedListener(this)
        shareVinylDialogFragment?.setOnDismissedListener(this)
        shareVinylDialogFragment?.show(activity?.supportFragmentManager, "now")
    }

    override fun onShared(sharedVinyl: VinylRelease) {
        shareVinylDialogFragment?.dismiss()
        presenter?.sendMessage("", uid, sharedVinyl)

        shareVinylButton.isClickable = true
    }

    override fun onDismiss(p0: DialogInterface?) {
        shareVinylButton.isClickable = true
    }

    private fun sendMessage() {
        val message = messageInput.text.toString()
        if (!message.isEmpty())
            presenter?.sendMessage(message, uid, null)

        messageInput.setText("")
    }

    override fun onStop() {
        super.onStop()
        presenter?.dispose()
    }
}