package com.example.coffeshop.Domain

data class WishlistModel(
    val id: Int,
    val user_id: Int,
    val menu_id: String, // ID Menu dari Firebase
    val menu: MenuModel? // Relasi ke Model Menu
)