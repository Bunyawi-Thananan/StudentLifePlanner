package com.studentlifeplanner.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    fun saveInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return prefs.getInt(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    fun saveLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return prefs.getLong(key, defaultValue)
    }

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    // Helper methods for user session
    fun setLoggedIn(isLoggedIn: Boolean) {
        saveBoolean(Constants.PREF_IS_LOGGED_IN, isLoggedIn)
    }

    fun isLoggedIn(): Boolean {
        return getBoolean(Constants.PREF_IS_LOGGED_IN, false)
    }

    fun setUserId(userId: Int) {
        saveInt(Constants.PREF_USER_ID, userId)
    }

    fun getUserId(): Int {
        return getInt(Constants.PREF_USER_ID, 0)
    }

    fun setUsername(username: String) {
        saveString(Constants.PREF_USERNAME, username)
    }

    fun getUsername(): String {
        return getString(Constants.PREF_USERNAME)
    }

    fun setFullName(fullName: String) {
        saveString(Constants.PREF_FULL_NAME, fullName)
    }

    fun getFullName(): String {
        return getString(Constants.PREF_FULL_NAME)
    }

    fun setEmail(email: String) {
        saveString(Constants.PREF_EMAIL, email)
    }

    fun getEmail(): String {
        return getString(Constants.PREF_EMAIL)
    }

    fun logout() {
        clear()
    }
}