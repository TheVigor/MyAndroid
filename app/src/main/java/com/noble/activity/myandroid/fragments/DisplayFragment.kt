package com.noble.activity.myandroid.fragments

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.constants.DISPLAY_INDEX
import com.noble.activity.myandroid.utilities.pxToDp
import kotlinx.android.synthetic.main.fragment_display.*
import kotlinx.android.synthetic.main.toolbar_ui.*
import java.text.DecimalFormat

class DisplayFragment : Fragment() {

    private val dm = DisplayMetrics()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_display, container, false)
        (activity as MainActivity).setAdapterPosition(DISPLAY_INDEX)
        return view
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()
        getDisplayInfo()

        fabDisplay.setOnClickListener {
            //fragmentUtil.addFragment(MultiTouchTestFragment.newInstance("DisplayView"), true, true)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        (activity as MainActivity).bottomSheetDisable(false)

        if (!hidden && isAdded) {
            initToolbar()
        }
    }

    private fun initToolbar() {
        iv_back.visibility = View.GONE
        tv_title.text = activity!!.resources.getString(R.string.display)
        tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.display))
    }

    private fun getDisplayInfo() {
        val display = (activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        activity!!.windowManager.defaultDisplay.getMetrics(dm)

        /*** Screen Size */
        val screenSizes: String = when (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_LARGE -> "Large screen"
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> "Normal screen"
            Configuration.SCREENLAYOUT_SIZE_SMALL -> "Small screen"
            else -> "Screen size is neither large, normal or small"
        }
        tv_screen_size.text = screenSizes

        /*** Screen physical size */
        val wm = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size1 = Point()
        wm.defaultDisplay.getRealSize(size1)

        activity!!.windowManager.defaultDisplay.getMetrics(dm)

        val windowManager = activity!!.windowManager
        val display1 = windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display1.getMetrics(displayMetrics)

        val mWidthPixels = displayMetrics.widthPixels
        val mHeightPixels = displayMetrics.heightPixels

        val wi = mWidthPixels.toDouble() / activity!!.resources.displayMetrics.xdpi.toDouble()
        val hi = mHeightPixels.toDouble() / activity!!.resources.displayMetrics.ydpi.toDouble()
        val x = Math.pow(wi, 2.0)
        val y = Math.pow(hi, 2.0)
        val screenInches = Math.round(Math.sqrt(x + y) * 10) / 10.toDouble()
        tv_physical_size.text = screenInches.toString().plus(" ").plus(activity!!.resources.getString(R.string.inches))

        /*** Screen default orientation */
        when {
            activity!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT ->
                tv_default_orientation.text = activity!!.resources.getString(R.string.orientation_portrait)
            activity!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ->
                tv_default_orientation.text = activity!!.resources.getString(R.string.orientation_landscape)
            activity!!.resources.configuration.orientation == Configuration.ORIENTATION_UNDEFINED ->
                tv_default_orientation.text = activity!!.resources.getString(R.string.orientation_undefined)
        }

        /*** Display screen width and height */
        tv_screen_total_width.text = size1.x.toString().plus(activity!!.resources.getString(R.string.px))
        tv_screen_total_height.text = size1.y.toString().plus(activity!!.resources.getString(R.string.px))

        /*** Screen refresh rate */
        tv_refresh_rate.text = display.refreshRate.toString().plus(activity!!.resources.getString(R.string.fps))

        /*** Display name */
        tv_screen_name.text = display.name.toString()

        /*** Screen Display buckets */
        when {
            activity!!.resources.displayMetrics.density == .75f ->
                tv_display_bucket.text = activity!!.resources.getString(R.string.ldpi)
            activity!!.resources.displayMetrics.density == 1.0f ->
                tv_display_bucket.text = activity!!.resources.getString(R.string.mdpi)
            activity!!.resources.displayMetrics.density == 1.5f ->
                tv_display_bucket.text = activity!!.resources.getString(R.string.hdpi)
            activity!!.resources.displayMetrics.density == 2.0f ->
                tv_display_bucket.text = activity!!.resources.getString(R.string.xhdpi)
            activity!!.resources.displayMetrics.density == 3.0f ->
                tv_display_bucket.text = activity!!.resources.getString(R.string.xxhdpi)
            activity!!.resources.displayMetrics.density == 4.0f ->
                tv_display_bucket.text = activity!!.resources.getString(R.string.xxxhdpi)
        }

        /*** Screen Dpi */
        tv_display_dpi.text = activity!!.resources.displayMetrics.densityDpi.toString().plus(activity!!.resources.getString(R.string.dpi))

        /*** Screen logical density */
        tv_logical_density.text = dm.density.toString()

        /*** Screen scaled density */
        tv_scaled_density.text = dm.scaledDensity.toString()

        /*** Screen xDpi and yDpi */
        tv_xdpi.text = dm.xdpi.toString().plus(activity!!.resources.getString(R.string.dpi))
        tv_ydpi.text = dm.ydpi.toString().plus(activity!!.resources.getString(R.string.dpi))


        /*** Screen usable width and height */
        val size = Point()
        display.getSize(size)
        tv_usable_width.text = size.x.toString().plus(activity!!.resources.getString(R.string.px))
        tv_usable_height.text = size.y.toString().plus(activity!!.resources.getString(R.string.px))

        /*** Screen density independent width and height */
        tv_independent_width.text =
            pxToDp(activity!! as MainActivity, dm.widthPixels).toString().plus(activity!!.resources.getString(R.string.dp))
        tv_independent_height.text =
            pxToDp(activity!! as MainActivity, dm.heightPixels).toString().plus(activity!!.resources.getString(R.string.dp))
    }

    private fun returnToDecimalPlaces(values: Double): String {
        val df = DecimalFormat("#.00")
        return df.format(values)
    }
}