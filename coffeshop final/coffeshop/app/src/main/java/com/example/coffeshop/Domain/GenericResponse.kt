package com.example.coffeshop.Domain

data class GenericResponse(
    val success: Boolean,
    val message: String? = null
)