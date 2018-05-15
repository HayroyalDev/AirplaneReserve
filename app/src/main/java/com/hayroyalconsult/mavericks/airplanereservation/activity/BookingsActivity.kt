package com.hayroyalconsult.mavericks.airplanereservation.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.google.gson.Gson
import com.hayroyalconsult.mavericks.airplanereservation.R
import com.hayroyalconsult.mavericks.airplanereservation.adapter.BookingAdapter
import com.hayroyalconsult.mavericks.airplanereservation.helper.DbHelper
import com.hayroyalconsult.mavericks.airplanereservation.helper.ModelConverter
import com.hayroyalconsult.mavericks.airplanereservation.model.Booking
import com.hayroyalconsult.mavericks.airplanereservation.model.User

import kotlinx.android.synthetic.main.activity_bookings.*
import kotlinx.android.synthetic.main.content_bookings.*

class BookingsActivity : AppCompatActivity() {

    val TAG = "BOOKING ACTIVITY"
    var user : User? = null
    var book_list : ArrayList<Booking>? = null
    var helper : DbHelper? = null
    var adapter : BookingAdapter? = null
    var lv : ListView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookings)
        setSupportActionBar(toolbar)
        toolbar.title = "View Bookings"
        lv = findViewById(R.id.booking_list)
        supportActionBar?.title = "Booking List"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        helper = DbHelper(this).open()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        user = ModelConverter.GsonToClass<User>(intent.getStringExtra("session"))
        book_list = if(user!!.role == 0) ModelConverter.conBooking(helper!!.allBookings())
        else ModelConverter.conBooking(helper!!.getBookingByUser(user!!.id))
        book_list!!.reverse()
        adapter = BookingAdapter(this,book_list!!)
        if(book_list!!.size > 0){
            lv!!.visibility = View.VISIBLE
            no_book.visibility = View.GONE
            lv!!.adapter = adapter
            lv!!.setOnItemClickListener { _, _, position, _ ->
                val booking : Booking = lv!!.getItemAtPosition(position) as Booking
                booking.let{
                    Log.e(TAG, booking.toString())
                    if(booking.status == Booking.CONFIRMED){
                        startActivity(Intent(this, DetailsActivity::class.java).apply {
                            putExtra("flight", Gson().toJson(booking.flight))
                            putExtra("session", Gson().toJson(user))
                        })
                    }else{
                        Snackbar.make(container, "Flight Is Closed", Snackbar.LENGTH_SHORT).show()
                    }
                }

            }

            lv!!.setOnItemLongClickListener { _, _, position, _ ->
                val booking : Booking = lv!!.getItemAtPosition(position) as Booking
                if(user!!.role == 1){
                    val mBottomSheet = BottomSheetDialog(this)
                    val sheetView = layoutInflater.inflate(R.layout.bottom_menu, null)
                    mBottomSheet.setContentView(sheetView)
                    val data = sheetView.findViewById<LinearLayout>(R.id.delete)
                    sheetView.findViewById<TextView>(R.id.action_name).text = "Delete Booking"
                    data.setOnClickListener({
                        mBottomSheet.dismiss()
                        if(helper!!.deleteBooking(booking.id)){
                            book_list!!.remove(booking)
                            Snackbar.make(container, "Booking Deleted", Snackbar.LENGTH_SHORT).show()
                        }
                        else Snackbar.make(container,"Unable To Delete Flight. Try Again", Snackbar.LENGTH_SHORT).show()
                        adapter!!.notifyDataSetChanged()
                    })
                    mBottomSheet.show()
                }
                return@setOnItemLongClickListener true
            }
        }else{
            lv!!.visibility = View.GONE
            no_book.visibility = View.VISIBLE
        }
    }

}
