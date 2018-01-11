package com.zachkirlew.applications.waxwanderer.message

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.Message


class MessageAdapter(private val chatList: ArrayList<Message>, private val mId: String) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val view: View
        if (viewType == MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_message_sent, parent, false)
        } else {
            view = LayoutInflater.from(parent.context)
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
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].author == mId) MESSAGE_SENT else MESSAGE_RECEIVED

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var txtMessage: TextView = view.findViewById<TextView>(R.id.txt_message) as TextView

    }

    companion object {

        private val MESSAGE_SENT = 1
        private val MESSAGE_RECEIVED = 2
    }
}