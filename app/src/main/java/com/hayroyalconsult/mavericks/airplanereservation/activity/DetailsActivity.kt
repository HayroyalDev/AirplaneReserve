package com.hayroyalconsult.mavericks.airplanereservation.activity

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.hayroyalconsult.mavericks.airplanereservation.R
import com.hayroyalconsult.mavericks.airplanereservation.helper.DbHelper
import com.hayroyalconsult.mavericks.airplanereservation.helper.ModelConverter
import com.hayroyalconsult.mavericks.airplanereservation.model.Booking
import com.hayroyalconsult.mavericks.airplanereservation.model.Flight
import com.hayroyalconsult.mavericks.airplanereservation.model.User

import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.content_details.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class DetailsActivity : AppCompatActivity() {

    var flight : Flight? = null
    var user : User? = null
    var helper:DbHelper? = null
    val TAG = "DETAILS ACTIVITY"
    var new_booking : Booking? = null
    var test : ArrayList<Booking>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        helper = DbHelper(this).open()
        user = ModelConverter.GsonToClass<User>(intent.getStringExtra("session"))
        flight = ModelConverter.GsonToClass<Flight>(intent.getStringExtra("flight"))
        flight.let {
            merchant.text = "${merchant.text} ${flight!!.merchant}"
            departure.text = "${departure.text} ${flight!!.departure}"
            destination.text = "${destination.text} ${flight!!.destination}"
            amount.text = "${amount.text} NGN${flight!!.amount}"
            time_of_departure.text = "${time_of_departure.text} ${flight!!.timeOfDeparture}"
            journey_time.text = "${journey_time.text} ${flight!!.journeyTime}"
            status.text = "${status.text} ${flight!!.status}"
            getTest()


            book_btn.setOnClickListener {
                getTest()
                if(test!!.size ==  0){
                    if(helper!!.addBooking(flight!!,user!!, seatNumber())){
                        AlertDialog.Builder(this)
                                .setMessage("Are You Sure You Want To Book This Flight?")
                                .setTitle("Flight Booking")
                                .setPositiveButton("Yes", { dialog, _ ->
                                    dialog.dismiss()
                                    doBooking()
                                })
                                .setNegativeButton("No", {dialog, _ ->
                                    dialog.dismiss()
                                }).show()
                    }else{
                        Snackbar.make(container,"Unable to book flight", Snackbar.LENGTH_SHORT).show()
                    }
                }else{
                    Snackbar.make(container,"This Flight Has Already Been Booked", Snackbar.LENGTH_SHORT).show()
                }

            }

        }
    }

    private fun getTest() {
        test = ModelConverter.conBooking(helper!!.getBooked(user!!.id, flight!!.id!!))
        if(user!!.role == 0){
            book_btn.visibility = View.GONE
        }else{
            if(test!!.size > 0){
                seat_number.visibility = View.VISIBLE
                seat_number.text = "Seat Number: ${test!![0].seat_number} | ${test!![0].status}"
            }
        }
    }

    fun doBooking(){
        val pb = ProgressDialog(this)
        pb.setMessage("Processing...")
        pb.show()
        Thread(Runnable {
            Thread.sleep(TimeUnit.SECONDS.toMillis(3))
            if(helper!!.addBooking(flight!!, user!!, seatNumber())){
                new_booking = ModelConverter.booking(helper!!.getBooked(user!!.id, flight!!.id!!))
                Log.e(TAG, new_booking!!.toString())
                runOnUiThread {
                    seat_number.visibility = View.VISIBLE
                    seat_number.text = "Seat Number: ${new_booking!!.seat_number} | ${new_booking!!.status}"
                    Snackbar.make(container,"Flight Booked", Snackbar.LENGTH_SHORT).show()
                    pb.dismiss()
                }
            }else{
                Snackbar.make(container,"Unable to book flight", Snackbar.LENGTH_SHORT).show()
            }
        }).start()

    }
    fun seatNumber():String{
        val letters = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        var selected = "${letters[Random().nextInt(letters.length)]}${Random().nextInt(99)}"
        Log.e(TAG, selected)
        return selected
    }
}
