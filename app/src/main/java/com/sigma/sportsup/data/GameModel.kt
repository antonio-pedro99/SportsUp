package com.sigma.sportsup.data

data class GameModel(
    var name:String,
    val image: String = "",
    val items:List<GameEvent> = listOf()
){
    constructor() : this("", "")
}