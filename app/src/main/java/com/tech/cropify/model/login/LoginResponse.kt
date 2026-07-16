package com.tech.cropify.model.login

data class LoginResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val userName: String,
    val emailId: String,
    val password: String?=null,
    val phone: String?=null,
    val state: String?=null,
    val city: String?=null,
    val village: String?=null,
    val profilePic: String?=null,
    val createdAt: String?=null,
    val updatedAt: String?=null
)
