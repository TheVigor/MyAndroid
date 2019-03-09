package com.noble.activity.myandroid.fragments

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.adapters.CPUAdapter
import com.noble.activity.myandroid.constants.CPU_INDEX
import com.noble.activity.myandroid.models.*
import com.noble.activity.myandroid.utilities.KeyUtil
import kotlinx.android.synthetic.main.fragment_cpu.*
import kotlinx.android.synthetic.main.toolbar_ui.*
import org.json.JSONObject
import java.io.*
import java.util.*
import java.util.regex.Pattern

class CPUFragment : Fragment(), View.OnClickListener {

    internal var statFile: RandomAccessFile? = null
    private var cHandler: Handler? = null
    internal var cpuLine = ""
    private var mCpuInfo: CpuInfo? = null
    private var activityManager: ActivityManager? = null
    private var memoryInfo: ActivityManager.MemoryInfo? = null
    private var animationUp: Animation? = null
    private var animationDown: Animation? = null
    private var isCpuinfo = false

    companion object {
        fun getInstance(mode: Int?): CPUFragment {
            val cpuFragment = CPUFragment()
            val bundle = Bundle()
            bundle.putInt(KeyUtil.IS_USER_COME_FROM_DASHBOARD, mode!!)
            cpuFragment.arguments = bundle
            return cpuFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cpu, container, false)

        animationUp = AnimationUtils.loadAnimation(activity, R.anim.slide_up)
        animationDown = AnimationUtils.loadAnimation(activity, R.anim.slide_down)

        (activity as MainActivity).setAdapterPosition(CPU_INDEX)

        return view
    }

