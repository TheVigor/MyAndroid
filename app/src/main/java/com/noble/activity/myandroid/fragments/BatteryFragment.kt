package com.noble.activity.myandroid.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.constants.BATTERY_INDEX
import com.noble.activity.myandroid.utilities.KeyUtil
import com.noble.activity.myandroid.utilities.getBatteryCapacity
import com.noble.activity.myandroid.utilities.isRequiredField
import kotlinx.android.synthetic.main.battery_sub_view.*
import kotlinx.android.synthetic.main.fragment_battery.*
import kotlinx.android.synthetic.main.toolbar_ui.*
import java.util.*

class BatteryFragment : Fragment() {

    private var health: Int = 0
    private var level: Int = 0
    private var plugged: Int = 0
    private var scale: Int = 0
    private var technology: String? = null
    private var temperature: Int = 0
    private var voltage: Int = 0
    private var deviceStatus: Int = 0

    companion object {
        fun getInstance(mode: Int): BatteryFragment {
            val batteryFragment = BatteryFragment()
            val bundle = Bundle()
            bundle.putInt(KeyUtil.IS_USER_COME_FROM_DASHBOARD, mode)
            batteryFragment.arguments = bundle
            return batteryFragment
        }
    }

    private val mBatLow = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                iv_battery_charging.setImageResource(R.mipmap.ic_low_battery)
            } catch (e: Exception) {

            }

        }
    }

    private val mBatInfoReceiver = object : BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        override fun onReceive(c: Context, intent: Intent) {

            deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
            plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0)
            technology = Objects.requireNonNull(intent.extras).getString(BatteryManager.EXTRA_TECHNOLOGY)
            temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)

            try {
                getBatteryInfo()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        activity!!.registerReceiver(mBatInfoReceiver, filter)

        val filter2 = IntentFilter(Intent.ACTION_BATTERY_LOW)
        activity!!.registerReceiver(mBatLow, filter2)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_battery, container, false)
        (activity as MainActivity).setAdapterPosition(BATTERY_INDEX)
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getBundleData()
        val value = activity!!.getBatteryCapacity()

        if (value != (-1).toDouble()) {
            tvBattryMahValue.visibility = View.VISIBLE
            tvBattryMahValue.text = value.toString() + " mAh"
        }
    }

    private fun getBundleData() {
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(KeyUtil.IS_USER_COME_FROM_DASHBOARD))
                initToolbar(bundle.getInt(KeyUtil.IS_USER_COME_FROM_DASHBOARD))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getBatteryInfo() {
        if (iv_battery_charging != null) {

            if (deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
                iv_battery_charging.visibility = View.VISIBLE
                iv_battery_charging.setImageResource(R.mipmap.ic_battery)
            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING)
                iv_battery_charging.visibility = View.GONE

            if (deviceStatus == BatteryManager.BATTERY_STATUS_FULL)
                iv_battery_charging.visibility = View.GONE

            if (deviceStatus == BatteryManager.BATTERY_STATUS_UNKNOWN)
                iv_battery_charging.visibility = View.GONE

            if (deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING)
                iv_battery_charging.visibility = View.GONE
        }

        val styledString = SpannableString("$level%")

        styledString.setSpan(
            RelativeSizeSpan(0.7f),
            styledString.length - 1,
            styledString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tv_battery_fragment_percentage.text = styledString
        pb_battery_fragment.progress = level

        tv_battery_temperature.text = temperature.toString() + activity!!.resources.getString(R.string.c_symbol)

        if (isRequiredField(technology)) {
            tv_battery_type.text = technology
        }

        tv_battery_voltage.text = voltage.toString() + "mV"
        tv_battery_scale.text = scale.toString()

        when (health) {
            1 -> tv_battery_health.text = activity!!.resources.getString(R.string.unknown)
            2 -> tv_battery_health.text = activity!!.resources.getString(R.string.good)
            3 -> tv_battery_health.text = activity!!.resources.getString(R.string.over_heated)
            4 -> tv_battery_health.text = activity!!.resources.getString(R.string.dead)
            5 -> tv_battery_health.text = activity!!.resources.getString(R.string.over_voltage)
            6 -> tv_battery_health.text = activity!!.resources.getString(R.string.failed)
            else -> tv_battery_health.text = activity!!.resources.getString(R.string.cold)
        }

        if (plugged == 1)
            tv_power_source.text = activity!!.resources.getString(R.string.ac_power)
        else
            tv_power_source.text = activity!!.resources.getString(R.string.battery)
    }

    private fun initToolbar(mode: Int) {
        if (mode == 1) {
            iv_back.visibility = View.VISIBLE
            iv_back.setColorFilter(ContextCompat.getColor(activity!!, R.color.darkBlue))
            iv_back.setOnClickListener { activity!!.onBackPressed() }
            (activity as MainActivity).bottomSheetDisable(true)
        } else {
            iv_back.visibility = View.GONE
        }

        tv_title.text = activity!!.resources.getString(R.string.battery)
        tv_title.setTextColor(activity!!.resources.getColor(R.color.battery))
    }

    override fun onDestroyView() {
        super.onDestroyView()

        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mBatInfoReceiver)
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mBatLow)
    }

}