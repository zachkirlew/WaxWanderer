package com.zachkirlew.applications.waxwanderer.message

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.Message
import com.zachkirlew.applications.waxwanderer.vinyl_detail.VinylDetailActivity
import java.util.*

class MessageAdapter(private val messageList: ArrayList<Message>, private val mId: String, val messageFragment: MessageFragment) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

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
        messageList.add(message)
    }

    fun updateMessage(message: Message,itemPosition : Int){
        messageList[itemPosition] = message
        notifyItemChanged(itemPosition)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        holder.txtMessage.text = message.message

        if(message.attachedVinyl !=null){
            holder.layoutAttachedVinyl.visibility = View.VISIBLE

            val attachedVinyl = message.attachedVinyl

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

                if(message.isRated){
                    holder.ratingBar.rating = message.rating?.toFloat()!!
                    holder.ratedText.text = holder.itemView.context.getString(R.string.message_rating_current_user_rated)
                }
                else{
                    holder.ratedText.text = holder.itemView.context.getString(R.string.message_rating_current_user_unrated)
                    holder.ratingBar.rating = 0f
                    holder.ratedText.setOnClickListener{messageFragment.showRatingDialog(message.id,attachedVinyl!!.id,position)}
                }
            }
            else{
                if(message.isRated){
                    holder.ratingBar.rating = message.rating?.toFloat()!!
                    holder.ratedText.text = holder.itemView.context.getString(R.string.message_rating_other_user_rated)
                }
                else{
                    holder.ratedText.text = holder.itemView.context.getString(R.string.message_rating_other_user_unrated)
                    holder.ratingBar.rating = 0f
                }
            }
        }
        else{
            holder.layoutAttachedVinyl.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].author == mId) MESSAGE_SENT else MESSAGE_RECEIVED
    }

    fun getItemPosition(key : String) : Int {
        val message = messageList.filter { key == it.id }[0]
        return messageList.indexOf(message)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var txtMessage: TextView = view.findViewById<TextView>(R.id.txt_message) as TextView
        internal var layoutAttachedVinyl : LinearLayout = view.findViewById<LinearLayout>(R.id.layout_attached_vinyl) as LinearLayout
        internal var ratedText : TextView = view.findViewById<TextView>(R.id.text_rated) as TextView
        internal var ratingBar : RatingBar = view.findViewById<RatingBar>(R.id.rating_bar_message) as RatingBar
    }

    companion object {

        private val MESSAGE_SENT = 1
        private val MESSAGE_RECEIVED = 2
    }
}