    @SuppressLint("ResourceType")
    override fun onClick(p0: View?) {
        when (p0) {
            llCpuSeeHideContainer -> {
                if (cvCpuProcessor.isShown) {
                    cvCpuProcessor.startAnimation(animationUp)
                    Handler().postDelayed({
                        cvCpuProcessor.visibility = View.GONE
                    }, 200)
                    tvCpuHideSeeGraph.text = activity!!.getText(R.string.see_graph)

                } else {
                    cvCpuProcessor.visibility = View.VISIBLE
                    tvCpuHideSeeGraph.text = activity!!.getText(R.string.hide_graph)
                    cvCpuProcessor.startAnimation(animationDown)
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getBundleData()
        llCpuSeeHideContainer.setOnClickListener(this)

        rv_cpu_feature_list.layoutManager = LinearLayoutManager(activity)
        rv_cpu_feature_list.hasFixedSize()

        activity!!.initCupGraph(linechart_cpu_fragment, 3)

        getCpuInfoMap()
        getMemoryInfo()

        class LoadJson : AsyncTask<Void, Void, Void>() {
            var tempList = HashMap<String, String>()
            @Suppress("NAME_SHADOWING")
            override fun doInBackground(vararg p0: Void?): Void? {
                try {
                    val s = Scanner(File("/proc/cpuinfo"))
                    val jsonFileContent = readJSONFromAsset()
                    val jObject: JSONObject = JSONObject(jsonFileContent).getJSONObject("list")

                    val s1 = Build.BOARD
                    tempList["Hardware"] = s1
                    val keys = jObject.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        if (s1.contains(key)) {
                            isCpuinfo = true
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

                    if (!isCpuinfo) {
                        while (s.hasNextLine()) {
                            val vals = s.nextLine().split(": ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            if (vals.size > 1) {
                                if (vals[0].contains("Hardware") || vals[0].contains("model name")) {
                                    tempList["Hardware"] = vals[1]
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
                                    break
                                }
                            }
                        }
                    }

                } catch (e: Exception) {
                    try {
                        val jsonFileContent = readJSONFromAsset()
                        val jObject: JSONObject = JSONObject(jsonFileContent).getJSONObject("list")
                        val s = Build.BOARD
                        tempList["Hardware"] = s
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

            @SuppressLint("SetTextI18n")
            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                try {
                    llMainContainer.visibility = View.VISIBLE
                    if (tempList.containsKey("VENDOR") && tempList.containsKey("NAME")) {
                        tlGraphicsContain.visibility = View.VISIBLE
                        tvGraphicsName.visibility = View.VISIBLE
                        ivGraphics.visibility = View.VISIBLE
                        llCpuSeeHideContainer.visibility = View.VISIBLE
                        tvGraphicsName.text = tempList.getValue("VENDOR") + "\n" + tempList.getValue("NAME")
                    } else if (tempList.containsKey("VENDOR")) {
                        tlGraphicsContain.visibility = View.VISIBLE
                        tvGraphicsName.visibility = View.VISIBLE
                        ivGraphics.visibility = View.VISIBLE
                        llCpuSeeHideContainer.visibility = View.VISIBLE
                        tvGraphicsName.text = tempList.getValue("VENDOR")
                    } else if (tempList.containsKey("NAME")) {
                        tlGraphicsContain.visibility = View.VISIBLE
                        tvGraphicsName.visibility = View.VISIBLE
                        tvGraphicsName.text = tempList.getValue("NAME")
                    } else {
                        cvCpuProcessor.visibility = View.VISIBLE
                    }
                    if (tempList.containsKey("Hardware")) {
                        tvHadrwareName.visibility = View.VISIBLE
                        tvHadrwareValue.text = tempList.getValue("Hardware")
                        tempList.containsKey("VENDOR") && tempList.containsKey("NAME")
                    }

                    if (tempList.containsKey("CPU")) {
                        tvCpuName.visibility = View.VISIBLE
                        tvCpuValue.text = tempList.getValue("CPU")
                    }

                    if (tempList.containsKey("FAB")) {
                        tvProcessName.visibility = View.VISIBLE
                        tvProcessValue!!.text = tempList.getValue("FAB")
                    }

                    val aa = getNumCores()
                    if (aa > 0) {
                        tvCoreName.visibility = View.VISIBLE
                        tvCoreValue.text = aa.toString()
                    }

                    val bb = getCpuMaxFreq("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
                    val cc = getCpuMaxFreq("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq")
                    if (bb != -1 && cc != -1) {
                        tvFrequenciesName.visibility = View.VISIBLE
                        tvFrequenciesValue.text = cc.toString() + " MHz - " + bb.toString() + " MHz"
                    }

                    try {
                        statFile = RandomAccessFile("/proc/stat", "r")
                    } catch (e: IOException) {
                        // rlCpuFragment!!.visibility = View.GONE
                        llCpuSeeHideContainer.visibility = View.GONE
                        vSeparate.visibility = View.GONE
                        cvCpuProcessor.visibility = View.GONE
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        LoadJson().execute()

    }

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
        try {
            statFile = RandomAccessFile("/proc/stat", "r")
            if (statFile != null) {
                if (cHandler == null) {
                    cHandler = Handler()
                    cHandler!!.postDelayed(cRunnable, 1000)
                }
            }
        } catch (e: IOException) {
            // rlCpuFragment!!.visibility = View.GONE
            llCpuSeeHideContainer.visibility = View.GONE
            if (cHandler != null) {
                cHandler!!.removeCallbacks(cRunnable)
            }
            e.printStackTrace()
        }

    }

    private val cRunnable: Runnable = object : Runnable {
        override fun run() {
            try {
                statFile = RandomAccessFile("/proc/stat", "r")
                if (statFile != null) {

                    cpuLine = statFile!!.readLine()
                    parseCpuLine(cpuLine)

                    activity?.setupGradient(linechart_cpu_fragment, R.color.line_chart_graph_color_2, R.color.line_chart_graph_color_1)
                    activity?.addEntry(3, mCpuInfo!!.usage.toFloat(), linechart_cpu_fragment)

                    val styledString = SpannableString(mCpuInfo!!.usage.toString() + "%")
                    styledString.setSpan(RelativeSizeSpan(0.5f), styledString.length - 1, styledString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    tv_cpu_fragment_data?.text = styledString
                }
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

    private fun parseCpuLine(cpuLine: String?) {
        if (cpuLine != null && cpuLine.isNotEmpty()) {
            val parts = cpuLine.split("[ ]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val cpuLabel = "cpu"
            if (parts[0].indexOf(cpuLabel) != -1) {
                if (mCpuInfo == null) mCpuInfo = CpuInfo()
                mCpuInfo!!.update(parts)
            }
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

        tv_title.text = activity!!.resources.getString(R.string.processor_label)
        tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.cpu))
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun getMemoryInfo() {
        activityManager = activity!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
    }

    private fun getCpuInfoMap() {
        val lists = ArrayList<FeaturesHW>()
        try {
            val s = Scanner(File("/proc/cpuinfo"))
            while (s.hasNextLine()) {
                val vals = s.nextLine().split(": ")
                if (vals.size > 1)
                    lists.add(FeaturesHW(vals[0].trim { it <= ' ' }, vals[1].trim { it <= ' ' }))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val adapter = CPUAdapter(lists)

        //now adding the adapter to RecyclerView
        rv_cpu_feature_list.adapter = adapter
    }

    private fun getCpuMaxFreq(x: String): Int {
        val cpuMaxFreq: String
        return try {
            val reader = RandomAccessFile(x, "r")
            cpuMaxFreq = reader.readLine()
            cpuMaxFreq.replace(" ", "")
            reader.close()
            Integer.parseInt(cpuMaxFreq) / 1000
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }

    }


    private fun getNumCores(): Int {
        class CpuFilter : FileFilter {
            override fun accept(pathname: File): Boolean {
                return Pattern.matches("cpu[0-9]+", pathname.name)
            }
        }

        return try {
            val dir = File("/sys/devices/system/cpu/")
            val files = dir.listFiles(CpuFilter())
            files.size
        } catch (e: Exception) {
            e.printStackTrace()
            1
        }

    }

    fun readJSONFromAsset(): String? {
        val json: String?
        try {
            val inputStream: InputStream = activity!!.assets.open("device_info_soc.json")
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}