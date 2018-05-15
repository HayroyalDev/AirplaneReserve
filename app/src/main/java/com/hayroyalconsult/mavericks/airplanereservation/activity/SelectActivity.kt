package com.hayroyalconsult.mavericks.airplanereservation.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.hayroyalconsult.mavericks.airplanereservation.R
import com.hayroyalconsult.mavericks.airplanereservation.helper.DbHelper
import com.hayroyalconsult.mavericks.airplanereservation.helper.ModelConverter
import com.hayroyalconsult.mavericks.airplanereservation.model.User
import kotlinx.android.synthetic.main.activity_select.*
import java.util.concurrent.TimeUnit



class SelectActivity : AppCompatActivity() {

    var role : Int? = 0
    var pb : ProgressDialog? = null
    var TAG = "SelectActivity"
    var helper : DbHelper? = null
    var user : User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        helper = DbHelper(this)
        helper!!.open()
        pb = ProgressDialog(this)
        admin_sel.setOnClickListener {
            role = 0
            select_layout.visibility = View.GONE
            form_layout.visibility = View.VISIBLE
        }
        user_sel.setOnClickListener {
            role = 1
            select_layout.visibility = View.GONE
            form_layout.visibility = View.VISIBLE
        }

        login_btn.setOnClickListener {
            doLogin()

        }

    }

    private fun doLogin(){
        val email = input_email.text.toString()
        val password = input_password.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            pb?.setMessage("Please Wait...$role")
            pb?.show()
            Thread(Runnable {
                Log.e(TAG,"New Tag")
                //Thread.sleep(TimeUnit.SECONDS.toMillis(3))
                user = ModelConverter.conUser(helper!!.getSpecificUser(email,password))
                Log.e(TAG,user.toString())
                user.let {
                  Log.e(TAG, "Not Null")
                }
                if(user != null || user != User()){
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("session",Gson().toJson(user))
                    })
                }else{
                    this.runOnUiThread({
                        pb!!.dismiss()
                        input_email.error = "Invalid Credentials"
                        input_password.error = "Invalid Credential"
                    })
                }
            }).start()
        }else{
            pb!!.dismiss()
            if(email.isEmpty()) input_email.error = "Cannot be empty"
            if(password.isEmpty()) input_password.error = "Cannot be empty"
        }
    }

    override fun onBackPressed() {
        if(form_layout.visibility == View.VISIBLE){
            select_layout.visibility = View.VISIBLE
            form_layout.visibility = View.GONE
        }else super.onBackPressed()
    }

    class ExecuteLogin : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            return "true"
        }

    }

}
