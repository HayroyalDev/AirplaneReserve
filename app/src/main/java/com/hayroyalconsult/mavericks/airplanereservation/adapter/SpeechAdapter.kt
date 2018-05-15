package com.hayroyalconsult.mavericks.airplanereservation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.hayroyalconsult.mavericks.airplanereservation.R

/**
 * Created by mavericks on 4/25/18.
 */
class SpeechAdapter(var context: Context, val list : ArrayList<String>) : BaseAdapter(){
    var mInflater = LayoutInflater.from(context)
    override fun getView(position: Int, altView: View?, parent: ViewGroup?): View {
        val holder : ViewHolder
        var convertView = altView
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.speech_row, null)
            holder = ViewHolder()
            holder.result = convertView.findViewById(R.id.speech_txt)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        holder.result?.text = list[position]
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
        var result : TextView? = null
    }

}