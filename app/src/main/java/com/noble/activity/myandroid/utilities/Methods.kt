package com.noble.activity.myandroid.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.DisplayMetrics
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import java.io.File
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

private val CURRENT_FRAGMENT = "CURRENT_FRAGMENT"
private val IS_FIRST_TIME = "isFirstTime"

fun isRequiredField(strText: String?): Boolean {
    return strText != null && !strText.trim { it <= ' ' }.isEmpty()
}


/**
 * @param mainActivity use for get applicationContext
 * @param px           value to convert into dp
 * @return converted dp from px value
 */
fun pxToDp(mainActivity: MainActivity, px: Int): Int {
    val displayMetrics = mainActivity.resources.displayMetrics
    return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}


fun getDate(timeStamp: Long): String {
    try {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(KeyUtil.datePattern)
        val netDate = Date(timeStamp)
        return sdf.format(netDate)
    } catch (ex: Exception) {
        return "xx"
    }

}

/* get Battery Capacity in mAh*/
fun Context.getBatteryCapacity(): Double? {
    var powerProfile: Any? = null
    var batteryCapacity = java.lang.Double.valueOf(-1.0)
    val powerProfileClass = "com.android.internal.os.PowerProfile"

    try {
        powerProfile = Class.forName(powerProfileClass)
            .getConstructor(Context::class.java).newInstance(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    try {
        batteryCapacity = Class.forName(powerProfileClass)
            .getMethod("getAveragePower", java.lang.String::class.java)
            .invoke(powerProfile, "battery.capacity") as Double
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return batteryCapacity
}

fun sizeConversion(size: Long): String {
    if (size > 0) {
        val logSize = (Math.log(size.toDouble()) / Math.log(2.0)).toLong()
        val suffixes = arrayOf(" B", " KB", " MB", " GB", " TB", " PB", " EB", " ZB", " YB")

        val suffixIndex = (logSize / 10).toInt() // 2^10 = 1024

        val displaySize = size / Math.pow(2.0, (suffixIndex * 10).toDouble())
        val df = DecimalFormat("#.##")
        return df.format(displaySize) + suffixes[suffixIndex]
    } else
        return "0B"
}

/*** Meaning of the constants
 * Dv: Absolute humidity in grams/meter3
 * m: Mass constant
 * Tn: Temperature constant
 * Ta: Temperature constant
 * Rh: Actual relative humidity in percent (%) from phone’s sensor
 * Tc: Current temperature in degrees C from phone’ sensor
 * A: Pressure constant in hP
 * K: Temperature constant for converting to kelvin
 */
fun calculateAbsoluteHumidity(temperature: Float, relativeHumidity: Float): Float {
    val Dv: Float
    val m = 17.62f
    val Tn = 243.12f
    val Ta = 216.7f
    val A = 6.112f
    val K = 273.15f
    Dv =
        (Ta.toDouble() * (relativeHumidity / 100).toDouble() * A.toDouble() * Math.exp((m * temperature / (Tn + temperature)).toDouble()) / (K + temperature)).toFloat()
    return Dv
}

/*** Meaning of the constants
 * Td: Dew point temperature in degrees Celsius
 * m: Mass constant
 * Tn: Temperature constant
 * Rh: Actual relative humidity in percent (%) from phone’s sensor
 * Tc: Current temperature in degrees C from phone’ sensor
 */
fun calculateDewPoint(temperature: Float, relativeHumidity: Float): Float {
    val Td: Float
    val m = 17.62f
    val Tn = 243.12f
    Td =
        (Tn * ((Math.log((relativeHumidity / 100).toDouble()) + m * temperature / (Tn + temperature)) / (m - (Math.log((relativeHumidity / 100).toDouble()) + m * temperature / (Tn + temperature))))).toFloat()
    return Td
}

fun isNetworkConnected(mActivity: MainActivity): Boolean {
    val cm = mActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo != null
}

fun isWifiConnected(context: Context): String {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    if (activeNetwork != null) { // connected to the internet
        if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
            // connected to wifi
            return context.resources.getString(R.string.wifi)
        } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
            // connected to the mobile provider's data plan
            return context.resources.getString(R.string.network)
        }
    } else
        return context.resources.getString(R.string.unavailable)
    return ""
}

/**
 * Returns MAC address of the given interface name.
 *
 * @param interfaceName eth0, wlan0 or NULL=use first interface
 * @return mac address or empty string
 */
fun getMACAddress(interfaceName: String?): String {
    try {
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            if (interfaceName != null) {
                if (!intf.name.equals(interfaceName, ignoreCase = true)) continue
            }
            val mac = intf.hardwareAddress ?: return ""
            val buf = StringBuilder()
            for (aMac in mac) buf.append(String.format("%02X:", aMac))
            if (buf.length > 0) buf.deleteCharAt(buf.length - 1)
            return buf.toString()
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    return ""
}

/**
 * Get IP address from first non-localhost interface
 *
 * @param useIPv4 true=return ipv4, false=return ipv6
 * @return address or empty string
 */
fun getIPAddress(useIPv4: Boolean): String {
    try {
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addrs = Collections.list(intf.inetAddresses)
            for (addr in addrs) {
                if (!addr.isLoopbackAddress) {
                    val sAddr = addr.hostAddress
                    //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                    val isIPv4 = sAddr.indexOf(':') < 0

                    if (useIPv4) {
                        if (isIPv4)
                            return sAddr
                    } else {
                        if (!isIPv4) {
                            val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                            return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
                        }
                    }
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    return ""
}

fun calculatePercentage(value: Double, total: Double): Int {
    val usage = (value * 100.0f / total).toInt().toDouble()
    return usage.toInt()
}

fun calculatePercentage1(value: Long, total: Long): Float {
    return value * 100.0f / total
}

fun Context.freeRamMemorySize(): Long {
    val mi = ActivityManager.MemoryInfo()
    val activityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    activityManager.getMemoryInfo(mi)
    return mi.availMem
}

fun Context.totalRamMemorySize(): Long {
    val mi = ActivityManager.MemoryInfo()
    val activityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    activityManager.getMemoryInfo(mi)
    return mi.totalMem
}

fun getBitmapFromVectorDrawable(draw: Drawable): Bitmap {
    var drawable = draw
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = DrawableCompat.wrap(drawable).mutate()
    }

    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}


