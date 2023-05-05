package com.sigma.sportsup.ui.chat

class User {
    var name:String? = null
    var email:String? = null
    var uid:String? = null
    var deviceToken:String? = null

    constructor(){}

    constructor(email:String?, uid:String?, deviceToken:String?){
        this.name = email
        this.email=email
        this.uid=uid
        this.deviceToken = deviceToken
    }
}