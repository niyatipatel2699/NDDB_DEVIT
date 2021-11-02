package com.devit.nddb.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.preference.PreferenceManager
import java.util.*


class LocaleManager private constructor(context: Context) {
    private val prefs: SharedPreferences
    fun setLocale(c: Context): Context {
        return updateResources(c, language)
    }

    fun setNewLocale(c: Context, language: String): Context {
        persistLanguage(c, language)
        return updateResources(c, language)
    }

    val language: String?
        get() = prefs.getString(LANGUAGE_KEY, LANGUAGE_ENGLISH)

    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(c: Context, language: String) {

        // use commit() instead of apply(), because sometimes we kill the application process immediately
        // which will prevent apply() to finish
        prefs.edit().putString(LANGUAGE_KEY, language).commit()
    }

    private fun updateResources(context: Context, language: String?): Context {
        var context: Context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res: Resources = context.getResources()
        val configuration: Configuration = res.getConfiguration()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            val localeList = LocaleList(locale)
            configuration.setLocales(localeList)
            configuration.setLayoutDirection(locale)
            context = context.createConfigurationContext(configuration)
            if (context.getApplicationContext() != null) {
                context.getApplicationContext().createConfigurationContext(configuration)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale)
            //            configuration.locale = locale;
            configuration.setLayoutDirection(locale)
            //            context = context.createConfigurationContext(configuration);
            res.updateConfiguration(configuration, res.getDisplayMetrics())
            if (context.getApplicationContext() != null) {
                context.getApplicationContext().getResources()
                    .updateConfiguration(configuration, res.getDisplayMetrics())
            }
        } else {
            configuration.locale = locale
            res.updateConfiguration(configuration, res.getDisplayMetrics())
            if (context.getApplicationContext() != null) {
                context.getApplicationContext().getResources()
                    .updateConfiguration(configuration, res.getDisplayMetrics())
            }
        }
        //        config.setLayoutDirection(locale);
        return context
    }

    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_UKRAINIAN = "uk"
        private const val LANGUAGE_KEY = "language_key"
        private var localeManager: LocaleManager? = null

        @Synchronized
        fun getLocaleManager(context: Context): LocaleManager? {
            if (localeManager == null) {
                localeManager = LocaleManager(context)
            }
            return localeManager
        }

        fun getLocale(res: Resources): Locale {
            val config: Configuration = res.getConfiguration()
            return if (Build.VERSION.SDK_INT >= 24) config.getLocales().get(0) else config.locale
        }
    }

    init {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }
}