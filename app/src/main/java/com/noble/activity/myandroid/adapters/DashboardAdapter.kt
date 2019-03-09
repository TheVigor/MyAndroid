package com.noble.activity.myandroid.adapters

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.extensions.avoidDoubleClicks
import com.noble.activity.myandroid.extensions.removeAllFragmentExceptDashboard
import com.noble.activity.myandroid.extensions.replaceFragment
import com.noble.activity.myandroid.models.DashboardInfo

class DashboardAdapter(private val dashboardList: List<DashboardInfo>) :
    RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {

    private var currentFragmentIndex = 0
    private val handler = Handler()

    var position: Int
        get() = currentFragmentIndex
        set(position) {
            currentFragmentIndex = position
            notifyDataSetChanged()
        }

    inner class DashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFragmentIcon: ImageView = itemView.findViewById(R.id.iv_fragment_icon)
        val ivFragmentTemp: ImageView = itemView.findViewById(R.id.iv_fragment_temp)
        val tvFragmentName: TextView = itemView.findViewById(R.id.tv_fragment_name)
        val cvDashboard: CardView = itemView.findViewById(R.id.cv_dashboard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DashboardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_dashboard_item, parent, false))

    override fun onBindViewHolder(holder: DashboardAdapter.DashboardViewHolder, position: Int) {
        val dashboardInfo = dashboardList[position]

        holder.ivFragmentIcon.setImageResource(dashboardInfo.fragmentIcon)

        if (currentFragmentIndex == position) {
            holder.cvDashboard.elevation = 0f

            holder.cvDashboard.setCardBackgroundColor(Color.parseColor(dashboardInfo.fragmentColor))

            holder.tvFragmentName.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.white))

            holder.ivFragmentIcon.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.white),
                PorterDuff.Mode.SRC_ATOP)

        } else {

            holder.cvDashboard.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.bottomsheet_card_bg))

            holder.tvFragmentName.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.font_black))

            holder.ivFragmentIcon.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.dashboard_icon_color),
                PorterDuff.Mode.SRC_ATOP)
        }

        holder.tvFragmentName.text = dashboardInfo.fragmentName
        holder.ivFragmentTemp.setBackgroundColor(Color.parseColor(dashboardInfo.fragmentColor))
        holder.cvDashboard.setOnClickListener{
            avoidDoubleClicks(holder.cvDashboard)
            (holder.itemView.context as MainActivity).hideBottomSheet()

            if (currentFragmentIndex == dashboardInfo.fragmentIndex) {
                return@setOnClickListener
            }
            currentFragmentIndex = dashboardInfo.fragmentIndex

            handler.postDelayed({
                if (position == 0) {
                    (holder.itemView.context as MainActivity)
                        .supportFragmentManager.popBackStackImmediate()
                } else {
                    (holder.itemView.context).removeAllFragmentExceptDashboard()
                    (holder.itemView.context).replaceFragment(dashboardInfo.fragment, true, false)
                }
            }, 300)
        }
    }

    override fun getItemCount() = dashboardList.size
}