package com.hayroyalconsult.mavericks.airplanereservation.helper

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.hayroyalconsult.mavericks.airplanereservation.model.Booking
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.content.ContentValues
import com.google.gson.Gson
import com.hayroyalconsult.mavericks.airplanereservation.model.Flight
import com.hayroyalconsult.mavericks.airplanereservation.model.User


/**
 * Created by mavericks on 4/5/18.
 */
class DbHelper(var context: Context) {
    val TAG = "DbHelper"
    val DATABASE_NAME = "air.db"
    var helper: SqlHelp? = null
    var db: SQLiteDatabase? = null
    private val DB_PATH = "/data/data/com.hayroyalconsult.mavericks.airplanereservation/databases/"
    var dbFile: File? = null

    @Throws(SQLException::class)
    fun open(): DbHelper {
        helper = SqlHelp(context)
        db = helper!!.writableDatabase
        return this@DbHelper
    }



    inner class SqlHelp(context: Context, private var dbFile: File = File(DB_PATH + DATABASE_NAME)) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

        @Synchronized
        override fun getWritableDatabase(): SQLiteDatabase {
            Log.e(TAG, DB_PATH)
            if (!dbFile.exists()) {
                val db = super.getWritableDatabase()
                copyDataBase(db.path)
            }
            return super.getWritableDatabase()
        }

        @Synchronized
        override fun getReadableDatabase(): SQLiteDatabase {
            if (!dbFile.exists()) {
                val db = super.getReadableDatabase()
                copyDataBase(db.path)
            }
            return super.getReadableDatabase()
        }

        private fun copyDataBase(dbPath: String) {
            try {
                val assestDB = context.assets.open(DATABASE_NAME)
                val appDB = FileOutputStream(dbPath, false)

                val buffer = ByteArray(1024)
                var length: Int = assestDB.read(buffer)
                while (length > 0) {
                    appDB.write(buffer, 0, length)
                    length = assestDB.read(buffer)
                }

                appDB.flush()
                appDB.close()
                assestDB.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, e.toString())
            }

        }

        override fun onCreate(db: SQLiteDatabase) {

        }

        override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {

        }

        override fun close() {
            db!!.close()
        }

    }


    fun getAllUser(): Cursor {
        return db!!.rawQuery("select * from users", null)
    }

    fun getSpecificUser(email:String, password:String) : Cursor{
        return db!!.rawQuery("select * from users where `email` = '$email'and `password` = '$password'", null)
    }

    fun getAllFlight() : Cursor{
        return db!!.rawQuery("select * from flights", null)
    }

    fun addFlight(flight: Flight) : Boolean{
        return try{
            val cv = ContentValues()
            cv.put("merchant", flight.merchant)
            cv.put("departure", flight.departure)
            cv.put("destination", flight.destination)
            cv.put("amount", flight.amount)
            cv.put("time_of_departure", flight.timeOfDeparture)
            cv.put("journey_time", flight.journeyTime)
            cv.put("status",flight.status)
            db!!.insert("flights", null, cv)
            true
        }catch (ex: Exception){
            Log.e(TAG, ex.toString())
            false
        }
    }

    fun deleteFlight(id: Int) : Boolean{
        db!!.execSQL("DELETE FROM flights WHERE `id` = '$id'")
        return true
    }
    fun deleteBooking(id: Int) : Boolean{
        db!!.rawQuery("DELETE FROM bookings WHERE `id` = '$id'", null)
        return true
    }

    fun addBooking(flight: Flight, user: User, sn:String) : Boolean{
        return try{
            val cv = ContentValues()
            cv.put("flight_id", flight.id)
            cv.put("user_id", user.id)
            cv.put("seat_number", sn)
            cv.put("status",Booking.CONFIRMED)
            cv.put("flight", Gson().toJson(flight))
            db!!.insert("bookings", null, cv)
            true
        }catch (ex: Exception){
            Log.e(TAG, ex.toString())
            false
        }
    }

    fun allBookings() : Cursor{
        return db!!.rawQuery("select * from bookings", null)
    }

    fun getBookingByUser(id:Int) : Cursor{
        return db!!.rawQuery("select * from bookings where `user_id` = $id", null)
    }

    fun getBooked(id : Int, flight_id : Int) : Cursor{
        return db!!.rawQuery("select * from bookings where `user_id` = $id and `flight_id` = $flight_id", null)
    }

    fun searchFlight(departure : String, destination : String) : Cursor{
        return db!!.rawQuery("select * from flights where `departure` = '$departure' COLLATE NOCASE and `destination` = '$destination' COLLATE NOCASE and `status` = 'OPEN' COLLATE NOCASE", null)
    }
}
