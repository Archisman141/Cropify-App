// viewModel/ProfileViewModel.kt
package com.tech.cropify.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.cropify.model.profile.SoilType
import com.tech.cropify.model.profile.UserProfile
import com.tech.cropify.repository.ProfileRepository
import com.tech.cropify.repository.SaveProfileResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    val profile: StateFlow<UserProfile> = repository.profile

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    fun updateLocationAndSoil(label: String, lat: Double, lon: Double, soilType: SoilType) {
        repository.updateLocationAndSoil(label, lat, lon, soilType)
    }

    /**
     * Called from EditProfileScreen's Save button. Hits POST /user-details,
     * and only updates shared app state (so Dashboard/Profile/Soil screens
     * refresh) once the server confirms the save.
     */
    fun saveProfile(updated: UserProfile, onSuccess: () -> Unit) {
        _isSaving.value = true
        _saveError.value = null
        viewModelScope.launch {
            when (val result = repository.saveProfileToServer(updated)) {
                is SaveProfileResult.Success -> {
                    _isSaving.value = false
                    onSuccess()
                }
                is SaveProfileResult.Error -> {
                    _isSaving.value = false
                    _saveError.value = result.message
                }
            }
        }
    }

    fun clearSaveError() {
        _saveError.value = null
    }
}