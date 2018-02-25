package com.zachkirlew.applications.waxwanderer.message


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.RatingBar
import com.zachkirlew.applications.waxwanderer.R


class RatingBarFragment : DialogFragment() {
    private lateinit var ratingBar : RatingBar

    var mCallback: RatingSubmittedListener? = null


    @Override
    override  fun  onCreateDialog(savedInstanceState : Bundle?) : Dialog {
        val  rootView = activity?.layoutInflater?.inflate(R.layout.fragment_rating, null)

        ratingBar = rootView?.findViewById(R.id.rating_bar_shared_vinyl) as RatingBar

        val messageId = arguments?.getSerializable("message_id") as String
        val vinylId = arguments?.getSerializable("vinyl_id") as Int

        val dialog = AlertDialog.Builder(activity!!)
                .setTitle("Rate that vinyl!")
                .setPositiveButton("Okay",{ _: DialogInterface?, i: Int ->
                    mCallback?.onRatingSubmitted(vinylId, ratingBar.rating.toDouble(),messageId)
                })
                .setNegativeButton("Cancel",{ dialogInterface: DialogInterface?, i: Int ->
                    dialogInterface?.dismiss()
                })


        dialog.setView(rootView)
        return dialog.create()
    }

    fun setOnRatingSubmittedListener(mCallback: RatingSubmittedListener) {
        this.mCallback = mCallback
    }

    interface RatingSubmittedListener {
        fun onRatingSubmitted(vinylId: Int,rating : Double,messageId : String)
    }
}