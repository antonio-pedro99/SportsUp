package com.sigma.sportsup.data

data class GameEvent(
    var name:String?,
    var host:String?,
    var venue:String?,
    var time: String?,
    var date:String?,
    var number_of_players:String?,
) {
    constructor() : this(null, null, null, null, null, null)
} 
