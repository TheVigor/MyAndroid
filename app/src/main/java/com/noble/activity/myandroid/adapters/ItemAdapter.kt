package com.noble.activity.myandroid.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.models.ItemInfo


class ItemAdapter(private var itemInformationData: ArrayList<ItemInfo>):
    RecyclerView.Adapter<ItemAdapter.ItemVH>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemVH(LayoutInflater.from(parent.context).inflate(R.layout.row_item_item, parent, false))

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.bindData(itemInformationData[position])
    }

    override fun getItemCount(): Int = itemInformationData.size

    inner class ItemVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindData(itemInfo: ItemInfo) {

            val tvLabel: TextView = itemView.findViewById(R.id.tvLabel)
            val tvData: TextView = itemView.findViewById(R.id.tvData)

            tvLabel.text = itemInfo.label
            tvData.text = itemInfo.data
        }
    }
}