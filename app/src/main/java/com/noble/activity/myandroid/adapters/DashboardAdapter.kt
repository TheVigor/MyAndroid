package com.noble.activity.myandroid.adapters

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
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

class DashboardAdapter(
    private val context: MainActivity,
    private val list: List<DashboardInfo>
) :
    RecyclerView.Adapter<DashboardAdapter.MyViewHolder>() {
    private var currentFragmentIndex = 0
    private val handler = Handler()

    var postion: Int
        get() = currentFragmentIndex
        set(postion) {
            currentFragmentIndex = postion
            notifyDataSetChanged()
        }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFragmentIcon: ImageView
        val ivFragmentTemp: ImageView
        val tvFragmentName: TextView
        val cvDashboard: CardView

        init {
            ivFragmentIcon = itemView.findViewById(R.id.iv_fragment_icon)
            tvFragmentName = itemView.findViewById(R.id.tv_fragment_name)
            ivFragmentTemp = itemView.findViewById(R.id.iv_fragment_temp)
            cvDashboard = itemView.findViewById(R.id.cv_dashboard)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_dashboard_item, parent, false)
        return MyViewHolder(view)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: DashboardAdapter.MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val dashboardInfo = list[position]
        holder.ivFragmentIcon.setImageResource(dashboardInfo.fragmentIcon)
        if (currentFragmentIndex == position) {
            holder.cvDashboard.elevation = 0f
            holder.cvDashboard.setCardBackgroundColor(Color.parseColor(dashboardInfo.fragmentColor))
            holder.tvFragmentName.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.ivFragmentIcon.setColorFilter(
                ContextCompat.getColor(context, R.color.white),
                PorterDuff.Mode.SRC_ATOP
            )
        } else {
            holder.cvDashboard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.bottomsheet_card_bg))
            holder.tvFragmentName.setTextColor(ContextCompat.getColor(context, R.color.black))
            holder.ivFragmentIcon.setColorFilter(
                ContextCompat.getColor(context, R.color.dashboard_icon_color),
                PorterDuff.Mode.SRC_ATOP
            )
        }
        holder.tvFragmentName.text = dashboardInfo.fragmentName
        holder.ivFragmentTemp.setBackgroundColor(Color.parseColor(dashboardInfo.fragmentColor))
        holder.cvDashboard.setOnClickListener(View.OnClickListener {
            avoidDoubleClicks(holder.cvDashboard)
            context.hideBottomSheet()
            if (currentFragmentIndex == dashboardInfo.fragmentIndex) {
//                val fragment = context.supportFragmentManager
//                    .findFragmentByTag(AboutUsFragment::class.java!!.getCanonicalName())
//                if (fragment != null) {
//                    context.getSupportFragmentManager().beginTransaction().remove(fragment).commit()
//                    context.getSupportFragmentManager().popBackStackImmediate()
//                }
//                return@OnClickListener
            }
            currentFragmentIndex = dashboardInfo.fragmentIndex
            handler.postDelayed({
                if (position == 0) {
                    context.supportFragmentManager.popBackStackImmediate()
                } else {
                    context.removeAllFragmentExceptDashboard()
                    context.replaceFragment(dashboardInfo.fragment, true, false)
                }
            }, 300)
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }


}