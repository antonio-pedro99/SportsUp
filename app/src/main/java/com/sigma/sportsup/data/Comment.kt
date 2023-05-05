package com.sigma.sportsup.data

data class Comment(
    val comment: String,
    val date: String,
    var id: String,
    val name: String,
    val profileImage: String,
    val uid: String
){
    constructor() : this("", "", "", "", "", "")
}
