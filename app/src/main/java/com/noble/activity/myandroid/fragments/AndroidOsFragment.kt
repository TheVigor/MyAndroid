package com.noble.activity.myandroid.fragments

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.constants.OS_INDEX
import com.noble.activity.myandroid.utilities.getDate
import kotlinx.android.synthetic.main.fragment_os.*
import kotlinx.android.synthetic.main.toolbar_ui.*

class AndroidOSFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_os, container, false)
        (activity as MainActivity).setAdapterPosition(OS_INDEX)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()
        updateOsInfo()
    }

    private fun initToolbar() {
        iv_back.visibility = View.GONE
        tv_title.text = activity!!.resources.getString(R.string.os_label)
        tv_title.setTextColor(activity!!.resources.getColor(R.color.os))
    }

    private fun updateOsInfo() {
        val androidVersion = android.os.Build.VERSION.SDK_INT

        tv_api_level.text = Build.VERSION.SDK_INT.toString()
        tv_version.text = Build.VERSION.RELEASE
        tv_build_id.text = Build.ID
        tv_build_time.text = getDate(Build.TIME)
        tv_fingerprint.text = Build.FINGERPRINT
        tv_Product_name.text = Build.PRODUCT

        tv_sdk_name.text = Build.VERSION_CODES::class.java.fields[android.os.Build.VERSION.SDK_INT].name

        when (androidVersion) {
            11 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.honeycomb).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("February 22, 2011")
            }

            12 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.honeycomb).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("May 10, 2011")
            }

            13 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.honeycomb).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("July 15, 2011")
            }

            14 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.ics).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("October 18, 2011")
            }

            15 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.ics).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("November 28, 2011")
            }

            16 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.jellybean).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("July 9, 2012")
            }

            17 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.jellybean).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("November 13, 2012")
            }

            18 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.jellybean).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("July 24, 2013")
            }

            19 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.kitkat).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("October 31, 2013")
            }

            21 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.lollipop).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("November 12, 2014")
            }

            22 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.lollipop).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("March 9, 2015")
            }

            23 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.marshmallow).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("October 5, 2015")
            }

            24 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.nougat).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("August 22, 2016")
            }

            25 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.nougat).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("October 4, 2016")
            }
            26 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.oreo).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("August 21, 2017")
            }
            27 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.oreo).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("August 3, 2018")
            }
            28 -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.pie).plus(" " + Build.VERSION.RELEASE.toString())
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("March 7, 2018")
            }
            else -> {
                tv_version_name.text = activity!!.resources
                    .getString(R.string.unknown_version)
                tv_release_date.text = activity!!.resources
                    .getString(R.string.release_date).plus("-")
            }
        }
    }

}