package com.tech.cropify.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.tech.cropify.R

class GoogleSignInUtils {

    companion object {

        fun doGoogleSignIn(
            context: Context,
            scope: CoroutineScope,
            launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
            login: (idToken: String, email: String, name: String?, phoneNumber: String?, photoUrl: String) -> Unit
        ) {
            val credentialManager = CredentialManager.create(context)

            scope.launch {
                try {
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(getCredentialOptions(context))
                        .build()

                    val result = credentialManager.getCredential(context, request)
                    handleCredentialResult(result, login)

                } catch (e: NoCredentialException) {
                    Log.e("GoogleSignIn", "NoCredentialException: ${e.message}")
                    try {
                        val result = credentialManager.getCredential(
                            context,
                            GetCredentialRequest.Builder()
                                .addCredentialOption(getAllAccountsCredentialOption(context))
                                .build()
                        )
                        handleCredentialResult(result, login)
                    } catch (e: NoCredentialException) {
                        Log.e("GoogleSignIn", "Fallback NoCredentialException: ${e.message}")
                        launcher?.launch(getIntent())
                    }

                } catch (e: GetCredentialException) {
                    Log.e("GoogleSignIn", "GetCredentialException: ${e.message}, type=${e.type}")
                    e.printStackTrace()
                }
            }
        }

        private fun getIntent(): Intent {
            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }

        // First try: only accounts already authorized for this app
        private fun getCredentialOptions(context: Context): CredentialOption {
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setAutoSelectEnabled(true)
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()
        }

        // Fallback: show ALL Google accounts on the device, let user pick
        private fun getAllAccountsCredentialOption(context: Context): CredentialOption {
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()
        }

        private fun handleCredentialResult(
            result: androidx.credentials.GetCredentialResponse,
            login: (idToken: String, email: String, name: String?, phoneNumber: String?, photoUrl: String) -> Unit
        ) {
            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                        val idToken = googleIdTokenCredential.idToken
                        val email = googleIdTokenCredential.id // email
                        val name = googleIdTokenCredential.displayName

                        val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)
                        Firebase.auth.signInWithCredential(firebaseCred)

                        val user = Firebase.auth.currentUser
                        val uid = user?.uid
                        val photoUrl = user?.photoUrl?.toString() ?: ""
                        val phoneNumber = user?.phoneNumber
                        val emailVerified = user?.isEmailVerified

                        // Trigger your backend login call
                        login(idToken, email, name,phoneNumber, photoUrl)
                    } else {
                        Log.e("GoogleSignIn", "Unexpected credential type: ${credential.type}")
                    }
                }
                else -> {
                    Log.e("GoogleSignIn", "Unexpected credential: $credential")
                }
            }
        }
    }
}