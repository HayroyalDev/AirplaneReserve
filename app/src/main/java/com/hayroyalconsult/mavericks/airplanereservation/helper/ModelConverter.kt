package com.hayroyalconsult.mavericks.airplanereservation.helper

import android.database.Cursor
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hayroyalconsult.mavericks.airplanereservation.model.Booking
import com.hayroyalconsult.mavericks.airplanereservation.model.Flight
import com.hayroyalconsult.mavericks.airplanereservation.model.User

/**
 * Created by mavericks on 4/15/18.
 */
class ModelConverter{
    companion object {
        val TAG = "ModelConverter"
        //.....................................................Method to convert gson to class
        inline fun <reified T> GsonToClass(value: String):T{
            val type = object : TypeToken<T>() {}.type
            return Gson().fromJson(value, type)
        }

        //........................................
        fun conUser(res : Cursor) : User?{
            var user = User()
            while (res.moveToNext()){
                user.id = res.getInt(res.getColumnIndex("id"))
                user.email = res.getString(res.getColumnIndex("email"))
                user.password = res.getString(res.getColumnIndex("password"))
                user.role = res.getInt(res.getColumnIndex("role"))
            }
            res.close()
            return user
        }

        fun conListUser(res : Cursor) : ArrayList<User>?{
            var users = ArrayList<User>()
            while (res.moveToNext()){
                var user = User()
                user.id = res.getInt(res.getColumnIndex("id"))
                user.email = res.getString(res.getColumnIndex("email"))
                user.password = res.getString(res.getColumnIndex("password"))
                user.role = res.getInt(res.getColumnIndex("role"))
                users.add(user)
            }
            res.close()
            return users
        }

        fun conFlight(res : Cursor) : Flight?{
            return Flight()
        }

        fun conListFlight(res : Cursor?) : ArrayList<Flight>?{
            var flights = ArrayList<Flight>()
            while (res!!.moveToNext()){
                var flight = Flight()
                flight.id = res.getInt(res.getColumnIndex("id"))
                flight.departure = res.getString(res.getColumnIndex("departure"))
                flight.destination = res.getString(res.getColumnIndex("destination"))
                flight.amount = res.getString(res.getColumnIndex("amount"))
                flight.timeOfDeparture = res.getString(res.getColumnIndex("time_of_departure"))
                flight.merchant = res.getString(res.getColumnIndex("merchant"))
                flight.journeyTime = res.getString(res.getColumnIndex("journey_time"))
                flight.status = res.getString(res.getColumnIndex("status"))
                flights.add(flight)
                Log.e(TAG,flight.toString())
            }
            res.close()
            return flights
        }

        fun conBooking(res:Cursor):ArrayList<Booking>{
            var bookings = ArrayList<Booking>()
            while (res.moveToNext()){
                var book = Booking()
                book.id = res.getInt(res.getColumnIndex("id"))
                book.user_id = res.getInt(res.getColumnIndex("user_id"))
                book.flight_id = res.getInt(res.getColumnIndex("flight_id"))
                book.seat_number = res.getString(res.getColumnIndex("seat_number"))
                book.flight = GsonToClass<Flight>(res.getString(res.getColumnIndex("flight")))
                book.status = res.getString(res.getColumnIndex("status"))
                bookings.add(book)
            }
            res.close()
            return bookings
        }

        fun booking(res:Cursor):Booking{
            val book= Booking()
            while (res.moveToNext()){
                book.id = res.getInt(res.getColumnIndex("id"))
                book.user_id = res.getInt(res.getColumnIndex("user_id"))
                book.flight_id = res.getInt(res.getColumnIndex("flight_id"))
                book.seat_number = res.getString(res.getColumnIndex("seat_number"))
                book.flight = GsonToClass<Flight>(res.getString(res.getColumnIndex("flight")))
                book.status = res.getString(res.getColumnIndex("status"))
            }
            res.close()
            return book
        }
    }
}