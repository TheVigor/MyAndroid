package com.noble.activity.myandroid

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.models.DeviceInfo
import java.util.ArrayList

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransitionEnter()
    }

    override fun finish() {
        super.finish()
        overridePendingTransitionExit()
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected fun overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected fun overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    companion object {
        var isAppListLoaded = false
        var deviceInfos: List<DeviceInfo> = ArrayList<DeviceInfo>()
    }
}