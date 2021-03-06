package com.hayroyalconsult.mavericks.airplanereservation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.hayroyalconsult.mavericks.airplanereservation.R
import com.hayroyalconsult.mavericks.airplanereservation.model.Booking

/**
 * Created by mavericks on 4/20/18.
 */
class BookingAdapter(var context: Context, var list : ArrayList<Booking>) : BaseAdapter(){

    var mInflater = LayoutInflater.from(context)
    override fun getView(position: Int, altView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        var convertView = altView
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.flight_row, null)
            holder = ViewHolder()
            holder.merchantName = convertView.findViewById(R.id.merchant_name)
            holder.flightDetail = convertView.findViewById(R.id.flight_detail)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        holder.merchantName!!.text = list[position].flight!!.merchant
        holder.flightDetail!!.text = "${list[position].flight!!.departure} - ${list[position].flight!!.destination} -> BOOKED"
        return convertView!!
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list.size.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    internal class ViewHolder {
        var merchantName : TextView? = null
        var flightDetail : TextView? = null
    }
}