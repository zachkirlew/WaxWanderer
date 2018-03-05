package com.waxwanderer.browse

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.waxwanderer.R
import com.waxwanderer.data.model.Style
import com.waxwanderer.vinyl.VinylActivity
import kotlinx.android.synthetic.main.browse_item.view.*

class StylesAdapter(private var styles: List<Style>, private val type: String) : RecyclerView.Adapter<StylesAdapter.ViewHolder>() {

    fun addStyles(styles : List<Style>){
        this.styles = styles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.browse_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(styles[position])

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            val params : HashMap<String,String> = HashMap()
            params[type] = styles[position].style!!

            val intent = Intent(context, VinylActivity::class.java)
            intent.putExtra("params", params)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return styles.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(style: Style) {

            itemView.text_style.text = style.style

            Picasso.with(itemView.context)
                    .load(style.backgroundImage)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(itemView.background_image)
        }
    }
}

