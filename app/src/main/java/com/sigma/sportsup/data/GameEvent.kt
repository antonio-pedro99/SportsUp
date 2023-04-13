package com.sigma.sportsup.data

import android.os.Parcel
import android.os.Parcelable

data class GameEvent(
    var id:String?,
    var name:String?,
    var host:String?,
    var venue:String?,
    var time: String?,
    var end_time:String?,
    var date:String?,
    var number_of_players:String?,
    var audience:String?,
    var current_players:Int?
) {


    constructor() : this(null, null, null, null, null, null,
        null, null, null, null)


} 
