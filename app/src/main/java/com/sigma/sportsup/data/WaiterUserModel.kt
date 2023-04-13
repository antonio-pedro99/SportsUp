package com.sigma.sportsup.data

data class WaiterUserModel(
    var waiter_id:String?,
    var waiter_name:String?,
    var waiter_image:String?
){
    constructor(): this(null, null, null)
}
