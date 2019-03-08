package com.noble.activity.myandroid.fragments

import android.annotation.SuppressLint
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
import com.noble.activity.myandroid.models.DeviceInfo
import com.noble.activity.myandroid.utilities.KeyUtil
import kotlinx.android.synthetic.main.fragment_apps.*
import kotlinx.android.synthetic.main.toolbar_ui.*

class AppsFragment : Fragment(), SearchView.OnQueryTextListener {

    var mode: Int? = 0
    var adapter: DeviceAdapter? = null

    private val lists = ArrayList<DeviceInfo>()

    override fun onQueryTextSubmit(query: String?): Boolean {
        adapter?.filter?.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter?.filter?.filter(newText)
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
            (activity as MainActivity).setAdapterPosition(9)
        else
            (activity as MainActivity).setAdapterPosition(10)

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

        if (mode?.equals(KeyUtil.IS_USER_COME_FROM_USER_APPS)!!) {
            tv_title.text = activity!!.resources.getString(R.string.user_apps)
            tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.user))
        } else {
            tv_title.text = activity!!.resources.getString(R.string.system_apps)
            tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.system))
        }
    }

    fun refreshList(position: Int) {
        if (lists.isNotEmpty() && rv_apps_list != null && rv_apps_list!!.adapter != null) {
            lists.removeAt(position)
            rv_apps_list.adapter!!.notifyDataSetChanged()
        }
    }

    private fun initAppsList() {
        lists.clear()

        // TODO: need do in async way !!!!!!!!!!!!!!!!!!!!!!!!!
        (activity as MainActivity).getAppList().filterTo(lists) { it.flags == mode }

        //creating our adapter
        adapter = mode?.let { DeviceAdapter(lists, activity as MainActivity, it) }

        if (mode == KeyUtil.IS_USER_COME_FROM_USER_APPS)
            snackBarCustom(coordinatorLayout,
                lists.size.toString() + " " + activity!!.resources.getString(R.string.user_apps), true)
        else
            snackBarCustom(coordinatorLayout,
                lists.size.toString() + " " + activity!!.resources.getString(R.string.system_apps), false)
        //now adding the adapter to RecyclerView
        rv_apps_list.adapter = adapter
    }

    private fun snackBarCustom(view: View, message: String, flag: Boolean) {
        val snackBar = Snackbar.make(view, "" + message, Snackbar.LENGTH_LONG)
        val params =
            snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(16, 12, 16, 20)
        snackBar.view.layoutParams = params
        ViewCompat.setElevation(snackBar.view, 6f)

        val sbView = snackBar.view
        if (flag) snackBar.view.background = ContextCompat.getDrawable(activity!!, R.drawable.material_snackbar_user_apps)
        else snackBar.view.background = ContextCompat.getDrawable(activity!!, R.drawable.material_snackbar_system_apps)

        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
        textView.maxLines = 10
        snackBar.show()
    }
}