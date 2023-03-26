package com.sigma.sportsup.data

data class GameModel(
    var name:String,
    val image: String = "",
    val items:List<Any> = listOf()
){
    constructor() : this("", "")
}