package com.hanly.pw

import android.content.Context
import android.content.SharedPreferences

public class BackgroundManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("background_prefs", Context.MODE_PRIVATE)
    }

    fun clearColorIndex() {
        sharedPreferences.edit().remove(KEY_COLOR_INDEX).apply()
    }

    fun clearImageChanged() {
        sharedPreferences.edit().putBoolean(KEY_IMAGE_CHANGED, false).apply()
    }

    val colorIndex: Int
        get() = sharedPreferences.getInt(KEY_COLOR_INDEX, 0)

    val isImageChanged: Boolean
        get() = sharedPreferences.getBoolean(KEY_IMAGE_CHANGED, false)

    fun markImageChanged() {
        sharedPreferences.edit().putBoolean(KEY_IMAGE_CHANGED, true).apply()
    }

    fun registerChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun saveColorIndex(index: Int) {
        sharedPreferences.edit().putInt(KEY_COLOR_INDEX, index).apply()
    }

    fun unregisterChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        const val KEY_COLOR_INDEX: String = "color_index"
        const val KEY_IMAGE_CHANGED: String = "image_changed"
    }
}