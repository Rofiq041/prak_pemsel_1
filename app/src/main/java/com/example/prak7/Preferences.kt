package com.example.prak7

import android.content.Context
import android.content.SharedPreferences

object Preferences {

    private const val PREF_NAME = "LoginPrefs"
    const val KEY_USERNAME_LOGGED_IN = "username_logged_in"
    const val KEY_STATUS_LOGGED_IN = "status_logged_in"
    const val KEY_REMEMBER_ME = "remember_me"
    const val KEY_LAST_LOGIN = "last_login"

    private fun getSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // =============== LOGIN SESSION FUNCTIONS ===============

    fun setLoggedInUser(context: Context, username: String) {
        val editor = getSharedPreference(context).edit()
        editor.putString(KEY_USERNAME_LOGGED_IN, username)
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
        editor.apply()
    }

    fun getLoggedInUser(context: Context): String? {
        return getSharedPreference(context).getString(KEY_USERNAME_LOGGED_IN, null)
    }

    fun setLoggedInStatus(context: Context, status: Boolean) {
        val editor = getSharedPreference(context).edit()
        editor.putBoolean(KEY_STATUS_LOGGED_IN, status)
        if (status) {
            editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
        }
        editor.apply()
    }

    fun getLoggedInStatus(context: Context): Boolean {
        return getSharedPreference(context).getBoolean(KEY_STATUS_LOGGED_IN, false)
    }

    fun setRememberMe(context: Context, remember: Boolean) {
        val editor = getSharedPreference(context).edit()
        editor.putBoolean(KEY_REMEMBER_ME, remember)
        editor.apply()
    }

    fun getRememberMe(context: Context): Boolean {
        return getSharedPreference(context).getBoolean(KEY_REMEMBER_ME, false)
    }

    fun getLastLoginTime(context: Context): Long {
        return getSharedPreference(context).getLong(KEY_LAST_LOGIN, 0L)
    }

    // =============== LOGOUT & CLEAR FUNCTIONS ===============

    fun logoutUser(context: Context) {
        val editor = getSharedPreference(context).edit()
        editor.remove(KEY_USERNAME_LOGGED_IN)
        editor.putBoolean(KEY_STATUS_LOGGED_IN, false)

        // Keep remember me setting, only clear if user doesn't want to be remembered
        if (!getRememberMe(context)) {
            // editor.remove(KEY_USER_REGISTERED) // Dihapus karena tidak lagi relevan
        }
        editor.apply()
    }

    fun clearLoggedInUser(context: Context) {
        val editor = getSharedPreference(context).edit()
        editor.remove(KEY_USERNAME_LOGGED_IN)
        editor.remove(KEY_STATUS_LOGGED_IN)
        editor.apply()
    }

    fun clearAllData(context: Context) {
        val editor = getSharedPreference(context).edit()
        editor.clear()
        editor.apply()
    }

    // =============== UTILITY FUNCTIONS ===============

    // Debug function - remove in production
    fun getAllPreferences(context: Context): Map<String, *> {
        return getSharedPreference(context).all
    }
}