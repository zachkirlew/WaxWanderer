package com.zachkirlew.applications.waxwanderer.message


import android.media.Image
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.Message
import com.zachkirlew.applications.waxwanderer.data.model.User
import java.util.*

class MessageFragment : Fragment(), MessageContract.View {

    private var messages: ArrayList<Message>? = null
    private lateinit var adapter: MessageAdapter

    private lateinit var uid : String

    private lateinit var chatList:RecyclerView

    private lateinit var messageInput:EditText

    private lateinit var emojiButton : ImageView

    private var presenter: MessageContract.Presenter? = null

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

        presenter?.loadMatch(matchedUser.id)

        return view
    }

    private fun setupAdapter() {
        messages = ArrayList<Message>()
        adapter = MessageAdapter(messages!!, uid)
    }

    private fun setupList() {
        chatList.layoutManager = LinearLayoutManager(chatList.context)
        chatList.adapter = adapter
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

    private fun sendMessage() {
        val message = messageInput.text.toString()
        if (!message.isEmpty())
            presenter?.sendMessage(Message(message, uid,System.currentTimeMillis().toString()))

        messageInput.setText("")
    }


}