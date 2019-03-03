package com.noble.activity.myandroid.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.*
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import com.github.mikephil.charting.charts.LineChart
import com.noble.activity.myandroid.*
import com.noble.activity.myandroid.extensions.addFragment
import com.noble.activity.myandroid.extensions.avoidDoubleClicks
import com.noble.activity.myandroid.models.CpuInfo
import com.noble.activity.myandroid.models.addEntry
import com.noble.activity.myandroid.models.initCupGraph
import com.noble.activity.myandroid.models.setupGradient
import com.noble.activity.myandroid.utilities.*
import com.takusemba.spotlight.OnSpotlightStateChangedListener
import com.takusemba.spotlight.OnTargetStateChangedListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.shape.Circle
import com.takusemba.spotlight.target.CustomTarget
import kotlinx.android.synthetic.main.brightness_progressbar_layout.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.io.IOException
import java.io.RandomAccessFile

class DashboardFragment : Fragment() {
    private var currentAzimuth: Float = 0.toFloat()
    private var networkStatus = 0
    private var mHandler: Handler? = null
    private var mStartTX: Long = 0
    private var mStartRX: Long = 0

    private var wifiManager: WifiManager? = null

    private var statFile: RandomAccessFile? = null
    private var mCpuInfo: CpuInfo? = null
    private var cHandler: Handler? = null

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var compass: Compass? = null

    private lateinit var x: String

    private var locationManager: LocationManager? = null
    private var GpsStatus: Boolean = false
    private lateinit var intent1: Intent

