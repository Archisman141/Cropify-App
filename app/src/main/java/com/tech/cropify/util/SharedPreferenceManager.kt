package com.tech.cropify.util

import android.content.Context
import android.util.Log
import com.google.gson.Gson

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

    fun clearToken(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(TOKEN)
            .apply()
    }

    fun saveUserProfile(context: Context, profile: String) {
        val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("user_profile", profile).apply()
    }

    // Retrieve user profile from SharedPreferences
    fun getUserProfile(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_profile", null)
    }

    //////// Profile User Name //////////
    fun saveUserName(context: Context, userName: String) {
        val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val gson = Gson()
        with(sharedPreferences.edit()) {
            putString("userName", userName)
            apply()
        }
    }

    fun getUserName(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userName", null)
    }
}