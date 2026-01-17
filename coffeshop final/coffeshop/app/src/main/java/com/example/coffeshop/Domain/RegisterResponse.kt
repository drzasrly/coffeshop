package com.example.coffeshop.Domain

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val user: RegisterUserData? // Nama diubah di sini
)

// Nama class diubah agar tidak bentrok dengan UserData yang sudah ada
data class RegisterUserData(
    val id: Int,
    val name: String,
    val email: String
)