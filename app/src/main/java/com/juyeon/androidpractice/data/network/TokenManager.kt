package com.juyeon.androidpractice.data.network

import android.content.Context

private const val PREF_NAME = "token_pref"
private const val KEY_TOKEN = "access_token"

class TokenManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun save(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()
    fun get(): String? = prefs.getString(KEY_TOKEN, null)
    fun clear() = prefs.edit().remove(KEY_TOKEN).apply()
}
