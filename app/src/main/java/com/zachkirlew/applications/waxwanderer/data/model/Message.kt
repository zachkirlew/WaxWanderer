package com.zachkirlew.applications.waxwanderer.data.model

class Message {

     lateinit var message: String
     lateinit var author: String

    // Default constructor is required for Firebase object mapping
    constructor() {}

    constructor(message: String, author: String) {
        this.message = message
        this.author = author
    }


}