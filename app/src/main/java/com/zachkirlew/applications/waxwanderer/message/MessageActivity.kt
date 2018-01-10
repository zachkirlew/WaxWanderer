package com.zachkirlew.applications.waxwanderer.message

import android.content.Context
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.Message
import kotlinx.android.synthetic.main.message_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class MessageActivity : AppCompatActivity(), MessageContract.View  {

    private val coordinatorLayout by lazy{findViewById<CoordinatorLayout>(R.id.main_content)}

    private val messageList by lazy {findViewById<RecyclerView>(R.id.list_messages)}
    private val imageButtonSend by lazy{ findViewById<ImageButton>(R.id.imageButton_send)}
    private val editTextMessage by lazy{ findViewById<EditText>(R.id.editText_message)}

    private val progressBar by lazy{ findViewById<ProgressBar>(R.id.progressBar)}

    private val  messages:ArrayList<Message> = ArrayList()

    private lateinit var messagePresenter : MessagePresenter

    private lateinit var messageAdapter: MessageActivity.MessageAdapter

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = "Inbox"

        progressBar.visibility = View.VISIBLE

        messageList.layoutManager = LinearLayoutManager(this)

        messageAdapter = MessageAdapter(this, messages)
        messageList.adapter = messageAdapter

        messagePresenter = MessagePresenter(this)

        messagePresenter.loadMessages()

        imageButtonSend.setOnClickListener({ messagePresenter.sendNewMessage(editTextMessage.text.toString().trim(), false) })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun clearTextBox() {
        editTextMessage.setText("")
    }

    override fun showMessage(message: Message) {
        messages.add(message)
        messageAdapter.notifyDataSetChanged()
        progressBar.visibility = View.GONE
        messageList.scrollToPosition(messageAdapter.itemCount - 1)
    }

    override fun removeMessage(message: Message) {
        messages.remove(message)
        messageAdapter.notifyDataSetChanged()
    }


    inner class MessageAdapter(private val mContext: Context, private val messages: ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, i: Int): MessageAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
            holder.bindItems(messages[position])
        }

        override fun getItemCount(): Int {
            return messages.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(message: Message) {

                val formattedDate = message.timestamp?.let { formatDate(it) }

                if (message.isNotification!!) {
                    itemView.textView_message.text = Html.fromHtml("<small><i><font color=\"#FFBB33\">" + " " + message.message + "</font></i></small>")
                } else {
                    itemView.textView_message.text = Html.fromHtml("<font color=\"#403835\">&#x3C;" + message.username + "&#x3E;</font>" + " " + message.message + " <font color=\"#999999\">" + formattedDate + "</font>")
                }
            }

            private fun formatDate(timestamp: Long): String {
                val cal = Calendar.getInstance()
                val tz = cal.timeZone//get your local time zone.
                val sdf = SimpleDateFormat("hh:mma",Locale.getDefault())

                sdf.timeZone = tz//set time zone.
                val localTime = sdf.format(Date(timestamp * 1000))
                return localTime.toLowerCase()
            }
        }

    }


}