package com.tech.cropify.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.google.gson.Gson
import com.tech.cropify.model.login.AuthRequest
import com.tech.cropify.model.login.LoginResponse
import com.tech.cropify.model.profile.SoilType
import com.tech.cropify.model.profile.UserProfile
import com.tech.cropify.network.ApiInterface
import com.tech.cropify.util.SharedPreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

sealed class SaveProfileResult {
    object Success : SaveProfileResult()
    data class Error(val message: String) : SaveProfileResult()
}

@Singleton
class ProfileRepository @Inject constructor(
    @Named("backendApiService") private val apiService: ApiInterface,
    @ApplicationContext private val appContext: Context
) {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile

    // ── Single source of truth for the avatar shown everywhere in the app ──
    // Backed by the repository (a @Singleton) rather than any one ViewModel,
    // so every screen — Dashboard, Profile, EditProfile — observes the SAME
    // flow no matter which ProfileViewModel instance Hilt/Nav hands them.
    // null      -> show the emoji / initials placeholder
    // non-null  -> backend URL, or a local file path if a photo was just
    //              picked and hasn't synced to the server yet
    private val _profileImageUrl = MutableStateFlow(loadCachedProfileImage())
    val profileImageUrl: StateFlow<String?> = _profileImageUrl

    private fun loadCachedProfileImage(): String? {
        val backendUrl = SharedPreferenceManager.getUserProfile(appContext)
        if (!backendUrl.isNullOrBlank()) return backendUrl
        val localPath = readSavedImagePath()
        if (!localPath.isNullOrBlank()) return localPath
        return null
    }

    private fun readSavedImagePath(): String? {
        val sharedPref = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("profile_image_path", null)
    }

    private fun writeSavedImagePath(path: String) {
        appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("profile_image_path", path)
            .apply()
    }

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
     * Persists the profile to the backend via POST /user-details.
     */
    suspend fun saveProfileToServer(
        updated: UserProfile,
        token: String,
        newProfileImageFile: File?   // pass the actual image File if the user picked a new one, else null
    ): SaveProfileResult {
        return try {
            val json = Gson().toJson(
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
            val dataPart = json.toRequestBody("application/json".toMediaTypeOrNull())

            val picPart = newProfileImageFile?.let { file ->
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profilePic", file.name, requestFile)
            }

            apiService.postUserDetails(
                data = dataPart,
                profilePic = picPart,
                token = token
            )

            // Backend only returns a success string, not the updated user —
            // so if a new pic was uploaded, cache its local path immediately
            // for a snappy preview across every screen. fetchUserDetails(),
            // called right after this by the ViewModel, overwrites this with
            // the authoritative backend URL once the server confirms sync.
            if (newProfileImageFile != null) {
                writeSavedImagePath(newProfileImageFile.absolutePath)
                _profileImageUrl.value = newProfileImageFile.absolutePath
            }

            _profile.value = updated
            SaveProfileResult.Success

        } catch (e: Exception) {
            SaveProfileResult.Error(e.message ?: "Failed to save profile. Please try again.")
        }
    }

    suspend fun getUserDetails(token: String): Result<LoginResponse> {
        return try {
            val response = apiService.getUserDetails(token)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val user = body.user
                    _profile.update { current ->
                        current.copy(
                            userName = user.userName ?: current.userName,
                            emailId = user.emailId ?: current.emailId,
                            phone = user.phone ?: current.phone,
                            state = user.state ?: current.state,
                            city = user.city ?: current.city,
                            village = user.village ?: current.village,
                            profileUri = user.profilePic?.toUri()
                        )
                    }

                    // Update the shared avatar flow immediately — every
                    // screen observing profileImageUrl reflects this the
                    // instant this call succeeds, regardless of which
                    // ProfileViewModel instance triggered the fetch.
                    val picUrl = user.profilePic?.takeIf { it.isNotBlank() }
                    SharedPreferenceManager.saveUserProfile(appContext, picUrl ?: "")
                    _profileImageUrl.value = picUrl ?: readSavedImagePath()

                    Result.success(body)
                } else {
                    Log.e("response", "Get user error: empty body")
                    Result.failure(Exception("Get user details failed"))
                }
            } else {
                Log.e("response", "Get user error: ${response.errorBody()}")
                Result.failure(Exception("Get user details failed"))
            }
        } catch (e: Exception) {
            Log.e("response", "Get user exception: ${e.message}")
            Result.failure(Exception("Get user details failed"))
        }
    }
}