package com.example.citizen_science

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectableItemAdapter(private val mContext : Context) : RecyclerView.Adapter<SelectableItemAdapter.ViewHolder>() {

    private val mItems = MutableList<SelectableItem>(0) { SelectableItem("","",false) }

    fun add(item : SelectableItem) {
        mItems.add(item)
        notifyItemChanged(mItems.size)
    }

    fun removeSelected() {
        mItems.removeAll {
            it.selected
        }
        notifyDataSetChanged()
    }

    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }

    fun getItem(pos: Int): Any {
        return mItems[pos]
    }

    fun getList() : List<SelectableItem> {
        return mItems
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.selectable_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.selectable_item, parent, false)
        val viewHolder = ViewHolder(v)

        viewHolder.mItemNameLabel = viewHolder.mItemLayout.findViewById(R.id.itemTitle)
        viewHolder.mCheckBox = viewHolder.mItemLayout.findViewById(R.id.deleteBox)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItemNameLabel!!.text = mItems[position].title
        holder.mCheckBox!!.isChecked = mItems[position].selected
        holder.mCheckBox!!.setOnCheckedChangeListener { _, b ->
            if(position < mItems.size)
            {
                mItems[position].selected = b
            }
        }
    }

    class ViewHolder internal constructor(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var mItemLayout: View = itemView
        var mItemNameLabel: TextView? = null
        var mCheckBox: CheckBox? = null
    }

    companion object {
        const val TAG = "SelectableItemAdapter"
    }
}