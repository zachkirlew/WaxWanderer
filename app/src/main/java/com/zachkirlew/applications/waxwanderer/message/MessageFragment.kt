package com.zachkirlew.applications.waxwanderer.message


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.Message
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.detail_vinyl.VinylDetailActivity
import java.io.Serializable
import java.util.*


class MessageFragment : Fragment(), MessageContract.View, ShareVinylDialogFragment.FavouriteAdapter.OnShareClickedListener, RatingBar.OnRatingBarChangeListener {

    private var messages: ArrayList<Message>? = null
    private lateinit var adapter: MessageAdapter

    private lateinit var uid : String

    private lateinit var chatList:RecyclerView

    private lateinit var messageInput:EditText

    private lateinit var shareVinylButton : ImageView

    private var presenter: MessageContract.Presenter? = null

    private var shareVinylDialogFragment : ShareVinylDialogFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uid = FirebaseAuth.getInstance().currentUser?.uid!!
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_message, container, false)

        chatList = view.findViewById<RecyclerView>(R.id.list_chat)

        initializePresenter()

        messageInput = view.findViewById<EditText>(R.id.input_message)

        val sendFab = view.findViewById<ImageButton>(R.id.button_sent)


        sendFab.setOnClickListener {
            sendMessage() }

        setupAdapter()
        setupList()

        val matchedUser = activity.intent.getSerializableExtra("matchedUserId") as User

        println(matchedUser.name)
        activity.title = matchedUser.name

        shareVinylButton = view.findViewById<ImageView>(R.id.shareVinylButton)
        shareVinylButton.setOnClickListener { presenter?.loadFavourites() }

        presenter?.loadMatch(matchedUser.id)

        return view
    }


    private fun setupAdapter() {
        messages = ArrayList<Message>()
        adapter = MessageAdapter(messages!!, uid,this)
    }

    private fun setupList() {
        chatList.layoutManager = LinearLayoutManager(chatList.context)
        chatList.adapter = adapter
    }

    fun showRatingDialog(){

        val ratingBarFragment = RatingBarFragment()
//        val bundle = Bundle()
//        bundle.putSerializable("favouriteList",favourites as Serializable)
//        shareVinylDialogFragment?.arguments = bundle
        ratingBarFragment.setOnRatingBarChangeListener(this)

        ratingBarFragment.show(fragmentManager, "dialog")
    }

    override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun initializePresenter() {
        if (presenter == null)
            presenter = MessagePresenter(this)
    }

    override fun showMessage(message: Message) {
        adapter.addMessage(message)
        adapter.notifyDataSetChanged()
        chatList.scrollToPosition(adapter.itemCount - 1)
    }

    override fun showChooseRecordDialog(favourites: List<VinylRelease>) {

        shareVinylDialogFragment = ShareVinylDialogFragment()
        val bundle = Bundle()
        bundle.putSerializable("favouriteList",favourites as Serializable)
        shareVinylDialogFragment?.arguments = bundle
        shareVinylDialogFragment?.setOnShareClickedListener(this)
        shareVinylDialogFragment?.show(activity.supportFragmentManager,"now")
    }

    override fun ShareClicked(sharedVinyl: VinylRelease) {
        shareVinylDialogFragment?.dismiss()
        presenter?.sendMessage("", uid,sharedVinyl)
    }

    private fun sendMessage() {
        val message = messageInput.text.toString()
        if (!message.isEmpty())
            presenter?.sendMessage(message, uid,null)

        messageInput.setText("")
    }

    class MessageAdapter(private val chatList: ArrayList<Message>, private val mId: String,val messageFragment: MessageFragment) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
            val view: View = if (viewType == MESSAGE_SENT) {
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_message_sent, parent, false)
            } else {
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_message_received, parent, false)
            }

            return ViewHolder(view)
        }

        fun addMessage(message: Message){
            chatList.add(message)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val chat = chatList[position]
            holder.txtMessage.text = chat.message

            if(chat.attachedVinyl !=null){
                holder.layoutAttachedVinyl.visibility = View.VISIBLE

                val attachedVinyl = chat.attachedVinyl

                val coverArt = holder.layoutAttachedVinyl.findViewById<ImageView>(R.id.cover_art) as ImageView
                val releaseTxt = holder.layoutAttachedVinyl.findViewById<TextView>(R.id.release_title) as TextView
                val yearTxt = holder.layoutAttachedVinyl.findViewById<TextView>(R.id.release_year) as TextView
                val codeTxt = holder.layoutAttachedVinyl.findViewById<TextView>(R.id.release_code) as TextView

                releaseTxt.text = attachedVinyl?.title
                yearTxt.text = attachedVinyl?.year
                codeTxt.text = attachedVinyl?.catno

                if(!attachedVinyl?.thumb.isNullOrEmpty()) {
                    Picasso.with(coverArt.context).load(attachedVinyl?.thumb).into(coverArt)
                }
                holder.layoutAttachedVinyl.setOnClickListener {
                    val context = holder.itemView.context

                    val intent = Intent(context, VinylDetailActivity::class.java)
                    intent.putExtra("selected vinyl",attachedVinyl)
                    context.startActivity(intent)
                }

                val itemViewType = getItemViewType(position)
                if(itemViewType== MESSAGE_RECEIVED){
                    holder.ratedText.setOnClickListener{messageFragment.showRatingDialog()}
                }
            }
            else{
                holder.layoutAttachedVinyl.visibility = View.GONE
            }
        }


        override fun getItemCount(): Int {
            return chatList.size
        }

        override fun getItemViewType(position: Int): Int {
            return if (chatList[position].author == mId) MESSAGE_SENT else MESSAGE_RECEIVED

        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            internal var txtMessage: TextView = view.findViewById<TextView>(R.id.txt_message) as TextView
            internal var layoutAttachedVinyl : LinearLayout = view.findViewById<LinearLayout>(R.id.layout_attached_vinyl) as LinearLayout
            internal var ratedText : TextView = view.findViewById<TextView>(R.id.text_rated) as TextView
        }

        companion object {

            private val MESSAGE_SENT = 1
            private val MESSAGE_RECEIVED = 2
        }
    }
}