package com.example.zikk.extensions

import android.content.Context
import androidx.core.content.edit

// extensions/ContextExtensions.kt
private const val PREF_NAME = "app_prefs"
private const val KEY_LOGIN_TOKEN = "auth_token"

fun Context.saveLoginToken(token: String) {
    getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        .edit() {
            putString(KEY_LOGIN_TOKEN, token)
        }
}

fun Context.getLoginToken(): String? {
    return getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        .getString(KEY_LOGIN_TOKEN, null)
}

fun Context.removeLoginToken() {
    getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        .edit() {
            remove(KEY_LOGIN_TOKEN)
        }
}