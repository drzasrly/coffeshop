package com.example.coffeshop.Domain

data class WishlistResponse(
    val success: Boolean,
    val message: String,
    val data: WishlistModel?
)