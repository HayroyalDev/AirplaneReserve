package com.hayroyalconsult.mavericks.airplanereservation.activity

import android.app.Activity
import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import com.hayroyalconsult.mavericks.airplanereservation.R
import com.hayroyalconsult.mavericks.airplanereservation.helper.DbHelper
import com.hayroyalconsult.mavericks.airplanereservation.helper.ModelConverter
import com.hayroyalconsult.mavericks.airplanereservation.model.Flight
import kotlinx.android.synthetic.main.activity_search.*
import android.speech.RecognizerIntent
import android.content.Intent
import android.content.ActivityNotFoundException
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import com.google.gson.Gson
import com.hayroyalconsult.mavericks.airplanereservation.adapter.MainAdapter
import com.hayroyalconsult.mavericks.airplanereservation.adapter.SpeechAdapter
import com.hayroyalconsult.mavericks.airplanereservation.model.User
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class SearchActivity : AppCompatActivity() {

    var helper : DbHelper? = null
    var result : ArrayList<Flight>? = null
    val TAG = "SearchResult"
    private val REQ_CODE_SPEECH_INPUT = 100
    var type = 0
    var user : User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        helper = DbHelper(this).open()
        user = ModelConverter.GsonToClass<User>(intent.getStringExtra("session"))

        departure.setOnClickListener {
            type = 0
            startVoiceInput()
        }
        destination.setOnClickListener {
            type = 1
            startVoiceInput()
        }
        search_btn.setOnClickListener {
            if(departure.text.toString().isNotEmpty() && destination.text.toString().isNotEmpty()){
                result = ModelConverter.conListFlight(helper!!.searchFlight(departure.text.toString(), destination.text.toString()))
                if(result!!.isEmpty()){
                    Snackbar.make(container, "Search Result is Empty", Snackbar.LENGTH_SHORT).show()
                }else{
                    search_title.visibility = View.VISIBLE
                    search_list.visibility = View.VISIBLE
                    var adapter = MainAdapter(this, result!!)
                    val lv = findViewById<ListView>(R.id.search_list)
                    lv.adapter = adapter
                    lv.setOnItemClickListener { _, _, position, _ ->
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

                    lv.setOnItemLongClickListener { _, _, position, _ ->
                        val flight : Flight = lv!!.getItemAtPosition(position) as Flight
                        if(user!!.role == 0){
                            val mBottomSheet = BottomSheetDialog(this)
                            val sheetView = layoutInflater.inflate(R.layout.bottom_menu, null)
                            mBottomSheet.setContentView(sheetView)
                            val data = sheetView.findViewById<LinearLayout>(R.id.delete)
                            data.setOnClickListener({
                                mBottomSheet.dismiss()
                                if(helper!!.deleteFlight(flight.id!!))result!!.remove(flight)
                                else Snackbar.make(drawer_layout,"Unable To Delete Flight. Try Again", Snackbar.LENGTH_SHORT).show()
                                adapter!!.notifyDataSetChanged()
                            })
                            mBottomSheet.show()
                        }
                        return@setOnItemLongClickListener true
                    }
                    Log.e(TAG, result.toString())
                }
            }else{
                Snackbar.make(container, "Search Result is Empty", Snackbar.LENGTH_SHORT).show()
            }

        }
    }


    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        if(type == 0){
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Flight Departing from?")
        }else{
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Destination of flight?")
        }
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Log.e(TAG, a.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if(result != null){
                        bringDialog(result)
                    }
                    Log.e(TAG, result.toString())
                }
            }
        }
    }

    private fun bringDialog( result : ArrayList<String>){
        val dialog : AlertDialog = AlertDialog.Builder(this).create()
        val mInflater = LayoutInflater.from(this)
        val v = mInflater.inflate(R.layout.speech_result, null)
        val lv = v.findViewById<ListView>(R.id.speech_list)
        val adapter = SpeechAdapter(this, result)
        lv.adapter = adapter
        lv.setOnItemClickListener { _, _, position, _ ->
            val selected = lv!!.getItemAtPosition(position) as String
            if(type == 0){
                departure.setText(selected)

            }else{
                destination.setText(selected)
            }
            dialog.dismiss()
        }
        dialog.setView(v)
        dialog.show()

    }
}
