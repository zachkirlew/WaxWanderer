package com.zachkirlew.applications.waxwanderer.data.model

import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease

class Message {

     lateinit var message: String
     lateinit var author: String
     var attachedVinyl : VinylRelease? = null
     lateinit var timestamp : String

    // Default constructor is required for Firebase object mapping
    constructor() {}

    constructor(message: String, author: String,attachedVinyl : VinylRelease?,timestamp : String) {
        this.message = message
        this.author = author
        this.attachedVinyl = attachedVinyl
        this.timestamp = timestamp
    }


}