package com.tech.cropify.util

import android.content.Context
import android.util.Log

object SharedPreferenceManager {

    const val PREF_NAME = "My Preference"

    const val TOKEN = "access_token"

    fun saveToken(token: String?, context: Context){
        Log.d("LOGIN", ">>>>>>>>> Saving token to Login: Token: $token")
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(TOKEN, token).apply()
    }

    fun getToken(context: Context): String?{
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(PREF_NAME, "") ?: ""
        Log.d("LOGIN", "<><><>Get the token<><><>: $name")
        return prefs.getString(TOKEN,null)
    }
}