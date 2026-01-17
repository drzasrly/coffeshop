package com.example.coffeshop.Domain

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("access_token") // Nama dari Laravel
    val accessToken: String?,       // Nama di Kotlin

    @SerializedName("user")
    val user: UserData?
)