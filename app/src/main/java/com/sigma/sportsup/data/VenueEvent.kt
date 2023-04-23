package com.sigma.sportsup.data

data class VenueEvent(
    val date:String?,
    val session:String?,
    val start_time:String?,
    val end_time:String?,
    val status:String?
){
    constructor(): this(null, null, null, null, null)
}
