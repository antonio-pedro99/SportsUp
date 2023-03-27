package com.sigma.sportsup.data

data class VenueModel(
    var name:String? = null,
    var image:String? = null,
    var events: List<Any>? = null,
    var isBusy : Boolean? = null
) {
    constructor():this(null, null, null, null)
}