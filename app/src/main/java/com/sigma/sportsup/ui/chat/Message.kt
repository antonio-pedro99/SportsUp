package com.sigma.sportsup.ui.chat

class Message {
    var message:String? = null
    var senderId:String? = null
    var rec_token:String? = null

    constructor()

    constructor(message :String?, senderId:String?,rec_token: String?){
        this.message = message
        this.senderId= senderId
        this.rec_token = rec_token
    }
}