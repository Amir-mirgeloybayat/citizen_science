package com.example.citizen_science

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MCQuestionAdapter(private val mContext : Context, private val mListener : MCRecyclerListener) : RecyclerView.Adapter<MCQuestionAdapter.ViewHolder>() {

    private val mItems = MutableList<MCQuestionItem>(0) { MCQuestionItem("","",List<String>(0) { "" }, false) }

    // Add a multiple choice question in the list format
    fun add(item : MCQuestionItem) {
        mItems.add(item)
        notifyItemChanged(mItems.size)
    }

    // Add a multiple choice question that hasn't yet been wrapped
    fun add(item : MultiChoiceQuestion) {
        mItems.add(MCQuestionItem(item))
        notifyItemChanged(mItems.size)
    }

    // Update a question to match the input
    fun edit(item : MultiChoiceQuestion, pos : Int) {
        if(pos >= 0) {
            mItems[pos].title = item.title
            mItems[pos].description = item.description
            mItems[pos].choices = item.choices
            notifyItemChanged(pos)
        }
    }

    fun clearSelections() {
        mItems.forEach {
            it.selected = false
        }
        notifyDataSetChanged()
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

    fun getList() : List<MCQuestionItem> {
        return mItems
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.selectable_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.multichoice_question, parent, false)
        val viewHolder = ViewHolder(v)

        viewHolder.mItemTitle = viewHolder.mItemLayout.findViewById(R.id.itemTitle)
        viewHolder.mItemDesc = viewHolder.mItemLayout.findViewById(R.id.itemDesc)
        viewHolder.mNumAnswers = viewHolder.mItemLayout.findViewById(R.id.numAnswers)
        viewHolder.mCheckBox = viewHolder.mItemLayout.findViewById(R.id.deleteBox)
        viewHolder.mEditButton = viewHolder.mItemLayout.findViewById(R.id.editButton)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItemTitle!!.text = mItems[position].title
        holder.mItemDesc!!.text = mItems[position].description
        holder.mNumAnswers!!.text = "# of possible answers: " + mItems[position].choices.size
        holder.mCheckBox!!.setOnCheckedChangeListener { _, b ->
            mItems[position].selected = b
        }
        holder.mEditButton!!.setOnClickListener {
            mListener.edit(mItems[position], position)
        }
    }

    class ViewHolder internal constructor(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var mItemLayout: View = itemView
        var mItemTitle: TextView? = null
        var mItemDesc: TextView? = null
        var mNumAnswers: TextView? = null
        var mCheckBox: CheckBox? = null
        var mEditButton: Button? = null
    }

    companion object {
        const val TAG = "SelectableItemAdapter"
    }
}