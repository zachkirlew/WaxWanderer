package com.zachkirlew.applications.waxwanderer.data.model

import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease

class Message {

    lateinit var id : String
    lateinit var message: String
    lateinit var author: String
    var attachedVinyl : VinylRelease? = null
    lateinit var timestamp : String
    var isRated  = false
    var rating : Double? = null

    // Default constructor is required for Firebase object mapping
    constructor() {}

    constructor(id : String,message: String, author: String,attachedVinyl : VinylRelease?,timestamp : String,isRated : Boolean,rating : Double?) {
        this.id = id
        this.message = message
        this.author = author
        this.attachedVinyl = attachedVinyl
        this.timestamp = timestamp
        this.isRated = isRated
        this.rating = rating
    }


}