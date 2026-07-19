package com.tech.cropify.model.login

data class AuthRequest(
    val userName: String?,
    val emailId: String,
    val password: String?,
    val phone: String?,
    val state: String? = null,
    val city: String? = null,
    val village: String? = null,
    val profilePic: String? = null,
)