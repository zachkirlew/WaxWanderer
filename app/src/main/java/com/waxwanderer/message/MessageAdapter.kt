package com.waxwanderer.message

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import com.waxwanderer.R
import com.waxwanderer.data.model.Message
import com.waxwanderer.vinyl_detail.VinylDetailActivity
import durdinapps.rxfirebase2.RxFirebaseRecyclerAdapter
import java.util.*


class MessageAdapter(private val messageList: ArrayList<Message>, private val mId: String, private val messageFragment: MessageFragment) : RxFirebaseRecyclerAdapter<MessageAdapter.ViewHolder, Message>(Message::class.java) {

    private val TAG = MessageAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = if (viewType == MESSAGE_SENT) {
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_message_sent, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_message_received, parent, false)
        }

        return ViewHolder(view)
    }

    fun getFirstItemId(): String {
        return messageList[0].id
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        holder.txtMessage.text = message.message

        if(message.attachedVinyl !=null){
            holder.layoutAttachedVinyl.visibility = View.VISIBLE

            val attachedVinyl = message.attachedVinyl

            val coverArt = holder.layoutAttachedVinyl.findViewById(R.id.cover_art) as ImageView
            val releaseTxt = holder.layoutAttachedVinyl.findViewById(R.id.release_title) as TextView
            val yearTxt = holder.layoutAttachedVinyl.findViewById(R.id.release_year) as TextView
            val codeTxt = holder.layoutAttachedVinyl.findViewById(R.id.release_code) as TextView

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
                    holder.rateButton?.visibility = View.GONE
                    holder.ratingBar.rating = message.rating?.toFloat()!!
                    holder.ratedText.text = holder.itemView.context.getString(R.string.message_rating_current_user_rated)
                    holder.ratingBar.visibility = View.VISIBLE
                }
                else{
                    holder.ratingBar.visibility = View.GONE
                    holder.rateButton?.visibility = View.VISIBLE
                    holder.ratingBar.rating = 0f
                    holder.ratedText.text = ""
                    holder.rateButton?.setOnClickListener{messageFragment.showRatingDialog(message.id, attachedVinyl!!.id)}
                }
            }
            else{
                holder.rateButton?.visibility = View.GONE
                if(message.isRated){
                    holder.ratingBar.visibility = View.VISIBLE
                    holder.ratingBar.rating = message.rating?.toFloat()!!
                    holder.ratedText.text = holder.itemView.context.getString(R.string.message_rating_other_user_rated)
                }
                else{
                    holder.ratingBar.visibility = View.GONE
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

    override fun itemAdded(message: Message, key: String, position: Int) {
        messageList.add(message)
        notifyItemInserted(position)
    }

    override fun itemChanged(oldMessage: Message, newMessage: Message, key: String, position: Int) {
        messageList[position] = newMessage
        notifyItemChanged(position)
    }

    override fun itemRemoved(item: Message, key: String, position: Int) {
        messageList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun itemMoved(item: Message, key: String, oldPosition: Int, newPosition: Int) {
        notifyItemMoved(oldPosition,newPosition)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var txtMessage: TextView = view.findViewById(R.id.txt_message) as TextView
        internal var layoutAttachedVinyl : LinearLayout = view.findViewById(R.id.layout_attached_vinyl) as LinearLayout
        internal var ratedText : TextView = view.findViewById(R.id.text_rated) as TextView
        internal var ratingBar : RatingBar = view.findViewById(R.id.rating_bar_message) as RatingBar
        internal var rateButton : Button? = view.findViewById(R.id.button_view_favourites) as Button?
    }

    companion object {

        private val MESSAGE_SENT = 1
        private val MESSAGE_RECEIVED = 2
    }
}