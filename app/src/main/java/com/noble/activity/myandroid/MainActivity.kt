package com.noble.activity.myandroid

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.widget.RelativeLayout
import com.noble.activity.myandroid.adapters.DashboardAdapter
import com.noble.activity.myandroid.constants.*
import com.noble.activity.myandroid.extensions.*
import com.noble.activity.myandroid.fragments.*
import com.noble.activity.myandroid.models.DashboardInfo
import com.noble.activity.myandroid.models.DeviceInfo
import com.noble.activity.myandroid.utilities.KeyUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var drawable: AnimatedVectorDrawable
    private lateinit var drawableBack: AnimatedVectorDrawable

    private lateinit var behavior: BottomSheetBehavior<*>

    private lateinit var listCollectors: MutableList<DashboardInfo>

    private lateinit var dashboardAdapter: DashboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawable = (ContextCompat.getDrawable(this, R.drawable.ic_menu_animatable)
                as AnimatedVectorDrawable)
        drawableBack = (ContextCompat.getDrawable(this, R.drawable.ic_menu_animatable_back)
                    as AnimatedVectorDrawable)

        behavior = BottomSheetBehavior.from<RelativeLayout>(bottom_sheet)

        iv_bottomsheet_category_icon.setImageDrawable(drawable)

        //getAppList().execute()

        clearBackStackFragments()
        replaceFragment(HomeFragment(), false, true)

        initFragmentCollectors()

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = AccelerateDecelerateInterpolator()
        fadeIn.duration = 1500

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateDecelerateInterpolator()
        fadeOut.duration = 1500

        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        tv_bottomsheet_category_name.animation = fadeOut
                        tv_bottomsheet_category_name.setText(R.string.hide_category)
                        tv_bottomsheet_category_name.animation = fadeIn

                        iv_bottomsheet_category_icon.setImageDrawable(drawable)
                        drawable.start()
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        tv_bottomsheet_category_name.animation = fadeOut
                        tv_bottomsheet_category_name.setText(R.string.view_category)
                        tv_bottomsheet_category_name.animation = fadeIn

                        iv_bottomsheet_category_icon.setImageDrawable(drawableBack)
                        drawableBack.start()

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset >= 0.1 && behavior.state != BottomSheetBehavior.STATE_COLLAPSED)
                    bg.visibility = View.VISIBLE
                else
                    bg.visibility = View.GONE
            }
        })

        cd_bottomsheet.setOnClickListener(this)

        rv_dashboard_contain.setHasFixedSize(true)
        rv_dashboard_contain.layoutManager = GridLayoutManager(this, 3)
        dashboardAdapter = DashboardAdapter(this, listCollectors)
        rv_dashboard_contain.adapter = dashboardAdapter
    }


    fun getFragment(pos: Int): Fragment {
        return listCollectors[pos].fragment
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()
                bottom_sheet.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt()))
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    fun setAdapterPosition(position: Int) {
        dashboardAdapter.postion = position
    }

    fun hideBottomSheet() {
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun bottomSheetDisable(isEnable: Boolean) {
        if (isEnable) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            ll_main_frame.setPadding(0, 0, 0, 0)
            bottom_sheet.visibility = View.GONE
        } else {
            val styledAttributes = this.theme.obtainStyledAttributes(
                intArrayOf(android.R.attr.actionBarSize)
            )
            val mActionBarSize = styledAttributes.getDimension(0, 0f).toInt()
            styledAttributes.recycle()
            ll_main_frame.setPadding(0, 0, 0, mActionBarSize)
            bottom_sheet.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED)
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        else
            super.onBackPressed()
    }

    fun refreshApps() {
        //getAppList().execute()
    }

    private fun initFragmentCollectors() {
        listCollectors = ArrayList()

        listCollectors.apply {
            add(DashboardInfo(R.mipmap.ic_home, HOME_COLOR, getString(R.string.home_title), HomeFragment(), HOME_INDEX))
            add(DashboardInfo(R.mipmap.ic_android, OS_COLOR, getString(R.string.os_title), AndroidOSFragment(), OS_INDEX))
            add(DashboardInfo(R.mipmap.ic_processor, CPU_COLOR, getString(R.string.cpu_title), CPUFragment.getInstance(0), CPU_INDEX))
            add(DashboardInfo(R.mipmap.ic_battery, BATTERY_COLOR, getString(R.string.battery_title), BatteryFragment.getInstance(0), BATTERY_INDEX))
            add(DashboardInfo(R.mipmap.ic_wifi, NETWORK_COLOR, getString(R.string.network_title), NetworkFragment.getInstance(0), NETWORK_INDEX))
            add(DashboardInfo(R.mipmap.ic_display, DISPLAY_COLOR, getString(R.string.display_title), DisplayFragment(), DISPLAY_INDEX))
            add(DashboardInfo(R.mipmap.ic_features, FEATURES_COLOR, getString(R.string.features_title), FeaturesFragment(), FEATURES_INDEX))
            add(DashboardInfo(R.mipmap.ic_ram, RAM_COLOR, getString(R.string.ram_title), RamFragment(), RAM_INDEX))
            add(DashboardInfo(R.mipmap.ic_graphics, GRAPHICS_COLOR, getString(R.string.graphics_title), GraphicsFragment(), GRAPHICS_INDEX))
            add(DashboardInfo(R.mipmap.ic_user, USER_APPS_COLOR, getString(R.string.user_apps_title), AppsFragment.getInstance(KeyUtil.IS_USER_COME_FROM_USER_APPS), USER_APPS_INDEX))
            add(DashboardInfo(R.mipmap.ic_system, SYSTEM_APPS_COLOR, getString(R.string.system_apps_title), AppsFragment.getInstance(KeyUtil.IS_USER_COME_FROM_SYSTEM_APPS), SYSTEM_APPS_INDEX))
            add(DashboardInfo(R.mipmap.ic_sensors, SENSORS_COLOR, getString(R.string.sensors_title), SensorsFragment.getInstance(0), SENSORS_INDEX))
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cd_bottomsheet -> {
                if (bg.visibility == View.VISIBLE) bg.visibility = View.GONE

                avoidDoubleClicks(cd_bottomsheet)

                if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED)
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                else
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }
    }

    fun getAppList(): MutableList<DeviceInfo> {
        val deviceInfo: MutableList<DeviceInfo> = ArrayList()

        val flags = PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES
        val applications = packageManager.getInstalledApplications(flags)

        applications.forEach{
            var appType = if (it.flags and ApplicationInfo.FLAG_SYSTEM == 1) 1 else 2

            val icon = packageManager.getApplicationIcon(it)
            deviceInfo.add(DeviceInfo(appType, icon,
                packageManager.getApplicationLabel(it).toString(), it.packageName))
        }

        return deviceInfo
    }
}
