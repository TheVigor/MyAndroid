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
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.TextView
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import java.io.File
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

var picturesCount = 0
var audiosCount = 0
var videosCount = 0
var zipCount = 0
var appsCount = 0
var documentsCount = 0
private val CURRENT_FRAGMENT = "CURRENT_FRAGMENT"
private val IS_FIRST_TIME = "isFirstTime"

fun isRequiredField(strText: String?): Boolean {
    return strText != null && !strText.trim { it <= ' ' }.isEmpty()
}

fun getCurrentSelectedFragmentPosition(context: Context): Int {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    return preferences.getInt(CURRENT_FRAGMENT, -1)
}

fun setCurrentSelectedFragmentPosition(context: Context, position: Int) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = preferences.edit()
    editor.putInt(CURRENT_FRAGMENT, position)
    editor.apply()
}

fun isFirstTime(context: Context): Boolean {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    return preferences.getBoolean(IS_FIRST_TIME, false)
}

fun setNotFirstTime(context: Context, position: Boolean) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = preferences.edit()
    editor.putBoolean(IS_FIRST_TIME, position)
    editor.apply()
}

/**
 * HideKeyBoard
 *
 * @param mActivity: Main activity object.
 */
fun hideKeyboard(mActivity: Activity?) {
    if (mActivity != null) {
        mActivity.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
        val view = mActivity.currentFocus
        if (view != null) {
            val imm = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

/***
 * To prevent from double clicking the row item and so prevents overlapping fragment.
 */
fun avoidDoubleClicks(view: View) {
    val DELAY_IN_MS: Long = 400
    if (!view.isClickable) {
        return
    }
    view.isClickable = false
    view.postDelayed({ view.isClickable = true }, DELAY_IN_MS)
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

/**
 * Share using intent.
 *
 * @param message: message
 */
fun Context.sharing(message: String) {
    val sendIntent = Intent(Intent.ACTION_SEND)
    sendIntent.type = "text/plain"
    sendIntent.putExtra(Intent.EXTRA_TEXT, message)
    this.startActivity(Intent.createChooser(sendIntent, "Sharing"))
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

fun getExtension(uri: String?): String? {
    if (uri == null) {
        return null
    }

    val dot = uri.lastIndexOf(".")
    return if (dot >= 0) {
        uri.substring(dot + 1)
    } else {
        // No extension.
        ""
    }
}

/**
 * Set string with spannable.
 *
 * @return: string with two different color
 */
@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.O)
//fun Context.getSpannableSensorText(text: String): SpannableStringBuilder {
//
//
//    val result = text.split("\n\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//    val first = result[0]
//    val second = result[1]
//
//    val font1 = this.resources.getFont(R.font.lato_light)
//    val font2 = this.resources.getFont(R.font.lato_regular)
//
//    val builder = SpannableStringBuilder()
//
//    val dkgraySpannable = SpannableString("$first\n\n ")
//    dkgraySpannable.setSpan(CustomTypefaceSpan("", font1), 0, second.length, 0)
//    builder.append(dkgraySpannable)
//
//    val blackSpannable = SpannableString(second)
//    blackSpannable.setSpan(CustomTypefaceSpan("", font2), 0, second.length, 0)
//    builder.append(blackSpannable)
//    return builder
//}

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

fun getExternalMounts(): HashSet<String> {
    val out = HashSet<String>()
    val reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*"
    val s = StringBuilder()
    try {
        val process = ProcessBuilder().command("mount")
            .redirectErrorStream(true).start()
        process.waitFor()
        val `is` = process.inputStream
        val buffer = ByteArray(1024)
        while (`is`.read(buffer) != -1) {
            s.append(String(buffer))
        }
        `is`.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    // parse output
    val lines = s.toString().split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    for (line in lines) {
        if (!line.toLowerCase(Locale.US).contains("asec")) {
            if (line.matches(reg.toRegex())) {
                //                    Toast.makeText(mActivity, line, Toast.LENGTH_LONG).show();
                val parts = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (part in parts) {
                    if (part.startsWith("/"))
                        if (!part.toLowerCase(Locale.US).contains("vold"))
                            out.add(part)
                }
            }
        }
    }
    return out
}

@SuppressLint("Recycle")
private fun Context.getMediaSizeFromUri(uri: Uri, column: String): Int {
    val cursor: Cursor?
    val columns = arrayOf(column)
    cursor = this.contentResolver.query(
        uri, columns, null, null, null
    )
    assert(cursor != null)
    return cursor!!.count
}

@SuppressLint("Recycle")
private fun Context.getNonMediaSizeFromUri(uri: Uri, column: String, selectionArgs: Array<String>): Int {
    val cursor: Cursor?
    val columns = arrayOf(column)

    val selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
    cursor = this.contentResolver.query(
        uri, columns,
        selectionMimeType, selectionArgs, null
    )
    return if (cursor != null) cursor!!.count else 0
}

@SuppressLint("Recycle")
private fun Context.getAllDocumentsSizeFromUri(uri: Uri, column: String, selectionArgs: Array<String>): Int {
    val cursor: Cursor?
    val columns = arrayOf(column)

    val where = (MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?")
    cursor = this.contentResolver.query(
        uri, columns,
        where, selectionArgs, null
    )
    assert(cursor != null)
    return cursor!!.count
}


fun getAllFiles1(path: String): List<File> {
    val files = File(path).listFiles()
    val flist = ArrayList<File>()
    val dlist = ArrayList<File>()
    for (file in files) {
        if (!file.name.startsWith("."))
            if (file.isDirectory)
                dlist.add(file)
            else
                flist.add(file)
    }
    Collections.sort(flist) { o1, o2 -> o1.name.compareTo(o2.name) }
    Collections.sort(dlist) { o1, o2 -> o1.name.compareTo(o2.name) }
    dlist.addAll(flist)
    if (path != Environment.getExternalStorageDirectory().absolutePath)
        dlist.add(0, File(path))
    return dlist
}

fun Context.getDataCount() {
    picturesCount =
        getMediaSizeFromUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID)
    audiosCount =
        getMediaSizeFromUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID)
    videosCount =
        getMediaSizeFromUri(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media._ID)
    zipCount = getNonMediaSizeFromUri(
        MediaStore.Files.getContentUri("external"), MediaStore.Files.FileColumns.DATA, arrayOf(
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("zip")
        )
    )
    appsCount = getNonMediaSizeFromUri(
        MediaStore.Files.getContentUri("external"), MediaStore.Files.FileColumns.DATA, arrayOf(
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk")
        )
    )
    documentsCount = getAllDocumentsSizeFromUri(
        MediaStore.Files.getContentUri("external"), MediaStore.Files.FileColumns.DATA, arrayOf(
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"),
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc"),
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx"),
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls"),
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx"),
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt"),
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx"),
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"),
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtx"),
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtf"),
            MimeTypeMap.getSingleton().getMimeTypeFromExtension("html")
        )
    )

}

fun Context.tabSelector(textview1: TextView, textview2: TextView,
                color: Int, background_fill: Int, background_unfill: Int) {
    textview1.setTextColor(ContextCompat.getColor(this, R.color.dashboard_background))
    textview1.setBackgroundResource(background_fill)
    textview1.setBackgroundColor(ContextCompat.getColor(this, color))
    textview2.setTextColor(ContextCompat.getColor(this, color))
    textview2.setBackgroundColor(ContextCompat.getColor(this, R.color.font_white))
    textview2.setBackgroundResource(background_unfill)
}