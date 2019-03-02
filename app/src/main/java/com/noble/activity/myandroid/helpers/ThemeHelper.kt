package com.noble.activity.myandroid.helpers

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate

object ThemeHelper {
    //private static final boolean isNightModeEnabled = false;
    private val NIGHT_MODE = "NIGHT_MODE"


    fun onAttach(context: Context) {
        val nightMode = isNightModeEnabled(context, false)
        setTheme(context, nightMode)
    }

    fun onAttach(context: Context, defaultTheme: Boolean) {
        val nightMode = isNightModeEnabled(context, defaultTheme)
        setTheme(context, nightMode)
    }

    fun isNightModeEnabled(context: Context, defaultTheme: Boolean): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(NIGHT_MODE, defaultTheme)
    }

    private fun setIsNightModeEnabled(context: Context, isNightModeEnabled: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean(NIGHT_MODE, isNightModeEnabled)
        editor.apply()
    }

    fun setTheme(context: Context, isNightModeEnabled: Boolean) {
        setIsNightModeEnabled(context, isNightModeEnabled)
        if (isNightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}