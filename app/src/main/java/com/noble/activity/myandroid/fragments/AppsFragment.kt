package com.noble.activity.myandroid.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.adapters.DeviceAdapter
import com.noble.activity.myandroid.constants.SYSTEM_APPS_INDEX
import com.noble.activity.myandroid.constants.USER_APPS_INDEX
import com.noble.activity.myandroid.models.DeviceInfo
import com.noble.activity.myandroid.utilities.KeyUtil
import com.noble.activity.myandroid.utilities.LoadStatus
import com.noble.activity.myandroid.viewmodel.AppsViewModel
import kotlinx.android.synthetic.main.fragment_apps.*
import kotlinx.android.synthetic.main.toolbar_ui.*

class AppsFragment : Fragment(), SearchView.OnQueryTextListener {

    var mode: Int = 0
    private lateinit var adapter: DeviceAdapter

    private val appsList = ArrayList<DeviceInfo>()

    override fun onQueryTextSubmit(query: String?): Boolean {
        adapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.filter.filter(newText)
        return false
    }

    companion object {
        var appsFragment: AppsFragment? = null

        fun getInstance(mode: Int): AppsFragment {
            val appsFragment = AppsFragment()
            val bundle = Bundle()
            bundle.putInt(KeyUtil.KEY_MODE, mode)
            appsFragment.arguments = bundle
            return appsFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_apps, container, false)
        appsFragment = this@AppsFragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getBundleData()

        if (mode == KeyUtil.IS_USER_COME_FROM_USER_APPS)
            (activity as MainActivity).setAdapterPosition(USER_APPS_INDEX)
        else
            (activity as MainActivity).setAdapterPosition(SYSTEM_APPS_INDEX)

        initToolbar()

        rv_apps_list.layoutManager = LinearLayoutManager(activity!!)
        rv_apps_list.hasFixedSize()

        initAppsList()
        searchField.setOnQueryTextListener(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isAdded) {
            initToolbar()
        }
    }

    private fun getBundleData() {
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(KeyUtil.KEY_MODE)) {
                mode = bundle.getInt(KeyUtil.KEY_MODE)
            }
        }
    }

    private fun initToolbar() {
        iv_back.visibility = View.GONE
        (activity as MainActivity).bottomSheetDisable(false)

        if (mode == KeyUtil.IS_USER_COME_FROM_USER_APPS) {
            tv_title.text = activity!!.resources.getString(R.string.user_apps)
            tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.user))
        } else {
            tv_title.text = activity!!.resources.getString(R.string.system_apps)
            tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.system))
        }
    }

    private fun initAppsList() {
        appsList.clear()

        val model = ViewModelProviders.of(activity!!).get(AppsViewModel::class.java)

        model.isAppsLoaded.observe(viewLifecycleOwner, Observer { isAppsLoaded ->
            isAppsLoaded?.let{
                if (isAppsLoaded == LoadStatus.LOADING) {
                    loadingProgressBar.visibility = View.VISIBLE
                } else {
                    loadingProgressBar.visibility = View.GONE
                }
            }
        })

        model.apps.observe(viewLifecycleOwner, Observer { apps ->
            apps?.filterTo(appsList) { it.flags == mode}

            adapter = DeviceAdapter(appsList, mode)

            if (mode == KeyUtil.IS_USER_COME_FROM_USER_APPS) {
                snackBarCustom(coordinatorLayout,
                        appsList.size.toString() + " " + activity!!.resources.getString(R.string.user_apps), true)
            }
            else {
                snackBarCustom(coordinatorLayout,
                        appsList.size.toString() + " " + activity!!.resources.getString(R.string.system_apps), false)
            }

            rv_apps_list.adapter = adapter

        })
    }

    private fun snackBarCustom(view: View, message: String, flag: Boolean) {
        val snackBar = Snackbar.make(view, "" + message, Snackbar.LENGTH_LONG)
        val params =
            snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(16, 12, 16, 20)
        snackBar.view.layoutParams = params
        ViewCompat.setElevation(snackBar.view, 6f)

        val sbView = snackBar.view

        if (flag) {
            snackBar.view.background = ContextCompat.getDrawable(activity!!, R.drawable.material_snackbar_user_apps)
        } else {
            snackBar.view.background = ContextCompat.getDrawable(activity!!, R.drawable.material_snackbar_system_apps)
        }

        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
        textView.maxLines = 10
        snackBar.show()
    }
}