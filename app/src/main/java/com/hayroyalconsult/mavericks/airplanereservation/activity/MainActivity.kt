package com.hayroyalconsult.mavericks.airplanereservation.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import com.hayroyalconsult.mavericks.airplanereservation.R
import com.hayroyalconsult.mavericks.airplanereservation.adapter.MainAdapter
import com.hayroyalconsult.mavericks.airplanereservation.helper.DbHelper
import com.hayroyalconsult.mavericks.airplanereservation.helper.ModelConverter
import com.hayroyalconsult.mavericks.airplanereservation.model.Flight
import com.hayroyalconsult.mavericks.airplanereservation.model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.widget.LinearLayout
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import com.google.gson.Gson


class MainActivity : AppCompatActivity(){

    var user : User? = null
    val TAG = "MainActivity"
    var navHeader : View? = null
    var adapter : MainAdapter? = null
    var flight_list : ArrayList<Flight>? = null
    var helper : DbHelper? = null
    var lv : ListView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.title = "View Flights"
        supportActionBar?.title = "Flight List"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        fab.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
        makeInitialization()
        user = ModelConverter.GsonToClass<User>(intent.getStringExtra("session"))
        Log.e(TAG, user.toString())
        makeRoleCheck()
    }

    private fun makeInitialization() {
       // navHeader = nav_view.getHeaderView(0)
        helper = DbHelper(this).open()
        lv = findViewById(R.id.flight_list)

    }

    override fun onResume() {
        super.onResume()
        adapterWork()
    }
    private fun adapterWork() {
        flight_list = ModelConverter.conListFlight(helper!!.getAllFlight())
        flight_list!!.reverse()
        adapter = MainAdapter(this,flight_list!!)
        if(flight_list!!.size > 0){
            lv!!.visibility = View.VISIBLE
            no_flight.visibility = View.GONE
            lv!!.adapter = adapter
            lv!!.setOnItemClickListener { parent, view, position, id ->
                val flight : Flight = lv!!.getItemAtPosition(position) as Flight
                flight.let{
                    if(flight.status == "OPEN"){
                        Log.e(TAG, flight.toString())
                        startActivity(Intent(this, DetailsActivity::class.java).apply {
                            putExtra("flight", Gson().toJson(flight))
                            putExtra("session", Gson().toJson(user))
                        })
                        Log.e(TAG, flight.toString())
                    }else{
                        Snackbar.make(drawer_layout, "Flight Is Closed", Snackbar.LENGTH_SHORT).show()
                    }

                }

            }

            lv!!.setOnItemLongClickListener { _, _, position, _ ->
                val flight : Flight = lv!!.getItemAtPosition(position) as Flight
                if(user!!.role == 0){
                    val mBottomSheet = BottomSheetDialog(this)
                    val sheetView = layoutInflater.inflate(R.layout.bottom_menu, null)
                    mBottomSheet.setContentView(sheetView)
                    val data = sheetView.findViewById<LinearLayout>(R.id.delete)
                    data.setOnClickListener({
                        mBottomSheet.dismiss()
                        if(helper!!.deleteFlight(flight.id!!)){
                            flight_list!!.remove(flight)
                            Snackbar.make(drawer_layout,"Flight Record Deleted", Snackbar.LENGTH_SHORT).show()
                        }
                        else Snackbar.make(drawer_layout,"Unable To Delete Flight. Try Again", Snackbar.LENGTH_SHORT).show()
                        adapter!!.notifyDataSetChanged()
                    })
                    mBottomSheet.show()
                }
                return@setOnItemLongClickListener true
            }
        }else{
            lv!!.visibility = View.GONE
            no_flight.visibility = View.VISIBLE
        }
    }

    fun makeRoleCheck() {
        if(user?.role == 1){
            fab.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> {
                startActivity(Intent(this, SearchActivity::class.java).apply {
                    putExtra("session", Gson().toJson(user))
                })
                true
            }
            R.id.action_bookings ->{
                if(user!!.role == 1){
                    startActivity(Intent(this, BookingsActivity::class.java).apply {
                        putExtra("session", Gson().toJson(user))
                    })
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
