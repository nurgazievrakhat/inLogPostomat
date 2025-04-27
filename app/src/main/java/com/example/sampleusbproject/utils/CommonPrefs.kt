package com.example.sampleusbproject.utils

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonPrefs @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private val gson: Gson = Gson()

    fun isAuthorized(): Boolean {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null) != null
    }

    fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun clearAccessToken() {
        sharedPreferences.edit().remove(KEY_ACCESS_TOKEN).apply()
    }

    fun saveCellMap(cellMap: Map<String, Int>) {
        val json = gson.toJson(cellMap)
        sharedPreferences.edit().putString(KEY_CELL_MAP, json).apply()
    }

    fun getCellMap(): Map<String, Int> {
        val json = sharedPreferences.getString(KEY_CELL_MAP, null) ?: return emptyMap()
        val type = object : TypeToken<Map<String, String>>() {}.type
        val stringMap: Map<String, String> = gson.fromJson(json, type) ?: return emptyMap()

        return stringMap.mapNotNull { (key, value) ->
            value.toIntOrNull()?.let { key to it }
        }.toMap()
    }

    companion object {
        private const val PREFS_NAME = "sample_usb_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_CELL_MAP = "cell_map"
    }
} 