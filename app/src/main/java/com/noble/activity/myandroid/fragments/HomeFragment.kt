package com.noble.activity.myandroid.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.utilities.KeyUtil
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.toolbar_ui.*

class HomeFragment : Fragment() {

    private var mode: Int = 0

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        window?.navigationBarColor = resources.getColor(R.color.colorPrimaryDark)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()
        getBundleData()
        getDeviceInfo()
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isAdded) {
            initToolbar()
        }
    }

    private fun initToolbar() {
        iv_menu.visibility = View.VISIBLE
        iv_back.visibility = View.GONE
        tv_title.setText((R.string.device))
        iv_menu.setOnClickListener { (activity as MainActivity).openDrawer() }
    }

    private fun getDeviceInfo() {
        tv_manufacturer.text = Build.MANUFACTURER
        tv_manufacturer.text = Build.BRAND
        tv_model.text = Build.MODEL
        tv_board.text = Build.BOARD
        tv_hardware.text = Build.HARDWARE

        tv_serial_no.text = Build.SERIAL

        @SuppressLint("HardwareIds") val androidID =
            Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
        tv_android_id.text = androidID

        val wm = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        tv_screen_resolution.text = "$width * $height Pixels"
        tv_boot_loader.text = Build.BOOTLOADER
        tv_host.text = Build.HOST
        tv_user.text = Build.USER
    }

    /**
     * Get data from bundle
     */
    private fun getBundleData() {
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(KeyUtil.KEY_MODE)) {
                mode = bundle.getInt(KeyUtil.KEY_MODE)
            }
        }
    }

    companion object {
        fun getInstance(mode: Int): HomeFragment {
            val homeFragment = HomeFragment()
            val bundle = Bundle()
            bundle.putInt(KeyUtil.KEY_MODE, mode)
            homeFragment.arguments = bundle

            return homeFragment
        }
    }
}
