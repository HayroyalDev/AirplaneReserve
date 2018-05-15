package com.hayroyalconsult.mavericks.airplanereservation.model

import com.orm.SugarRecord

/**
 * Created by mavericks on 4/15/18.
 */
class Booking{
    var id : Int = 0
    var flight_id : Int = 0
    var user_id : Int = 0
    var seat_number : String? = null
    var status : String? = null
    var flight :Flight? = null

    companion object {
        val CONFIRMED = "CONFIRMED"
        val NOT_CONFIRMED = "NOT CONFIRMED"
    }

    override fun toString(): String {
        return "Booking(id=$id, flight_id=$flight_id, user_id=$user_id, seat_number=$seat_number, status='$status', flight=${flight.toString()})"
    }


}