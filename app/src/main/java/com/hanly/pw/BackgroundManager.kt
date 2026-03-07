package com.hanly.pw

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class BackgroundManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("background_prefs", Context.MODE_PRIVATE)

    fun clearImageChanged() {
        sharedPreferences.edit { remove(KEY_IMAGE_CHANGED) }
    }

    val colorIndex: Int
        get() {
            val index = sharedPreferences.getInt(KEY_COLOR_INDEX, 0)
            android.util.Log.d("BackgroundManager", "Color index retrieved: $index")
            return index
        }

    val isImageChanged: Boolean
        get() = sharedPreferences.getBoolean(KEY_IMAGE_CHANGED, false)

    fun markImageChanged() {
        sharedPreferences.edit { putBoolean(KEY_IMAGE_CHANGED, true) }
    }

    fun registerChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun saveColorIndex(index: Int) {
        sharedPreferences.edit { putInt(KEY_COLOR_INDEX, index) }
        android.util.Log.d("BackgroundManager", "Color index saved: $index")
    }

    fun unregisterChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        const val KEY_COLOR_INDEX: String = "color_index"
        const val KEY_IMAGE_CHANGED: String = "image_changed"
    }
}