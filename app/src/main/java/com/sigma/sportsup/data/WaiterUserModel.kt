package com.sigma.sportsup.data

data class WaiterUserModel(
    var id:String?,
    var name:String?,
    var image:String?
) {
    constructor(): this(null, null, null)
}
