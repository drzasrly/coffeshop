package com.example.coffeshop.Domain

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("user") val user: UserData?
)

data class UserData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String
)