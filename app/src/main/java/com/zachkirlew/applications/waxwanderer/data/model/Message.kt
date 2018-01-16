package com.zachkirlew.applications.waxwanderer.data.model

class Message {

     lateinit var message: String
     lateinit var author: String
     lateinit var timestamp : String

    // Default constructor is required for Firebase object mapping
    constructor() {}

    constructor(message: String, author: String,timestamp : String) {
        this.message = message
        this.author = author
        this.timestamp = timestamp
    }


}