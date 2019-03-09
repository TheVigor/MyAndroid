package com.noble.activity.myandroid.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.utilities.KeyUtil
import kotlinx.android.synthetic.main.fragment_app_info.*
import kotlinx.android.synthetic.main.toolbar_ui.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AppInfoFragment : Fragment() {

    private var packageInfo: PackageInfo? = null
    private var mPackageManager: PackageManager? = null
    private var mPackageName: String? = null

    var mode: Int = 0
    private var pos: Int = 0

    companion object {

        const val POS = "pos"
        const val PACKAGE_NAME = "package_name"

        fun getInstance(mode: Int, packageName: String, pos: Int): AppInfoFragment {
            val apkInfoFragment = AppInfoFragment()

            val bundle = Bundle()
            bundle.putInt(KeyUtil.KEY_MODE, mode)
            bundle.putInt(POS, pos)
            bundle.putString(PACKAGE_NAME, packageName)
            apkInfoFragment.arguments = bundle

            return apkInfoFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_app_info, container, false)

        mPackageName = arguments!!.getString(PACKAGE_NAME)
        mPackageManager = activity!!.packageManager
        packageInfo = getPackageInfo(mPackageName!!)

        return view
    }

    @Suppress("DEPRECATION")
    @SuppressLint("PackageManagerGetSignatures")
    private fun getPackageInfo(packageName: String): PackageInfo? {
        return try {
            mPackageManager!!.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS
                    or PackageManager.GET_ACTIVITIES or PackageManager.GET_RECEIVERS or PackageManager.GET_PROVIDERS
                    or PackageManager.GET_SERVICES or PackageManager.GET_URI_PERMISSION_PATTERNS
                    or PackageManager.GET_SIGNATURES or PackageManager.GET_CONFIGURATIONS)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getBundleData()

        (activity as MainActivity).bottomSheetDisable(true)

        initToolbar()
        setValues()
    }

    private fun getBundleData() {
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(KeyUtil.KEY_MODE)) {
                mode = bundle.getInt(KeyUtil.KEY_MODE)
                pos = bundle.getInt(POS)
            }
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    private fun setValues() {
        if (packageInfo == null) return

        try {
            appName.text = context!!.packageManager.getApplicationLabel(packageInfo!!.applicationInfo)
            packageName.text = packageInfo!!.packageName

            val appIcon = mPackageManager!!.getApplicationIcon(packageInfo!!.applicationInfo)
            appIconImage.setImageDrawable(appIcon)

            installedDateTime.text = getString(R.string.installed) + setDateFormat(packageInfo!!.firstInstallTime)
            lastUsedTime.text = getString(R.string.updated) + setDateFormat(packageInfo!!.lastUpdateTime)
            version.text = getString(R.string.version_app) + packageInfo!!.versionName

            if (mode == KeyUtil.IS_USER_COME_FROM_SYSTEM_APPS) {
                redirectCardView.visibility = View.GONE
            }

            settingsCardView.setOnClickListener {
                getInstalledAppDetails(activity!!)
            }

            redirectCardView.setOnClickListener {
                redirectAppToPlayStore()
            }


            if (packageInfo!!.requestedPermissions != null)
                req_permission.text = getPermissions(packageInfo!!.requestedPermissions)
            else
                req_permission.text = "-"
        } catch (e: Exception) { }
    }


    private fun redirectAppToPlayStore() {
        val appPackageName = packageInfo!!.packageName // getPackageName() from Context or Activity object
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isAdded) {
            initToolbar()
        }
    }

    private fun getInstalledAppDetails(context: Activity?) {
        if (context == null) {
            return
        }
        try {
            val i = Intent()
            i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            i.addCategory(Intent.CATEGORY_DEFAULT)
            i.data = Uri.parse("package:" + packageInfo!!.packageName)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            context.startActivity(i)
        } catch (e: Exception) {}
    }

    private fun initToolbar() {
        try {
            iv_back.visibility = View.VISIBLE
            tv_title.text = context!!.packageManager.getApplicationLabel(packageInfo!!.applicationInfo)
            if (mode == KeyUtil.IS_USER_COME_FROM_USER_APPS)
                tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.user))
            else
                tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.system))
            iv_back.setColorFilter(ContextCompat.getColor(activity!!, R.color.darkBlue))
            iv_back.setOnClickListener {
                activity!!.onBackPressed()
                (activity as MainActivity).bottomSheetDisable(false)
            }
        } catch (e: Exception) {}
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDateFormat(time: Long): String {
        val date = Date(time)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return formatter.format(date)
    }

    private fun getPermissions(requestedPermissions: Array<String>): String {
        var permission = ""

        requestedPermissions.forEach {
            permission += "$it,\n"
        }

        return permission
    }
}