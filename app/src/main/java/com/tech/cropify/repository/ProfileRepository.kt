package com.tech.cropify.repository
import com.tech.cropify.model.login.AuthRequest
import com.tech.cropify.model.profile.SoilType
import com.tech.cropify.model.profile.UserProfile
import com.tech.cropify.network.ApiInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

sealed class SaveProfileResult {
    object Success : SaveProfileResult()
    data class Error(val message: String) : SaveProfileResult()
}

@Singleton
class ProfileRepository @Inject constructor(
    @Named("backendApiService") private val apiService: ApiInterface
) {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile

    fun updateProfile(update: (UserProfile) -> UserProfile) {
        _profile.update(update)
    }

    fun updateLocationAndSoil(
        label: String,
        lat: Double,
        lon: Double,
        soilType: SoilType
    ) {
        _profile.update {
            it.copy(locationLabel = label, latitude = lat, longitude = lon, soilType = soilType)
        }
    }

    /**
     * Persists the profile to the backend via POST /user-details, then
     * updates local state only after a successful response so the UI
     * doesn't show stale/unsaved data if the request fails.
     */
    suspend fun saveProfileToServer(updated: UserProfile): SaveProfileResult {
        return try {
            apiService.postUserDetails(
                AuthRequest(
                    userName = updated.userName,
                    emailId = updated.emailId,
                    password = updated.password,
                    phone = updated.phone,
                    state = updated.state,
                    city = updated.city,
                    village = updated.village
                )
            )
            _profile.value = updated
            SaveProfileResult.Success
        } catch (e: Exception) {
            SaveProfileResult.Error(e.message ?: "Failed to save profile. Please try again.")
        }
    }
}