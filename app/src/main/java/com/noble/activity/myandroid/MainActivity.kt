package com.noble.activity.myandroid

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.CompoundButton
import android.widget.RelativeLayout
import android.widget.Switch
import com.noble.activity.myandroid.adapters.DashboardAdapter
import com.noble.activity.myandroid.extensions.*
import com.noble.activity.myandroid.fragments.*
import com.noble.activity.myandroid.helpers.LocaleHelper
import com.noble.activity.myandroid.helpers.ThemeHelper
import com.noble.activity.myandroid.models.DashboardInfo
import com.noble.activity.myandroid.models.LanguageInfo
import com.noble.activity.myandroid.utilities.sharing
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var pm: PackageManager

    private lateinit var drawable: AnimatedVectorDrawable
    private lateinit var drawableBack: AnimatedVectorDrawable

    private lateinit var behavior: BottomSheetBehavior<*>

    private var isLang: Boolean = false

    private lateinit var list: MutableList<DashboardInfo>

    private var dashboardAdapter: DashboardAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pm = packageManager

        drawable = (ContextCompat.getDrawable(this, R.drawable.ic_menu_animatable)
                as AnimatedVectorDrawable?)!!
        drawableBack = (ContextCompat.getDrawable(this, R.drawable.ic_menu_animatable_back)
                    as AnimatedVectorDrawable?)!!


        behavior = BottomSheetBehavior.from<RelativeLayout>(bottom_sheet)

        iv_bottomsheet_category_icon.setImageDrawable(drawable)

        //getAppList().execute()

        clearBackStackFragments()
        replaceFragment(HomeFragment(), false, true)

        getFragmentData()

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = AccelerateDecelerateInterpolator() // add this
        fadeIn.duration = 1500

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateDecelerateInterpolator() // add this
        fadeOut.duration = 1500

        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (!isLang) {
                            tv_bottomsheet_category_name.animation = fadeOut
                            tv_bottomsheet_category_name.setText(R.string.hide_category)
                            tv_bottomsheet_category_name.animation = fadeIn
                        }
                            iv_bottomsheet_category_icon.setImageDrawable(drawable)
                            drawable.start()
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                        tv_bottomsheet_category_name.animation = fadeOut
                        tv_bottomsheet_category_name.setText(R.string.view_category)
                        tv_bottomsheet_category_name.animation = fadeIn

                        if (isLang) {
                            isLang = false

                            llBottomSheetFragments.visibility = View.VISIBLE
                        }

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

        tvRateUs.setOnClickListener(this)

        rv_dashboard_contain.setHasFixedSize(true)
        rv_dashboard_contain.layoutManager = GridLayoutManager(this, 4)
        dashboardAdapter = DashboardAdapter(this, list)
        rv_dashboard_contain.adapter = dashboardAdapter

        LocaleHelper.setLocale(this, Locale.getDefault().language)
    }


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    fun getFragment(pos: Int): Fragment {
        return list[pos].fragment
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
        dashboardAdapter?.postion = position
    }

    fun getAdapterPostion(): Int {
        return if (dashboardAdapter != null) dashboardAdapter!!.postion else 0
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

    fun getFragmentData() {
        list = ArrayList()
        list.add(DashboardInfo(R.mipmap.ic_home, "#e57373", "Home", HomeFragment(), 0))
        list.add(DashboardInfo(R.mipmap.ic_android, "#f06292", "Android", AndroidOSFragment(), 1))
        list.add(DashboardInfo(R.mipmap.ic_processor, "#9575cd", "CPU", CPUFragment.getInstance(0), 2))
        list.add(DashboardInfo(R.mipmap.ic_battery, "#7986cb", "Battery", BatteryFragment.getInstance(0), 3))
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

            R.id.tvRateUs -> {
                avoidDoubleClicks(tvRateUs)
                hideBottomSheet()
                rateUsApp()
            }
        }
    }



}
