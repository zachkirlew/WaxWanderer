package com.zachkirlew.applications.waxwanderer.data.model

import com.google.firebase.database.DataSnapshot
import durdinapps.rxfirebase2.RxFirebaseChildEvent


data class RxChildEvent<T>(val rxSnapShot : RxFirebaseChildEvent<DataSnapshot>, val value : T?)