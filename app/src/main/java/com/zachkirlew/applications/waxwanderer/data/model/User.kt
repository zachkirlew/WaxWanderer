package com.zachkirlew.applications.waxwanderer.data.model

import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import java.io.Serializable
import java.util.*

class User : Serializable{

    var name: String? = null
    var id : String? =null
    var email: String? = null
    var dob: Date? = null
    var imageurl : String? = null
    var gender: String? = null
    var location: String? = null
    var score : Int = 0
}