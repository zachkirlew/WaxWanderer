package com.waxwanderer.vinyl_detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.waxwanderer.R
import com.waxwanderer.data.model.discogs.detail.Tracklist

class TrackListAdapter
(context: Context, resource: Int, tracklist: List<Tracklist>?) : ArrayAdapter<Tracklist>(context, resource, tracklist) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView

        // Get the data item for this position
        val track = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.tracklist_item, parent, false)
        }
        // Lookup view for data population
        val sideText = convertView?.findViewById<TextView>(R.id.text_track_side) as TextView
        val trackText = convertView.findViewById<TextView>(R.id.text_track_name) as TextView
        // Populate the data into the template view using the data object

        sideText.text = track?.position
        trackText.text = track?.title

        // Return the completed view to render on screen
        return convertView
    }
}