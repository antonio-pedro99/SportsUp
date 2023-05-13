package com.sigma.sportsup.data

import android.os.Parcel
import android.os.Parcelable

data class GameEvent(
    var id:String?,
    var name:String?,
    var host:String?,
    var venue:String?,
    var host_ref:String?,
    var start_time: String?,
    var end_time:String?,
    var date:String?,
    var number_of_players:Int?,
    var audience:String?,
    var current_players:Int?,
    var waiting:Int?,
    var game_event_name: String? = null,
    var event_image: String? = null,
) {

    constructor() : this(null, null, null, null, null, null,null,null,
        null, null, null, null, null, null)

} 
