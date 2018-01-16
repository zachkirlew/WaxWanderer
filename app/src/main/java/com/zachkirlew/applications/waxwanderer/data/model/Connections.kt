package com.zachkirlew.applications.waxwanderer.data.model

import java.io.Serializable

class Connections : Serializable{
    var matches: Map<String,String>? = null
    var likes: Map<String,Boolean>? = null
}