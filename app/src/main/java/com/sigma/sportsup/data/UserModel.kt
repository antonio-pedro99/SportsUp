package com.sigma.sportsup.data

data class UserModel(
    var age:Int?,
    var name:String?,
    var phone:String?,
    var id:String?
) {
    constructor():this(null, null, null, null)
}
