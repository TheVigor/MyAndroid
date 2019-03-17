package com.noble.activity.myandroid.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.adapters.SensorAdapter
import com.noble.activity.myandroid.constants.SENSORS_INDEX
import com.noble.activity.myandroid.models.SensorInfo
import com.noble.activity.myandroid.utilities.KeyUtil
import kotlinx.android.synthetic.main.fragment_sensors.*
import kotlinx.android.synthetic.main.toolbar_ui.*

class SensorsFragment : Fragment() {

    private var navMode: Int = 0

    companion object {
        fun getInstance(mode: Int): SensorsFragment {
            val sensorsFragment = SensorsFragment()

            val bundle = Bundle()
            bundle.putInt(KeyUtil.IS_USER_COME_FROM_DASHBOARD, mode)
            sensorsFragment.arguments = bundle

            return sensorsFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sensors, container, false)

        (activity as MainActivity).setAdapterPosition(SENSORS_INDEX)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getBundleData()

        rv_sensors_list.layoutManager = GridLayoutManager(activity, 2)
        rv_sensors_list.hasFixedSize()

        initSensorsList()
    }

    private fun getBundleData() {
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(KeyUtil.IS_USER_COME_FROM_DASHBOARD)) {
                navMode = bundle.getInt(KeyUtil.IS_USER_COME_FROM_DASHBOARD)
                initToolbar(bundle.getInt(KeyUtil.IS_USER_COME_FROM_DASHBOARD))
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isAdded)
            initToolbar(navMode)
    }

    private fun initToolbar(mode: Int) {
        if (mode == 1) {
            iv_back.visibility = View.VISIBLE
            iv_back.setColorFilter(ContextCompat.getColor(activity!!, R.color.darkBlue))
            iv_back.setOnClickListener { activity!!.onBackPressed() }
            (activity!! as MainActivity).bottomSheetDisable(true)
        } else {
            iv_back.visibility = View.GONE
            (activity!! as MainActivity).bottomSheetDisable(false)
        }

        tv_title.text = activity!!.resources.getString(R.string.sensors)
        tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.sensor))
    }

    @SuppressLint("SetTextI18n")
    private fun initSensorsList() {
        val lists = ArrayList<SensorInfo>()
        val sm = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val list = sm.getSensorList(Sensor.TYPE_ALL)

        if (list.size > 1) {
            snackBarCustom(coordinatorLayout, list.size.toString() + " " + activity!!.resources.getString(R.string.available_sensor))
        } else {
            snackBarCustom(coordinatorLayout, list.size.toString() + " " + activity!!.resources.getString(R.string.available_sensor))
        }
        for (s in list) {
            lists.add(SensorInfo(s.name, s.type))
        }

        val adapter = SensorAdapter(lists)
        rv_sensors_list.adapter = adapter
    }

    private fun snackBarCustom(view: View, message: String) {
        val snackBar = Snackbar.make(view, "" + message, Snackbar.LENGTH_LONG)
        val params = snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(16, 12, 16, 20)
        snackBar.view.layoutParams = params
        ViewCompat.setElevation(snackBar.view, 6f)

        val sbView = snackBar.view
        snackBar.view.background =
            ContextCompat.getDrawable(activity!!, R.drawable.material_snackbar_sensor)

        val textView =
            sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView

        textView.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
        textView.maxLines = 10

        snackBar.show()
    }
}