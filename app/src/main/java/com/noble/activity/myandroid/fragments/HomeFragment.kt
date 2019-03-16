package com.noble.activity.myandroid.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.constants.HOME_INDEX
import com.noble.activity.myandroid.utilities.KeyUtil
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.toolbar_ui.*
import java.security.SecureRandom
import java.util.*

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        (activity as MainActivity).setAdapterPosition(HOME_INDEX)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()

        getBundleData()
        getDeviceInfo()
    }

    private fun initToolbar() {
        iv_back.visibility = View.GONE
        tv_title.text = activity!!.resources.getString(R.string.device)
        tv_title.setTextColor(activity!!.resources.getColor(R.color.dashboard))
        iv_back.setColorFilter(ContextCompat.getColor(activity!!, R.color.darkBlue))
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceInfo() {
        tv_manufacturer.text = Build.MANUFACTURER
        tv_brand_name.text = Build.BRAND
        tv_model_number.text = Build.MODEL
        tv_board.text = Build.BOARD
        tv_hardware.text = Build.HARDWARE
        tv_serial_no.text = Build.SERIAL
        tv_architecture.text = Build.CPU_ABI
        tv_build_date.text =
            DateFormat.format("MMMM dd, yyyy\nh:mm:ss aa", Date(Build.TIME)).toString()
        tv_kernel.text = System.getProperty("os.version")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv_security_patch_level.text = Build.VERSION.SECURITY_PATCH
        }
        else {
            tv_security_patch_level.visibility = View.GONE
            tv_security_patch_level_label.visibility = View.GONE
        }

        @SuppressLint("HardwareIds") val androidID =
            Settings.Secure.getString(activity!!.contentResolver, Settings.Secure.ANDROID_ID)
        tv_android_id.text = androidID

        val wm = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()

        wm.defaultDisplay.getRealSize(size)

        tv_screen_resolution.text = "${size.x} * ${size.y} ${getString(R.string.pixels)}"
        tv_boot_loader.text = Build.BOOTLOADER
        tv_host.text = Build.HOST
        tv_user.text = Build.USER
    }

    private fun getBundleData() {
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(KeyUtil.KEY_MODE)) {
                val mode = bundle.getInt(KeyUtil.KEY_MODE)
            }
        }
    }
}
