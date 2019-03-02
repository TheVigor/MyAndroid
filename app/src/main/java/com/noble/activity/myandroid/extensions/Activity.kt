package com.noble.activity.myandroid.extensions

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import com.noble.activity.myandroid.R

fun AppCompatActivity.addFragment(fragment: Fragment, isAddToBackStack: Boolean, shouldAnimate: Boolean) {
    pushFragment(fragment, null, R.id.fragment_container,
        isAddToBackStack, true, shouldAnimate, false)
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, isAddToBackStack: Boolean, shouldAnimate: Boolean) {
    pushFragment(fragment, null, R.id.fragment_container,
        isAddToBackStack, false, shouldAnimate, false)
}

fun AppCompatActivity.addFragmentIgnorIfCurrent(fragment: Fragment,
                                                isAddToBackStack: Boolean, shouldAnimate: Boolean) {
    pushFragment(fragment, null, R.id.fragment_container,
        isAddToBackStack, true, shouldAnimate, true)
}

fun AppCompatActivity.replaceFragmentIgnorIfCurrent(fragment: Fragment,
                                                    isAddToBackStack: Boolean, shouldAnimate: Boolean) {
    pushFragment(fragment, null, R.id.fragment_container,
        isAddToBackStack, false, shouldAnimate, true)
}


fun AppCompatActivity.addChildFragment(
    fragment: Fragment,
    parentFragment: Fragment,
    containerId: Int,
    isAddToBackStack: Boolean,
    shouldAnimate: Boolean
) {
    pushFragment(fragment, parentFragment, containerId,
        isAddToBackStack, true, shouldAnimate, false)
}

fun AppCompatActivity.replaceChildFragment(
    fragment: Fragment,
    parentFragment: Fragment,
    containerId: Int,
    isAddToBackStack: Boolean,
    shouldAnimate: Boolean
) {
    pushFragment(fragment, parentFragment, containerId,
        isAddToBackStack, false, shouldAnimate, false)
}

fun AppCompatActivity.addChildFragmentIgnoreIfCurrent(
    fragment: Fragment,
    parentFragment: Fragment,
    containerId: Int,
    isAddToBackStack: Boolean,
    shouldAnimate: Boolean
) {
    pushFragment(fragment, parentFragment, containerId,
        isAddToBackStack, true, shouldAnimate, true)
}

fun AppCompatActivity.replaceChildFragmentIgnorIfCurrent(
    fragment: Fragment,
    parentFragment: Fragment,
    containerId: Int,
    isAddToBackStack: Boolean,
    shouldAnimate: Boolean
) {
    pushFragment(fragment, parentFragment, containerId,
        isAddToBackStack, false, shouldAnimate, true)
}


private fun AppCompatActivity.pushFragment(
    fragment: Fragment?,
    parentFragment: Fragment?,
    containerId: Int,
    isAddToBackStack: Boolean,
    isJustAdd: Boolean,
    shouldAnimate: Boolean,
    ignoreIfCurrent: Boolean
) {
    if (fragment == null)
        return


    val fragmentManager =
        parentFragment?.childFragmentManager ?: this.supportFragmentManager// = getSupportFragmentManager();


    // Find current visible fragment
    val fragmentCurrent = fragmentManager.findFragmentById(R.id.fragment_container)

    if (ignoreIfCurrent && fragmentCurrent != null) {
        if (fragment.javaClass.canonicalName.equals(fragmentCurrent.tag!!, ignoreCase = true)) {
            return
        }
    }

    val fragmentTransaction = fragmentManager.beginTransaction()

    if (shouldAnimate) {
        fragmentTransaction.setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    } else {
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
    }

    if (fragmentCurrent != null) {
        fragmentTransaction.hide(fragmentCurrent)
    }

    if (isAddToBackStack) {
        fragmentTransaction.addToBackStack(fragment.javaClass.canonicalName)
    }

    if (isJustAdd) {
        fragmentTransaction.add(containerId, fragment, fragment.javaClass.canonicalName)
    } else {
        fragmentTransaction.replace(containerId, fragment, fragment.javaClass.canonicalName)
    }


    try {
        fragmentTransaction.commitAllowingStateLoss()

        //Methods.hideKeyboard(mActivity)

    } catch (e: Exception) { }

}

fun AppCompatActivity.getCurrentFragment(): Fragment? {
    val fragmentManager = this.supportFragmentManager

    return fragmentManager.findFragmentById(R.id.fragment_container)
}

fun AppCompatActivity.clearBackStackFragmets() {

    try {
        // in my case I get the support fragment manager, it should work with the native one too
        val fragmentManager = this.supportFragmentManager

        // this will clear the back stack and displays no animation on the screen
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        // fragmentManager.popBackStackImmediate(SplashFragment.class.getCanonicalName(),FragmentManager.POP_BACK_STACK_INCLUSIVE);
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


fun clearBackStackFragmets(fragmentManager: FragmentManager) {

    // in my case I get the support fragment manager, it should work with the native one too
    //        FragmentManager fragmentManager = getSupportFragmentManager();
    // this will clear the back stack and displays no animation on the screen
    fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    // fragmentManager.popBackStackImmediate(SplashFragment.class.getCanonicalName(),FragmentManager.POP_BACK_STACK_INCLUSIVE);

    val fragmentList = fragmentManager.fragments
    if (fragmentList != null && !fragmentList.isEmpty()) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        for (fragment in fragmentList) {
            if (fragment != null) {
                fragmentTransaction.remove(fragment)
            }
        }
        fragmentTransaction.commit()
    }


}

fun addTabClildFragment(fragmentParent: Fragment?, fragmentChild: Fragment?) {
    if (fragmentParent != null && fragmentChild != null) {
        val fragmentManager = fragmentParent.childFragmentManager
        clearBackStackFragmets(fragmentManager)
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, fragmentChild, fragmentChild.javaClass.canonicalName)
        fragmentTransaction.commit()
    }
}

fun AppCompatActivity.clearBackStackFragmets(tag: String) {

    // in my case I get the support fragment manager, it should work with the native one too
    val fragmentManager = this.supportFragmentManager

    // this will clear the back stack and displays no animation on the screen
    fragmentManager.popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)


}