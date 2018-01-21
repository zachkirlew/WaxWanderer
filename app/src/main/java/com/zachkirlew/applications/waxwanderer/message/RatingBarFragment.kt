package com.zachkirlew.applications.waxwanderer.message


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import com.zachkirlew.applications.waxwanderer.R


class RatingBarFragment : DialogFragment() {
    private lateinit var ratingBar : RatingBar

    var mCallback: RatingBar.OnRatingBarChangeListener? = null


    @Override
    override  fun  onCreateDialog(savedInstanceState : Bundle?) : Dialog {
        val dialog = AlertDialog.Builder(activity)
                .setTitle("Rate that vinyl!")
                .setNegativeButton("Cancel",{ dialogInterface: DialogInterface?, i: Int ->
                    dialogInterface?.dismiss()
                })

        //val list = arguments.getSerializable("favouriteList") as List<VinylRelease>

        val  rootView = activity.layoutInflater.inflate(R.layout.fragment_rating, null)

        ratingBar = rootView?.findViewById<RatingBar>(R.id.rating_bar_shared_vinyl) as RatingBar

        dialog.setView(rootView)
        return dialog.create()
    }

    fun setOnRatingBarChangeListener(mCallback: RatingBar.OnRatingBarChangeListener) {
        this.mCallback = mCallback
    }
}