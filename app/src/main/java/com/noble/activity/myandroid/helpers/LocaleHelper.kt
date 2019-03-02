package com.noble.activity.myandroid.helpers

import android.content.Context
import android.preference.PreferenceManager
import java.util.*

object LocaleHelper {
    private val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    private val NEW_LANGUAGE = "NEW_LANGUAGE"

    fun onAttach(context: Context): Context {
        val lang = getPersistedData(context, Locale.getDefault().language)
        return setLocale(context, lang)
    }

    fun onAttach(context: Context, defaultLanguage: String): Context {
        val lang = getPersistedData(context, defaultLanguage)
        return setLocale(context, lang)
    }

    fun getLanguage(context: Context): String? {
        return getPersistedData(context, Locale.getDefault().language)
    }

    fun setLocale(context: Context, language: String?): Context {
        persist(context, language)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }*/

        return updateResourcesLegacy(context, language!!)
    }

    private fun getPersistedData(context: Context, defaultLanguage: String): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
    }

    private fun persist(context: Context, language: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
    }


    private fun updateResources(context: Context, language: String): Context {
        val locale: Locale
        if (language.equals("pt", ignoreCase = true))
            locale = Locale(language, "BR")
        else
            locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale: Locale

        if (language.equals("pt", ignoreCase = true))
            locale = Locale(language, "BR")
        else
            locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    /*** Set preference for showing new badge
     */
    fun getNewBadge(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(NEW_LANGUAGE, true)
    }

    fun setNewBadge(context: Context, language: Boolean?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        editor.putBoolean(NEW_LANGUAGE, language!!)
        editor.apply()
    }
}