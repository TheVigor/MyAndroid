package com.noble.activity.myandroid.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.extensions.addFragment
import com.noble.activity.myandroid.extensions.avoidDoubleClicks
import com.noble.activity.myandroid.fragments.AppInfoFragment
import com.noble.activity.myandroid.models.DeviceInfo

class DeviceAdapter(appsList: ArrayList<DeviceInfo>, internal var mode: Int)
    : RecyclerView.Adapter<DeviceAdapter.DeviceVH>(), Filterable {

    private val itemsList:ArrayList<DeviceInfo> = appsList
    private var itemsListFiltered: List<DeviceInfo>

    init {
        itemsListFiltered = appsList
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(charSequence:CharSequence): FilterResults {
                val charString = charSequence.toString()
                itemsListFiltered = if (charString.isEmpty()) {
                    itemsList
                } else {
                    val filteredList = ArrayList<DeviceInfo>()
                    for (row in itemsList) {
                        if (row.appLabel.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = itemsListFiltered
                return filterResults
            }

            override fun publishResults(charSequence:CharSequence, filterResults: FilterResults) {
                itemsListFiltered = filterResults.values as ArrayList<DeviceInfo>
                this@DeviceAdapter.notifyDataSetChanged()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DeviceVH(LayoutInflater.from(parent.context).inflate(R.layout.row_infomation, parent, false))


    override fun onBindViewHolder(holder: DeviceVH, position: Int) {
        holder.bindData(itemsListFiltered[position], position)
    }

    override fun getItemCount(): Int {
        return itemsListFiltered.size
    }

    inner class DeviceVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(deviceInfo: DeviceInfo, position: Int) {

            val ivAppLogo: ImageView? = itemView.findViewById(R.id.iv_app_icon)
            val tvAppName: TextView? = itemView.findViewById(R.id.appname)
            val tvAppPackageName: TextView? = itemView.findViewById(R.id.tv_app_package_name)

            tvAppName?.text = deviceInfo.appLabel
            tvAppPackageName?.text = deviceInfo.packageName
            ivAppLogo?.setImageDrawable(deviceInfo.appLogo)

            itemView.setOnClickListener {
                avoidDoubleClicks(itemView)
                itemView.context.addFragment(AppInfoFragment.getInstance(mode, deviceInfo.packageName, position), true, true)
            }
        }
    }
}