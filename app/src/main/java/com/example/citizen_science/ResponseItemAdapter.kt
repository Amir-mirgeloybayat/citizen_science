package com.example.citizen_science

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResponseItemAdapter(val mContext: Context, val responses:List<String>, val mLayout:Int) : RecyclerView.Adapter<ResponseItemAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(mLayout, viewGroup, false)
        return ViewHolder(v)
    }

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            //use position value  to get clicked data from list

            //TODO pass the user id back to the activity, so it can run ViewResultsActivity
        }
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}