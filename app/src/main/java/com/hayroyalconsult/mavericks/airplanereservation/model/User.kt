package com.hayroyalconsult.mavericks.airplanereservation.model

/**
 * Created by mavericks on 4/15/18.
 */
class User(id : Int = 0,
           email : String = "",
           password : String = "",
           role : Int = 0){
    var id : Int = id
    var email : String = email
    var password : String = password
    var role : Int = role
    override fun toString(): String {
        return "User(id=$id, email='$email', password='$password', role=$role)"
    }


}