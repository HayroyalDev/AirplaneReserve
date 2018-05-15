package com.hayroyalconsult.mavericks.airplanereservation.model

import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import java.util.*

/**
 * Created by mavericks on 4/15/18.
 */
class Flight{
    var id : Int? = null
    var departure : String = ""
    var destination : String = ""
    var timeOfDeparture : String? = null
    var amount : String? = null
    var merchant : String? = null
    var journeyTime : String? = null
    var status : String? = null
    override fun toString(): String {
        return "Flight(id=$id, departure='$departure', destination='$destination', timeOfDeparture=$timeOfDeparture, amount=$amount, merchant=$merchant, journeyTime=$journeyTime, status=$status)"
    }

}