    private val cRunnable = object : Runnable {
        override fun run() {
            try {
                statFile = RandomAccessFile("/proc/stat", "r")
                val cpuLine = statFile!!.readLine()
                parseCpuLine(cpuLine)
                activity!!.setupGradient(
                    linechart_cpu,
                    R.color.dashboard_processor_graph_color_1,
                    R.color.dashboard_processor_graph_color_2
                )
                activity!!.addEntry(2, mCpuInfo!!.usage.toFloat(), linechart_cpu)
                val styledString = SpannableString(mCpuInfo!!.usage.toString() + "%")
                styledString.setSpan(
                    RelativeSizeSpan(0.5f),
                    styledString.length - 1,
                    styledString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv_processor_data.text = styledString
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (statFile != null) {
                    try {
                        statFile!!.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
            cHandler!!.postDelayed(this, 1000)
        }
    }

    private val mRunnable = object : Runnable {
        @SuppressLint("DefaultLocale")
        override fun run() {
            if (isConnected) {
                val rxBytes = TrafficStats.getTotalRxBytes()
                val txBytes = TrafficStats.getTotalTxBytes()
                val tStartTX = rxBytes - mStartRX
                x = ""
                if (tStartTX < 1023)
                    x = tStartTX.toInt().toString() + "B/s"
                else if (tStartTX < 1048575)
                    x = (tStartTX / 1024).toInt().toString() + "KB/s"
                else
                    x = String.format("%.1f", tStartTX.toFloat() / 1048576) + "MB/s"
                activity!!.setupGradient(
                    linechart_net,
                    R.color.dashboard_network_graph_color_1,
                    R.color.dashboard_network_graph_color_2
                )
                activity!!.addEntry(1, (tStartTX / 1.2).toFloat(), linechart_net)
                val styledString = SpannableString(x)
                styledString.setSpan(
                    RelativeSizeSpan(0.5f),
                    if (x.lastIndexOf("M") > 0) x.lastIndexOf("M") else if (x.lastIndexOf("K") > 0) x.lastIndexOf("K") else x.lastIndexOf(
                        "B"
                    ), x.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv_network_data.text = styledString
                mStartRX = rxBytes
                mStartTX = txBytes
            }
            mHandler!!.postDelayed(this, 1200)
        }
    }

    private// connected to the internet
    val isConnected: Boolean
        get() {
            val cm = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm!!.activeNetworkInfo
            if (activeNetwork != null) {

                linechart_net.visibility = View.VISIBLE
                tv_network_data.visibility = View.VISIBLE
                tv_network_not_available.visibility = View.GONE

                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                    iv_dashboard_network.setImageResource(R.drawable.ic_brightness_low)
                    pulsator_network.start()
                    iv_dashboard_processor_network_image.setImageResource(R.drawable.ic_brightness_high)
                    iv_dashboard_network.setColorFilter(
                        activity!!.resources.getColor(R.color.dashboard_icon_color))
                    networkStatus = 1
                } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                    iv_dashboard_processor_network_image.setImageResource(R.drawable.ic_brightness_low)
                    iv_dashboard_network.setImageResource(R.drawable.ic_brightness_high)
                    iv_dashboard_network.setColorFilter(activity!!.resources.getColor(R.color.dashboard_icon_color))
                    pulsator_network.start()
                    networkStatus = 2
                }
                return true
            } else {
                linechart_net.visibility = View.GONE
                tv_network_data.visibility = View.GONE
                tv_network_not_available.visibility = View.VISIBLE
                tv_network_not_available.setText(R.string.no_internet_connection)
                pulsator_network.stop()
                networkStatus = 0
                iv_dashboard_network.setColorFilter(
                    activity!!.resources.getColor(R.color.dashboard_icon_color))
                iv_dashboard_network.setImageResource(R.drawable.ic_brightness_high)
                return false
            }

            return false
        }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action!!
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_ON -> {
                        iv_dashboard_blurtooth.setImageResource(R.drawable.ic_brightness_high)
                        iv_dashboard_blurtooth.setColorFilter(
                            activity!!.resources.getColor(R.color.dashboard_icon_color))
                        pulsator!!.start()
                    }
                    BluetoothAdapter.STATE_OFF -> {
                        iv_dashboard_blurtooth.setImageResource(R.drawable.ic_brightness_low)
                        iv_dashboard_blurtooth.setColorFilter(
                            activity!!.resources.getColor(R.color.dashboard_icon_color))
                        pulsator!!.stop()
                    }
                }
            }
        }
    }

    private val mBatInfoReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(c: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
            val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10
            val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)

            try {
                //set Battery Info
                tv_dashboard_volt_data.text =
                    "" + temperature.toString() + activity!!.resources.getString(R.string.c_symbol)
                tv_dashboard_volt_data.text = "" + (voltage.toString() + "mV")

                val value = activity!!.getBatteryCapacity()!!.toInt()
                if (value != -1) {
                    tv_dashboard_health_name.setText(R.string.battery_capacity)
                    tv_dashboard_health_data.text = value.toString() + "mAh"
                } else {
                    tv_dashboard_health_name.setText(R.string.battery_health)
                    when (health) {
                        1 -> tv_dashboard_health_data.text = activity!!.resources.getString(R.string.unknown)
                        2 -> tv_dashboard_health_data.text = activity!!.resources.getString(R.string.good)
                        3 -> tv_dashboard_health_data.text = activity!!.resources.getString(R.string.over_heated)
                        4 -> tv_dashboard_health_data.text = activity!!.resources.getString(R.string.dead)
                        5 -> tv_dashboard_health_data!!.text = activity!!.resources.getString(R.string.over_voltage)
                        6 -> tv_dashboard_health_data!!.text = activity!!.resources.getString(R.string.failed)
                        else -> tv_dashboard_health_data!!.text = activity!!.resources.getString(R.string.cold)
                    }
                }
                val styledString = SpannableString("$level%")
                styledString.setSpan(
                    RelativeSizeSpan(0.5f),
                    styledString.length - 1,
                    styledString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv_dashboard_battery_percentage.text = styledString
                pb_dashboard_battery.progress = level

            } catch (e: NullPointerException) {
                e.printStackTrace()
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_dashboard, container, false)

        (activity as MainActivity).setAdapterPosition(0)

        wifiManager = (activity as MainActivity).applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager?
        setupCompass()
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        activity!!.registerReceiver(broadcastReceiver, filter)
        val filter1 = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        activity!!.registerReceiver(mBatInfoReceiver, filter1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isFirstTime(activity!!)) {
            Handler().postDelayed({ getCenter(clMain) }, 900)
            setNotFirstTime(activity!!, true)
        }
    }

    fun getCenter(layout: View?) {
        val vto = layout!!.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("SetTextI18n")
            override fun onGlobalLayout() {
                layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val inflater = LayoutInflater.from(activity)

                val homeLayout = inflater.inflate(R.layout.target_layout, null)
                val homeTarget = CustomTarget.Builder(activity)
                    .setPoint(tv_dashboard_device_name)
                    .setShape(
                        Circle(if (tv_dashboard_device_name.width > tv_dashboard_device_name.height)
                            tv_dashboard_device_name.width.toFloat() else tv_dashboard_device_name.height.toFloat()
                        )
                    )
                    .setOverlay(homeLayout)
                    .setOnSpotlightStartedListener(object : OnTargetStateChangedListener<CustomTarget> {
                        override fun onStarted(target: CustomTarget) {}

                        override fun onEnded(target: CustomTarget) {

                        }
                    })
                    .build()

                val locationLayout = inflater.inflate(R.layout.target_layout, null)
                val locationTarget = CustomTarget.Builder(activity)
                    .setPoint(iv_dashboard_location)
                    .setShape(Circle(iv_dashboard_location.width.toFloat()))
                    .setOverlay(locationLayout)
                    .build()

                val blurtoothLayout = inflater.inflate(R.layout.target_layout, null)
                val blurtoothTarget = CustomTarget.Builder(activity)
                    .setPoint(iv_dashboard_blurtooth)
                    .setShape(Circle(iv_dashboard_blurtooth.width.toFloat()))
                    .setOverlay(blurtoothLayout)
                    .build()

                val brightnessLayout = inflater.inflate(R.layout.target_layout, null)
                val brightnessTarget = CustomTarget.Builder(activity)
                    .setPoint(iv_dashboard_brightness)
                    .setShape(Circle(iv_dashboard_brightness.width.toFloat()))
                    .setOverlay(brightnessLayout)
                    .build()

                val networkLayout = inflater.inflate(R.layout.target_layout, null)
                val networkTarget = CustomTarget.Builder(activity)
                    .setPoint(iv_dashboard_network)
                    .setShape(Circle(iv_dashboard_network.width.toFloat()))
                    .setOverlay(networkLayout)
                    .build()

                val spotlight = Spotlight.with(activity!!)
                    .setOverlayColor(R.color.background)
                    .setDuration(1000L)
                    .setAnimation(DecelerateInterpolator(2f))
                    .setTargets(homeTarget, locationTarget, brightnessTarget, blurtoothTarget, networkTarget)
                    .setClosedOnTouchedOutside(false)
                    .setOnSpotlightStateListener(object : OnSpotlightStateChangedListener {
                        override fun onStarted() {}

                        override fun onEnded() {}
                    })
                spotlight.start()
                val closeTarget = View.OnClickListener { spotlight.closeCurrentTarget() }

                val closeSpotlight = View.OnClickListener { spotlight.closeSpotlight() }

                homeLayout.findViewById<RelativeLayout>(R.id.tlTargetMain).setOnClickListener(closeTarget)
                locationLayout.findViewById<RelativeLayout>(R.id.tlTargetMain).setOnClickListener(closeTarget)
                blurtoothLayout.findViewById<RelativeLayout>(R.id.tlTargetMain).setOnClickListener(closeTarget)
                brightnessLayout.findViewById<RelativeLayout>(R.id.tlTargetMain).setOnClickListener(closeTarget)
                networkLayout.findViewById<RelativeLayout>(R.id.tlTargetMain).setOnClickListener(closeTarget)



                setMarginsToTargetView(homeLayout, tv_dashboard_device_name)
                val tvDesc = homeLayout.findViewById<TextView>(R.id.tvTargetDesc)
                tvDesc.text = "Device detail"
                val tvTitle = homeLayout.findViewById<TextView>(R.id.tvTargetTitle)
                tvTitle.text = "Click here to see more details related to device"

                setMarginsToTargetView(locationLayout, iv_dashboard_location)
                val tvDescLocation = locationLayout.findViewById<TextView>(R.id.tvTargetDesc)
                tvDescLocation.text = "Location"
                val tvTitleLocation = locationLayout.findViewById<TextView>(R.id.tvTargetTitle)
                tvTitleLocation.text = "Click here to turn on/off location"

                setMarginsToTargetView(blurtoothLayout, iv_dashboard_blurtooth)
                val tvDescBlurtooth = blurtoothLayout.findViewById<TextView>(R.id.tvTargetDesc)
                tvDescBlurtooth.text = "Bluetooth setting"
                val tvTitleBlurtooth = blurtoothLayout.findViewById<TextView>(R.id.tvTargetTitle)
                tvTitleBlurtooth.text = "Click here to turn on/off bluetooth"

                setMarginsToTargetView(brightnessLayout, iv_dashboard_brightness)
                val tvDescBrightness = brightnessLayout.findViewById<TextView>(R.id.tvTargetDesc)
                tvDescBrightness.text = "Brightness switch"
                val tvTitleBrightness = brightnessLayout.findViewById<TextView>(R.id.tvTargetTitle)
                tvTitleBrightness.text = "Click here to adjust phone brightness"

                setMarginsToTargetView(networkLayout, iv_dashboard_network)
                val tvDescNetwork = networkLayout.findViewById<TextView>(R.id.tvTargetDesc)
                tvDescNetwork.text = "Network setting"
                val tvTitleNetwork = networkLayout.findViewById<TextView>(R.id.tvTargetTitle)
                tvTitleNetwork.text = "Tabe here to turn on/off wifi or mobile data"

                homeLayout.findViewById<Button>(R.id.btnBack).setOnClickListener(closeSpotlight)
                locationLayout.findViewById<Button>(R.id.btnBack).setOnClickListener(closeSpotlight)
                blurtoothLayout.findViewById<Button>(R.id.btnBack).setOnClickListener(closeSpotlight)
                brightnessLayout.findViewById<Button>(R.id.btnBack).setOnClickListener(closeSpotlight)
                networkLayout.findViewById<Button>(R.id.btnBack).setOnClickListener(closeSpotlight)
            }
        })
    }

    private fun setMarginsToTargetView(targetLayout: View, view: View) {
        val twoLocation = IntArray(2)
        view.getLocationInWindow(twoLocation)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )

        params.setMargins(120, twoLocation[1] + view.width + 160, 120, 0)
        val llTarget = targetLayout.findViewById<LinearLayout>(R.id.llTarget)
        llTarget.layoutParams = params
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tv_dashboard_device_name.text = Build.BRAND
        tv_dashboard_model_number.text = Build.MODEL + ""

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled) {
            iv_dashboard_blurtooth.setImageResource(R.mipmap.ic_bluetooth)
            iv_dashboard_blurtooth.setColorFilter(activity?.resources?.getColor(R.color.dashboard_icon_color)!!)
            pulsator.start()
        } else {
            iv_dashboard_blurtooth.setImageResource(R.mipmap.ic_bluetooth)
            iv_dashboard_blurtooth.setColorFilter(activity?.resources?.getColor(R.color.dashboard_icon_color)!!)
            pulsator!!.stop()
        }
        val total = activity!!.totalRamMemorySize()
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                arc_dashbord_ram.setCurrentValues(
                    calculatePercentage(
                        (total - activity!!.freeRamMemorySize()).toDouble(),
                        total.toDouble()
                    ).toFloat()
                )
                changeBrightnessImageViewIcon(
                    Settings.System.getInt(
                        activity!!.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS,
                        0
                    )
                )
                changeLocationImageViewIcon()
                handler.postDelayed(this, 2000)
            }
        }
        handler.postDelayed(runnable, 10)

        activity!!.initCupGraph(linechart_net, 1)
        activity!!.initCupGraph(linechart_cpu, 2)

        iv_dashboard_blurtooth.setOnClickListener {
            avoidDoubleClicks(iv_dashboard_blurtooth)
            if (mBluetoothAdapter != null && !mBluetoothAdapter!!.isEnabled) {
                val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBTIntent, KeyUtil.REQUEST_ENABLE_BT)
            } else {
                try {
                    mBluetoothAdapter!!.disable()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        iv_dashboard_network.setOnClickListener {
            avoidDoubleClicks(iv_dashboard_network)
            if (networkStatus == 1)
                wifiManager!!.isWifiEnabled = !wifiManager!!.isWifiEnabled
            else if (networkStatus == 0)
                wifiManager!!.isWifiEnabled = true
        }

        tv_dashboard_device_name.setOnClickListener {
            avoidDoubleClicks(tv_dashboard_device_name)
            //fragmentUtil.addFragment(HomeFragment(), true, true)
        }

        tv_dashboard_model_number.setOnClickListener {
            avoidDoubleClicks(tv_dashboard_model_number)
            //fragmentUtil.addFragment(HomeFragment(), true, true)
        }

        iv_dashboard_brightness.setOnClickListener {
            avoidDoubleClicks(iv_dashboard_brightness)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(activity)) {
                    val intent =
                        Intent(
                            Settings.ACTION_MANAGE_WRITE_SETTINGS,
                            Uri.parse("package:" + activity!!.packageName)
                        )
                    startActivityForResult(intent, 200)
                } else
                    showChangeBrightnessDialog()
            } else
                showChangeBrightnessDialog()
        }

        cardNetwork!!.setOnClickListener {
            avoidDoubleClicks(cardNetwork)
            //fragmentUtil.addFragment(NetworkFragment.Companion.getInstance(1), true, true)
        }

        cardProcessor!!.setOnClickListener {
            avoidDoubleClicks(cardProcessor)
            //fragmentUtil.addFragment(CPUFragment.Companion.getInstance(1), true, true)
        }

        cardBattery!!.setOnClickListener {
            avoidDoubleClicks(cardBattery)
            //fragmentUtil.addFragment(BatteryFragment.getInstance(1), true, true)
        }

        cardSensors!!.setOnClickListener {
            avoidDoubleClicks(cardSensors)
            //fragmentUtil.addFragment(SensorCategoryFragment.Companion.getInstance(1), true, true)
        }
        iv_dashboard_location.setOnClickListener {
            avoidDoubleClicks(iv_dashboard_location)
            GPSStatus()
            if (GpsStatus) {
                iv_dashboard_location.setImageResource(R.drawable.ic_brightness_low)
            } else {
                iv_dashboard_location.setImageResource(R.drawable.ic_brightness_high)
            }

            intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent1)
        }

        val pos = getCurrentSelectedFragmentPosition(activity!!)
        if (pos >= 0) {
            activity!!.addFragment((activity as MainActivity).getFragment(pos), true, true)
            setCurrentSelectedFragmentPosition(activity as MainActivity, -1)
        }


    }

    fun GPSStatus() {
        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        GpsStatus = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun changeBrightnessImageViewIcon(progress: Int) {
        if (progress <= 50)
            iv_dashboard_brightness.setImageResource(R.drawable.ic_brightness_low)
        else if (progress >= 200)
            iv_dashboard_brightness.setImageResource(R.drawable.ic_brightness_high)
        else
            iv_dashboard_brightness.setImageResource(R.drawable.ic_brightness_medium)
    }

    private fun showChangeBrightnessDialog() {
        val brightnessDialog = Dialog(activity)
        brightnessDialog.setContentView(R.layout.brightness_progressbar_layout)
        val window = brightnessDialog.window!!
        window.setGravity(Gravity.CENTER)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val currentProgress =
            Settings.System.getInt(activity!!.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0)
        sb_brightness.progress = currentProgress
        changeBrightnessImageViewIcon(currentProgress)
        sb_brightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                changeScreenBrightness(activity!!, i)
                changeBrightnessImageViewIcon(i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                brightnessDialog.dismiss()
            }
        })
        brightnessDialog.show()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        (activity as MainActivity).setAdapterPosition(0)
        super.onHiddenChanged(hidden)
        if (!hidden && isAdded) (activity as MainActivity).bottomSheetDisable(false)
    }

    override fun onResume() {
        super.onResume()
        val filter1 = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        activity!!.registerReceiver(mBatInfoReceiver, filter1)
        if (compass!!.gSensor != null && compass!!.mSensor != null)
            compass!!.start()
        else {
            tv_compass_data_not_available.visibility = View.VISIBLE
            iv_dashboard_compass.visibility = View.GONE
            tv_dashboard_compass_degree.visibility = View.GONE
        }
        mStartRX = TrafficStats.getTotalRxBytes()
        mStartTX = TrafficStats.getTotalTxBytes()
        if (mStartRX == TrafficStats.UNSUPPORTED.toLong() || mStartTX == TrafficStats.UNSUPPORTED.toLong()) {
            linechart_net.visibility = View.GONE
            tv_network_data.visibility = View.GONE
            tv_network_not_available.visibility = View.VISIBLE
        } else {
            if (mHandler == null) {
                mHandler = Handler()
                mHandler!!.postDelayed(mRunnable, 1200)
            }
        }
        try {
            statFile = RandomAccessFile("/proc/stat/", "r")
            if (cHandler == null) {
                cHandler = Handler()
                cHandler!!.postDelayed(cRunnable, 1000)
            }
        } catch (e: IOException) {
            linechart_cpu.visibility = View.GONE
            tv_processor_data.visibility = View.GONE
            tv_processor_data_not_available.visibility = View.VISIBLE
            if (cHandler != null) cHandler!!.removeCallbacks(cRunnable)
            e.printStackTrace()
        }

    }

    private fun setupCompass() {
        compass = Compass(activity as Context)
        val cl = object : Compass.CompassListener {
            override fun onNewAzimuth(azimuth: Float) {
                adjustArrow(azimuth)
            }
        }
        compass!!.setListener(cl)
    }

    private fun changeLocationImageViewIcon() {
        val lm = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            if (lm != null)
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        try {
            if (lm != null)
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        if (!gps_enabled && !network_enabled)
            iv_dashboard_location.setImageResource(R.drawable.ic_brightness_low)
        else
            iv_dashboard_location.setImageResource(R.drawable.ic_brightness_high)
    }

    @SuppressLint("SetTextI18n")
    private fun adjustArrow(azimuth: Float) {
        tv_dashboard_compass_degree.text = azimuth.toInt().toString() + "\u00B0"
        val an = RotateAnimation(
            -currentAzimuth,
            -azimuth,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        currentAzimuth = azimuth
        an.duration = 300
        an.repeatCount = 0
        an.fillAfter = true
        iv_dashboard_compass.startAnimation(an)
    }

    override fun onPause() {
        super.onPause()
        compass!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(broadcastReceiver)
        activity!!.unregisterReceiver(mBatInfoReceiver)
    }

    private fun parseCpuLine(cpuLine: String?) {
        if (cpuLine != null && cpuLine.length > 0) {
            val parts = cpuLine.split("[ ]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val cpuLabel = "cpu"
            if (parts[0].contains(cpuLabel)) {
                if (mCpuInfo == null) mCpuInfo = CpuInfo()
                mCpuInfo!!.update(parts)
            }
        }
    }

    // This function only take effect in real physical android device,
    // it can not take effect in android emulator.
    private fun changeScreenBrightness(context: Context, screenBrightnessValue: Int) {
        // Change the screen brightness change mode to manual.
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
        // Apply the screen brightness value to the system, this will change the value in Settings ---> Display ---> Brightness level.
        // It will also change the screen brightness for the device.
        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue)
    }
}