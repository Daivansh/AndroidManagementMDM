package com.daivansh.androidmanagementmdm.utils


import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceHelper {

    /**
     * Set a string shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    fun setSharedPreferenceString(context: Context, prefFile: String, key: String, value: String?) {
        val settings = context.getSharedPreferences(prefFile, 0)
        val editor = settings.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * Set a integer shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    fun setSharedPreferenceInt(context: Context, prefFile: String, key: String, value: Int) {
        val settings = context.getSharedPreferences(prefFile, 0)
        val editor = settings.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    /**
     * Set a Boolean shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    fun setSharedPreferenceBoolean(context: Context, prefFile: String, key: String, value: Boolean) {
        val settings = context.getSharedPreferences(prefFile, 0)
        val editor = settings.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * Get a string shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    fun getSharedPreferenceString(context: Context, prefFile: String, key: String, defValue: String): String {
        val settings = context.getSharedPreferences(prefFile, 0)
        return settings.getString(key, defValue)
    }

    /**
     * Get a integer shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    fun getSharedPreferenceInt(context: Context, prefFile: String, key: String, defValue: Int): Int {
        val settings = context.getSharedPreferences(prefFile, 0)
        return settings.getInt(key, defValue)
    }

    /**
     * Get a boolean shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    fun getSharedPreferenceBoolean(context: Context, prefFile: String, key: String, defValue: Boolean): Boolean {
        val settings = context.getSharedPreferences(prefFile, 0)
        return settings.getBoolean(key, defValue)
    }
}