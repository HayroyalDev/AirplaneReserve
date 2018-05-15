package com.hayroyalconsult.mavericks.airplanereservation.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import com.hayroyalconsult.mavericks.airplanereservation.R

import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.content_add.*
import android.widget.TimePicker
import android.widget.DatePicker
import com.hayroyalconsult.mavericks.airplanereservation.helper.DbHelper
import com.hayroyalconsult.mavericks.airplanereservation.model.Flight
import java.text.SimpleDateFormat
import java.util.*


class AddActivity : AppCompatActivity() {

    var time : String? = null
    val TAG = "AddACTIVITY"
    var helper : DbHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setSupportActionBar(toolbar)
        helper = DbHelper(this).open()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        time_of_departure.setOnClickListener {
            val dialogView = View.inflate(this, R.layout.date_time_picker, null)
            val alertDialog = AlertDialog.Builder(this).create()
            dialogView.findViewById<Button>(R.id.date_time_set).setOnClickListener{
                val datePicker = dialogView.findViewById(R.id.date_picker) as DatePicker
                val timePicker = dialogView.findViewById(R.id.time_picker) as TimePicker
                val calendar = GregorianCalendar(datePicker.year,
                        datePicker.month,
                        datePicker.dayOfMonth,
                        timePicker.currentHour,
                        timePicker.currentMinute)

                val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS")
                time = sdf.format(calendar.time)
                time_of_departure.setText(time.toString())
                alertDialog.dismiss()
            }
            alertDialog.setView(dialogView)
            alertDialog.show()
        }

        add_flight_btn.setOnClickListener {
            when {
                merchant.text.isEmpty() -> merchant.error ="Field cannot be empty"
                departure.text.isEmpty() -> departure.error ="Field cannot be empty"
                destination.text.isEmpty() -> destination.error ="Field cannot be empty"
                amount.text.isEmpty() -> amount.error ="Field cannot be empty"
                time_of_departure.text.isEmpty() -> time_of_departure.error ="Field cannot be empty"
                journey_time.text.isEmpty() -> journey_time.error ="Field cannot be empty"
                else -> {
                    val flight = Flight()
                    flight.merchant = merchant.text.toString()
                    flight.destination = destination.text.toString()
                    flight.departure = departure.text.toString()
                    flight.amount = amount.text.toString()
                    flight.timeOfDeparture = time_of_departure.text.toString()
                    flight.journeyTime = journey_time.text.toString()
                    flight.status = "OPEN"
                    if(helper!!.addFlight(flight)){
                        Snackbar.make(container, "Flight Added", Snackbar.LENGTH_SHORT).show()
                        onBackPressed()
                    }else{
                        Snackbar.make(container, "Unable to add flight. ", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}
