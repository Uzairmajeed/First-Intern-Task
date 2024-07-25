package com.facebook.firsttask

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("login_pref", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_IS_LOGGED_IN_AS_ADMIN = "isLoggedInAsAdmin"
        private const val KEY_IS_LOGGED_IN_AS_PARENT = "isLoggedInAsParent"
    }

    fun saveAuthToken(token: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_AUTH_TOKEN, token)
            apply()
        }
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun setLoggedInAsAdmin(isLoggedIn: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_IS_LOGGED_IN_AS_ADMIN, isLoggedIn)
            apply()
        }
    }

    fun isLoggedInAsAdmin(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN_AS_ADMIN, false)
    }

    fun setLoggedInAsParent(isLoggedIn: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_IS_LOGGED_IN_AS_PARENT, isLoggedIn)
            apply()
        }
    }

    fun isLoggedInAsParent(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN_AS_PARENT, false)
    }
}
