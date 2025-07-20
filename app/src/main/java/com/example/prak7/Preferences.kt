package com.example.prak7

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import java.security.MessageDigest

object Preferences {

    private const val PREF_NAME = "LoginPrefs"
    const val KEY_USER_REGISTERED = "user_registered"
    const val KEY_PASS_REGISTERED = "pass_registered"
    const val KEY_USERNAME_LOGGED_IN = "username_logged_in"
    const val KEY_STATUS_LOGGED_IN = "status_logged_in"
    const val KEY_REMEMBER_ME = "remember_me"
    const val KEY_LAST_LOGIN = "last_login"

    private fun getSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // =============== REGISTRATION FUNCTIONS ===============

    fun setRegisteredUser(context: Context, username: String) {
        val editor = getSharedPreference(context).edit()
        editor.putString(KEY_USER_REGISTERED, username)
        editor.apply()
    }

    fun getRegisteredUser(context: Context): String? {
        return getSharedPreference(context).getString(KEY_USER_REGISTERED, null)
    }

    fun setRegisteredPass(context: Context, password: String) {
        val editor = getSharedPreference(context).edit()
        // Hash password before storing (basic security)
        val hashedPassword = hashPassword(password)
        editor.putString(KEY_PASS_REGISTERED, hashedPassword)
        editor.apply()
    }

    fun getRegisteredPass(context: Context): String? {
        return getSharedPreference(context).getString(KEY_PASS_REGISTERED, null)
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

    // =============== AUTHENTICATION FUNCTIONS ===============

    fun isUserRegistered(context: Context): Boolean {
        val username = getRegisteredUser(context)
        val password = getRegisteredPass(context)
        return !username.isNullOrEmpty() && !password.isNullOrEmpty()
    }

    fun validateLogin(context: Context, username: String, password: String): Boolean {
        val registeredUsername = getRegisteredUser(context)
        val registeredPassword = getRegisteredPass(context)

        return if (registeredUsername != null && registeredPassword != null) {
            val hashedInputPassword = hashPassword(password)
            username == registeredUsername && hashedInputPassword == registeredPassword
        } else {
            false
        }
    }

    fun registerUser(context: Context, username: String, password: String): Boolean {
        return try {
            setRegisteredUser(context, username)
            setRegisteredPass(context, password)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun loginUser(context: Context, username: String, password: String): Boolean {
        return if (validateLogin(context, username, password)) {
            setLoggedInUser(context, username)
            setLoggedInStatus(context, true)
            true
        } else {
            false
        }
    }

    // =============== LOGOUT & CLEAR FUNCTIONS ===============

    fun logoutUser(context: Context) {
        val editor = getSharedPreference(context).edit()
        editor.remove(KEY_USERNAME_LOGGED_IN)
        editor.putBoolean(KEY_STATUS_LOGGED_IN, false)

        // Keep remember me setting, only clear if user doesn't want to be remembered
        if (!getRememberMe(context)) {
            editor.remove(KEY_USER_REGISTERED)
        }
        editor.apply()
    }

    fun clearAllData(context: Context) {
        val editor = getSharedPreference(context).edit()
        editor.clear()
        editor.apply()
    }

    fun clearRegistrationData(context: Context) {
        val editor = getSharedPreference(context).edit()
        editor.remove(KEY_USER_REGISTERED)
        editor.remove(KEY_PASS_REGISTERED)
        editor.apply()
    }

    // =============== UTILITY FUNCTIONS ===============

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return Base64.encodeToString(hashBytes, Base64.DEFAULT)
    }

    // Debug function - remove in production
    fun getAllPreferences(context: Context): Map<String, *> {
        return getSharedPreference(context).all
    }
}