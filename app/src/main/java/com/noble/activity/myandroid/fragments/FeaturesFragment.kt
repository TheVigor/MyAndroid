package com.noble.activity.myandroid.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import kotlinx.android.synthetic.main.fragment_features.*
import kotlinx.android.synthetic.main.toolbar_ui.*

class FeaturesFragment : Fragment() {

    private var packageManager: PackageManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_features, container, false)

        (activity as MainActivity).setAdapterPosition(6)
        packageManager = activity!!.packageManager

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()
        getDeviceFeatures()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isAdded) {
            initToolbar()
        }
    }

    private fun initToolbar() {
        iv_back.visibility = View.GONE
        tv_title.text = activity!!.resources.getString(R.string.features)
        tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.features))
    }

    private fun getDeviceFeatures() {
        /** WIFI feature */
        val connManager = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        // WIFI
        if (mWifi.isAvailable) {
            tv_wifi.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_wifi.text = activity!!.resources.getString(R.string.not_supported)
        }

        // WIFI Direct
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)!!) {
            tv_wifi_direct.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_wifi_direct.text = activity!!.resources.getString(R.string.not_supported)
        }

        // Bluetooth
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)!!) {
            tv_bluetooth.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_bluetooth.text = activity!!.resources.getString(R.string.not_supported)
        }

        // Bluetooth LE
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)!!) {
            tv_bluetooth_le.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_bluetooth_le.text = activity!!.resources.getString(R.string.not_supported)
        }

        // GPS
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)!!) {
            tv_gps.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_gps.text = activity!!.resources.getString(R.string.not_supported)
        }

        // Camera Flash
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)!!) {
            tv_camera_flash.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_camera_flash.text = activity!!.resources.getString(R.string.not_supported)
        }

        // Camera Front
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)!!) {
            tv_camera_front.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_camera_front.text = activity!!.resources.getString(R.string.not_supported)
        }

        // Microphone
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)!!) {
            tv_microphone.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_microphone.text = activity!!.resources.getString(R.string.not_supported)
        }

        // NFC
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_NFC)!!) {
            tv_nfc.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_nfc.text = activity!!.resources.getString(R.string.not_supported)
        }

        // USB Host
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_USB_HOST)!!) {
            tv_usb_host.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_usb_host.text = activity!!.resources.getString(R.string.not_supported)
        }

        // USB Accessory
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY)!!) {
            tv_usb_accessory.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_usb_accessory.text = activity!!.resources.getString(R.string.not_supported)
        }

        // Multitouch
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)!!) {
            tv_multitouch.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_multitouch.text = activity!!.resources.getString(R.string.not_supported)
        }

        // Audio low-latency
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_AUDIO_LOW_LATENCY)!!) {
            tv_audio_low_latency.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_audio_low_latency.text = activity!!.resources.getString(R.string.not_supported)
        }

        // Audio Output
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)!!) {
            tv_audio_output.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_audio_output.text = activity!!.resources.getString(R.string.not_supported)
        }

        // Professional Audio
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_AUDIO_PRO)!!) {
            tv_professional_audio.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_professional_audio.text = activity!!.resources.getString(R.string.not_supported)
        }

        // Consumer IR
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR)!!) {
            tv_consumer_ir.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_consumer_ir.text = activity!!.resources.getString(R.string.not_supported)
        }

        // GamePad Support
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_GAMEPAD)!!) {
            tv_gamepad_support.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_gamepad_support.text = activity!!.resources.getString(R.string.not_supported)
        }

        // HIFI Sensor
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_HIFI_SENSORS)!!) {
            tv_hifi_sensor.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_hifi_sensor.text = activity!!.resources.getString(R.string.not_supported)
        }

        // Printing
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_PRINTING)!!) {
            tv_printing.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_printing.text = activity!!.resources.getString(R.string.not_supported)
        }

        // CDMA
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA)!!) {
            tv_cdma.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_cdma.text = activity!!.resources.getString(R.string.not_supported)
        }

        // GSM
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_GSM)!!) {
            tv_gsm.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_gsm.text = activity!!.resources.getString(R.string.not_supported)
        }

        // Finger-print
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)!!) {
            tv_fingerprint.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_fingerprint.text = activity!!.resources.getString(R.string.not_supported)
        }

        // App Widgets
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_APP_WIDGETS)!!) {
            tv_app_widgets.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_app_widgets.text = activity!!.resources.getString(R.string.not_supported)
        }

        // SIP
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_SIP)!!) {
            tv_sip.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_sip.text = activity!!.resources.getString(R.string.not_supported)
        }

        // SIP based VOIP
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_SIP_VOIP)!!) {
            tv_sip_based_voip.text = activity!!.resources.getString(R.string.available)
        } else {
            tv_sip_based_voip.text = activity!!.resources.getString(R.string.not_supported)
        }
    }
}