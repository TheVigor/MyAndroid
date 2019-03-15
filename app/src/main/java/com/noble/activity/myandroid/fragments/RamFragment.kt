package com.noble.activity.myandroid.fragments

import android.content.Context
import android.graphics.Point
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.constants.RAM_INDEX
import com.noble.activity.myandroid.utilities.calculatePercentage
import com.noble.activity.myandroid.utilities.freeRamMemorySize
import com.noble.activity.myandroid.utilities.sizeConversion
import com.noble.activity.myandroid.utilities.totalRamMemorySize
import kotlinx.android.synthetic.main.fragment_ram.*
import kotlinx.android.synthetic.main.toolbar_ui.*
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.util.*

class RamFragment : Fragment() {

    private lateinit var ramHandler: Handler
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ram, container, false)

        (activity as MainActivity).setAdapterPosition(RAM_INDEX)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val total = activity!!.totalRamMemorySize()
        tv_total_memory_value.text = sizeConversion(total)

    }

    override fun onStart() {
        super.onStart()

        ramHandler = Handler()
        ramHandler.postDelayed(ramRunnable, 1000)
    }

    override fun onStop() {
        super.onStop()

        ramHandler.removeCallbacks(ramRunnable)
    }

    private fun initToolbar() {
        tv_title.text = activity!!.resources.getString(R.string.ram)
        tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.gpu_ram))
    }


    private val ramRunnable: Runnable = object : Runnable {
        override fun run() {

            val total = activity!!.totalRamMemorySize()

            tv_used_memory_value?.text =
                    sizeConversion(total - activity!!.freeRamMemorySize())

            tv_free_memory_value?.text = sizeConversion(activity!!.freeRamMemorySize())

            arc_graphics_ram?.setCurrentValues(
                    calculatePercentage((total - activity!!.freeRamMemorySize()).toDouble(),
                            total.toDouble()).toFloat())
            ramHandler.postDelayed(this, 1000)
        }
    }

}