package com.zachkirlew.applications.waxwanderer.message

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.Message
import com.zachkirlew.applications.waxwanderer.detail_vinyl.VinylDetailActivity


class MessageAdapter(private val chatList: ArrayList<Message>, private val mId: String) : RecyclerView.Adapter<MessageAdapter.ViewHolder>(), View.OnClickListener {

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

        }
        else{
            holder.layoutAttachedVinyl.visibility = View.GONE
        }
    }

    //on click on shared vinyl
    override fun onClick(p0: View?) {

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

    }

    companion object {

        private val MESSAGE_SENT = 1
        private val MESSAGE_RECEIVED = 2
    }
}