package com.zachkirlew.applications.waxwanderer.message


import android.content.DialogInterface
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
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.Message
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.recommendation.RecommenderImp
import java.io.Serializable
import java.util.*
import com.google.firebase.database.DataSnapshot
import com.zachkirlew.applications.waxwanderer.data.remote.notification.PushHelper
import durdinapps.rxfirebase2.RxFirebaseChildEvent




class MessageFragment : Fragment(), MessageContract.View, ShareVinylDialogFragment.FavouriteAdapter.OnShareClickedListener, RatingBarFragment.RatingSubmittedListener, DialogInterface.OnDismissListener {

    private var messages: ArrayList<Message>? = null
    private lateinit var adapter: MessageAdapter

    private lateinit var uid: String

    private lateinit var chatList: RecyclerView

    private lateinit var messageInput: EditText

    private lateinit var shareVinylButton: ImageView

    private var presenter: MessageContract.Presenter? = null

    private var shareVinylDialogFragment: ShareVinylDialogFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uid = FirebaseAuth.getInstance().currentUser?.uid!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)

        chatList = view.findViewById(R.id.list_chat)

        initializePresenter()

        messageInput = view.findViewById<EditText>(R.id.input_message)

        val sendFab = view.findViewById<ImageButton>(R.id.button_sent)

        sendFab.setOnClickListener {
            sendMessage()
        }

        setupAdapter()
        setupList()

        val matchedUser = activity?.intent?.getSerializableExtra("matchedUserId") as User

        println(matchedUser.name)
        activity?.title = matchedUser.name

        shareVinylButton = view.findViewById(R.id.button_vinyl_share)
        shareVinylButton.setOnClickListener {
            it.isClickable = false
            presenter?.loadFavourites()
        }

        presenter?.loadMessages(matchedUser)

        return view
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

    fun showRatingDialog(chatId: String, vinylId: Int?, position: Int) {

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