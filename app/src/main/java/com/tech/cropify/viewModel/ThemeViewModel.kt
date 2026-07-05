package com.tech.cropify.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.cropify.util.ThemeMode
import com.tech.cropify.util.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreferences: ThemePreference
): ViewModel() {
    val theme: StateFlow<ThemeMode> =
        themePreferences.themeFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ThemeMode.SYSTEM
            )

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.saveTheme(mode)
        }
    }
}