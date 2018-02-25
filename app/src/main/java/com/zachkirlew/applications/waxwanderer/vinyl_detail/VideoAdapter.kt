package com.zachkirlew.applications.waxwanderer.vinyl_detail

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.lucasurbas.listitemview.ListItemView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.Video
import kotlinx.android.synthetic.main.video_item.view.*


class VideoAdapter
(private val mContext: Context, resource: Int, videos: List<Video>?) : ArrayAdapter<Video>(mContext, resource, videos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView as ListItemView?

        // Get the data item for this position
        val video = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false) as ListItemView
        }

        convertView.list_item_view.title = video?.title
        convertView.list_item_view.subtitle = formatDuration(video?.duration!!)

        val videoId = video.uri?.takeLast(11)

        val thumbnailUrl = "https://i1.ytimg.com/vi/$videoId/default.jpg"

        Picasso.with(convertView.context)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_youtube)
                .into(convertView.list_item_view.avatarView)


        convertView.setOnClickListener {
            val intent = YouTubeStandalonePlayer.createVideoIntent(mContext as AppCompatActivity, DeveloperKey.DEVELOPER_KEY, videoId,0, true, true)
            convertView.context?.startActivity(intent)

        }

        // Return the completed view to render on screen
        return convertView
    }

    private fun formatDuration(duration : Int): String {

        val minutes = duration / 60
        val seconds = duration % 60

        val disMinu = (if (minutes < 10) "0" else "") + minutes
        val disSec = (if (seconds < 10) "0" else "") + seconds

        return disMinu + ":" + disSec
    }
}