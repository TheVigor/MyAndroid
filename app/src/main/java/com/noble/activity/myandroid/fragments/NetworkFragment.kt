package com.noble.activity.myandroid.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.view.ContextThemeWrapper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.models.addEntry
import com.noble.activity.myandroid.models.initCupGraph
import com.noble.activity.myandroid.models.setupGradient
import com.noble.activity.myandroid.utilities.*
import kotlinx.android.synthetic.main.fragment_network.*
import kotlinx.android.synthetic.main.toolbar_ui.*

class NetworkFragment : Fragment() {

    private var mStartTX: Long = 0
    private var mStartRX: Long = 0
    private var tStartTX: Long = 0
    private var txBytes: Long = 0
    private var rxBytes: Long = 0
    private var mHandler: Handler? = null
    internal var x = ""
    private var networkStatus = 0

    companion object {
        fun getInstance(mode: Int?): NetworkFragment {
            val networkFragment = NetworkFragment()
            val bundle = Bundle()
            bundle.putInt(KeyUtil.IS_USER_COME_FROM_DASHBOARD, mode!!)
            networkFragment.arguments = bundle
            return networkFragment
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        activity!!.registerReceiver(mNetworkReceiver, filter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.NetworkTheme)
        inflater.cloneInContext(contextThemeWrapper)
        val view = inflater.inflate(R.layout.fragment_network, container, false)

        (activity as MainActivity).setAdapterPosition(4)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getBundleData()
        activity!!.initCupGraph(linechart_network_fragment, 4)
    }

    /*** Get data from bundle */
    private fun getBundleData() {
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(KeyUtil.IS_USER_COME_FROM_DASHBOARD)) {
                initToolbar(bundle.getInt(KeyUtil.IS_USER_COME_FROM_DASHBOARD))
            }
        }
    }

    override fun onResume() {
        super.onResume()

        mStartRX = TrafficStats.getTotalRxBytes()
        mStartTX = TrafficStats.getTotalTxBytes()

        if (mStartRX == TrafficStats.UNSUPPORTED.toLong() || mStartTX == TrafficStats.UNSUPPORTED.toLong()) {
            tv_network_fragment_data_not_available.visibility = View.VISIBLE
            linechart_network_fragment.visibility = View.GONE
            tv_network_fragment_data.visibility = View.GONE
        } else {
            if (mHandler == null) {
                mHandler = Handler()
                mHandler!!.postDelayed(mRunnable, 1)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(mNetworkReceiver)
    }

    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            if (isConnected()) {

                rxBytes = TrafficStats.getTotalRxBytes()
                txBytes = TrafficStats.getTotalTxBytes()
                tStartTX = rxBytes - mStartRX
                x = ""

                if (tStartTX < 1023) {
                    x = tStartTX.toInt().toString() + "B/s"
                } else if (tStartTX < 1048575) {
                    x = (tStartTX / 1024).toInt().toString() + "KB/s"
                }
                else
                    x = String.format("%.1f", (tStartTX.toFloat() / 1048576)) + "MB/s"

                activity!!.setupGradient(linechart_network_fragment, R.color.line_chart_graph_color_2, R.color.line_chart_graph_color_2)
                activity!!.addEntry(0, (tStartTX / 1.2).toFloat(), linechart_network_fragment)

                val styledString = SpannableString(x)
                styledString.setSpan(RelativeSizeSpan(0.5f), if (x.lastIndexOf("M") > 0) x.lastIndexOf("M") else if (x.lastIndexOf("K") > 0) x.lastIndexOf("K") else x.lastIndexOf("B"), x.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                tv_network_fragment_data.text = styledString

                mStartRX = rxBytes
                mStartTX = txBytes
            }
            mHandler!!.postDelayed(this, 1200)
        }
    }

    fun isConnected(): Boolean {
        val cm = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected) { // connected to the internet
            tv_network_fragment_data_not_available.visibility = View.GONE
            linechart_network_fragment.visibility = View.VISIBLE
            tv_network_fragment_data.visibility = View.VISIBLE
            return true
        } else {
            if (tv_network_fragment_data_not_available != null) {
                tv_network_fragment_data_not_available.visibility = View.VISIBLE
                tv_network_fragment_data_not_available.text =
                    activity!!.resources.getString(R.string.no_internet_connection)
            }
            linechart_network_fragment.visibility = View.GONE
            tv_network_fragment_data.visibility = View.GONE
            val styledString = SpannableString("0B/s")
            styledString.setSpan(RelativeSizeSpan(0.5f), 1, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            tv_network_fragment_data.text = styledString
            networkStatus = 0
            return false
        }
    }

    private fun initToolbar(mode: Int) {
        if (mode == 1) {
            iv_back.visibility = View.VISIBLE
            iv_back.setColorFilter(ContextCompat.getColor(activity!!, R.color.darkBlue))
            iv_back.setOnClickListener { activity!!.onBackPressed() }
            (activity as MainActivity).bottomSheetDisable(true)
        } else {
            iv_back.visibility = View.GONE
            (activity as MainActivity).bottomSheetDisable(false)
        }
        tv_title.text = activity!!.resources.getString(R.string.network)
        tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.network))
    }

    @SuppressLint("SetTextI18n", "WifiManagerLeak")
    private fun getNetworkInfo() {
        if (isNetworkConnected(activity as MainActivity)) {
            tv_connection_status.text = activity!!.resources.getString(R.string.connect)
            tv_ip_address.text = getIPAddress(true)
        } else {
            tv_connection_status.text = activity!!.resources.getString(R.string.disconnect)
            tv_ip_address.text = activity!!.resources.getString(R.string.unavailable)
        }

        when {
            isWifiConnected(activity!!) == activity!!.resources.getString(R.string.wifi) -> {

                val wifiManager = activity!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo

                tv_data_type.text = activity!!.resources.getString(R.string.wifi)
                tv_network_type.text = activity!!.resources.getString(R.string.wifi)
                tv_ssid.text = (wifiInfo.ssid.toString()).removePrefix("\"").removeSuffix("\"")
                tv_mac_address.text = getMACAddress("wlan0")
                tv_link_speed.text = wifiInfo.linkSpeed.toString() + activity!!.resources.getString(R.string.mbps)
            }
            isWifiConnected(activity!!) == activity!!.resources.getString(R.string.network) -> {
                tv_data_type.text = activity!!.resources.getString(R.string.network)
                tv_network_type.text = activity!!.resources.getString(R.string.network)
                tv_ssid.text = activity!!.resources.getString(R.string.unavailable)
                tv_mac_address.text = getMACAddress("eth0")
                tv_link_speed.text = activity!!.resources.getString(R.string.unavailable)
            }
            else -> {
                tv_data_type.text = activity!!.resources.getString(R.string.unavailable)
                tv_network_type.text = activity!!.resources.getString(R.string.unavailable)
                tv_mac_address.text = ""
                tv_link_speed.text = activity!!.resources.getString(R.string.unavailable)
                tv_ssid.text = activity!!.resources.getString(R.string.unavailable)
            }
        }
    }

    private val mNetworkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                getNetworkInfo()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }

        }
    }

}