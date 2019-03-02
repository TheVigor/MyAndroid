package com.noble.activity.myandroid

import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.View
import com.noble.activity.myandroid.extensions.clearBackStackFragmets
import com.noble.activity.myandroid.extensions.replaceFragment
import com.noble.activity.myandroid.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*

class MainActivity : AppCompatActivity() {

    private var animationDrawable: AnimationDrawable? = null

    private var navItemIndex = 0
    private var lastSelectedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigationView()
        drawer_layout.setStatusBarBackground(R.color.colorPrimaryDark)

        //getAppsList()
        this.clearBackStackFragmets()
        this.replaceFragment(HomeFragment(), false, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }
    }

    private fun setupNavigationView() {

        navigationView.apply {
            menu.getItem(0).isChecked = true
            itemIconTintList = null
        }

//        animationDrawable = ll_nav_header_parent.background as AnimationDrawable?
//
//        animationDrawable?.apply {
//            setEnterFadeDuration(10000)
//            setExitFadeDuration(5000)
//            start()
//        }


        drawerlistener()
        //device_name.text = Build.BRAND
        //model_number.text = Build.MODEL

        navigationView.setNavigationItemSelectedListener { menuItem ->

            navItemIndex = when (menuItem.itemId) {
                R.id.nav_device -> 0
                else -> 0
            }

            menuItem.isChecked = !menuItem.isChecked
            menuItem.isChecked = true

            loadHomeFragment()
            true
        }
    }

    private fun drawerlistener() {
        try {
            drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(p0: View, p1: Float) {
                }

                override fun onDrawerOpened(drawerView: View) {
                    drawer_layout.openDrawer(GravityCompat.START)
                }

                override fun onDrawerClosed(drawerView: View) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                }

                override fun onDrawerStateChanged(newState: Int) {

                }

            })
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    private fun selectNavMenu() {
        navigationView.menu.getItem(navItemIndex).isChecked = true
    }


    private fun loadHomeFragment() {
        selectNavMenu()

        if (lastSelectedPosition == navItemIndex) {
            drawer_layout.closeDrawers()
            return
        }

        lastSelectedPosition = navItemIndex

        drawer_layout.closeDrawers()

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app

//        mHandler.postDelayed({
//            val fragment = getFragmentFromDrawer()
//            if (fragment != null) {
//                fragmentUtil.clearBackStackFragmets()
//                fragmentUtil.replaceFragment(fragment, false, true)
//            }
//        }, 300)

        // refresh toolbar menu
        invalidateOptionsMenu()
    }

    private fun getFragmentFromDrawer(): Fragment? {
        return when (navItemIndex) {
            0 -> HomeFragment.getInstance(0)
            else -> HomeFragment()
        }
    }


    fun openDrawer() {
        //Methods.hideKeyboard(this@MainActivity)
        drawer_layout.openDrawer(GravityCompat.START)
    }

    /**
     * Close drawer
     */
    fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
    }

    /**
     * Manage drawer's visibility.
     */
    fun disableDrawer(isEnabled: Boolean) {
        if (isEnabled) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

//    fun getAppsList(): List<DeviceInfo> {
//        val deviceInfos = ArrayList<DeviceInfo>()
//
//        val flags = PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES
//
//        val pm = packageManager
//        val applications = pm.getInstalledApplications(flags)
//
//        for (appInfo in applications) {
//            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) {
//                // System application
//                val icon = pm.getApplicationIcon(appInfo)
//                deviceInfos.add(DeviceInfo(1, icon, pm.getApplicationLabel(appInfo).toString(), appInfo.packageName))
//            } else {
//                // Installed by User
//                val icon = pm.getApplicationIcon(appInfo)
//                deviceInfos.add(DeviceInfo(2, icon, pm.getApplicationLabel(appInfo).toString(), appInfo.packageName))
//            }
//        }
//        return deviceInfos
//    }


}
