package com.noble.activity.myandroid.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.noble.activity.myandroid.R

fun Context.addFragment(fragment: Fragment, isAddToBackStack: Boolean, shouldAnimate: Boolean) {
    pushFragment(fragment, R.id.fragment_container, isAddToBackStack, true, shouldAnimate)
}

fun Context.replaceFragment(fragment: Fragment, isAddToBackStack: Boolean, shouldAnimate: Boolean) {
    pushFragment(fragment, R.id.fragment_container, isAddToBackStack, false, shouldAnimate)
}

private fun Context.pushFragment(
    fragment: Fragment?,
    containerId: Int,
    isAddToBackStack: Boolean,
    isJustAdd: Boolean,
    shouldAnimate: Boolean
) {
    if (fragment == null) return
    val fragmentManager: FragmentManager = (this as AppCompatActivity).supportFragmentManager

    val fragmentCurrent = fragmentManager.findFragmentById(R.id.fragment_container)
    val fragmentTransaction = fragmentManager.beginTransaction()

    if (shouldAnimate)
        fragmentTransaction.setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    else
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

    if (fragmentCurrent != null) fragmentTransaction.hide(fragmentCurrent)
    if (isAddToBackStack)
        fragmentTransaction.addToBackStack(fragment.javaClass.canonicalName)
    if (isJustAdd)
        fragmentTransaction.add(containerId, fragment, fragment.javaClass.canonicalName)
    else
        fragmentTransaction.replace(containerId, fragment, fragment.javaClass.canonicalName)

    try {
        fragmentTransaction.commitAllowingStateLoss()
        this.hideKeyboard()
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun Context.removeAllFragmentExceptDashboard() {
    try {
        val fragmentManager = (this as AppCompatActivity).supportFragmentManager

        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentList = fragmentManager.fragments

        if (!fragmentList.isEmpty()) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            for (i in fragmentList.size - 1 downTo 1) {
                fragmentTransaction.remove(fragmentList[i])
            }
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            fragmentTransaction.commit()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun Context.clearBackStackFragments() {
    try {
        val fragmentManager = (this as AppCompatActivity).supportFragmentManager

        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentList = fragmentManager.fragments

        if (fragmentList != null && !fragmentList.isEmpty()) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            for (fragment in fragmentList) {
                if (fragment != null) {
                    fragmentTransaction.remove(fragment!!)
                }
            }

            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            fragmentTransaction.commit()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun Context.hideKeyboard() {
    (this as AppCompatActivity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

    val view = this.currentFocus

    if (view != null) {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun avoidDoubleClicks(view: View) {
    val DELAY_IN_MS: Long = 400
    if (!view.isClickable) {
        return
    }
    view.isClickable = false
    view.postDelayed({ view.isClickable = true }, DELAY_IN_MS)
}


fun Context.rateUsApp() {
    val uri = Uri.parse("market://details?id=com.noble.activity.artifactcards")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    goToMarket.addFlags(
        Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    try {
        this.startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        this.startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=com.noble.activity.artifactcards")))
    }
}
