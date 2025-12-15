package com.example.coffeshop.Domain

data class WishlistResponse(
    val success: Boolean,
    val data: List<WishlistModel>
)