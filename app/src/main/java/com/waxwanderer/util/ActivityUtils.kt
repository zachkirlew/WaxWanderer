package com.waxwanderer.util

import android.content.Context
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.waxwanderer.R

object ActivityUtils {

    fun addFragmentToActivity(@NonNull fragmentManager: FragmentManager,
                              @NonNull fragment: Fragment, frameId: Int) {
        checkNotNull(fragmentManager)
        checkNotNull(fragment)
        val transaction = fragmentManager.beginTransaction()
        transaction.add(frameId, fragment)
        transaction.commit()
    }

    fun changeFragment(@NonNull fragmentManager: FragmentManager,
                              @NonNull fragment: Fragment, frameId: Int) {
        checkNotNull(fragmentManager)
        checkNotNull(fragment)
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(frameId, fragment)
        transaction.commit()
    }



}