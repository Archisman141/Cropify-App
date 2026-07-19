package com.tech.cropify.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.cropify.model.profile.SoilType
import com.tech.cropify.model.profile.UserProfile
import com.tech.cropify.repository.ProfileRepository
import com.tech.cropify.repository.SaveProfileResult
import com.tech.cropify.util.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    val profile: StateFlow<UserProfile> = repository.profile

    // Now sourced from the singleton repository — shared across every
    // ProfileViewModel instance (Dashboard, Profile, EditProfile all see
    // the same value, regardless of how each screen obtained its instance).
    val profileImageUrl: StateFlow<String?> = repository.profileImageUrl

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadError = MutableStateFlow<String?>(null)
    val loadError: StateFlow<String?> = _loadError.asStateFlow()

    fun updateLocationAndSoil(label: String, lat: Double, lon: Double, soilType: SoilType) {
        repository.updateLocationAndSoil(label, lat, lon, soilType)
    }

    /**
     * Called from EditProfileScreen's Save button. Hits POST /user-details,
     * then re-fetches so the repository's shared flows (profile,
     * profileImageUrl) reflect the authoritative backend state.
     */
    fun saveProfile(
        updated: UserProfile,
        token: String,
        context: Context,
        newProfileImageFile: File? = null,
        onSuccess: () -> Unit
    ) {
        _isSaving.value = true
        _saveError.value = null

        viewModelScope.launch {
            when (val result = repository.saveProfileToServer(updated, token, newProfileImageFile)) {
                is SaveProfileResult.Success -> {
                    _isSaving.value = false
                    fetchUserDetails(context) // pulls the synced URL back from the server
                    onSuccess()
                }
                is SaveProfileResult.Error -> {
                    _isSaving.value = false
                    _saveError.value = result.message
                }
            }
        }
    }

    /**
     * Refreshes the profile from GET /user-details. Call this on Profile and
     * Dashboard open so edits made elsewhere / on another device show up.
     */
    fun fetchUserDetails(context: Context) {
        val token = SharedPreferenceManager.getToken(context)
        if (token.isNullOrBlank()) {
            _loadError.value = "Not logged in."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _loadError.value = null

            repository.getUserDetails(token)
                .onFailure { error ->
                    _loadError.value = error.message ?: "Couldn't load your profile."
                }

            _isLoading.value = false
        }
    }

    fun clearSaveError() {
        _saveError.value = null
    }
}