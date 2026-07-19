package com.tech.cropify.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.tech.cropify.util.LanguageManager
import com.tech.cropify.util.findActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguageViewModel : ViewModel() {

    private val _selectedLanguageCode = MutableStateFlow("en")
    val selectedLanguageCode: StateFlow<String> = _selectedLanguageCode.asStateFlow()

    fun init(context: Context) {
        _selectedLanguageCode.value = LanguageManager.getSavedLanguageCode(context)
    }

    /**
     * Saves the chosen language and restarts the activity so the new
     * locale takes effect across the whole app immediately.
     */
    fun changeLanguage(context: Context, code: String) {
        if (_selectedLanguageCode.value == code) return
        _selectedLanguageCode.value = code
        LanguageManager.saveLanguageCode(context, code)

        // Restart the host activity to rebuild the Compose tree with new locale.
        // Use findActivity() instead of a direct cast — LocalContext.current
        // is often a wrapped Context, so `context as? Activity` can silently
        // return null and skip the recreate entirely.
        val activity = context.findActivity()
        Log.d("LanguageVM", "Recreating activity for locale=$code: $activity")
        activity?.recreate()
    }
}