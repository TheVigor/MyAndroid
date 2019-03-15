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

    val resolution: String
        get() {
            val wm = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val size = Point()
            wm.defaultDisplay.getRealSize(size)
            return size.x.toString() + "x" + size.y
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ram, container, false)

        (activity as MainActivity).setAdapterPosition(RAM_INDEX)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()

        class LoadJson : AsyncTask<Void, Void, Void>() {
            var tempList = HashMap<String, String>()
            @Suppress("NAME_SHADOWING")
            override fun doInBackground(vararg p0: Void?): Void? {
                try {
                    val s = Scanner(File("/proc/cpuinfo"))
                    while (s.hasNextLine()) {
                        val vals = s.nextLine().split(": ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val jsonFileContent = readJSONFromAsset()
                        val jObject: JSONObject = JSONObject(jsonFileContent).getJSONObject("list")
                        if (vals.size > 1) {
                            if (vals[0].contains("Hardware") || vals[0].contains("model name")) {
                                val keys = jObject.keys()
                                while (keys.hasNext()) {
                                    val key = keys.next()
                                    if (vals[1].contains(key)) {
                                        val innerJObject = jObject.getJSONObject(key)
                                        val innerKeys = innerJObject.keys()
                                        while (innerKeys.hasNext()) {
                                            val innerKkey = innerKeys.next()
                                            val value = innerJObject.getString(innerKkey)
                                            if (value.isNotEmpty())
                                                tempList[innerKkey] = value
                                        }
                                        break
                                    }
                                }
                            } else {
                                val s = Build.BOARD
                                val keys = jObject.keys()
                                while (keys.hasNext()) {
                                    val key = keys.next()
                                    if (s.contains(key)) {
                                        val innerJObject = jObject.getJSONObject(key)
                                        val innerKeys = innerJObject.keys()
                                        while (innerKeys.hasNext()) {
                                            val innerKkey = innerKeys.next()
                                            val value = innerJObject.getString(innerKkey)
                                            if (value.isNotEmpty())
                                                tempList[innerKkey] = value
                                        }
                                        break
                                    }
                                }
                            }
                        }

                    }
                } catch (e: Exception) {
                    try {
                        val jsonFileContent = readJSONFromAsset()
                        val jObject: JSONObject = JSONObject(jsonFileContent).getJSONObject("list")
                        val s = Build.BOARD
                        val keys = jObject.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            if (s.contains(key)) {
                                val innerJObject = jObject.getJSONObject(key)
                                val innerKeys = innerJObject.keys()
                                while (innerKeys.hasNext()) {
                                    val innerKkey = innerKeys.next()
                                    val value = innerJObject.getString(innerKkey)
                                    if (value.isNotEmpty())
                                        tempList[innerKkey] = value
                                }
                                break
                            }
                        }
                    } catch (e: Exception) {

                    }
                }
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                try {
                    if (tempList.containsKey("MEMORY")) {
                        tvMemoryName.visibility = View.VISIBLE
                        tvMemoryValue.text = tempList.getValue("MEMORY")
                    }

                    if (tempList.containsKey("BANDWIDTH")) {
                        tvBandwidthName.visibility = View.VISIBLE
                        tvBandwidthValue.text = tempList.getValue("BANDWIDTH")
                    }

                    if (tempList.containsKey("CHANNELS")) {
                        tvChannelName.visibility = View.VISIBLE
                        tvChannelValue!!.text = tempList.getValue("CHANNELS")
                    }
                } catch (e: Exception) {

                }
            }

        }
        LoadJson().execute()

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


    fun readJSONFromAsset(): String? {
        val json: String?
        try {
            val inputStream: InputStream = activity!!.assets.open("soc.json")
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
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