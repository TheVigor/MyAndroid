package com.noble.activity.myandroid.fragments

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.utilities.KeyUtil
import com.noble.activity.myandroid.utilities.calculateAbsoluteHumidity
import com.noble.activity.myandroid.utilities.calculateDewPoint
import kotlinx.android.synthetic.main.fragment_sensors_detail.*
import kotlinx.android.synthetic.main.sensor_sub_view.*
import kotlinx.android.synthetic.main.toolbar_ui.*
import java.text.DecimalFormat

class SensorInfoFragment : Fragment(), SensorEventListener {

    private var sensorName: String? = ""
    private var sensorType: Int? = 0
    private var sensorManager: SensorManager? = null

    fun getInstance(mode: String, type: Int, imageBytes: ByteArray): SensorInfoFragment {
        val sensorDetailFragment = SensorInfoFragment()

        val bundle = Bundle()
        bundle.putString(KeyUtil.KEY_SENSOR_NAME, mode)
        bundle.putInt(KeyUtil.KEY_SENSOR_TYPE, type)
        bundle.putByteArray(KeyUtil.KEY_SENSOR_ICON, imageBytes)
        sensorDetailFragment.arguments = bundle

        return sensorDetailFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sensors_detail, container, false)
        return view
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).bottomSheetDisable(true)

        getBundleData()
        initToolbar()

        sensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager?.registerListener(this, sensorType?.let { sensorManager?.getDefaultSensor(it) }, SensorManager.SENSOR_DELAY_NORMAL)

        displaySensorsDetails(sensorManager!!)
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, sensorType?.let { sensorManager?.getDefaultSensor(it) }, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isAdded) {
            initToolbar()
        }
    }

    private fun initToolbar() {
        iv_back.visibility = View.VISIBLE
        tv_title.text = sensorName
        tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.sensor))
        iv_back.setColorFilter(ContextCompat.getColor(activity!!, R.color.darkBlue))
        iv_back.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let { displayAccelerometer(it) }
    }

    /**
     * Get data from bundle
     */
    private fun getBundleData() {
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(KeyUtil.KEY_SENSOR_NAME)) {
                sensorName = bundle.getString(KeyUtil.KEY_SENSOR_NAME)
                tv_sensor_name.text = sensorName
            }
            if (bundle.containsKey(KeyUtil.KEY_SENSOR_TYPE)) {
                sensorType = bundle.getInt(KeyUtil.KEY_SENSOR_TYPE)
            }
            if (bundle.containsKey(KeyUtil.KEY_SENSOR_ICON)) {
                val byteArray: ByteArray? = bundle.getByteArray(KeyUtil.KEY_SENSOR_ICON)
                val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                if (bmp != null) {
                    iv_sensor_ic.setImageBitmap(bmp)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n", "NewApi")
    private fun displayAccelerometer(event: SensorEvent?) {
        val formatter = DecimalFormat("#0.00")
        /*** Accelerometer sensors
         * Gravity sensor */
        if (sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.type === sensorType
            || sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED)?.type === sensorType
            || sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.type === sensorType
            || sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)?.type === sensorType) {

            ll_top.visibility = View.VISIBLE

            val x = event!!.values[0]
            val y = event.values[1]
            val z = event.values[2]

            tv_x.text = (Html.fromHtml("X: " + formatter.format(x) + activity!!.resources.getString(R.string.ms) + "<small><sup>2</sup></small>"))
            tv_y.text = (Html.fromHtml("Y: " + formatter.format(y) + activity!!.resources.getString(R.string.ms) + "<small><sup>2</sup></small>"))
            tv_z.text = (Html.fromHtml("Z: " + formatter.format(z) + activity!!.resources.getString(R.string.ms) + "<small><sup>2</sup></small>"))
        }
        /*** Magnetic sensors */
        else if (sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.type === sensorType
            || sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)?.type === sensorType) {

            ll_top.visibility = View.VISIBLE
            val x = event!!.values[0]
            val y = event.values[1]
            val z = event.values[2]

            if (sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.type === (sensorType)) {
                tv_x.text = ("X: " + formatter.format(x) + activity!!.resources.getString(R.string.mu_tesla))
                tv_y.text = ("Y: " + formatter.format(y) + activity!!.resources.getString(R.string.mu_tesla))
                tv_z.text = ("Z: " + formatter.format(z) + activity!!.resources.getString(R.string.mu_tesla))
            } else if (sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)?.type === (sensorType)) {
                tv_x.text = (activity!!.resources.getString(R.string.geomagnetic_field) + "X: " + formatter.format(x) + activity!!.resources.getString(R.string.mu_tesla))
                tv_y.text = ("Y: " + formatter.format(y) + activity!!.resources.getString(R.string.mu_tesla))
                tv_z.text = ("Z: " + formatter.format(z) + activity!!.resources.getString(R.string.mu_tesla))
            }

        }
        /*** Gyroscope sensors */
        else if (sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.type === (sensorType)
            || sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED)?.type === (sensorType)) {

            ll_top.visibility = View.VISIBLE
            val x = event!!.values[0]
            val y = event.values[1]
            val z = event.values[2]

            tv_x.text = ("X: " + formatter.format(x) + activity!!.resources.getString(R.string.rad))
            tv_y.text = ("Y: " + formatter.format(y) + activity!!.resources.getString(R.string.rad))
            tv_z.text = ("Z: " + formatter.format(z) + activity!!.resources.getString(R.string.rad))

        }
        /*** Rotation sensors */
        else if (sensorManager?.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)?.type === (sensorType)
            || sensorManager?.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)?.type === (sensorType)
            || sensorManager?.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)?.type === (sensorType)
            || sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)?.type === (sensorType)
            || sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.type === (sensorType)) {
            ll_top.visibility = View.VISIBLE
            val x = event!!.values[0]
            val y = event.values[1]
            val z = event.values[2]

            if (sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)?.type === sensorType) {
                tv_x.text = ("X: " + formatter.format(x) + activity!!.resources.getString(R.string.degree_icon))
                tv_y.text = ("Y: " + formatter.format(y) + activity!!.resources.getString(R.string.degree_icon))
                tv_z.text = ("Z: " + formatter.format(z) + activity!!.resources.getString(R.string.degree_icon))
            } else {
                tv_x.text = ("X: " + formatter.format(x))
                tv_y.text = ("Y: " + formatter.format(y))
                tv_z.text = ("Z: " + formatter.format(z))
            }

        }
        /*** Pressure sensor (Barometer) */
        else if (sensorManager?.getDefaultSensor(Sensor.TYPE_PRESSURE)?.type === (sensorType)) {
            if (event != null) {
                tv_x.text = (activity!!.resources.getString(R.string.pressure) + event.values[0] + activity!!.resources.getString(R.string.hpa))
            }
        }
        /*** Step counter sensor
        Proximity sensor
        Light sensor */
        else if (sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.type == sensorType
            || sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.type == sensorType
            || sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)?.type == sensorType
            || sensorManager?.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)?.type == sensorType
            || sensorManager?.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)?.type == sensorType) {

            ll_top.visibility = View.VISIBLE

            if (event != null) {
                if (event.sensor.type == Sensor.TYPE_RELATIVE_HUMIDITY && event.values[0] < sensorManager?.getDefaultSensor(
                        Sensor.TYPE_PROXIMITY)?.maximumRange!!) {
                    tv_x.text = (activity!!.resources.getString(R.string.proximity_sensor) + event.values[0].toString() + activity!!.resources.getString(R.string.cm))
                } else {
                    tv_x.text = (activity!!.resources.getString(R.string.proximity_sensor) + event.values[0].toString() + activity!!.resources.getString(R.string.cm))
                }

                if (event.sensor.type == Sensor.TYPE_RELATIVE_HUMIDITY) {
                    tv_x.text = (activity!!.resources.getString(R.string.humidity_sensor) + event.values[0].toString() + activity!!.resources.getString(R.string.percentage))
                    KeyUtil.KEY_LAST_KNOWN_HUMIDITY = event.values[0]
                }

                if (event.sensor.type == Sensor.TYPE_LIGHT) {
                    tv_x.text = (activity!!.resources.getString(R.string.illuminance) + event.values[0].toString() + activity!!.resources.getString(R.string.lx))
                }


                if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE && KeyUtil.KEY_LAST_KNOWN_HUMIDITY != 0f) {
                    val temperature = event.values[0]
                    val absoluteHumidity = calculateAbsoluteHumidity(temperature, KeyUtil.KEY_LAST_KNOWN_HUMIDITY)
                    tv_x.text = (activity!!.resources.getString(R.string.absolute_humidity_temperature_sensor) + formatter.format(absoluteHumidity) + activity!!.resources.getString(R.string.percentage))
                    val dewPoint = calculateDewPoint(temperature, KeyUtil.KEY_LAST_KNOWN_HUMIDITY)
                    tv_y.text = (activity!!.resources.getString(R.string.due_point_temperature) + formatter.format(dewPoint) + activity!!.resources.getString(R.string.percentage))
                }

                if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                    tv_x.text = (activity!!.resources.getString(R.string.steps) + event.values[0].toString())
                }
            }
        }
        /*** Other sensors */
        else {
            ll_top.visibility = View.VISIBLE
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private fun displaySensorsDetails(sensorManager: SensorManager) {
        tv_int_type.text = sensorType?.let { sensorManager.getDefaultSensor(it)?.stringType }
        tv_vendor.text = sensorType?.let { sensorManager.getDefaultSensor(it)?.vendor }
        tv_version.text = sensorType?.let { sensorManager.getDefaultSensor(it)?.version.toString() }
        tv_resolution.text = sensorType?.let { Html.fromHtml(sensorManager.getDefaultSensor(it)?.resolution.toString() + " m/s" + "<small><sup>2</sup></small>") }
        tv_power.text = sensorType?.let { sensorManager.getDefaultSensor(it)?.power.toString() + activity!!.resources.getString(R.string.ma) }
        tv_maximum_range.text = sensorType?.let { Html.fromHtml(sensorManager.getDefaultSensor(it)?.maximumRange.toString() + " m/s" + "<small><sup>2</sup></small>") }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_sensor_id.text = sensorType?.let { sensorManager.getDefaultSensor(it)?.id.toString() }
            tv_is_dynamic_sensor.text = sensorType?.let { sensorManager.getDefaultSensor(it)?.isDynamicSensor.toString() }
            tv_is_wakeup_sensor.text = sensorType?.let { sensorManager.getDefaultSensor(it)?.isWakeUpSensor.toString() }
            tv_reporting_mode.text = sensorType?.let { sensorManager.getDefaultSensor(it)?.reportingMode.toString() }
        } else {
            tv_sensor_id.text = "-"
            tv_is_dynamic_sensor.text = "-"
            tv_is_wakeup_sensor.text = "-"
            tv_reporting_mode.text = "-"
        }
    }
}