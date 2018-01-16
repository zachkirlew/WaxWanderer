package com.zachkirlew.applications.waxwanderer.data.model

class Match {

    lateinit var matchedWithId: String
    lateinit var chatId: String

    // Default constructor is required for Firebase object mapping
    constructor() {}

    constructor(matchedWithId: String, chatId: String) {
        this.matchedWithId = matchedWithId
        this.chatId = chatId
    }


}